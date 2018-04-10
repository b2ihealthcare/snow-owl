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

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.SummaryParameter;
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
	protected ObjectMapper mapper;
	
	protected final <T> T toRequest(Parameters.Fhir in, Class<T> request) {
		return mapper.convertValue(in.toJson(), request);
	}
	
	protected final Parameters.Fhir toResponse(Object response) {
		return new Parameters.Fhir(Parameters.from(response));
	}
	
	protected MappingJacksonValue applyResponseFilter(String summaryParameter, List<String> elementsParameter, Object filteredObject) {

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		
		if (summaryParameter !=null) {
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(SummaryParameter.fromRequestParameter(summaryParameter)));
		} else if (elementsParameter != null) {
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(getRequestedFields(elementsParameter)));
		}
		
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredObject);
		mappingJacksonValue.setFilters(filterProvider);
		mapper.setFilterProvider(filterProvider);
		return mappingJacksonValue;
	}
	
	protected void validateSearchParams(String summary, List<String> elements) {
		if (summary != null && elements !=null) {
			throw new BadRequestException("Both search parameters '_summary' and '_elements' cannot be specified at the same time.");
		}
	}
	
	protected List<String> getRequestedFields(List<String> elements) {
		
		List<String> requestedParameters = Lists.newArrayList();
		for (String element : elements) {
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
		return requestedParameters;
	}
	
}
