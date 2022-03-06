package com.gcc.airbase.esserver.actuator;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.gcc.airbase.esserver.dto.ESearchParamDto;
import com.gcc.airbase.esserver.dto.SearchRetDto;
import com.gcc.airbase.esserver.log.annotation.ExecuteLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.query.QueryAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.util.*;

@ConditionalOnProperty(name="airbase.esserver-enable", havingValue="true")
@Component
@Slf4j
public class ElasticSearchActuatorService implements ElasticSearchActuator{

    @Qualifier("EsHighLevelClient")
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    TransportClient transportClient;

    @Override
    public boolean checkIndex(String indexName) {
        if(!StrUtil.isBlankIfStr(indexName)){
            try{
                return restHighLevelClient.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
            }catch (Exception e){
                log.error("Es检测索引"+indexName+"失败!."+e);
            }
        }
        return false;
    }

    @Override
    public boolean createIndex(String indexName, JSONObject mapping) {
        if(checkIndex(indexName)){
            log.warn("索引"+indexName+"已经存在，创建索引失败.");
            return false;
        }
        if(null == mapping || mapping.isEmpty() || StrUtil.isBlankIfStr(indexName)){
            log.warn("构建mapping或索引名称为空，创建索引失败.");
        }
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        Map<String,Object> source = new HashMap<>();
        source.put("properties",mapping);
        request.mapping(source);
        try {
            restHighLevelClient.indices().create(request,RequestOptions.DEFAULT);
            return true;
        }catch (Exception e){
            log.error("构建索引"+indexName+"失败，es错误 "+e);
        }
        return false;
    }

    @Override
    public boolean deleteIndex(String indexName) {
        if(!checkIndex(indexName)){
            log.warn("索引"+indexName+"不存在，创建删除失败.");
            return false;
        }
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        try {
            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            return true;
        }catch (Exception e){
            log.error("删除索引"+indexName+"失败"+e);
        }
        return false;
    }

