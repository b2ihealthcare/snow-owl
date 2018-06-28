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

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.property.CodeConceptProperty;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.SummaryParameter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;

/**
 * @since 6.4
 */
public class FilterTest extends FhirTest {
	
	private FilteredClass filteredClass;
	
	private CodeSystem codeSystem;

	@Before
	public void setupFilter() {
		filteredClass = new FilteredClass("ID123", "Balazs", "Banfai", "Andrassy Ave.");
		
		Identifier identifier = Identifier.builder()
				.use(IdentifierUse.OFFICIAL)
				.system(new Uri("www.hl7.org"))
				.value("OID:1234.1234")
				.build();
		
		codeSystem = CodeSystem.builder("repo/shortName")
				.addProperty(SupportedConceptProperty.builder(CommonConceptProperties.CHILD).build())
				.description("Code system description")
				.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
				.identifier(identifier)
				.language("en")
				.name("Local code system")
				.narrative(NarrativeStatus.ADDITIONAL, "<div>Some html text</div>")
				.title("title")
				.publisher("B2i")
				.status(PublicationStatus.ACTIVE)
				.content(CodeSystemContentMode.COMPLETE)
				.url(new Uri("code system uri"))
				.version("2018.01.01")
				.addConcept(Concept.builder()
					.code("conceptCode")
					.definition("This is a code definition")
					.display("Label")
					.addDesignation(Designation.builder()
						.languageCode("uk_en")
						.use(Coding.builder()
							.code("internal")
							.system("http://b2i.sg/test")
							.build()
							)
						.value("conceptLabel_uk")
						.build())
					.addProperties(CodeConceptProperty.builder()
							.code("childConcept")
							.value(new Code("childId"))
							.build())
					.build())
				.build();
		
	}
	
	//basic MVM capture of query parameters
	@Test
	public void multiValueMapTest() {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost?a=1, 2");
		MultiValueMap<String,String> queryParams = uriComponentsBuilder.build().getQueryParams();
		Set<String> keySet = queryParams.keySet();
		for (String key : keySet) {
			System.out.println("Key: " + key + ": " + queryParams.get(key));
			assertThat(queryParams.get(key), contains("1, 2"));
		}
	}
	
	/*
	 * x, y are incorrect as fields on FilteredClass
	 * only Mandatory fields (id) are returned
	 */
	@Test
	public void filterIncorrectElementsTest() throws Exception {

		MultiValueMap<String, String> elements = UriComponentsBuilder.fromHttpUrl("http://localhost?_elements=x, y").build().getQueryParams();

		List<String> requestedFields = getRequestedFields(elements, "_elements");
		System.out.println("Requested fields: " + requestedFields);

		setupElementsFilter(requestedFields);

		printPrettyJson(filteredClass);
		String jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"id\":\"ID123\"}", jsonString);
		
