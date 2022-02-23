package com.gcc.airbase.dbserver;

import com.alibaba.druid.wall.WallFilter;
import com.gcc.airbase.config.DataBaseProperties;
import com.gcc.airbase.config.model.OrmCondition;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 加载ORM组件
 * @author GCC
 */
@Configuration
@Slf4j
public class LoadRecordOrm {

    @Autowired
    private DataBaseProperties dbInfo;

    @Conditional(OrmCondition.class)
    @Bean
    public ActiveRecordPlugin initActiveRecordPlugin(){
        DruidPlugin druidPlugin = new DruidPlugin(dbInfo.getUrl(), dbInfo.getUser(), dbInfo.getPassword()).setRemoveAbandoned(true);
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType(dbInfo.getDbType());
        druidPlugin.setMaxActive(10);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dbInfo.getDbName(), druidPlugin);
        try {
            druidPlugin.start();
            arp.start();
            log.info(" ActiveRecordPlugin init successed！");
        } catch (Exception e) {
            log.error("The database component failed to load. If there are no other orm components, the system cannot be used" + e);
            druidPlugin.stop();
            arp.stop();
            arp = null;
        }
        return arp;
    }

}
