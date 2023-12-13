/*
 * Copyright 2019-2023 B2i Healthcare, https://b2ihealthcare.com
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

import org.elasticsearch.core.List;
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
	
	public static final String CLASSIFICATIONS = "classifications";
	
	public static final String CONCEPTS = "concepts";
	public static final String DESCRIPTIONS = "descriptions";
	public static final String RELATIONSHIPS = "relationships";
	public static final String MEMBERS = "members";
	public static final String REFSETS = "refSets";
	
	public static final String IMPORT = "import";
	public static final String EXPORT = "export";
	
	public static final String MRCM = "mrcm";

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
			B2I_SITE,
			"info@b2ihealthcare.com",
			"API License",
			B2I_SITE,
			"This describes the resources that make up the official Snow Owl® SNOMED CT Terminology API.\n" + 
			"Detailed documentation is available at the [official documentation site](https://docs.b2ihealthcare.com/snow-owl).",
			List.of(CONCEPTS, DESCRIPTIONS, RELATIONSHIPS, REFSETS, MEMBERS, IMPORT, EXPORT, CoreApiConfig.VALIDATIONS)
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
