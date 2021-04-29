/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.search.RawRequestParameter;
import com.b2international.snowowl.fhir.core.search.SupportedFhirUriParameterDefinitions;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;


/**
 * @since 6.4
 */
public class FhirRequestParameterTest extends FhirTest {
	
	@Test
	public void parameterParseTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=1, 2");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter fhirParameter = new RawRequestParameter(key, values);
		assertThat(fhirParameter.getParameterName(), equalTo("_summary"));
		assertThat(fhirParameter.getParameterValues(), contains("1", "2"));
		assertThat(fhirParameter.getParameterModifier(), equalTo(null));
		
		paramMap = convertToMultimap("http://localhost?_text:exact=test");
		key = paramMap.keySet().iterator().next();
		values = paramMap.get(key);
		fhirParameter = new RawRequestParameter(key, values);
		assertThat(fhirParameter.getParameterName(), equalTo("_text"));
		assertThat(fhirParameter.getParameterValues(), contains("test"));
		assertThat(fhirParameter.getParameterModifier(), equalTo("exact"));
		
	}
	
	@Test
	public void validationTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=data");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter fhirParameter = new RawRequestParameter(key, values);
		
		
		
		SupportedFhirUriParameterDefinitions definitions = SupportedFhirUriParameterDefinitions.createDefinitions(CodeSystem.class);
		
		definitions.classifyParameter(fhirParameter);
		
		definitions.validateFilterParameter(fhirParameter);
		
		
	}
	
	
	//Not really a test case - to check Spring/Guava parameter processing
	@Test
	public void uriParsingTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost");
		assertTrue(paramMap.isEmpty());
		
		paramMap = convertToMultimap("http://localhost?_summary=1, 2");
		Collection<String> paramValues = paramMap.get("_summary");
		assertThat(paramValues, contains("1", "2"));
		
		paramMap = convertToMultimap("http://localhost?_summary=1&_summary=2");
		paramValues = paramMap.get("_summary");
		assertThat(paramValues, contains("1", "2"));

		paramMap = convertToMultimap("http://localhost?_summary");
		paramValues = paramMap.get("_summary");
		assertTrue(paramValues.isEmpty());
		
		paramMap = convertToMultimap("http://localhost?_summary=1, 2&_elements=id");
		paramValues = paramMap.get("_summary");
		assertThat(paramValues, contains("1", "2"));
		paramValues = paramMap.get("_elements");
		assertThat(paramValues, contains("id"));
		
		paramMap = convertToMultimap("http://localhost?_summary=1, 2&_summary=3");
		paramValues = paramMap.get("_summary");
		assertThat(paramValues, contains("1", "2", "3")); //split already

		paramMap = convertToMultimap("http://localhost?property=isActive&property=effectiveTime");
		paramValues = paramMap.get("property");
		assertThat(paramValues, contains("isActive", "effectiveTime"));
	}
	
	/*
	 * Similar code as in BaseFhirResourceRestService
	 */
	private Multimap<String, String> convertToMultimap(final String urlString) {
		
		MultiValueMap<String, String> multiValueMap = UriComponentsBuilder
				.fromHttpUrl(urlString)
				.build()
				.getQueryParams();
		
		Multimap<String, String> multiMap = HashMultimap.create();
		multiValueMap.keySet().forEach(k -> {
			List<String> values = multiValueMap.get(k);
			
			List<String> requestedParameters = Lists.newArrayList();
			for (String element : values) {
				if (StringUtils.isEmpty(element)) continue;
				element = element.replaceAll(" ", "");
				if (element.contains(",")) {
					String requestedFields[] = element.split(",");
					requestedParameters.addAll(Lists.newArrayList(requestedFields));
				} else {
					requestedParameters.add(element);
				}
			}
			multiMap.putAll(k, requestedParameters);
		});
		return multiMap;
	}
	
	private void printQueryParams(MultiValueMap<String, String> queryParams) {
		Set<String> keySet = queryParams.keySet();
		for (String key : keySet) {
			System.out.println("Key: " + key + ": " + queryParams.get(key));
		}
	}
	
}
