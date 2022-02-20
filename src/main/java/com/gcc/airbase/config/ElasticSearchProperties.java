package com.gcc.airbase.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.elasticsearch")
@Data
public class ElasticSearchProperties {

    private String host;


    private String port;


    private String tcpPort;


    private String user = "";


    private String password = "";


    private String httpScheme;

    public Integer getPort(){
        return Integer.valueOf(port);
    }

    public Integer getTcpPort() {
        if(!StrUtil.isBlankIfStr(tcpPort)) {
            return Integer.valueOf(tcpPort);
        }else {
            return getPort();
        }
    }
}