		//questionable what to do if the requested fields are null or empty, for now we assume it to be a filter for mandatory only elements
		setupElementsFilter(null);
		printPrettyJson(filteredClass);
		jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"id\":\"ID123\"}", jsonString);
	}
	

	@Test
	public void filterElementsTest() throws Exception {

		MultiValueMap<String, String> elements = UriComponentsBuilder.fromHttpUrl("http://localhost?_elements=firstName, lastName").build().getQueryParams();

		List<String> requestedFields = getRequestedFields(elements, "_elements");
		System.out.println("Requested fields: " + requestedFields);

		setupElementsFilter(requestedFields);

		printPrettyJson(codeSystem);
		String jsonString = objectMapper.writeValueAsString(codeSystem);
		
		String expectedJson = "{\"resourceType\":\"CodeSystem\","
							+ "\"id\":\"repo/shortName\","
							+ "\"name\":\"Local code system\","
							+ "\"status\":\"active\","
							+ "\"content\":\"complete\"}";
		
		assertEquals(expectedJson, jsonString);
		
		setupElementsFilter(null);
		printPrettyJson(codeSystem);
		jsonString = objectMapper.writeValueAsString(codeSystem);
		assertEquals(expectedJson, jsonString);
	}
	
	@Test
	public void summaryFalseTest() throws Exception {

		MultiValueMap<String, String> elements = UriComponentsBuilder.fromHttpUrl("http://localhost?_summary=false").build().getQueryParams();

		List<String> requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + requestedFields);
		SummaryParameter summaryParameter = SummaryParameter.fromRequestParameter(requestedFields.get(0));

		setupSummaryFilter(summaryParameter);

		printPrettyJson(filteredClass);
		
		//This is stupid, we should assert parts or use JSONAssert
		String expectedJson = "{\"resourceType\":\"CodeSystem\","
				+ "\"id\":\"repo/shortName\","
				+ "\"language\":\"en\","
				+ "\"text\":{\"status\":\"additional\",\"div\":\"<div>Some html text</div>\"},"
				+ "\"url\":\"code system uri\","
				+ "\"identifier\":"
					+ "{\"use\":\"official\",\"system\":\"www.hl7.org\","
					+ "\"value\":\"OID:1234.1234\"},"
				+ "\"version\":\"2018.01.01\","
				+ "\"name\":\"Local code system\","
				+ "\"title\":\"title\","
				+ "\"status\":\"active\","
				+ "\"publisher\":\"B2i\","
				+ "\"description\":\"Code system description\","
				+ "\"hierarchyMeaning\":\"is-a\","
				+ "\"content\":\"complete\","
				+ "\"property\":"
					+ "[{\"code\":\"child\","
					+ "\"uri\":\"http://hl7.org/fhir/concept-properties/child\""
					+ ",\"description\":\"Child\",\"type\":\"code\"}],"
				+ "\"concept\":"
					+ "[{\"code\":\"conceptCode\","
					+ "\"display\":\"Label\","
					+ "\"definition\":\"This is a code definition\","
					+ "\"designation\":"
						+ "[{\"language\":\"uk_en\","
						+ "\"use\":{\"code\":\"internal\",\"system\":\"http://b2i.sg/test\",\"userSelected\":false},"
					+ "\"value\":\"conceptLabel_uk\","
					+ "\"languageCode\":\"uk_en\"}],"
					+ "\"properties\":[{\"code\":\"childConcept\",\"valueCode\":\"childId\"}]"
					+ "}"
				+ "]}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(codeSystem));
	}
	
	@Test
	public void summaryTrueTest() throws Exception {

		MultiValueMap<String, String> elements = UriComponentsBuilder.fromHttpUrl("http://localhost?_summary=true").build().getQueryParams();

		List<String> requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + requestedFields);
		SummaryParameter summaryParameter = SummaryParameter.fromRequestParameter(requestedFields.get(0));


		setupSummaryFilter(summaryParameter);

		printJson(codeSystem);
		String jsonString = objectMapper.writeValueAsString(codeSystem);
		assertEquals("{\"resourceType\":\"CodeSystem\",\"id\":\"repo/shortName\","
				+ "\"url\":\"code system uri\","
				+ "\"identifier\":"
				+ "{\"use\":\"official\",\"system\":\"www.hl7.org\",\"value\":\"OID:1234.1234\"},"
				+ "\"version\":\"2018.01.01\",\"name\":\"Local code system\","
				+ "\"title\":\"title\","
				+ "\"status\":\"active\","
				+ "\"publisher\":\"B2i\","
				+ "\"hierarchyMeaning\":\"is-a\","
				+ "\"content\":\"complete\","
				+ "\"property\":[{\"code\":\"child\","
					+ "\"uri\":\"http://hl7.org/fhir/concept-properties/child\","
				+ "\"description\":\"Child\",\"type\":\"code\"}]}", jsonString);
	}
	
	@Test
	public void summaryCountTest() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
				.status(PublicationStatus.ACTIVE)
				.content(CodeSystemContentMode.COMPLETE)
				.version("2018.01.01")
				.count(12)
				.build();

		MultiValueMap<String, String> elements = UriComponentsBuilder.fromHttpUrl("http://localhost?_summary=count").build().getQueryParams();

		List<String> requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + requestedFields);
		SummaryParameter summaryParameter = SummaryParameter.fromRequestParameter(requestedFields.get(0));

		setupSummaryFilter(summaryParameter);

		printJson(codeSystem);
		String jsonString = objectMapper.writeValueAsString(codeSystem);
		assertEquals("{\"resourceType\":\"CodeSystem\","
						+ "\"id\":\"repo/shortName\","
						+ "\"status\":\"active\","
						+"\"content\":\"complete\","
						+ "\"count\":12}", jsonString);
	}

	private void setupElementsFilter(List<String> requestedFields) {
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredClass);

		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		filterProvider.addFilter(FhirBeanPropertyFilter.FILTER_NAME, FhirBeanPropertyFilter.createFilter(requestedFields));
		mappingJacksonValue.setFilters(filterProvider);
		objectMapper.setFilterProvider(filterProvider);
	}
	
	private void setupSummaryFilter(SummaryParameter summaryParameter) {
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
