package MyRule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LdwSelfRule {

	@Bean
	public IRule mySelfRule() {
		return new RandomRule();
	}

}
