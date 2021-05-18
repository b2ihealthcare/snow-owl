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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.Pair;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.search.FhirFilterParameter;
import com.b2international.snowowl.fhir.core.search.FhirParameter.PrefixedValue;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter.Builder;
import com.b2international.snowowl.fhir.core.search.FhirUriFilterParameterDefinition;
import com.b2international.snowowl.fhir.core.search.FhirUriParameterDefinition.FhirRequestParameterType;
import com.b2international.snowowl.fhir.core.search.FhirUriSearchParameterDefinition.SearchRequestParameterValuePrefix;
import com.b2international.snowowl.fhir.core.search.FhirUriParameterManager;
import com.b2international.snowowl.fhir.core.search.FhirUriSearchParameterDefinition;
import com.b2international.snowowl.fhir.core.search.RawRequestParameter;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;


/**
 * @since 6.4
 */
public class FhirRequestParameterTest extends FhirTest {
	
	private static FhirUriParameterManager parameterManager;
	
	private static Logger LOGGER = LoggerFactory.getLogger(FhirRequestParameterTest.class);

	@BeforeClass
	public static void loadParameterDefinitions() {
		parameterManager = FhirUriParameterManager.createFor(CodeSystem.class);
		LOGGER.info(parameterManager.toString());
	}
	
	@Test
	public void rawRequestParameterTest() {
		
		RawRequestParameter fhirParameter = new RawRequestParameter("_summary:data", ImmutableSet.of("1 ,2", "3"));
		assertThat(fhirParameter.getName(), equalTo("_summary"));
		assertThat(fhirParameter.getValues(), contains("1", "2", "3"));
		assertThat(fhirParameter.getModifier(), equalTo("data"));
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=1, 2");
		String key = paramMap.keySet().iterator().next();
		Collection<String> values = paramMap.get(key);
		
		fhirParameter = new RawRequestParameter(key, values);
		
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
	
	@Test
	public void testPrefixParsing() {
		
		PrefixedValue pv = PrefixedValue.of("");
		assertNull(pv.getPrefix());
		assertThat(pv.getValue(), equalTo(""));
		
		//SearchRequestParameterValuePrefix
		pv = PrefixedValue.of("eq");
		assertThat(pv.getPrefix(), equalTo(SearchRequestParameterValuePrefix.eq));
		assertThat(pv.getValue(), equalTo(""));
		
		pv = PrefixedValue.of("gt2012-01-01");
		assertThat(pv.getPrefix(), equalTo(SearchRequestParameterValuePrefix.gt));
		assertThat(pv.getValue(), equalTo("2012-01-01"));
	}
	
	@Test
	public void invalidDateTimeParseTest() {
		
		//yyyy-mm-ddThh:mm:ss[Z|(+|-)hh:mm]
		exception.expect(FhirException.class);
		exception.expectMessage("Invalid DATETIME type parameter value 'A' are submitted for parameter '_dateTimeParameter'.");
		
		FhirUriSearchParameterDefinition definition = new FhirUriSearchParameterDefinition("_dateTimeParameter", "DATETIME", Collections.emptySet(), false);
		FhirSearchParameter.builder()
				.parameterDefinition(definition)
				.values(ImmutableSet.of(PrefixedValue.of("A"))).build();
	}
	
	/*
	 * yyyy-mm-ddThh:mm:ss[Z|(+|-)hh:mm]
	 * A date, date-time or partial date (e.g. just year or year + month) as used in human communication. 
	 * The format is YYYY, YYYY-MM, YYYY-MM-DD or YYYY-MM-DDThh:mm:ss+zz:zz, e.g. 2018, 1973-06, 1905-08-23, 2015-02-07T13:28:17-05:00 or 2017-01-01T00:00:00.000Z. 
	 * If hours and minutes are specified, a time zone SHALL be populated. 
	 * Seconds must be provided due to schema type constraints but may be zero-filled and may be ignored at receiver discretion. 
	 * Dates SHALL be valid dates. The time "24:00" is not allowed. Leap Seconds are allowed
	 */
	@Test
	public void validDateTimeParseTest() {
		
		String keyString = "_dateTimeParameter";
		FhirUriSearchParameterDefinition definition = new FhirUriSearchParameterDefinition(keyString, "DATETIME", Collections.emptySet(), false);
		Builder parameter = FhirSearchParameter.builder().parameterDefinition(definition);
		
		PrefixedValue value = PrefixedValue.of("2021");
		FhirSearchParameter searchParameter = parameter.values(ImmutableSet.of(value)).build();
		assertThat(searchParameter.getValues().iterator().next(), equalTo(value));
		
		value = PrefixedValue.of("2021-05");
		searchParameter = parameter.values(ImmutableSet.of(value)).build();
		assertThat(searchParameter.getValues().iterator().next(), equalTo(value));
		
		value = PrefixedValue.of("2021-05-09");
		searchParameter = parameter.values(ImmutableSet.of(value)).build();
		assertThat(searchParameter.getValues().iterator().next(), equalTo(value));
		
		value = PrefixedValue.of("2021-05-09T13:24:24-05:00");
		searchParameter = parameter.values(ImmutableSet.of(value)).build();
		assertThat(searchParameter.getValues().iterator().next(), equalTo(value));
		
		value = PrefixedValue.of("2021-05-09T13:24:24.000-05:00");
		searchParameter = parameter.values(ImmutableSet.of(value)).build();
		assertThat(searchParameter.getValues().iterator().next(), equalTo(value));
		
		value = PrefixedValue.of("2021-05-09T13:24:24-0500");
		searchParameter = parameter.values(ImmutableSet.of(value)).build();
		assertThat(searchParameter.getValues().iterator().next(), equalTo(value));

		value = PrefixedValue.of("2021-05-09T13:24:24.000-0500");
		searchParameter = parameter.values(ImmutableSet.of(value)).build();
		assertThat(searchParameter.getValues().iterator().next(), equalTo(value));
		
		value = PrefixedValue.of("2017-01-01T00:00:00.000Z");
		searchParameter = parameter.values(ImmutableSet.of(value)).build();
		assertThat(searchParameter.getValues().iterator().next(), equalTo(value));
	}
	
	@Test
	public void supportedParameterDefinitionsTest() {
		
		FhirUriParameterManager supportedDefinitions = FhirUriParameterManager.createFor(CodeSystem.class);
		
		Map<String, FhirUriFilterParameterDefinition> supportedFilterParameters = supportedDefinitions.getSupportedFilterParameters();
		
		Set<String> supportedFilterKeys = supportedFilterParameters.keySet();
		
		assertFalse(supportedFilterKeys.isEmpty());
		
		Optional<String> summaryFilterOptional = supportedFilterKeys.stream().filter(f -> f.equals(FhirUriFilterParameterDefinition.FhirFilterParameterKey._summary.name())).findFirst();
		assertTrue(summaryFilterOptional.isPresent());
		
		FhirUriFilterParameterDefinition summaryFilterParameter = supportedFilterParameters.get(summaryFilterOptional.get());
		
		assertThat(summaryFilterParameter.getType(), equalTo(FhirRequestParameterType.STRING));
	}
	
	@Test
	public void unknownParameterTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("URI parameter unknownparameter is unknown.");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?unknownparameter=true");
		parameterManager.processParameters(paramMap);
	}
	
