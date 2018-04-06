/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameterKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;

/**
 * @since 6.4
 */
public abstract class BaseFhirRestService {

	public static final String APPLICATION_FHIR_JSON = "application/fhir+json";
	public static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
	
	@Autowired
	private ObjectMapper mapper;
	
	protected final <T> T toRequest(Parameters.Fhir in, Class<T> request) {
		return mapper.convertValue(new Parameters.Json(in.parameters()), request);
	}
	
	protected final Parameters.Fhir toResponse(Object response) {
		return new Parameters.Fhir(Parameters.from(response));
	}
	
	protected MappingJacksonValue applyResponseFilter(MultiValueMap<String, String> searchParams, Object filteredObject) {

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		
		if (searchParams.containsKey(SearchRequestParameterKey._summary.name())) {
			List<String> summaryParameter = getRequestedFields(searchParams, SearchRequestParameterKey._summary.name());
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(summaryParameter));
		} else if (searchParams.containsKey(SearchRequestParameterKey._elements.name())) {
			List<String> requestedElements = getRequestedFields(searchParams, SearchRequestParameterKey._elements.name());
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(requestedElements));
		}
		
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredObject);
		mappingJacksonValue.setFilters(filterProvider);
		mapper.setFilterProvider(filterProvider);
		return mappingJacksonValue;
	}
	
	protected void validateSearchParams(MultiValueMap<String, String> searchParams) {
		
		if (searchParams.containsKey(SearchRequestParameterKey._summary.name()) && 
				searchParams.containsKey(SearchRequestParameterKey._elements.name())) {
			throw new BadRequestException("Both search parameters '_summary' and '_elements' cannot be specified at the same time.");
		}
	}
	
	protected List<String> getRequestedFields(MultiValueMap<String, String> elements, String paramName) {
		
		List<String> requestedParameters = Lists.newArrayList();
		if (elements.containsKey(paramName)) {
			List<String> returnFields = elements.get(paramName);
			for (String element : returnFields) {
				element = element.replaceAll(" ", "");
				if (element.contains(",")) {
					String requestedFields[] = element.split(",");
					requestedParameters.addAll(Lists.newArrayList(requestedFields));
				} else {
					if (!StringUtils.isEmpty(element)) {
						requestedParameters.add(element);
					}
				}
			}
		}
		return requestedParameters;
	}
	
}
