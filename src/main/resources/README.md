# Airbase使用说明

> ​	Airbase是一组为快速开发SpringBoot Web项目而产的SDK程序，其中包含 “初始数据库”、“recordDbORM组件”、“elasticsearch执行器”、“Kafka消息使用 ” 等web开发常用底层包，可直接导入SpringBoot项目中，开箱即用。

## 1、依赖组件说明

​	该SDK使用SpringBoot、Es、Kafka、Mysql、activerecord、huitool、Conscript等组件开发，如有需要，可自行修改相应版本进行使用

| 依赖组件            | 版本          |
| ------------------- | ------------- |
| SpringBoot          | 2.3.6.RELEASE |
| ElasticSearchClient | 7.6.2         |
| mysql               | 8.0.23        |
| activerecord        | 4.9.16        |
| druid               | 1.1.12        |
| hutool              | 5.7.8         |

## 2、引入Airbase

**方式一：**

若本地有Airbase环境（Airbase在本地的maven中发布），可直接在pom文件中引入

```xml
<dependency>
    <groupId>com.gcc</groupId>
    <artifactId>airbase</artifactId>
    <version>1.0</version>
</dependency>
```

**方式二：**

若本地maven无发布版本，可直接将Airbase的jar包引入到项目中即可

## 3、功能及使用说明

### 3.1 功能选择

> Airbase 内置功能包括 “初始化数据库”、“自带ORM框架”、“接口请求记录器”、“Es执行器组件”功能，使用时可根据需要，启用相关功能，操作方式仅为配置yml文件

若想选用Airbase中的部分功能，可在yml文件中追加 airbase的以下配置项：

```yml
airbase:
  initdatabase-enable: true #是否启用数据库初始化功能，默认状态为禁用
  recorddborm-enable: true #是否启用record Db组件（ORM），默认状态为禁用
  apilog-enable: true     #是否启用接口请求记录器，默认状态为启用 
  esserver-enable: false #es执行器组件，启用状态默认是禁用
```

注：**若不进行此配置，则Airbase仅 接口请求记录器功能 可用**

### 3.2  初始化数据库

> 该SDK自带Conscript初始化数据库功能，目前仅支持Mysql数据库，可通过增量SQL文件，自动进行数据库的创建、升级

1.使用时，yml配置文件中必须包含以下配置信息：

```yml
spring:
  datasource:
    url: #数据库连接url eg: jdbc:mysql://X.X.X:MMMM/数据库名……
    user: #数据库用户名
    password: #数据库密码
    dbtype: #数据库类型 eg:mysql
    dbname: #数据库名称
    dbport: #数据库服务端口
    host: #数据库服务地址 eg:127.0.0.1
    dbconfigfile: #版本控制文件，建议使用 mysql-dbconfig.json
```

2.在resource下新建目录sql，在sql目录中放置系统需要的sql文件

3.需要在resource目录下，新建 ***mysql-dbconfig.json*** 文件（若配置 yml 配置文件中的 dbconfigfile 配置项的文件名为自定义，则创建自定义的文件），文件样例如下：

```json
[
   {
      "version": "1.0",  //数据库版本号
      "sqlfile": "XXX_db.sql", //增量的sql文件,文件名与sql目录下的保持一致
      "desc": "数据模拟工具箱数据库"  //此次升级的说明
   },
    {
       "version": "1.1",  
      "sqlfile": "XXXX_db2.sql", 
      "desc": "版本升级增量sql文件"  
    }
 ]
```

4.若程序中需要知道数据库是否初始化成功，可注入名字为isInitEd的bean，进行判断，eg:

```java
public class AfterStartProcess implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Qualifier("isInitEd")
    @Autowired
    Boolean dbflag;

    @Override
    public void run(ApplicationArguments args) throws Exception {
      if (dbflag){
            log.info("系统数据库初始化成功.");
        }else {
            log.error("系统数据库不可用");
            SpringApplication.exit(applicationContext);
        }
    }

}
```

### 3.3  自动携带ORM组件

> 使用Airbase，可以不用引入jdbc的orm组件，Airbase本身内嵌一版ORM组件，在初始化数据库完成后会自动加载

该组件yml配置文件中必须包含以下配置信息：

