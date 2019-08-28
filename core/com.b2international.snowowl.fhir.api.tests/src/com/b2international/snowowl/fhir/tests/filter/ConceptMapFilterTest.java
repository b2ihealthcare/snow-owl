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
package com.b2international.snowowl.fhir.tests.filter;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement;
import com.b2international.snowowl.fhir.core.model.conceptmap.Group;
import com.b2international.snowowl.fhir.core.model.conceptmap.Target;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SummaryParameterValue;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;

import io.restassured.path.json.JsonPath;

/**
 * @since 7.2
 */
public class ConceptMapFilterTest extends FhirTest {
	
	private FilteredClass filteredClass;
	
	private ConceptMap conceptMap;

	@Before
	public void setupFilter() {
		filteredClass = new FilteredClass("ID123", "Balazs", "Banfai", "Andrassy Ave.");
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(new Uri("www.hl7.org"))
			.value("OID:1234.1234")
			.build();
		
		conceptMap = ConceptMap.builder("repo/refsetId")
			.description("Code system description")
			.identifier(identifier)
			.language("en")
			.name("Test concept map")
			.narrative(NarrativeStatus.ADDITIONAL, "<div>Some html text</div>")
			.title("Refset name")
			.publisher("B2i")
			.status(PublicationStatus.ACTIVE)
			.url(new Uri("uri://codesystem.uri"))
			.version("2018.01.01")
			.addGroup(Group.builder()
					.source("SNOMEDCT")
					.sourceVersion("SNOMED SOURCE VERSION")
					.target("ATC")
					.targetVersion("ATC TARGET VERSION")
					.addElement(ConceptMapElement.builder()
							.code("1234")
							.display("Source code")
							.addTarget(Target.builder()
									.code("Target code")
									.display("Target term")
									.build())
							.build())
					.build())
			.build();
	}
	
	@Test
	public void summaryFalseTest() throws Exception {

		MultiValueMap<String, String> elements = UriComponentsBuilder.fromHttpUrl("http://localhost?_summary=false").build().getQueryParams();

		List<String> requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + requestedFields);
		SummaryParameterValue summaryParameter = SummaryParameterValue.fromRequestParameter(requestedFields.get(0));

		setupSummaryFilter(summaryParameter);
		printPrettyJson(filteredClass);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(conceptMap));
		assertThat(jsonPath.getString("group"), notNullValue());
	}
	
	@Test
	public void summaryTrueTest() throws Exception {

		MultiValueMap<String, String> elements = UriComponentsBuilder.fromHttpUrl("http://localhost?_summary=true").build().getQueryParams();

		List<String> requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + requestedFields);
		SummaryParameterValue summaryParameter = SummaryParameterValue.fromRequestParameter(requestedFields.get(0));
		System.out.println("Summary parameter: " + summaryParameter);

		setupSummaryFilter(summaryParameter);

		printPrettyJson(conceptMap);
		//String jsonString = objectMapper.writeValueAsString(conceptMap);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(conceptMap));
		assertThat(jsonPath.getString("group"), nullValue());
	}
	
	private void setupSummaryFilter(SummaryParameterValue summaryParameter) {
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredClass);

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(summaryParameter));
		mappingJacksonValue.setFilters(filterProvider);
		objectMapper.setFilterProvider(filterProvider);
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
					requestedParameters.add(element);
				}
			}
		}
		return requestedParameters;
	}


}
