import com.google.gson.GsonBuilder;
import com.microservice.es.EsDemoApplication;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Cat;
import io.searchbox.indices.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EsDemoApplication.class)
@Slf4j
public class JestTest {
    JestClient jestClient=null;

    @Before
    public void before() {

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://127.0.0.1:9200")
                .multiThreaded(true)
                .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create())
                .multiThreaded(true)
                .readTimeout(10000)
                .build());
        this.jestClient = factory.getObject();
    }

    @Test
    public void createIndex( ) {
        try {
            JestResult jestResult = jestClient.execute(new CreateIndex.Builder("index2").build());
            System.out.println("createIndex:{}" + jestResult.isSucceeded());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllIndex() throws IOException {
        Cat cat = new Cat.IndicesBuilder().build();
        JestResult result = jestClient.execute(cat);
        System.out.println(result.getJsonString());
    }


}
