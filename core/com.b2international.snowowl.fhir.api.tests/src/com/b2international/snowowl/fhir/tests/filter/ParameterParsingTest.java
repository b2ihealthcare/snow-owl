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
package com.b2international.snowowl.fhir.tests.filter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;


/**
 * @since 6.4
 */
public class ParameterParsingTest extends FhirTest {
	
	@Test
	public void convertToGuavaTest() {
		
		MultiValueMap<String,String> queryParams = createQueryParams("http://localhost?_summary=1, 2&_elements=id&_summary=3");
		List<String> requestedFields = getRequestedFields(queryParams, "_summary");
		assertThat(requestedFields, contains("1", "2", "3"));
		requestedFields = getRequestedFields(queryParams, "_elements");
		assertThat(requestedFields, contains("id"));
		
		Multimap<String, String> multiMap = HashMultimap.create();
		queryParams.keySet().forEach(k -> multiMap.putAll(k, queryParams.get(k)));
		multiMap.keySet().forEach(k-> System.out.println(k + " : " + multiMap.get(k)));
		assertThat(multiMap.keySet().size(), equalTo(2));
		assertThat(multiMap.get("_summary"), hasItems("1, 2", "3")); //note, that this is one string
		
	}
	
	@Test
	public void parsingTest() {
		
		MultiValueMap<String,String> queryParams = createQueryParams("http://localhost");
		List<String> requestedFields = getRequestedFields(queryParams, "_summary");
		assertTrue(requestedFields.isEmpty());
		
		queryParams = createQueryParams("http://localhost?_summary=1, 2");
		requestedFields = getRequestedFields(queryParams, "_summary");
		assertThat(requestedFields, contains("1", "2"));
		
		
		queryParams = createQueryParams("http://localhost?_summary=1&_summary=2");
		requestedFields = getRequestedFields(queryParams, "_summary");
		assertThat(requestedFields, contains("1", "2"));
		
		queryParams = createQueryParams("http://localhost?_summary=");
		requestedFields = getRequestedFields(queryParams, "_summary");
		assertTrue(requestedFields.isEmpty());
		
		queryParams = createQueryParams("http://localhost");
		requestedFields = getRequestedFields(queryParams, "_summary");
		assertTrue(requestedFields.isEmpty());
		requestedFields = getRequestedFields(queryParams, "_elements");
		assertTrue(requestedFields.isEmpty());
		
		queryParams = createQueryParams("http://localhost?_summary=1, 2&_elements=id");
		requestedFields = getRequestedFields(queryParams, "_summary");
		assertThat(requestedFields, contains("1", "2"));
		requestedFields = getRequestedFields(queryParams, "_elements");
		assertThat(requestedFields, contains("id"));
		
		queryParams = createQueryParams("http://localhost?property=isActive&property=effectiveTime");
		requestedFields = getRequestedFields(queryParams, "property");
		assertThat(requestedFields, contains("isActive", "effectiveTime"));
		
	}
	
	private MultiValueMap<String, String> createQueryParams(String urlString) {
		return UriComponentsBuilder.fromHttpUrl(urlString)
				.build()
				.getQueryParams();
	}

	private void printQueryParams(MultiValueMap<String, String> queryParams) {
		Set<String> keySet = queryParams.keySet();
		for (String key : keySet) {
			System.out.println("Key: " + key + ": " + queryParams.get(key));
		}
	}
	
	private List<String> getRequestedFields(MultiValueMap<String, String> elements, String paramName) {
		
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
					} else {
						//thow an exception??
					}
				}
			}
		}
		return requestedParameters;
	}

}
