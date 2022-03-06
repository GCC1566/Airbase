package com.gcc.airbase.esserver.log.entity;

import cn.hutool.core.date.DateUtil;


import java.text.DecimalFormat;

public class EsExecuteLog {

    private final static String ITEM_STR = "--------------------------";

    private String FORMAT_STR = "  INFO  [        task-x] ElasticSearch Execute Notes                  : ";

    private String methodName;

    private String indexName;

    private String queryDsl;

    private String timeSpan;

    public String getLogInfo(){
        String handStr = "\n"+ DateUtil.now() +FORMAT_STR;
        String tempStr = ITEM_STR + "[method] " + this.methodName + ITEM_STR;
        StringBuilder retStr = new StringBuilder("\n");
        retStr.append(handStr).append(tempStr)
              .append(handStr).append("param-index : "+this.indexName)
              .append(handStr).append("param-dsl   : "+this.queryDsl)
              .append(handStr).append("method-span : "+this.timeSpan)
              .append(handStr).append(tempStr);
        return retStr.toString();
    }

    public void setTimeSpan(Long timeSpan) {
        Double b = Double.parseDouble(""+timeSpan);
        Double span = b/1000;
        this.timeSpan = new DecimalFormat("#,##0.000").format(span) +"s";
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setQueryDsl(String queryDsl) {
        this.queryDsl = queryDsl;
    }

}
