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

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.FhirResource;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SummaryParameterValue;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 6.4
 */
public abstract class BaseFhirResourceRestService<R extends FhirResource> {

	public static final String APPLICATION_FHIR_JSON = "application/fhir+json;charset=utf-8";
	public static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
	
	@Autowired
	protected ObjectMapper mapper;
	
	protected final <T> T toRequest(Parameters.Fhir in, Class<T> request) {
		return mapper.convertValue(in.toJson(), request);
	}
	
	protected final Parameters.Fhir toResponse(Object response) {
		return new Parameters.Fhir(Parameters.from(response));
	}
	
	protected MappingJacksonValue applyResponseContentFilter(FhirResource filteredFhirResource, SearchRequestParameters parameters) {

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		SummaryParameterValue summaryParameter = parameters.getSummary();
		Collection<String> elementsParameters = parameters.getElements();
		
		if (summaryParameter !=null) {
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(summaryParameter));
			filteredFhirResource.setSubsetted();
		} else if (elementsParameters != null) {
			filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(getRequestedFields(elementsParameters)));
			filteredFhirResource.setSubsetted();
		}
		
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredFhirResource);
		mappingJacksonValue.setFilters(filterProvider);
		mapper.setFilterProvider(filterProvider);
		
		return mappingJacksonValue;
	}
	
	protected int applySearchParameters(Bundle.Builder builder, String uri, Collection<R> fhirResources, SearchRequestParameters parameters) {
		
		Collection<FhirResource> filteredResources = Sets.newHashSet(fhirResources);
		int total = 0;
		
		for (FhirResource fhirResource : filteredResources) {
			applyResponseContentFilter(fhirResource, parameters);
			String resourceUrl = String.format("%s/%s", uri, fhirResource.getId().getIdValue());
			Entry entry = new Entry(new Uri(resourceUrl), fhirResource);
			builder.addEntry(entry);
			total++;
		}
		return total;
	}
	
	protected List<String> getRequestedFields(Collection<String> elements) {
		
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
