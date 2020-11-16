import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anqi.es.DemoEsApplication;
import com.anqi.es.entity.Product;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

@SpringBootTest(classes = DemoEsApplication.class)
@RunWith(SpringRunner.class)
public class ProductTest {

    @Autowired
    private RestHighLevelClient client;

    private String index = "bntang";


    @Test
    public void addDoc() {
        // 1.准备需要保存到索引库的Json文档数据
        Date today = new Date();

        for (int i = 0; i < 50; i++) {
            Product product = new Product(Long.valueOf(i), "小米手机", "手机", "小米", Double.valueOf(100 * i),
                    "http://www.baidu.com", DateUtil.offsetDay(today, (0 - 2 * i)), new Date());
            // 2.将对象转为Json字符串
            String jsonString = JSON.toJSONString(product);

            System.out.println("插入的记录：  " + jsonString);

            // 3.创建请求对象,指定索引库、类型、id(可选)
            IndexRequest indexRequest = new IndexRequest(index);
            // 4.调用source方法将请求数据封装到IndexRequest请求对象中
            indexRequest.source(jsonString, XContentType.JSON);
            try {
                // 5.调用方法进行数据通信
                IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                // 6.解析输出结果
                System.out.println("结果：" + JSON.toJSONString(indexResponse));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据id查看文档
     */
    @Test
    public void getDocById() {
        // 1.构建GetRequest请求对象,指定索引库、类型、id
        GetRequest getRequest = new GetRequest(index, "0");
        try {
            // 2.调用方法进行数据通信
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            // 3.解析输出结果
            System.out.println("结果：" + JSON.toJSONString(getResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * /**
     * * 组合查询
     * 居然默认只查出10条记录
     * * must(QueryBuilders) :   AND
     * * mustNot(QueryBuilders): NOT
     * * should:                  : OR
     */
    @Test
    public void queryTest() {
        SearchSourceBuilder sourceBuilder;
        sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(100);
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        sourceBuilder.query(boolBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//            System.out.println(response);
            SearchHits hits = response.getHits();
            System.out.println("总共有" + hits.getHits().length + "条记录");
            for (SearchHit hit : hits) {
                JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
                Long millseconds = jsonObject.getLong("updatedTime");
                System.out.print("updateTime: " + DateUtil.format(new Date(millseconds), DatePattern.UTC_SIMPLE_PATTERN) + "---" + jsonObject);
                System.out.println();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void search() throws IOException {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("fields.entity_id", "319");//这里可以根据字段进行搜索，must表示符合条件的，相反的mustnot表示不符合条件的
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("updatedTime"); //新建range条件
        Long startDate = new Date().getTime();
        Long endDate = DateUtil.offsetDay(new Date(), -3).toJdkDate().getTime();


        System.out.println(startDate);
        System.out.println(endDate);

        rangeQueryBuilder.gte(endDate); //开始时间   1605542257234
        // 1605286255697     1605545455697
        rangeQueryBuilder.lte(startDate); //结束时间
        boolBuilder.must(rangeQueryBuilder);
//        boolBuilder.must(matchQueryBuilder);
        sourceBuilder.query(boolBuilder); //设置查询，可以是任何类型的QueryBuilder。
        sourceBuilder.from(0); //设置确定结果要从哪个索引开始搜索的from选项，默认为0
        sourceBuilder.size(100); //设置确定搜素命中返回数的size选项，默认为10
//        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); //设置一个可选的超时，控制允许搜索的时间。
//        sourceBuilder.fetchSource(new String[]{"fields.port", "fields.entity_id", "fields.message"}, new String[]{}); //第一个是获取字段，第二个是过滤的字段，默认获取全部
        SearchRequest searchRequest = new SearchRequest(index); //索引
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
            Long millseconds = jsonObject.getLong("updatedTime");
            System.out.print("updateTime: " + DateUtil.format(new Date(millseconds), DatePattern.UTC_SIMPLE_PATTERN) + "---" + jsonObject);
            System.out.println();
        }
    }


    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    @Test
    public void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
       /*   if (null != settings && !"".equals(settings)) {
            request.settings(settings, XContentType.JSON);
        }*/
     /*   if (null != mapping && !"".equals(mapping)) {
            request.mapping(mapping, XContentType.JSON);
        }*/
        client.indices().create(request, RequestOptions.DEFAULT);
    }


}