	@Test
	public void unsupportedFilterParameterTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Filter parameter _contained is not supported.");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_contained=true");
		parameterManager.processParameters(paramMap);
		
	}
	
	@Test
	public void unsupportedFilterParameterValueTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Filter parameter value [uknownvalue] is not supported.");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=uknownvalue");
		parameterManager.processParameters(paramMap);
	}
	
	@Test
	public void tooManyFilterParameterValuesTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Too many filter parameter values [true, false] are submitted for parameter '_summary'.");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=true, false");
		parameterManager.processParameters(paramMap);
	}
	
	@Test
	public void testInvalidCrossFilterParameters() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Both '_summary' and '_elements' search parameters cannot be specified at the same time.");
		parameterManager.processParameters(convertToMultimap("http://localhost?_summary=true&_elements=1"));
	}
	
	@Test
	public void invalidParameterModifierTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Invalid modifier ");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_lastUpdated:type");
		parameterManager.processParameters(paramMap);
	}
	
	@Test
	public void summaryFilterParameterTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_summary=true");
		Pair<Set<FhirFilterParameter>,Set<FhirSearchParameter>> parameters = parameterManager.processParameters(paramMap);
		assertFalse(parameters.getA().isEmpty());
		assertTrue(parameters.getB().isEmpty());
		
		FhirFilterParameter fhirParameter = parameters.getA().iterator().next();
		assertThat(fhirParameter.getName(), equalTo("_summary"));
		assertThat(fhirParameter.getType(), equalTo(FhirRequestParameterType.STRING));
		assertThat(fhirParameter.getValues(), contains(PrefixedValue.of("true")));
	}
	
	@Test
	public void idSearchParameterTest() {
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_id=1");
		Pair<Set<FhirFilterParameter>,Set<FhirSearchParameter>> parameters = parameterManager.processParameters(paramMap);
	
		assertTrue(parameters.getA().isEmpty());
		assertFalse(parameters.getB().isEmpty());
		
		FhirSearchParameter fhirParameter = parameters.getB().iterator().next();
		
		assertThat(fhirParameter.getClass(), equalTo(FhirSearchParameter.class));
		assertThat(fhirParameter.getName(), equalTo("_id"));
		assertThat(fhirParameter.getType(), equalTo(FhirRequestParameterType.STRING));
		assertThat(fhirParameter.getValues(), contains(PrefixedValue.witoutPrefix("1")));
	}
	
	@Test
	public void invalidDateParameterValueTest() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Invalid DATE type parameter value '1' are submitted for parameter '_lastUpdated'.");
		
		Multimap<String, String> paramMap = convertToMultimap("http://localhost?_lastUpdated=1");
		parameterManager.processParameters(paramMap);
		
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
