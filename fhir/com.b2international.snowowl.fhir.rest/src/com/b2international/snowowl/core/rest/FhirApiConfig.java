/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The Spring configuration class for Snow Owl's FHIR REST API.
 * 
 * @since 7.0
 */
@Configuration
@ComponentScan("com.b2international.snowowl.fhir.rest")
public class FhirApiConfig extends BaseApiConfig {

	public static final String CODESYSTEM = "CodeSystem";
	public static final String CONCEPTMAP = "ConceptMap";
	public static final String VALUESET = "ValueSet";
	public static final String BUNDLE = "Bundle";
	public static final String CAPABILITY_STATEMENT = "CapabilityStatement";
	public static final String STRUCTURE_DEFINITION = "StructureDefinition";
	
	@Override
	public String getApiBaseUrl() {
		return "/fhir";
	}

	@Bean
	public GroupedOpenApi fhirDocs() {
		return docs(
			getApiBaseUrl(), 
			"fhir", 
			"R5", 
			"FHIR API", 
			B2I_SITE, 
			"info@b2ihealthcare.com", 
			"API License", 
			B2I_SITE, 
			"This describes the resources that make up the official Snow Owl® [FHIR® Terminology Service](https://hl7.org/fhir/R5/terminology-service.html) API.\r\n" + 
			"Detailed documentation is available at the [official documentation site](https://docs.b2ihealthcare.com/snow-owl/rest-apis/fhir).",
			List.of(CAPABILITY_STATEMENT, CODESYSTEM, VALUESET, CONCEPTMAP, BUNDLE, STRUCTURE_DEFINITION)
		);
	}
	
}
