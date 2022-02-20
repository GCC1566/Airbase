package com.gcc.airbase.esserver.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * es查询结果返回体
 * @author GCC
 */
@Data
public class HitsRetDto implements Serializable {

    private long total = 0l;

    private List<Object> hits = new ArrayList<>();


    public void addHits(Object jsonObject){
        hits.add(jsonObject);
    }

    public void addHits(List<Object> jsonObject){
        hits.addAll(jsonObject);
    }

}
