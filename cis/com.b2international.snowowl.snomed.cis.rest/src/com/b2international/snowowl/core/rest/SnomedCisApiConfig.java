/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.core.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.spring.web.plugins.Docket;

/**
 * @since 6.18
 */
@Configuration
@ComponentScan({"com.b2international.snowowl.snomed.cis.rest"})
public class SnomedCisApiConfig extends BaseApiConfig {

	@Override
	public String getApiBaseUrl() {
		return "/cis";
	}

	@Bean
	public Docket cisDocs() {
		return docs(
			getApiBaseUrl(), 
			"cis", 
			"1.0",
			"SNOMED CT CIS API",
			"https://b2i.sg/",
			"support@b2i.sg",
			"API License",
			"https://b2i.sg/",
			"This describes the resources that make up the official Snow Owl® SNOMED CT Component Identifier Service API.\n"+
			"Detailed documentation is available at the [official documentation site](https://docs.b2i.sg/snow-owl/api/cis)."
		);
	}
	
}
