/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;

/**
 * @since 7.2
 */
@Configuration
@ComponentScan("com.b2international.snowowl.snomed.core.rest")
public class SnomedApiConfig extends BaseApiConfig {
	
	public static final String REPOSITORY_ID = SnomedTerminologyComponentConstants.TOOLING_ID;

	@Override
	public String getApiBaseUrl() {
		return "/snomedct";
	}
	
	@Bean
	public GroupedOpenApi snomedDocs() {
		return docs(
			getApiBaseUrl(),
			"snomedct",
			"3.0",
			"SNOMED CT API",
			"https://b2i.sg",
			"support@b2i.sg",
			"API License",
			"https://b2i.sg",
			"This describes the resources that make up the official Snow Owl® SNOMED CT Terminology API.\n" + 
			"Detailed documentation is available at the [official documentation site](https://docs.b2i.sg/snow-owl/api/snomed)."
		);
	}
	
	@Bean
	public Integer maxReasonerRuns() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance()
				.getServiceChecked(SnowOwlConfiguration.class)
				.getModuleConfig(SnomedCoreConfiguration.class)
				.getMaxReasonerRuns();
	}
	
}
