package com.masteringapi.attendees;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
public class AttendeesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendeesApplication.class, args);
	}

	/**
	 * This bean is required configuration to filter out some of the health endpoints that Spring provides
	 * @return Configuration to remove custom Spring Boot additions
	 */
	@Bean
	public Docket api() {
		ApiInfo apiInfo = new ApiInfoBuilder()
				.title("Attendees Mastering API")
				.version("1.0")
				.description("Example accompanying the Mastering API Book")
				.build();
		return new Docket(DocumentationType.OAS_30)
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.masteringapi.attendees"))
				.paths(PathSelectors.any())
				.build();
	}
}
