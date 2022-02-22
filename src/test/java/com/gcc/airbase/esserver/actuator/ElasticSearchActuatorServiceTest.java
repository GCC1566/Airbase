package com.gcc.airbase.esserver.actuator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gcc.airbase.StartApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class ElasticSearchActuatorServiceTest extends StartApplicationTest {

    @Autowired
    ElasticSearchActuator elasticSearchActuator;

    @Test
    void translateSQL() {
        String sql = "select name,Max(age),sex from tb_user-* where name like '%张三%' AND age > 1 group by age order by time DESC";

        String dsl = elasticSearchActuator.translateSQL(sql);

        log.info(dsl);
    }


    @Test
    void createIndex(){

        String index = "test";

        String mapping = "{\n" +
                "    \"mappings\": {\n" +
                "            \"properties\": {\n" +
                "                \"id\": {\n" +
                "                \t\"type\": \"long\",\n" +
                "                    \"store\": true\n" +
                "                },\n" +
                "                \"title\": {\n" +
                "                \t\"type\": \"text\",\n" +
                "                    \"store\": true,\n" +
                "                    \"analyzer\":\"standard\"\n" +
                "                },\n" +
                "                \"content\": {\n" +
                "                \t\"type\": \"text\",\n" +
                "                    \"store\": true,\n" +
                "                    \"analyzer\":\"standard\"\n" +
                "                }\n" +
                "            }\n" +
                "    }\n" +
                "}\n";
        log.info("mapping:\n{}",mapping);
        elasticSearchActuator.createIndex(index, JSONObject.parseObject(mapping));

    }

    @Test
    void addData(){

        JSONObject data = JSONObject.parseObject("{\n" +
                "    \"id\":1,\n" +
                "    \"title\":\"第一篇测试文章\",\n" +
                "    \"content\":\"我是你爸爸，嘿嘿\"\n" +
                "}");

        elasticSearchActuator.addIndexData("test",data);
        log.info("成功插入一条数据：我是你爸爸");

        JSONArray jsonArray = JSONArray.parseArray("[\n" +
                "{\n" +
                "    \"id\":2,\n" +
                "    \"title\":\"第2篇测试文章\",\n" +
                "    \"content\":\"我是你爸爸，嘿嘿\"\n" +
                "},\n" +
                "{\n" +
                "    \"id\":3,\n" +
                "    \"title\":\"第3篇测试文章\",\n" +
                "    \"content\":\"我是你老子，嘿嘿\"\n" +
                "}\n" +
                "]");
        elasticSearchActuator.addIndexData("test",jsonArray);
        log.info("成功插入两条数据");


    }


}