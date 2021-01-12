/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.fhir.core.provider.ICodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.provider.IConceptMapApiProvider;
import com.b2international.snowowl.fhir.core.provider.IValueSetApiProvider;

import springfox.documentation.spring.web.plugins.Docket;

/**
 * The Spring configuration class for Snow Owl's FHIR REST API.
 * 
 * @since 7.0
 */
@Configuration
@ComponentScan("com.b2international.snowowl.fhir.rest")
public class FhirApiConfig extends BaseApiConfig {

	@Override
	public String getApiBaseUrl() {
		return "/fhir";
	}

	@Bean
	public Docket fhirDocs() {
		return docs(
			getApiBaseUrl(), 
			"fhir", 
			"1.0", 
			"FHIR API", 
			"https://b2i.sg/", 
			"support@b2i.sg", 
			"API License", 
			"https://b2i.sg/", 
			"This describes the resources that make up the official Snow Owl® Snow Owl® <a href=\\\"http://hl7.org/fhir/\\\">FHIR®</a> API.\r\n" + 
			"Detailed documentation is available at the [official documentation site](https://docs.b2i.sg/snow-owl/api/fhir)."
		);

	}
	
	@Bean
	public ICodeSystemApiProvider.Registry codeSystemProviderRegistry() {
		return ApplicationContext.getServiceForClass(ICodeSystemApiProvider.Registry.class);
	}
	
	@Bean
	public IConceptMapApiProvider.Registry conceptMapProviderRegistry() {
		return ApplicationContext.getServiceForClass(IConceptMapApiProvider.Registry.class);
	}
	
	@Bean
	public IValueSetApiProvider.Registry valueSetProviderRegistry() {
		return ApplicationContext.getServiceForClass(IValueSetApiProvider.Registry.class);
	}
	
//	/*
//	 * Add properties filter.
//	 * TODO: https://github.com/krishna81m/jackson-nested-prop-filter
//	 * @return
//	 */
//	@Bean
//	public ObjectMapper objectMapper() {
//		final ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
//		final SimpleDateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
//		df.setTimeZone(TimeZone.getTimeZone("UTC"));
//		objectMapper.setDateFormat(df);
//		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//		//objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//		return objectMapper;
//	}

}
