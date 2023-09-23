# SpringBoot 项目初始模板

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。


[toc]

## 模板特点

### 主流框架 & 特性

- Spring Boot 2.7.x（贼新）
- Spring MVC
- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring Boot 调试工具和项目处理器
- Spring AOP 切面编程
- Spring Scheduler 定时任务
- Spring 事务注解

### 数据存储

- MySQL 数据库
- Redis 内存数据库
- Elasticsearch 搜索引擎
- 腾讯云 COS 对象存储

### 工具类

- Easy Excel 表格处理
- Hutool 工具库
- Gson 解析库
- Apache Commons Lang3 工具类
- Lombok 注解

### 业务特性

- Spring Session Redis 分布式登录
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- Swagger + Knife4j 接口文档
- 自定义权限注解 + 全局校验
- 全局跨域处理
- 长整数丢失精度解决
- 多环境配置


## 业务功能

- 提供示例 SQL（用户、帖子、帖子点赞、帖子收藏表）
- 用户登录、注册、注销、更新、检索、权限管理
- 帖子创建、删除、编辑、更新、数据库检索、ES 灵活检索
- 帖子点赞、取消点赞
- 帖子收藏、取消收藏、检索已收藏帖子
- 帖子全量同步 ES、增量同步 ES 定时任务
- 支持微信开放平台登录
- 支持微信公众号订阅、收发消息、设置菜单
- 支持分业务的文件上传

### 单元测试

- JUnit5 单元测试
- 示例单元测试类

### 架构设计

- 合理分层


## 快速上手

> 所有需要修改的地方鱼皮都标记了 `todo`，便于大家找到修改的位置~

### MySQL 数据库

1）修改 `application.yml` 的数据库配置为你自己的：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db
    username: root
    password: 123456
```

2）执行 `sql/create_table.sql` 中的数据库语句，自动创建库表

3）启动项目，访问 `http://localhost:8101/api/doc.html` 即可打开接口文档，不需要写前端就能在线调试接口了~

![](doc/swagger.png)

### Redis 分布式登录

1）修改 `application.yml` 的 Redis 配置为你自己的：

```yml
spring:
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
    password: 123456
```

2）修改 `application.yml` 中的 session 存储方式：

```yml
spring:
  session:
    store-type: redis
```

3）移除 `MainApplication` 类开头 `@SpringBootApplication` 注解内的 exclude 参数：

修改前：

```java
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

修改后：


```java
@SpringBootApplication
```

### Elasticsearch 搜索引擎

1）修改 `application.yml` 的 Elasticsearch 配置为你自己的：

```yml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456
```

2）复制 `sql/post_es_mapping.json` 文件中的内容，通过调用 Elasticsearch 的接口或者 Kibana Dev Tools 来创建索引（相当于数据库建表）

```
PUT post_v1
{
 参数见 sql/post_es_mapping.json 文件
}
```

这步不会操作的话需要补充下 Elasticsearch 的知识，或者自行百度一下~

3）开启同步任务，将数据库的帖子同步到 Elasticsearch

找到 job 目录下的 `FullSyncPostToEs` 和 `IncSyncPostToEs` 文件，取消掉 `@Component` 注解的注释，再次执行程序即可触发同步：

```java
// todo 取消注释开启任务
//@Component
```


# 聚合搜索平台项目文档

## 项目介绍

一个企业级的聚合搜索平台（简化版的搜索中台）

项目意义：

用户角度：允许用户在同一个页面集中搜索出不同来源，不同类型的内容，提升用户的检索效率和搜索体验。

企业角度：当企业有多个项目的数据需要被搜索时，无需针对每个项目单独开发搜索功能，可以直接将数据接入搜索中台，提高开发效率。



## 技术栈介绍

### 前端

- Vue
- Ant Design Vue



### 后端

- Spring Boot
- MySQL
- Elasticsearch（Elastic Stack）搜索引擎
- 数据抓取
  - 离线
  - 实时
- 数据同步（4种同步方式）
  - 定时
  - 双写
  - Logstash
  - Canal
- JMeter压力测试



## 业务流程

1. 先得到各种不同分类的数据
2. 提供一个搜索页面（单一搜索+聚合搜索），支持搜索



项目架构图：

![image-20230923134804436](C:\Users\LiuMingyao\AppData\Roaming\Typora\typora-user-images\image-20230923134804436.png)



## 项目实现

### 获取不同类型的数据源

#### 数据抓取流程

1. 分析数据源，怎么获取？
2. 拿到数据后，怎么处理？
3. 写入数据库等存储



#### 数据抓取的几种方式

1. 直接请求数据接口（最方便），可使用HttpClient，OKHttp，RestTemplate，Hutool等客户端发送请求
2. 等网页渲染出明文内容后，从前端完整页面中解析出需要的内容
3. 有一些网站可能是动态请求的，他不会一次性加载所有的数据，而是需要点击某个按钮，输入某个验证码才会显示出数据。可使用无头浏览器：selenium, node.js puppeteer



#### 1.获取文章数据

内部没有，可以从互联网上获取基础数据 =》 爬虫

```java
package com.yupi.springbootinit.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取初始帖子列表
 */
