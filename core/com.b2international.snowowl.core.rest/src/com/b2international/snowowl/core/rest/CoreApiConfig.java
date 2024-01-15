/*
 * Copyright 2021-2024 B2i Healthcare Pte Ltd, http://b2i.sg
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
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 8.0
 */
@Configuration
public class CoreApiConfig extends BaseApiConfig {

	public static final String ADMINISTRATION = "administration";
	public static final String BUNDLES = "bundles";
	public static final String RESOURCES = "resources";
	public static final String CODESYSTEMS = "codesystems";
	public static final String API_KEY = "apikey";
	public static final String CONCEPTS = "concepts";
	public static final String MISC = "misc";
	
	public static final String VALIDATIONS = "validations";
	
	@Override
	public String getApiBaseUrl() {
		return "/";
	}
	
	@Bean
	public GroupedOpenApi codeSystemDocs() {
		return docs(
			getApiBaseUrl(),
			"core",
			"2.0",
			"Core API",
			B2I_SITE,
			"support@b2i.sg",
			"API License",
			B2I_SITE,
			"This describes the resources that make up the official Snow Owl® Core API. <br > Detailed documentation is available at the [official documentation site](https://docs.b2i.sg/snow-owl/api/core).",
			List.of(API_KEY, RESOURCES, CODESYSTEMS, CONCEPTS, BUNDLES, ADMINISTRATION, MISC)
		);
	}

}