```yml
spring:
  datasource:
    url: #数据库连接url eg: jdbc:mysql://.X.X:MMMM/数据库名称……
    user: #数据库用户名
    password: #数据库密码
    dbtype: #数据库类型 eg:mysql
    dbname: #数据库名称
    dbport: #数据库服务端口
    host: #数据库服务地址 eg:127.0.0.1
```

使用ORM组件Demo：

初始化数据库后，可在程序的任意地方使用SQL查询，结果返回Record对象（类似JSON）

```java

public void testFrom(){
    List<Record> retList = Db.find("select name from test where age > ?",13);
    for(Record record:retList){
        if(record.getStr("name").equals("张三")){
            System.out.println("发现了年龄为13的张三");
        }
    }
}
```

Db内置了完整的CRUD方法、分页方法等常规数据库操作，可满足常规的数据库操作ORM需求。

### 3.4  接口请求记录器功能

在yml配置文件中将配置打开后，web项目可自行记录每条接口的请求情况，并在日志、控制台输出；

输出日志样例：

```shell
EagleEye-Listener : =======================【Request Start】=======================
EagleEye-Listener : request-method :POST
EagleEye-Listener : request-host   :127.0.0.1
EagleEye-Listener : request-uri    :/Emulator/utils/timeutil/stamptodatestr
EagleEye-Listener : request-param  :{  "formatStr": "yyyy-MM-dd",  "stamp": }
EagleEye-Listener : =======================【Request   End】=======================
```

内置RequestRecord注解，可注解于Controller层的方法中，每次请求该接口，日志将记录其中内容

| 属性     | 数据类型 | 作用                     |
| -------- | -------- | ------------------------ |
| item     | String   | 打印日志的起始区分符     |
| timeSpan | Boolean  | 是否开启接口相应时间计算 |



```java
    @GetMapping("/savexxxxx")
    @RequestRecord(item = "*",timespan =false)
    public ResponseData savePage(String name){
        …………
    }
```



### 3.5  自带Es执行器组件

> Airbase里自带了es的执行器，可直接执行es的查询dsl，同时，该版本es执行器增加了sql转换dsl的功能

引入jar后，可直接在yml配置文件中追加以下部分即可使用：

```yml
spring:
  elasticsearch:
    host: 127.0.0.1   #es服务地址（必填）
    port: 9500        #es http服务端口（必填）
    tcpport: 9300     #es  集群的通信端口（非必填，考虑性能建议添加）
    user: elastic     #es 登录账号名（非必填）
    password: 123ABCdef* #es 登录密码（非必填）
    scheme: http        #es 的请求方式（非必填）
```

使用es执行器组件demo：

 在任意bean中，注入ElasticSearchActuator即可，例：

```java
@Service
public class ElasticServerCompent {

    @Autowired
    ElasticSearchActuator elasticSearchActuator;

    public String transferSql(String sql){
       String dsl =  elasticSearchActuator.translateSQL(sql);
       log.info("sql语句="+sql+",转换为es查询DSL为："+dsl);
       return dsl;
    }
    
    public JSONObject searchData(){
        SearchSourceBuilder querybody = new SearchSourceBuilder()
                .size(10)
                .from(1)
                .query(QueryBuilders.boolQuery().must(
                        QueryBuilders.termQuery("name","李梅")
                ));
       JSONObject json = elasticSearchActuator.seachEsData("index_name",querybody);
       return json;
    }
}
```

### 3.6 完整yml模板

```yml
airbase:
  initdatabase-enable: true #数据库初始化功能
  recorddborm-enable: true  #自带ORM组件
  apilog-enable: true     #是否启用接口请求记录器
  esserver-enable: false    #es执行器组件
spring:
  datasource:
    url: #数据库连接url eg: jdbc:mysql://X.X.X:MMMM/数据库名……
    user: #数据库用户名
    password: #数据库密码
    dbtype: #数据库类型 eg:mysql
    dbname: #数据库名称
    dbport: #数据库服务端口
    host: #数据库服务地址 eg:127.0.0.1
    dbconfigfile: #版本控制文件，建议使用 mysql-dbconfig.json
elasticsearch: #(若airbase.esserver-enable=false，可忽略此配置)
    host: 127.0.0.1   #es服务地址（必填）
    port: 9500        #es http服务端口（必填）
    tcpport: 9300     #es集群的通信端口（非必填，考虑性能建议添加）
    user: elastic     #es 登录账号名（非必填）
    password: 123ABCdef* #es 登录密码（非必填）
    scheme: http        #es 的请求方式（非必填）
```