// 取消注释后，每次启动springboot项目时会执行一次run方法
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;


    @Override
    public void run(String... args) {
        // 1.获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest.post(url)
                .body(json)
                .execute().body();
        // 2.json转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            postList.add(post);
        }

        // 3.数据入库
        boolean b = postService.saveBatch(postList);
        if (b) {
            log.info("获取初始化帖子列表成功，条数 = {}", postList.size());
        }else {
            log.info("获取初始化帖子失败");
        }
    }
}

```



#### 2.用户获取

每个网站用户都是自己的，一般无需从站外获取。



#### 3.图片获取

实时获取：我们自己的数据库不存在这些数据，用户要搜的时候，直接从别人的接口（网站/数据库）去搜。

jsoup解析库：支持发送请求获取到HTML文档，然后从中解析出需要的字段。

~~~java
 @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize;
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s",searchText, current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（murl）
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
//            System.out.println(murl);
            // 取标签
            String title = element.select(".inflnk").get(0).attr("aria-label");
//            System.out.println(m);
//            System.out.println(title);
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            pictureList.add(picture);
            if (pictureList.size() > pageSize){
                break;
            }
        }
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        picturePage.setRecords(pictureList);
        return picturePage;
    }
~~~





### 现有业务场景分析

目前是在页面加载时，调用三个接口分别获取文章，图片，用户数据。

几种不同的业务场景：

1. 其实可以用户点击某个tab的时候，只调用这个tab的接口
2. 如果是针对聚合内容的网页，其实可以一个请求搞定
3. 有可能的还要查询其他的信息，比如其他数据的总数，同时给用户反馈，比如B站搜索页



根据实际情况选择方式：

目前设计存在的问题：

1. 请求数量比较多，可能会收到浏览器的限制
2. 请求不同接口的参数可能不一致，增加前后端沟通成本
3. 前端写调用多个接口的代码，重复代码





### 聚合接口

1. 请求数量较多，可能会收到浏览器的限制 =》用一个接口请求完所有的数据（后端可以并发，几乎没有并发数量的限制）

   ~~~java
   {
   user =  userService.query
   post = postService.query
   picture = pictureService.query
   return user + post + picture
   }
   ~~~

2. 请求不同接口的参数可能不一致，增加前后端沟通成本 =》 用一个接口把请求参数统一，前端每次传固定的参数，后端去对参数进行转换。

   统一返回结果：比如都使用Page页面封装

   ~~~java
   {
    前端统一传 searchText
    后端把 searchText 转换为 userName => queryUser
   }
   ~~~

3. 前端写调用多个接口的代码，重复代码 =》用一个接口，通过不同的参数去区分查询的数据源

   ~~~java
   {
   前端传 type 调用后端同一个接口，后端根据 type 调用不同的 service 查询
   比如：type =  user，userService.query
   }
   ~~~

   



#### 搜索接口优化（运用3种设计模式）

问题：怎么才能让前端一次搜出所有数据，又能够分别获取某一类数据

解决方法：

新增type字段：前端传type调用后端同一个接口，后端根据type调用不同的service查询

比如前端传递type=user，后端执行userService.query



逻辑：

1. 如果type为空，那么搜出所有数据
2. 如果type不为空
   - 如果type合法，查出对应的数据
   - 不合法，报错

问题：

- type增多后，要把查询逻辑堆积在controller处理吗？
- 怎么才能让搜索系统更轻松地接入数据源？



#### 门面模式

帮助用户（客户端）去更轻松地实现功能，不需要关心门面背后的细节。

聚合搜索业务基本都是门面模式：即前端不需要关心后端从哪里来，怎么去取不同来源，怎么去聚合不同来源的数据，更方便的获取到内容。

~~~java
/**
 * 聚合搜索接口
 *
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }
}

~~~



#### 适配器模式

1. 制定统一的数据源接入规范
   - 什么数据允许接入？
   - 你的数据源接入需要满足什么要求？
   - 需要接入方注意什么事情？

本系统要球：任何接入我们系统的数据，它必须要能够根据关键词搜索，并且支持分页搜索。

通过声明接口的方式来定义规范。

~~~java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源接入服务
 *
 * @param <T>
 */
public interface DataSource<T> {

    Page<T> doSearch (String searchText, long pageNum, long pageSize);
}

~~~

2. 假如说我们的数据源已经支持了搜索，但是原有的方法参数和我们的规范不一致，怎么办？

   使用适配器模式：通过转换，让两个系统能够完成对接





#### 注册器模式（本质也是单例）

提前通过一个 map 或者其他类型存储好后面需要调用的对象。
效果：替代了 if... else...，代码量大幅度减少，可维护可扩展。

~~~java
package com.yupi.springbootinit.datasource;

import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSourceRegistry {

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    private  Map<String, DataSource<T>> typeDataSourceMap;

    @PostConstruct
    public void doInit(){
        typeDataSourceMap = new HashMap() {{
            put(SearchTypeEnum.POST.getValue(), postDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
            put(SearchTypeEnum.USER.getValue(), userDataSource);
        }};
    }

    public DataSource getDataSource(String type){
        if (typeDataSourceMap == null) {
            return null;
        }
        return typeDataSourceMap.get(type);
    }

}

~~~





### 搜索优化

问题：搜索不够灵活，需要分词搜索



### Elastic Stack

官网：https://www.elastic.co/cn/

包含了数据的整合 => 提取 => 存储 => 使用，一整套！

各组件介绍：

- beats 套件：从各种不同类型的文件 / 应用中采集数据。比如：a,b,c,d,e,aa,bb,cc
- Logstash：从多个采集器或数据源来抽取 / 转换数据，向 es 输送。比如：a,bb,cc
- elasticsearch：存储、查询数据
- kibana：可视化 es 的数据



注意安装ES，只要是一套技术，所有版本必须一致





#### ES调用方式

1. HTTP Restful调用
2. kibana操作（dev tools）
3. 客户端操作（Java）



Java 操作 ES
3 种方式：
1）ES 官方的 Java API
https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/7.17/introduction.html
快速开始：https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/7.17/connecting.html

2）ES 以前的官方 Java API，HighLevelRestClient（已废弃，不建议用）

3）Spring Data Elasticsearch（推荐）

spring-data 系列：spring 提供的操作数据的框架
spring-data-redis：操作 redis 的一套方法
spring-data-mongodb：操作 mongodb 的一套方法
spring-data-elasticsearch：操作 elasticsearch 的一套方法

官方文档：https://docs.spring.io/spring-data/elasticsearch/docs/4.4.10/reference/html/

自定义方法：用户可以指定接口的方法名称，框架帮你自动生成查询



#### 用ES实现搜索接口 

 1、建表（建立索引） 

数据库表结构：

~~~sql
-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;
~~~

ES Mapping：

id（可以不放到字段设置里）

ES 中，尽量存放需要用户筛选（搜索）的数据



aliases：别名（为了后续方便数据迁移）

字段类型是 text，这个字段是可被分词的、可模糊查询的；而如果是 keyword，只能完全匹配、精确查询。

analyzer（存储时生效的分词器）：用 ik_max_word，拆的更碎、索引更多，更有可能被搜出来

search_analyzer（查询时生效的分词器）：用 ik_smart，更偏向于用户想搜的分词

如果想要让 text 类型的分词字段也支持精确查询，可以创建 keyword 类型的子字段：

~~~json
  "fields": {
    "keyword": {
      "type": "keyword",
      "ignore_above": 256 // 超过字符数则忽略查询
    }
  }
~~~

建表结构：

~~~json
POST post_v1
{
  "aliases": {
    "post": {}
  },
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "content": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "tags": {
        "type": "keyword"
      },
      "userId": {
        "type": "keyword"
      },
      "createTime": {
        "type": "date"
      },
      "updateTime": {
        "type": "date"
      },
      "isDelete": {
        "type": "keyword"
      }
    }
  }
}
~~~

 2、增删改查 

第一种方式：ElasticsearchRepository<PostEsDTO, Long>，默认提供了简单的增删改查，多用于可预期的、相对没那么复杂的查询、自定义查询，返回结果相对简单直接。

接口代码：

~~~java
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    <S extends T> S save(S entity);

    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    Iterable<T> findAll();

    Iterable<T> findAllById(Iterable<ID> ids);

    long count();

    void deleteById(ID id);

    void delete(T entity);

    void deleteAllById(Iterable<? extends ID> ids);

    void deleteAll(Iterable<? extends T> entities);

    void deleteAll();
}
~~~



ES 中，_开头的字段表示系统默认字段，比如 _id，如果系统不指定，会自动生成。但是不会在 _source 字段中补充 id 的值，所以建议大家手动指定。



支持根据方法名自动生成方法，比如：

~~~java
List<PostEsDTO> findByTitle(String title);
~~~



第二种方式：Spring 默认给我们提供的操作 es 的客户端对象 ElasticsearchRestTemplate，也提供了增删改查，它的增删改查更灵活，适用于更复杂的操作，返回结果更完整，但需要自己解析。

对于复杂的查询，建议用第二种方式。

三个步骤：

1. 取参数

2. 把参数组合为 ES 支持的搜索条件

3. 从返回值中取结果



 3、查询 DSL 

参考文档：

- https://www.elastic.co/guide/en/elasticsearch/reference/7.17/query-filter-context.html

- https://www.elastic.co/guide/en/elasticsearch/reference/7.17/query-dsl-bool-query.html

示例代码：

~~~json
GET post/_search
{
  "query": { 
    "bool": { // 组合条件
      "must": [ // 必须都满足
        { "match": { "title":   "鱼皮"        }}, // match 模糊查询
        { "match": { "content":   "知识星球"        }}
      ],
      "filter": [ 
        { "term":  { "status": "published" }}, // term 精确查询
        { "range": { "publish_date": { "gte": "2015-01-01" }}} // range 范围查询
      ]
    }
  }
}
~~~



wildcard 模糊查询

regexp 正则匹配查询



查询结果中，score 代表匹配分数

建议先测试 DSL、再翻译成 Java

~~~json
{
  "query": {
    "bool": {
      "must_not": [
        {
          "match": {
            "title": ""
          }
        },
      ]
      "should": [
        {
          "match": {
            "title": ""
          }
        },
        {
          "match": {
            "desc": ""
          }
        }
      ],
      "filter": [
        {
          "term": {
            "isDelete": 0
          }
        },
        {
          "term": {
            "id": 1
          }
        },
        {
          "term": {
            "tags": "java"
          }
        },
        {
          "term": {
            "tags": "框架"
          }
        }
      ],
      "minimum_should_match": 0
    }
  },
  "from": 0, // 分页
  "size": 5, // 分页
  "_source": ["name", "_createTime", "desc", "reviewStatus", "priority", "tags"], // 要查的字段
  "sort": [ // 排序
    {
      "priority": {
        "order": "desc"
      }
    },
    {
      "_score": {
        "order": "desc"
      }
    },
    {
      "publishTime": {
        "order": "desc"
      }
    }
  ]
}
~~~



翻译为 Java：

~~~java
BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 必须包含所有标签
        if (CollectionUtils.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }
        // 包含任何一个标签即可
        if (CollectionUtils.isNotEmpty(orTagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : orTagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<PostEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, PostEsDTO.class);
~~~



动静分离设计：先模糊筛选静态数据，查出数据后，再根据查到的内容 id 去数据库查找到 动态数据。



#### 数据同步 

一般情况下，如果做查询搜索功能，使用 ES 来模糊搜索，但是数据是存放在数据库 MySQL 里的，所以说我们需要把 MySQL 中的数据和 ES 进行同步，保证数据一致（以 MySQL 为主）。



MySQL => ES （单向）



首次安装完 ES，把 MySQL 数据全量同步到 ES 里，写一个单次脚本

4 种方式，全量同步（首次）+ 增量同步（新数据）：

1. 定时任务，比如 1 分钟 1 次，找到 MySQL 中过去几分钟内（至少是定时周期的 2 倍）发生改变的数据，然后更新到 ES。

​		优点：简单易懂、占用资源少、不用引入第三方中间件

​		缺点：有时间差

​		应用场景：数据短时间内不同步影响不大、或者数据几乎不发生修改

2. 双写：写数据的时候，必须也去写 ES；更新删除数据库同理。（事务：建议先保证 MySQL 写成功，如果 ES 写失败了，可以通过定时任务 + 日志 + 告警进行检测和修复（补偿））

3. 用 Logstash 数据同步管道（一般要配合 kafka 消息队列 + beats 采集器）：

4. Canal 监听 MySQL Binlog，实时同步



 Logstash 

传输 和 处理 数据的管道

https://www.elastic.co/guide/en/logstash/7.17/getting-started-with-logstash.html

https://artifacts.elastic.co/downloads/logstash/logstash-7.17.9-windows-x86_64.zip



好处：用起来方便，插件多

缺点：成本更大、一般要配合其他组件使用（比如 kafka）



![image.png](https://cdn.nlark.com/yuque/0/2023/png/398476/1679841417794-18ef85ce-4382-4109-8a40-d441b78b45d2.png)





事件 Demo：

~~~bash
cd logstash-7.17.9
.\bin\logstash.bat -e "input { stdin { } } output { stdout {} }"
~~~





快速开始文档：https://www.elastic.co/guide/en/logstash/7.17/running-logstash-windows.html

监听 udp 并输出：

~~~bash
# Sample Logstash configuration for receiving
# UDP syslog messages over port 514

input {
  udp {
    port => 514
    type => "syslog"
  }
}

output {
  stdout { codec => rubydebug }
}
~~~





要把 MySQL 同步给 Elasticsearch。



问题 1：找不到 mysql 的包

Error: unable to load mysql-connector-java-5.1.36-bin.jar from :jdbc_driver_library, file not readable (please check user and group permissions for the path)

  Exception: LogStash::PluginLoadingError



解决：修改 Logstash 任务配置中的 jdbc_driver_library 为驱动包的绝对路径（驱动包可以从 maven 仓库中拷贝）



增量配置：是不是可以只查最新更新的？可以记录上次更新的数据时间，只查出来 > 该更新时间的数据



小知识：预编译 SQL 的优点？

1灵活

2模板好懂

3快（有缓存）

4部分防注入



sql_last_value 是取上次查到的数据的最后一行的指定的字段，如果要全量更新，只要删除掉 E:\software\ElasticStack\logstash-7.17.9\data\plugins\inputs\jdbc\logstash_jdbc_last_run 文件即可（这个文件存储了上次同步到的数据）

~~~bash
input {
  jdbc {
    jdbc_driver_library => "E:\software\ElasticStack\logstash-7.17.9\config\mysql-connector-java-8.0.29.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://localhost:3306/my_db"
    jdbc_user => "root"
    jdbc_password => "123456"
    statement => "SELECT * from post where updateTime > :sql_last_value"
    tracking_column => "updatetime"
    tracking_column_type => "timestamp"
    use_column_value => true
    parameters => { "favorite_artist" => "Beethoven" }
    schedule => "*/5 * * * * *"
    jdbc_default_timezone => "Asia/Shanghai"
  }
}

output {
  stdout { codec => rubydebug }
}
~~~



注意查询语句中要按 updateTime 排序，保证最后一条是最大的：

~~~bash
input {
  jdbc {
    jdbc_driver_library => "E:\software\ElasticStack\logstash-7.17.9\config\mysql-connector-java-8.0.29.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://localhost:3306/my_db"
    jdbc_user => "root"
    jdbc_password => "123456"
    statement => "SELECT * from post where updateTime > :sql_last_value and updateTime < now() order by updateTime desc"
    tracking_column => "updatetime"
    tracking_column_type => "timestamp"
    use_column_value => true
    parameters => { "favorite_artist" => "Beethoven" }
    schedule => "*/5 * * * * *"
    jdbc_default_timezone => "Asia/Shanghai"
  }
}

output {
  stdout { codec => rubydebug }

  elasticsearch {
    hosts => "http://localhost:9200"
    index => "post_v1"
    document_id => "%{id}"
  }
}
~~~



两个问题：

1字段全变成小写了

2多了一些我们不想同步的字段



可以编写过滤：

~~~bash
input {
  jdbc {
    jdbc_driver_library => "E:\software\ElasticStack\logstash-7.17.9\config\mysql-connector-java-8.0.29.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://localhost:3306/my_db"
    jdbc_user => "root"
    jdbc_password => "123456"
    statement => "SELECT * from post where updateTime > :sql_last_value and updateTime < now() order by updateTime desc"
    tracking_column => "updatetime"
    tracking_column_type => "timestamp"
    use_column_value => true
    parameters => { "favorite_artist" => "Beethoven" }
    schedule => "*/5 * * * * *"
    jdbc_default_timezone => "Asia/Shanghai"
  }
}

filter {
    mutate {
        rename => {
          "updatetime" => "updateTime"
          "userid" => "userId"
          "createtime" => "createTime"
          "isdelete" => "isDelete"
        }
        remove_field => ["thumbnum", "favournum"]
    }
}

output {
  stdout { codec => rubydebug }

  elasticsearch {
    hosts => "127.0.0.1:9200"
    index => "post_v1"
    document_id => "%{id}"
  }
}
~~~



 订阅数据库流水的同步方式 Canal 

https://github.com/alibaba/canal/

优点：实时同步，实时性非常强

原理：数据库每次修改时，会修改 binlog 文件，只要监听该文件的修改，就能第一时间得到消息并处理

canal：帮你监听 binlog，并解析 binlog 为你可以理解的内容。

它伪装成了 MySQL 的从节点，获取主节点给的 binlog，如图：

![img](https://camo.githubusercontent.com/63881e271f889d4a424c55cea2f9c2065f63494fecac58432eac415f6e47e959/68747470733a2f2f696d672d626c6f672e6373646e696d672e636e2f32303139313130343130313733353934372e706e67)





快速开始：https://github.com/alibaba/canal/wiki/QuickStart

windows 系统，找到你本地的 mysql 安装目录，在根目录下新建 my.ini 文件：

~~~properties
[mysqld]
log-bin=mysql-bin # 开启 binlog
binlog-format=ROW # 选择 ROW 模式
server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复
~~~



如果 java 找不到，修改 startup.bat 脚本为你自己的 java home：

~~~bash
set JAVA_HOME=C:\Users\59278\.jdks\corretto-1.8.0_302
echo %JAVA_HOME%
set PATH=%JAVA_HOME%\bin;%PATH%
echo %PATH%
~~~



问题：mysql 无法链接，Caused by: java.io.IOException: caching_sha2_password Auth failed

解决方案：

https://github.com/alibaba/canal/issues/3902

ALTER USER 'canal'@'%' IDENTIFIED WITH mysql_native_password BY 'canal';

ALTER USER 'canal'@'%' IDENTIFIED BY 'canal' PASSWORD EXPIRE NEVER;

FLUSH PRIVILEGES;



 压力测试 

官方文档：https://jmeter.apache.org/

找到 jar 包：apache-jmeter-5.5\apache-jmeter-5.5\bin\ApacheJMeter.jar 启动

配置线程组 => 请求头 => 默认请求 => 单个请求 => 响应断言 => 聚合报告 / 结果树





 更多学习 

插件：https://jmeter-plugins.org/install/Install/

下载后文件为[plugins-manager.jar](https://jmeter-plugins.org/get/)格式，将其放入jmeter安装目录下的lib/ext目录，然后重启jmeter，即可。

参考文章：https://blog.csdn.net/weixin_45189665/article/details/125278218
























