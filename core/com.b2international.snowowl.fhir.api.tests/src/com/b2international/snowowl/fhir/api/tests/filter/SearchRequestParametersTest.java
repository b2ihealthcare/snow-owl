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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SearchRequestParameterKey;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SearchRequestParameterModifier;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SearchRequestParameterValuePrefix;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SummaryParameterValue;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 6.7
 */
public class SearchRequestParametersTest extends FhirTest {
	
	@Test
	public void buildInvalidParameter() {
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		
		SearchRequestParameter.builder()
			.name(SearchRequestParameterKey._id)
			.build();
	}
	
	@Test
	public void testUknownParameter() {
		exception.expect(IllegalArgumentException.class);
		getSearchRequestParameters("http://localhost?dummy=1");
	}
	
	@Test
	public void testId() {
		SearchRequestParameters parameters = getSearchRequestParameters("http://localhost?_id=1");
		assertEquals("1", parameters.getId());
	}
	
	@Test
	public void testSummary() {
		SearchRequestParameters parameters = getSearchRequestParameters("http://localhost?_summary=true");
		assertEquals(SummaryParameterValue.TRUE, parameters.getSummary());
	}
	
	@Test
	public void testInvalidSummary() {
		exception.expect(IllegalArgumentException.class);
		getSearchRequestParameters("http://localhost?_summary=");
	}
	
	@Test
	public void testElement() {
		SearchRequestParameters parameters = getSearchRequestParameters("http://localhost?_elements=1");
		assertEquals("1", parameters.getElements().iterator().next());
	}
	
	@Test
	public void testElements() {
		SearchRequestParameters parameters = getSearchRequestParameters("http://localhost?_elements=1, 2&_elements=3");
		assertThat(parameters.getElements(), hasItems("1", "2", "3"));
	}
	
	@Test
	public void testModifier() {
		SearchRequestParameters parameters = getSearchRequestParameters("http://localhost?_lastUpdated:missing");
		assertThat(parameters.getLastUpdatedParameter().getModifier(), equalTo(SearchRequestParameterModifier.missing));
	}

	@Test
	public void testInvalidModifier() {
		
		exception.expect(FhirException.class);
		exception.expectMessage("Invalid modifier [type] for date/datetime type parameter [_lastUpdated].");
		getSearchRequestParameters("http://localhost?_lastUpdated:type");
	}
	
	@Test
	public void testPrefix() {
		SearchRequestParameters parameters = getSearchRequestParameters("http://localhost?_lastUpdated=gt20120131");
		assertThat(parameters.getLastUpdatedParameter().getPrefix(), equalTo(SearchRequestParameterValuePrefix.gt));
		assertThat(parameters.getLastUpdatedParameter().getValues(), hasItems("20120131"));
	}
	
	@Test
	public void testModifierAndPrefix() {
		SearchRequestParameters parameters = getSearchRequestParameters("http://localhost?_lastUpdated:missing=gt20120131");
		assertThat(parameters.getLastUpdatedParameter().getModifier(), equalTo(SearchRequestParameterModifier.missing));
		assertThat(parameters.getLastUpdatedParameter().getPrefix(), equalTo(SearchRequestParameterValuePrefix.gt));
		assertThat(parameters.getLastUpdatedParameter().getValues(), hasItems("20120131"));
	}
	
	@Test
	public void testInvalidCrossField() {
		exception.expect(FhirException.class);
		getSearchRequestParameters("http://localhost?_summary=true&_elements=1");
	}
	
	private SearchRequestParameters getSearchRequestParameters(String urlString) {
		MultiValueMap<String,String> queryParams = UriComponentsBuilder.fromHttpUrl(urlString)
			.build()
			.getQueryParams();
		
		Multimap<String, String> multiMap = HashMultimap.create();
		queryParams.keySet().forEach(k -> multiMap.putAll(k, queryParams.get(k)));
		return new SearchRequestParameters(multiMap);
	}

}
