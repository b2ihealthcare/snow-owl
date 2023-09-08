/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import org.springframework.context.annotation.Configuration;

import com.b2international.snowowl.fhir.rest.CodeSystemResourceProvider;
import com.b2international.snowowl.fhir.rest.NestedRestfulServer;
import com.b2international.snowowl.fhir.rest.SearchAfterPagingProvider;

import ca.uhn.fhir.rest.server.FifoMemoryPagingProvider;
import ca.uhn.fhir.rest.server.IPagingProvider;
import ca.uhn.fhir.rest.server.IResourceProvider;

/**
 * The Spring configuration class for Snow Owl's FHIR REST API.
 * 
 * @since 7.0
 */
@Configuration
public class FhirApiConfig {

	public static final String CODESYSTEM = "CodeSystem";
	public static final String CONCEPTMAP = "ConceptMap";
	public static final String VALUESET = "ValueSet";
	public static final String BUNDLE = "Bundle";
	public static final String CAPABILITY_STATEMENT = "CapabilityStatement";
	public static final String STRUCTURE_DEFINITION = "StructureDefinition";
	
	public static String getApiBaseUrl() {
		return "/fhir";
	}

	@Bean
	public IResourceProvider codeSystemResourceProvider() {
		return new CodeSystemResourceProvider();
	}
	
	@Bean(name = "/fhir/**")
	public NestedRestfulServer nestedRestfulServer() {
		return new NestedRestfulServer();
	}

	@Bean
	public IPagingProvider delegatePagingProvider() {
		// Store at most 100 pageable searches
		final FifoMemoryPagingProvider pp = new FifoMemoryPagingProvider(100);
		pp.setDefaultPageSize(1000);
		pp.setMaximumPageSize(10_000);
		return pp;
	}
	
	@Bean
	public SearchAfterPagingProvider searchAfterPagingProvider() {
		return new SearchAfterPagingProvider();
	}
}
