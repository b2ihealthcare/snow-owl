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
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.search.FhirFilterParameter;
import com.b2international.snowowl.fhir.core.search.FhirParameter;
import com.b2international.snowowl.fhir.core.search.SupportedParameter.FhirRequestParameterType;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;
import com.b2international.snowowl.fhir.core.search.RawRequestParameter;
import com.b2international.snowowl.fhir.core.search.SupportedFhirUriParameterDefinitions;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;


/**
 * @since 6.4
 */
public class FhirRequestParameterTest extends FhirTest {
	
	private static SupportedFhirUriParameterDefinitions definitions;

	@BeforeClass
	public static void loadParameterDefinitions() {
		definitions = SupportedFhirUriParameterDefinitions.createDefinitions(CodeSystem.class);
	}
	
	//Raw unprocessed parameter
	@Test
	public void rawRequestParameterTest() {
		
		RawRequestParameter fhirParameter = new RawRequestParameter("_summary:data", ImmutableSet.of("1 ,2", "3"));
		assertThat(fhirParameter.getName(), equalTo("_summary"));
		assertThat(fhirParameter.getValues(), contains("1", "2", "3"));
		assertThat(fhirParameter.getModifier(), equalTo("data"));
	}
	
	//URI -> Raw unprocessed parameter
	@Test
	public void parameterParseTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=1, 2");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter fhirParameter = new RawRequestParameter(key, values);
		assertThat(fhirParameter.getName(), equalTo("_summary"));
		assertThat(fhirParameter.getValues(), contains("1", "2"));
		assertThat(fhirParameter.getModifier(), equalTo(null));
		
		paramMap = convertToMultimap("http://localhost?_text:exact=test");
		key = paramMap.keySet().iterator().next();
		values = paramMap.get(key);
		fhirParameter = new RawRequestParameter(key, values);
		assertThat(fhirParameter.getName(), equalTo("_text"));
		assertThat(fhirParameter.getValues(), contains("test"));
		assertThat(fhirParameter.getModifier(), equalTo("exact"));
	}
	
	//URI->Raw -> unknown param
	@Test
	public void unknownParameterTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("URI parameter unknownparameter is unknown.");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?unknownparameter=true");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter rawParameter = new RawRequestParameter(key, values);
		definitions.classifyParameter(rawParameter);
	}
	
	@Test
	public void unsupportedFilterParameterTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Filter parameter _contained is not supported.");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_contained=true");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter rawParameter = new RawRequestParameter(key, values);
		definitions.classifyParameter(rawParameter);
	}
	
	@Test
	public void unsupportedFilterParameterValueTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Filter parameter value [uknownvalue] is not supported.");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=uknownvalue");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter rawParameter = new RawRequestParameter(key, values);
		definitions.classifyParameter(rawParameter);
	}
	
	//URI->Raw -> filter
	@Test
	public void filterParameterTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=true");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter rawParameter = new RawRequestParameter(key, values);
		FhirParameter fhirParameter = definitions.classifyParameter(rawParameter);
		
		assertThat(fhirParameter.getClass(), equalTo(FhirFilterParameter.class));
		assertThat(fhirParameter.getName(), equalTo("_summary"));
		assertThat(fhirParameter.getType(), equalTo(FhirRequestParameterType.STRING));
		assertThat(fhirParameter.getValues(), contains("true"));
	}
	
	//URI->Raw -> filter
	@Test
	public void searchParameterTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_id=1");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter rawParameter = new RawRequestParameter(key, values);
		FhirParameter fhirParameter = definitions.classifyParameter(rawParameter);
		
		assertThat(fhirParameter.getClass(), equalTo(FhirSearchParameter.class));
		assertThat(fhirParameter.getName(), equalTo("_id"));
		assertThat(fhirParameter.getType(), equalTo(FhirRequestParameterType.STRING));
		assertThat(fhirParameter.getValues(), contains("1"));
	}
	
	@Test
	public void validationTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=data");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		RawRequestParameter fhirParameter = new RawRequestParameter(key, values);
		
		
		
		SupportedFhirUriParameterDefinitions definitions = SupportedFhirUriParameterDefinitions.createDefinitions(CodeSystem.class);
		System.out.println(definitions);
		
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
		assertThat(paramValues, contains("1, 2")); //Not split yet!
		
		paramMap = convertToMultimap("http://localhost?_summary=1&_summary=2");
		paramValues = paramMap.get("_summary");
		assertThat(paramValues, contains("1", "2"));

		paramMap = convertToMultimap("http://localhost?_summary");
		paramValues = paramMap.get("_summary");
		assertThat(paramValues.iterator().next(), equalTo(null)); //Bizarre Spring parameter handling
		
		paramMap = convertToMultimap("http://localhost?_summary=1, 2&_elements=id");
		paramValues = paramMap.get("_summary");
		assertThat(paramValues, contains("1, 2")); //not split!
		paramValues = paramMap.get("_elements");
		assertThat(paramValues, contains("id"));
		
		paramMap = convertToMultimap("http://localhost?_summary=1, 2&_summary=3");
		paramValues = paramMap.get("_summary");
		assertThat(paramValues, contains("1, 2", "3")); //not split!

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
		multiValueMap.keySet().forEach(k -> multiMap.putAll(k, multiValueMap.get(k)));
		return multiMap;
	}
	
	private void printQueryParams(MultiValueMap<String, String> queryParams) {
		Set<String> keySet = queryParams.keySet();
		for (String key : keySet) {
			System.out.println("Key: " + key + ": " + queryParams.get(key));
		}
	}
	
}
