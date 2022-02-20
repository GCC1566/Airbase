package com.gcc.airbase.esserver.actuator;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.gcc.airbase.esserver.dto.ESearchParamDto;
import com.gcc.airbase.esserver.dto.SearchRetDto;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;
import java.util.Map;

/**
 * es执行器
 * @author GCC
 */
public interface ElasticSearchActuator {


    /**
     * 判断索引是否存在
     * @param indexName 索引名称
     * @return boolean
     */
    boolean checkIndex(String indexName);

    /**
     * 创建索引
     * @param indexName 索引名称
     * @param mapping 创建索引的mapping
     * @return boolean
     */
    boolean createIndex(String indexName, JSONObject mapping);

    /**
     * 删除索引
     * @param indexName 所有名称
     * @return boolean
     */
    boolean deleteIndex(String indexName);


    /**
     * 向指定索引内追加单条数据
     * @param indexName 索引名称
     * @param data 数据
     * @return boolean
     */
    boolean addIndexData(String indexName,JSONObject data);

    /**
     * 向指定索引中批量追加数据
     * @param indexName 索引名称
     * @param datas 数据
     * @return boolean
     */
    boolean addIndexData(String indexName, JSONArray datas);


    /**
     * 删除指定索引内的所有数据
     * @param indexName 索引名称
     * @return boolean
     */
    boolean clearIndexData(String indexName);

    /**
     * 根据id删除指定索引内的数据
     * @param indexName 索引名称
     * @param id 待删除数据的id
     * @return boolean
     */
    boolean deleteIndexDataById(String indexName,String id);

    /**
     * 获取所有索引名称
     * @return List<String>
     */
    List<String> getAllIndexName();

    /**
     * 获取最高可用索引
     * @param index 索引名称
     * @return String
     */
    String getMaxIndex(String index);

    /**
     * 根据索引名称获取对应的字段集合
     * @param indexName 索引名称
     * @return List<String>
     */
    List<Map<String,Object>> getFieldsByIndexName(String indexName);


    /**
     * 查询es
     * @param indexName 索引名称
     * @param searchSourceBuilder 自定义查询内容
     * @return JSONArray
     */
    JSONObject seachEsData(String indexName, SearchSourceBuilder searchSourceBuilder);


    /**
     * 查询es
     * @param searchParam es查询条件
     * @return SearchRetDto
     */
    SearchRetDto seachEsData(ESearchParamDto searchParam);


    /**
     * 聚合es数据
     * @param indexName 索引名称
     * @param searchSourceBuilder 自定义查询内容
     * @return
     */
    JSONObject aggsEsData(String indexName,SearchSourceBuilder searchSourceBuilder);

    /**
     * 聚合es数据
     * @param searchParam es查询条件
     * @return SearchRetDto
     */
    SearchRetDto aggsEsData(ESearchParamDto searchParam);


    /**
     * 执行查询dsl
     * @param indexName 索引名称
     * @param dslBodyStr dsl
     * @return JSONObject
     */
    JSONObject ExeuceSearchDsl(String indexName,String dslBodyStr);


    /**
     * 翻译sql语句为DSL
     * @param sql sql语句
     * @return String
     */
    String translateSQL(String sql);


}
