import com.alibaba.fastjson.JSON;
import com.anqi.es.DemoEsApplication;
import com.anqi.es.entity.News;
import com.anqi.es.highclient.RestHighLevelClientService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = DemoEsApplication.class)
@RunWith(SpringRunner.class)
public class NewsTest {
    @Autowired
    private RestHighLevelClient rhlClient;
    private String index = "demo";


    @Autowired
    RestHighLevelClientService service;


    @Test
    public void batchAddTest() {
        BulkRequest bulkRequest = new BulkRequest();
        List<IndexRequest> requests = generateRequests();
        for (IndexRequest indexRequest : requests) {
            bulkRequest.add(indexRequest);
        }
        try {
            rhlClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<IndexRequest> generateRequests() {
        List<IndexRequest> requests = new ArrayList<>();
        requests.add(generateNewsRequest("中印边防军于拉达克举行会晤 强调维护边境和平", "军事", "2018-01-27T08:34:00Z", 130, 33.3));
        requests.add(generateNewsRequest("费德勒收郑泫退赛礼 进决赛战西里奇", "体育", "2018-01-26T14:34:00Z", 1, 11.3));
        requests.add(generateNewsRequest("欧文否认拿动手术威胁骑士 兴奋全明星联手詹皇", "体育", "2018-01-26T08:34:00Z", 20, 123.33));
        requests.add(generateNewsRequest("皇马官方通告拉莫斯伊斯科伤情 将缺阵西甲关键战", "体育", "2018-01-26T20:34:00Z", 13, 20.00));
        return requests;
    }

    public IndexRequest generateNewsRequest(String title, String tag, String publishTime, Integer num, Double price) {
        IndexRequest indexRequest = new IndexRequest(index);
        News news = new News();
        news.setTitle(title);
        news.setTag(tag);
        news.setPublishTime(publishTime);
        news.setNum(num);
        news.setPrice(price);
        String source = JSON.toJSONString(news);
        indexRequest.source(source, XContentType.JSON);
        return indexRequest;
    }


    /**
     * /**
     * * 组合查询
     * * must(QueryBuilders) :   AND
     * * mustNot(QueryBuilders): NOT
     * * should:                  : OR
     */
    @Test
    public void queryTest() {
        SearchSourceBuilder sourceBuilder;
        sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(10);
//        sourceBuilder.fetchSource(new String[]{"title"}, new String[]{});
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "费");

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("userId", 222);
        TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("tableId", 111111);

//
//        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime");
//        rangeQueryBuilder.gte("2018-01-26T08:00:00Z");
//        rangeQueryBuilder.lte("2018-01-26T20:00:00Z");
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        boolBuilder.must(matchQueryBuilder);
        boolBuilder.must(termQueryBuilder);
        boolBuilder.must(termQueryBuilder2);
//        boolBuilder.must(rangeQueryBuilder);
        sourceBuilder.query(boolBuilder);
        SearchRequest searchRequest = new SearchRequest("user_collection");
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
//            System.out.println(response);
            SearchHits hits = response.getHits();
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

}
