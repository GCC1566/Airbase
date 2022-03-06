package com.gcc.airbase.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "airbase")
@Component
public class AirBaseProperties {

    private String description;

    private String version = "v1.0";

    private String initDataBaseEnable;

    private String recordDbOrmEnable;

    private String apiLogEnable;

    private String esServerEnable;

    private String esServerLogNotes = "true";

    public Boolean getInitDataBaseEnable() {
        if(null == initDataBaseEnable){
            return false;
        }
        return Boolean.valueOf(initDataBaseEnable);
    }

    public Boolean getEsServerEnable() {
        if(null == esServerEnable){
            return false;
        }
        return Boolean.valueOf(esServerEnable);
    }

    public Boolean getRecordDbOrmEnable() {
        if(null == recordDbOrmEnable){
            return false;
        }
        return Boolean.valueOf(recordDbOrmEnable);
    }

    public Boolean getApiLogEnable() {
        if(null == apiLogEnable){
            return true;
        }
        return Boolean.valueOf(apiLogEnable);
    }

    public Boolean getEsServerLogNotes() {
        return Boolean.valueOf(esServerLogNotes);
    }

    public void setEsServerLogNotes(String esServerLogNotes) {
        this.esServerLogNotes = esServerLogNotes;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setInitDataBaseEnable(String initDataBaseEnable) {
        this.initDataBaseEnable = initDataBaseEnable;
    }

    public void setRecordDbOrmEnable(String recordDbOrmEnable) {
        this.recordDbOrmEnable = recordDbOrmEnable;
    }

    public void setApiLogEnable(String apiLogEnable) {
        this.apiLogEnable = apiLogEnable;
    }

    public void setEsServerEnable(String esServerEnable) {
        this.esServerEnable = esServerEnable;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }
}
