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

}
