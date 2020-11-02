package com.ldw.microservice.docker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


// * API接口测试：http://localhost:8080/swagger-ui.html

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private String appName;
	private String appVersion;
	private String appUrl;
	private String appEmail;

	@Bean
	public Docket userApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("幸运派").apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.ldw.microservice.docker")).paths(PathSelectors.any()).build();
	}

	// 预览地址:swagger-ui.html
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Spring 中使用Swagger2构建文档").termsOfServiceUrl(appUrl)
				.contact(new Contact(appName, appUrl, appEmail)).version(appVersion).build();
	}
}
