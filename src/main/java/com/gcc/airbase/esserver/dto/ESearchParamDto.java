package com.gcc.airbase.esserver.dto;

import lombok.Data;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.Serializable;

/**
 * es 查询参数对象
 * @author GCC
 */
@Data
public class ESearchParamDto implements Serializable {

    private String indexName;

    private SearchSourceBuilder searchSource;

    public ESearchParamDto(){}

    public ESearchParamDto(String indexName,SearchSourceBuilder searchSourceBuilder){
        this.indexName = indexName;
        this.searchSource = searchSourceBuilder;
    }

    static class Builder{

        private ESearchParamDto eSearchParamDto = new ESearchParamDto();

        public Builder builderIndex(String indexName){
            eSearchParamDto.setIndexName(indexName);
            return this;
        }

        public Builder builderSearchSource(SearchSourceBuilder searchSourceBuilder){
            eSearchParamDto.setSearchSource(searchSourceBuilder);
            return this;
        }

        public ESearchParamDto builder(){
            return this.eSearchParamDto;
        }

    }

}
