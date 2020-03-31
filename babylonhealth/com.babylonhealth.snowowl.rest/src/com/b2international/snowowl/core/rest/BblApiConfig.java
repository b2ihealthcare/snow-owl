package com.b2international.snowowl.core.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.spring.web.plugins.Docket;


@Configuration
@ComponentScan("com.b2international.snowowl.core.bbl")
public class BblApiConfig extends BaseApiConfig {

	@Override
	public String getApiBaseUrl() {
		return "/";
	}

	@Bean
	public Docket bblDocs() {
		return docs(
			getApiBaseUrl(), 
			"bbl", 
			"1.0", 
			"BBl API", 
			"", 
			"", 
			"", 
			"",
			"API added by Babylon Health"
		);

	}	

}
