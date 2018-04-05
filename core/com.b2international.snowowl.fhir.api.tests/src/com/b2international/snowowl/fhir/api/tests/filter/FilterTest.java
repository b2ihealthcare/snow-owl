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

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.SummaryParameter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;

/**
 * @since 6.4
 */
public class FilterTest extends FhirTest {
	
	private FilteredClass filteredClass;

	@Before
	public void setupFilter() {
		filteredClass = new FilteredClass("ID123", "Balazs", "Banfai", "Andrassy Ave.");
	}

	@Test
	public void filterElementsTest() throws Exception {

		MultiValueMap<String, String> elements = getParametersMap("_elements=firstName, lastName");

		String[] requestedFields = getRequestedFields(elements, "_elements");
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));

		setupElementsFilter(requestedFields);

		printPrettyJson(filteredClass);
		String jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"firstName\":\"Balazs\",\"lastName\":\"Banfai\",\"id\":\"ID123\"}", jsonString);
		
		setupElementsFilter(null);
		printPrettyJson(filteredClass);
		jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"id\":\"ID123\"}", jsonString);
	}
	
	@Test
	public void summaryFalseTest() throws Exception {

		MultiValueMap<String, String> elements = getParametersMap("_summary=false");

		String[] requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));
		SummaryParameter summaryParameter = SummaryParameter.valueOf(requestedFields[0].toUpperCase());

		setupSummaryFilter(summaryParameter);

		printPrettyJson(filteredClass);
		String jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"firstName\":\"Balazs\",\"lastName\":\"Banfai\","
				+ "\"id\":\"ID123\",\"address\":\"Andrassy Ave.\"}", jsonString);
	}
	
	@Test
	public void summaryTrueTest() throws Exception {

		MultiValueMap<String, String> elements = getParametersMap("_summary=true");

		String[] requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));
		SummaryParameter summaryParameter = SummaryParameter.valueOf(requestedFields[0].toUpperCase());

		setupSummaryFilter(summaryParameter);

		printPrettyJson(filteredClass);
		String jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"id\":\"ID123\",\"address\":\"Andrassy Ave.\"}", jsonString);
	}

	private void setupElementsFilter(String[] requestedFields) {
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredClass);

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		filterProvider.addFilter("TestClassFilter", FhirBeanPropertyFilter.createFilter(requestedFields));
		mappingJacksonValue.setFilters(filterProvider);
		objectMapper.setFilterProvider(filterProvider);
	}
	
	private void setupSummaryFilter(SummaryParameter summaryParameter) {
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredClass);

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		filterProvider.addFilter("TestClassFilter", FhirBeanPropertyFilter.createFilter(summaryParameter));
		mappingJacksonValue.setFilters(filterProvider);
		objectMapper.setFilterProvider(filterProvider);
	}

	private String[] getRequestedFields(MultiValueMap<String, String> elements, String paramName) {
		
		String[] requestedFields = null;
		if (elements.containsKey(paramName)) {
			List<String> returnFields = elements.get(paramName);
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
	private MultiValueMap<String, String> getParametersMap(String paramLine) {
		
		String[] splitParams = paramLine.split("=");
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		List<String> values = Lists.newArrayList(splitParams[0]+"=" + splitParams[1]);
		multiValueMap.put(splitParams[0], values);
		return multiValueMap;
	}
}
