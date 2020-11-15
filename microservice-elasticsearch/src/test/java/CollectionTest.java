import com.alibaba.fastjson.JSON;
import com.anqi.es.DemoEsApplication;
import com.anqi.es.entity.UserCollection;
import com.anqi.es.highclient.RestHighLevelClientService;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

@SpringBootTest(classes = DemoEsApplication.class)
@RunWith(SpringRunner.class)
public class CollectionTest {
    @Autowired
    private RestHighLevelClient rhlClient;
    private String index = "user_collection";


    @Autowired
    RestHighLevelClientService service;

    @Test
    public void addDoc() throws IOException {
        for (int i = 0; i < 50; i++) {
            UserCollection userCollection = new UserCollection();
            userCollection.setUserId(Long.valueOf(i));
            userCollection.setCreatedTime("1980-11-2" + i + " 11:22:21");
            userCollection.setUpdatedTime(new Date());
            userCollection.setTableName("t" + i);
            userCollection.setTableId(23123L);
            userCollection.setIsCollected(1);
            userCollection.setBrowserCount(Long.valueOf(i));

            String source = JSON.toJSONString(userCollection);
            IndexResponse response = service.addDoc(index, source);
            System.out.println(response.status());
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
        sourceBuilder.from(9);
        sourceBuilder.size(20);
//        sourceBuilder.fetchSource(new String[]{"title"}, new String[]{});
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "费");

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("userId", "2313123");
        TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("tableId", 111111);

//
//        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime");
//        rangeQueryBuilder.gte("2018-01-26T08:00:00Z");
//        rangeQueryBuilder.lte("2018-01-26T20:00:00Z");
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        boolBuilder.must(matchQueryBuilder);
//        boolBuilder.must(termQueryBuilder);
//        boolBuilder.must(termQueryBuilder2);
//        boolBuilder.must(rangeQueryBuilder);
        sourceBuilder.query(boolBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
//            System.out.println(response);
            SearchHits hits = response.getHits();
            System.out.println("总共有" + hits.getHits().length + "条记录");
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void matchAll() throws IOException {
        //设置索引
        SearchRequest searchRequest = new SearchRequest(index);
        //构建查询
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //        MatchQueryBuilder matchQueryBuilder1 = QueryBuilders.matchQuery("text", "test");
        //        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("Time");
        //起始时间
        //        rangeQueryBuilder.gte("2020-04-01T00:00:00+08:00");
        ////结束时间
        //        rangeQueryBuilder.lte("2020-04-31T23:59:59+08:00");
        //        boolBuilder.must(matchQueryBuilder1).must(rangeQueryBuilder);
        sourceBuilder.query(boolBuilder);

        //按时间聚合，求TX的和
        //DateHistogramInterval.minutes(5)是指按5分钟聚合
        //format("yyyy-MM-dd HH:mm")是指聚合的结果的Time的格式
        //BucketOrder.aggregation("tx_sum", false)对聚合结果的排序 true为正序 false为倒序
        AggregationBuilder aggregation = AggregationBuilders.dateHistogram("time_count")/*.field("createdTime.keyword")*/
//                                                            .fixedInterval(DateHistogramInterval.minutes(5))
//                                                                .format("yyyy-MM-dd HH:mm")
                                                                .order(BucketOrder.aggregation("createdTime.keyword", false));
                                                //        aggregation.subAggregation(AggregationBuilders.sum("tx_sum").field("Tx"));
        sourceBuilder.aggregation(aggregation);
        searchRequest.source(sourceBuilder);
        //发送请求
        SearchResponse searchResponse = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
        //获取聚合的结果
        Map<String, Double> map = new LinkedHashMap<>();
        Aggregations aggregations = searchResponse.getAggregations();
        Aggregation aggregation1 = aggregations.get("time_count");
        List<? extends Histogram.Bucket> buckets = ((Histogram) aggregation1).getBuckets();
        for (Histogram.Bucket bucket : buckets) {
            String keyAsString = bucket.getKeyAsString();
            Sum sum = bucket.getAggregations().get("tx_sum");
            map.put(keyAsString, sum.getValue());
        }
    }


    @Test
    public void updateTest() {
        UpdateRequest updateRequest = new UpdateRequest("user_collection", "BFGZp3UBLrpUfe32xBvP");
        Map<String, Object> map = new HashMap<>();
        map.put("isCollected", "0");
        map.put("isDeleted", "1");
        updateRequest.doc(map);
        try {
            rhlClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void statucs() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user_collection");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        AggregationBuilder aggregationBuild = AggregationBuilders.terms("actors_agg")
                .field("isCollected")
                .size(3)
                .shardSize(50)
                .collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST)
                .subAggregation(AggregationBuilders.terms("costars_agg")
                        .field("tableId")
                        .size(3));
        sourceBuilder.aggregation(aggregationBuild);
        sourceBuilder.size(0);

        searchRequest.source(sourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    public void search() throws IOException {
        SearchResponse response = service.search("num", "50", 0, 30, index);
        Arrays.asList(response.getHits().getHits())
                .forEach(e -> System.out.println(e.getSourceAsString()));
    }

}
