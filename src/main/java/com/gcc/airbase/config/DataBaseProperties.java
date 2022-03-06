package com.gcc.airbase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DataBaseProperties {

    private String url;

    private String dbType;

    private String host;

    private String dbPort;

    private String dbName;

    private String driverClassName;

    private String user;

    private String password;

    private String dbConfigFile;

    public String getUrl(){
        if(url == null || "".equals(url)){
            return "jdbc:mysql://"+this.host+":"+this.dbPort+"/"+this.dbName+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&serverTimezone=GMT%2B8";
        }
        return url;
    }
}
