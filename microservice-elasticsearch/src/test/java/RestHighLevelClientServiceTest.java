import com.alibaba.fastjson.JSON;
import com.anqi.es.DemoEsApplication;
import com.anqi.es.entity.Cloth;
import com.anqi.es.util.SnowflakeIdWorker;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

//有RunWith才会有ioc容器
@SpringBootTest(classes = DemoEsApplication.class)
@RunWith(SpringRunner.class)
@ComponentScan("com.anqi.es")
public class RestHighLevelClientServiceTest {

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    RestHighLevelClientService service;

    @Test
    public void createIndex() throws IOException{
        CreateIndexResponse response = service.createIndex("user_collection", null, null);
        if (response.isAcknowledged()) {
            System.out.println("创建成功");
        }
    }

    @Test
    public void deleteIndex() throws IOException{
        AcknowledgedResponse response = service.deleteIndex("user_collection");
        if (response.isAcknowledged()) {
            System.out.println("删除成功");
        }
    }

    @Test
    public void indexExists() throws IOException{
        System.out.println(service.indexExists("idx_tt"));
    }


    @Test
    public void addDoc() throws IOException {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        Cloth cloth = new Cloth(idWorker.nextId()+"","新版日系毛衣", "潮流前线等你来Pick!", 60, 100, new Date());
        String source = JSON.toJSONString(cloth);
        IndexResponse response = service.addDoc("idx_cloth", source);
        System.out.println(response.status());
    }

    @Test
    public void search() throws IOException{
        SearchResponse response = service.search("num", "50", 0, 30, "user_collection");
        Arrays.asList(response.getHits().getHits())
                .forEach(e -> System.out.println(e.getSourceAsString()));
    }
    @Test
    public void queryTest() {
        SearchSourceBuilder sourceBuilder;
        sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(3);
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
        SearchRequest searchRequest = new SearchRequest("user_collection");
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(response);
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
    public void termSearch() throws IOException{
        SearchResponse response = service.termSearch("name", "nike潮流毛衣", 0, 50,"idx_cloth");
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    public void deleteDoc() throws IOException{
        String id = "yvJ_Q24BymdyZW22Os2D";
        SearchResponse search = service.search("id", "2", 0, 5, "idx_cloth");

        for (SearchHit hit : search.getHits().getHits()) {
            id = hit.getId();
        }
        DeleteResponse response = service.deleteDoc("idx_cloth", id);
        if (response.status().equals(RestStatus.OK)) {
            System.out.println("删除成功");
        }
    }

//    @Test
//    public void importAll() throws IOException{
//        List<Cloth> list = buildJson();
//
//        String[] cloths = new String[list.size()];
//
//        for (int i = 0; i < list.size(); i++) {
//            cloths[i] = JSON.toJSONString(list.get(i));
//        }
//
//        for (String cloth : cloths) {
//            System.out.println(cloth);
//        }
//
//        BulkResponse bulk = service.importAll("idx_cloth", true, cloths);
//
//        if (bulk.hasFailures()) {
//            System.out.println("批量失败");
//            System.out.println(bulk.buildFailureMessage());
//        }
//
//    }
//
//    private List<Cloth> buildJson(){
//        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
//
//        List<Cloth> cloths = new ArrayList<>();
//
//        String[] tags = new String[]{"nike","阿迪达斯","阿迪达斯三叶草","鸿星尔克",
//                "乔丹","飞人乔丹","哈雷乔丹","cba","法国老人头","特步","花花公子","海澜之家"};
//
//        String[] adj = new String[]{"性感","宽松","潮流","fashion","nice","热卖","新版"};
//        String[] cls = new String[]{"半袖","衬衫","外套","跑鞋","运动衣","毛衣","长裤","棉裤","背心"};
//
//
//        String[] descPre = new String[]{"双十一来袭,","618活动大促销,","店面到期亏本清仓,","换季大甩卖,","打造潮流攻势,"};
//
//        String[] descAft = new String[]{"商品满100减五十!","买一送一，买不了吃亏买不了上当!","关注店铺收藏商品即可立减五十!","包邮到家包邮到家!"};
//
//        Random random = new Random();
//        DecimalFormat df = new DecimalFormat( "0.00");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        for (int i = 0; i < 100; i++) {
//            cloths.add(new Cloth(idWorker.nextId()+"",
//                    tags[random.nextInt(tags.length)] + adj[random.nextInt(adj.length)] + cls[random.nextInt(cls.length)],
//                    descPre[random.nextInt(descPre.length)] + descAft[random.nextInt(descAft.length)],
//                    random.nextInt(200),Double.valueOf(df.format(random.nextDouble()*300)), new Date()
//            ));
//        }
//
//        return cloths;
//    }
}