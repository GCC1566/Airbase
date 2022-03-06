package com.gcc.airbase.esserver.dto;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import java.io.Serializable;
@Data
public class SearchRetDto implements Serializable {

    private HitsRetDto hits;

    private JSONObject aggregations;

    public SearchRetDto(){
    }

    public SearchRetDto(Object obj){
        aggregations = JSONObject.parseObject(JSONUtil.parseObj(obj).toJSONString(0));
    }

    public SearchRetDto(SearchResponse searchResponse){
        SearchHit[] searchHitArr = searchResponse.getHits().getHits();
        HitsRetDto hits = new HitsRetDto();
        hits.setTotal(searchResponse.getHits().getTotalHits().value);
        for (SearchHit searchHit:searchHitArr){
            hits.addHits(searchHit.getSourceAsMap());
        }
    }

    public void addHitsRetDto(SearchRetDto searchRetDto){
        hits.addHits(searchRetDto.getHits().getHits());
    }

    public void addHitsRetDto(HitsRetDto hits){
        hits.addHits(hits.getHits());
    }

    public void addHitsRetObject(Object sourceObject){
        hits.addHits(sourceObject);
    }
}
