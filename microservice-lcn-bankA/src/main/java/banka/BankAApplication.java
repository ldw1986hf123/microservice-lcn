package banka;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableDistributedTransaction
@EnableFeignClients
public class BankAApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAApplication.class, args);
    }

}
