package com.gcc.airbase.dbserver;

import com.gcc.airbase.config.DataBaseProperties;
import com.gcc.conscript.DataBaseInitorFactory;
import com.gcc.conscript.InitDataBase;
import com.gcc.conscript.entity.DbConConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 加载数据库
 * @author GCC
 */
@Configuration
@Slf4j
public class LoadDataBase {

    @Autowired
    private DataBaseProperties dbInfo;

    @ConditionalOnProperty(name="airbase.initdatabase-enable", havingValue="true")
    @Bean("isInitEd")
    public Boolean initDataBase(){
        DbConConfiguration dbconfig = new DbConConfiguration.Builder()
                .setConfigFileUrl(dbInfo.getDbConfigFile())
                .setUrl(dbInfo.getUrl())
                .setDbname(dbInfo.getDbName())
                .setUser(dbInfo.getUser())
                .setPassword(dbInfo.getPassword())
                .setDbtype(dbInfo.getDbType())
                .setDriverclassname(dbInfo.getDriverClassName())
                .setHost(dbInfo.getHost())
                .setDbPort(dbInfo.getDbPort())
                .build();
        InitDataBase initdb = DataBaseInitorFactory.createInitiator(dbconfig);
        try{
            initdb.startCoreJob();
            return initdb.isInitEd();
        }catch (Exception e){
            log.error("the database init failure！Please check the configuration file `spring.datasource` value  OR sql file !"+e);
        }
        return false;
    }

}
