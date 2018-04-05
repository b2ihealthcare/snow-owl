/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests.filter;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;

/**
 * @since 6.4
 */
public class FilterTest extends FhirTest {

	@Test
	public void filterParametersTest() throws Exception {

		FilteredClass filteredClass = new FilteredClass("ID123", "Balazs", "Banfai");
		
		MultiValueMap<String, String> elements = getParametersMap("firstName", "lastName");

		String[] requestedFields = getRequestedFields(elements);
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));

		setupFilters(requestedFields, filteredClass);

		printPrettyJson(filteredClass);
		String jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"firstName\":\"Balazs\",\"lastName\":\"Banfai\"}", jsonString);
		
		elements = getParametersMap("id");
		requestedFields = getRequestedFields(elements);
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));

		setupFilters(requestedFields, filteredClass);
		printPrettyJson(filteredClass);
		jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"id\":\"ID123\"}", jsonString);
	}
	
	
	

	/**
	 * @param filteredClass
	 */
	private void setupFilters(String[] requestedFields, FilteredClass filteredClass) {
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredClass);

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
//		if (requestedFields != null) {
//			filters.addFilter("TestClassFilter", FhirPropertyFilter.filterOutAllExcept(requestedFields));
//		} else {
//			filters.addFilter("TestClassFilter", FhirPropertyFilter.serializeAll());
//		}
		
		filterProvider.addFilter("TestClassFilter", new FhirBeanPropertyFilter(requestedFields));
		// filters.addFilter("Whatever", new AnnotationBasedPropertyFilter());
		mappingJacksonValue.setFilters(filterProvider);

		objectMapper.setFilterProvider(filterProvider);
		
	}

	/**
	 * @param elements 
	 * @return
	 */
	private String[] getRequestedFields(MultiValueMap<String, String> elements) {
		
		String[] requestedFields = null;
		if (elements.containsKey("_elements")) {
			List<String> returnFields = elements.get("_elements");
			returnFields.replaceAll(f -> f.replaceAll(" ", ""));
			for (String element : returnFields) {
				String[] split = element.split("=");
				requestedFields = split[1].split(",");
			}
		}
		return requestedFields;
	}

	/**
	 * @param string
	 * @return
	 */
	private MultiValueMap<String, String> getParametersMap(String ...params) {
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		String requestString = Arrays.stream(params).map(p-> p.toString()).collect(Collectors.joining(", "));

		List<String> values = Lists.newArrayList("_elements=" + requestString);
		multiValueMap.put("_elements", values);
		return multiValueMap;
	}
}
