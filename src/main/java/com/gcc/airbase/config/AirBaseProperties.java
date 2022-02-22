package com.gcc.airbase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "airbase")
@Data
public class AirBaseProperties {

    private String description;

    private String version;

    private String initDataBaseEnable;

    private String recordDbOrmEnable;

    private String apiLogEnable;

    private String esServerEnable;

    public Boolean getInitDataBaseEnable() {
        return Boolean.valueOf(initDataBaseEnable);
    }

    public Boolean getEsServerEnable() {
        return Boolean.valueOf(esServerEnable);
    }

    public Boolean getRecordDbOrmEnable() {
        return Boolean.valueOf(recordDbOrmEnable);
    }

    public Boolean getApiLogEnable() {
        return Boolean.valueOf(apiLogEnable);
    }
}