    @Override
    public boolean addIndexData(String indexName, JSONObject data) {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest().index(indexName).opType("create").source(data, XContentType.JSON).create(true));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            return true;
        }catch (Exception e){
            log.error("插入数据失败"+e);
        }
        return false;
    }

    @Override
    public boolean addIndexData(String indexName, JSONArray datas) {
        BulkRequest request = new BulkRequest();
        for (Object obj:datas){
            JSONObject dataMap = (JSONObject)obj;
            request.add(new IndexRequest().index(indexName).opType("create").source(dataMap,XContentType.JSON).create(true));
        }
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            return true;
        }catch (Exception e){
            log.error("批量插入数据失败"+e);
        }
        return false;
    }

    @Override
    public boolean clearIndexData(String indexName) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, "doc");
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            return true;
        }catch (Exception e){
            log.error("清除数据失败！"+e);
        }
        return false;
    }

    @Override
    public boolean deleteIndexDataById(String indexName, String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, "doc", id);
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            return true;
        }catch (Exception e){
            log.error("清除数据成功！"+e);
        }
        return false;
    }

    @Override
    public List<String> getAllIndexName() {
        List<String> result = new ArrayList<>();
        GetAliasesRequest request = new GetAliasesRequest();
        GetAliasesResponse response = null;
        try {
            response = restHighLevelClient.indices().getAlias(request, RequestOptions.DEFAULT);
        }catch (Exception e){
            log.error("es查询错误，"+e);
        }
        if(null != response){
            Map<String, Set<AliasMetaData>> indexmap = response.getAliases();
            for(String key:indexmap.keySet()){
                result.add(key);
            }
        }
        return result;
    }

    @Override
    public String getMaxIndex(String index) {
        List<String> indexs = getAllIndexName();
        return getMaxIndex(index,indexs);
    }

    @Override
    public List<Map<String, Object>> getFieldsByIndexName(String indexName) {
        List<Map<String,Object>> result = new ArrayList<>();
        GetMappingsRequest getMappings=new GetMappingsRequest().indices(indexName);
        try {
            GetMappingsResponse getMappingResponse = restHighLevelClient.indices().getMapping(getMappings, RequestOptions.DEFAULT);
            Map<String, MappingMetaData> allMappings = getMappingResponse.mappings();
            MappingMetaData indexMapping = allMappings.get(indexName);
            Map<String, Object> mapping = indexMapping.sourceAsMap();
            Map<String,Object> fields = (Map<String,Object>)mapping.get("properties");
            Iterator<Map.Entry<String,Object>> entries=fields.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry<String, Object> entry = entries.next();
                String key = entry.getKey();
                Map<String,String> value = (Map<String, String>)entry.getValue();
                String type = value.get("type");
                if(null == type || "null".equals(type)){
                    continue;
                }
                Map<String,Object> temp = new HashMap<>();
                temp.put("properties",key);
                temp.put("type",type);
                result.add(temp);
            }
        }catch (Exception e){
            log.error("es查询错误"+e);
        }
        return result;
    }

    @Override
    @ExecuteLog
    public JSONObject seachEsData(String indexName, SearchSourceBuilder searchSourceBuilder) {
        JSONObject resultMap = new JSONObject();
        JSONArray result = new JSONArray();
        SearchRequest searchRequest = getSearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return JSONObject.parseObject(searchResp.toString(), Feature.OrderedField);
        } catch (Exception e) {
            log.error("es查询错误，"+e+"\n dslStr:{}",searchSourceBuilder);
        }
        return resultMap;
    }

    @Override
    @ExecuteLog
    public SearchRetDto seachEsData(ESearchParamDto searchParam) {
        SearchRequest searchRequest = getSearchRequest(searchParam.getIndexName());
        searchRequest.source(searchParam.getSearchSource());
        try {
            SearchResponse searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return new SearchRetDto(searchResp);
        } catch (Exception e) {
            log.error("es查询错误，"+e+"\n dslStr:{}",searchParam.getSearchSource());
        }
        return null;
    }

    @Override
    @ExecuteLog
    public JSONObject aggsEsData(String indexName, SearchSourceBuilder searchSourceBuilder) {
        JSONObject result = new JSONObject();
        SearchRequest searchRequest = getSearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (null != searchResp.getAggregations()) {
                result = JSONObject.parseObject(JSONUtil.parse(searchResp.getAggregations().asMap()).toJSONString(0));
            }
        } catch (Exception e) {
            log.error("es查询错误，"+e+"\n dslStr:{}",searchSourceBuilder);
        }
        return result;
    }

    @Override
    @ExecuteLog
    public SearchRetDto aggsEsData(ESearchParamDto searchParam) {
        SearchRequest searchRequest = getSearchRequest(searchParam.getIndexName());
        searchRequest.source(searchParam.getSearchSource());
        try {
            SearchResponse searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (null != searchResp.getAggregations()) {
                return new SearchRetDto(searchResp.getAggregations().asMap());
            }
        } catch (Exception e) {
            log.error("es查询错误，"+e+"\n dslStr:{}",searchParam.getSearchSource());
        }
        return null;
    }

    @Override
    @ExecuteLog
    public JSONObject ExeuceSearchDsl(String indexName, String dslBodyStr) {
        Request req = getRestRequest(indexName, dslBodyStr);
        JSONObject retJson = null;
        try {
             retJson = search(req);
        }catch (Exception e){
            log.error("执行dslStr失败！"+e+" dsl：{}",dslBodyStr);
        }
        return retJson;
    }

    @Override
    public String translateSQL(String sql) {
        try {
            SearchDao searchDao = new SearchDao(transportClient);
            QueryAction queryAction = searchDao.explain(sql);
            return queryAction.explain().explain();
        } catch (Exception e) {
            log.error("sql转换DSL失败！"+e+"sql:{}",sql);
        }
        return null;
    }

    /**
     * 获取search请求对象
     * @param indexName 索引名
     * @return SearchRequest
     */
    private SearchRequest getSearchRequest(String indexName){
        SearchRequest searchRequest = new SearchRequest(indexName);
        return searchRequest;
    }


    /**
     * 通过索引名称获取 request
     * @param indexName 索引名称
     * @param body      请求体
     * @return es 请求
     */
    private Request getRestRequest(String indexName, String body) {
        Request req = new Request("post", "/" + indexName + "/_search");
        req.setJsonEntity(body);
        return req;
    }

    /**
     *  查询 es
     * @param request
     * @return 返回查询JSon
     */
    private JSONObject search(Request request) throws Exception {
        JSONObject retJson = null;
        Response resp = restHighLevelClient.getLowLevelClient().performRequest(request);
        if (resp.getStatusLine().getStatusCode() == 200) {
            retJson = JSONObject.parseObject(EntityUtils.toString(resp.getEntity()));
        }
        return retJson;
    }

    private String getMaxIndex(String indexName,List<String> indexs){
        List<String> list = new ArrayList<>();
        indexName = indexName.replace("*","").replaceAll("-","");
        for(String s:indexs){
            if(s.contains(indexName) && s.contains("-")){
                String[] array = s.split("-");
                list.add(array[array.length-1]);
            }
        }
        if(list.isEmpty()){
            return indexName+"-*";
        }
        return indexName+"-"+ CollectionUtil.max(list);

    }


}
