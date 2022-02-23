package com.gcc.airbase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "airbase")
@Data
public class AirBaseProperties {

    private String description;

    private String version = "v1.0";

    private String initDataBaseEnable = "false";

    private String recordDbOrmEnable = "false";

    private String apiLogEnable = "true";

    private String esServerEnable = "false";

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
