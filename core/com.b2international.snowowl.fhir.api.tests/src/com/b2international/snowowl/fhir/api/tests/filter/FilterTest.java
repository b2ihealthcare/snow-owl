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
		Identifier identifier = new Identifier(IdentifierUse.OFFICIAL, null, new Uri("www.hl7.org"), "OID:1234.1234");
		
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
	
	@Test
	public void filterIncorrectElementsTest() throws Exception {

		MultiValueMap<String, String> elements = getParametersMap("_elements=f, l");

		String[] requestedFields = getRequestedFields(elements, "_elements");
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));

		setupElementsFilter(requestedFields);

		printPrettyJson(filteredClass);
		String jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"id\":\"ID123\"}", jsonString);
		
		setupElementsFilter(null);
		printPrettyJson(filteredClass);
		jsonString = objectMapper.writeValueAsString(filteredClass);
		assertEquals("{\"id\":\"ID123\"}", jsonString);
	}
	

	@Test
	public void filterElementsTest() throws Exception {

		MultiValueMap<String, String> elements = getParametersMap("_elements=firstName, lastName");

		String[] requestedFields = getRequestedFields(elements, "_elements");
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));

		setupElementsFilter(requestedFields);

		printPrettyJson(codeSystem);
		String jsonString = objectMapper.writeValueAsString(codeSystem);
		String expectedJson = "{\"resourceType\":\"CodeSystem\",\"id\":\"repo/shortName\",\"status\":\"active\"}";
		assertEquals(expectedJson, jsonString);
		
		setupElementsFilter(null);
		printPrettyJson(codeSystem);
		jsonString = objectMapper.writeValueAsString(codeSystem);
		assertEquals(expectedJson, jsonString);
	}
	
	@Test
	public void summaryFalseTest() throws Exception {

		MultiValueMap<String, String> elements = getParametersMap("_summary=false");

		String[] requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));
		SummaryParameter summaryParameter = SummaryParameter.valueOf(requestedFields[0].toUpperCase());

		setupSummaryFilter(summaryParameter);

		printPrettyJson(filteredClass);
		//This is stupid, we should assert parts or use JSONAssert
		String expectedJson = "{\"resourceType\":\"CodeSystem\","
				+ "\"id\":\"repo/shortName\","
				+ "\"language\":\"en\","
				+ "\"text\":{\"status\":\"additional\","
				+ "\"div\":\"<div>Some html text</div>\"},"
				+ "\"url\":\"code system uri\","
				+ "\"identifier\":{\"use\":\"official\",\"system\":\"www.hl7.org\",\"value\":\"OID:1234.1234\"},"
				+ "\"version\":\"2018.01.01\",\"name\":\"Local code system\","
				+ "\"title\":\"title\",\"status\":\"active\",\"description\":\"Code system description\","
				+ "\"hierarchyMeaning\":\"is-a\",\"count\":0,\"property\":[{\"code\":\"child\",\"uri\":\"http://hl7.org/fhir/concept-properties/child\",\"description\":\"Child\",\"type\":\"code\"}],\"concept\":[{\"code\":\"conceptCode\",\"display\":\"Label\",\"definition\":\"This is a code definition\",\"designation\":[{\"language\":\"uk_en\",\"use\":{\"code\":\"internal\",\"system\":\"http://b2i.sg/test\",\"userSelected\":false},\"value\":\"conceptLabel_uk\"}],\"properties\":[[{\"name\":\"code\",\"valueCode\":\"childConcept\"},{\"name\":\"valueCode\",\"valueCode\":\"childId\"}]]}]}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(codeSystem));
	}
	
	@Test
	public void summaryTrueTest() throws Exception {

		MultiValueMap<String, String> elements = getParametersMap("_summary=true");

		String[] requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));
		SummaryParameter summaryParameter = SummaryParameter.fromRequestParameter(requestedFields[0]);

		setupSummaryFilter(summaryParameter);

		printJson(codeSystem);
		String jsonString = objectMapper.writeValueAsString(codeSystem);
		assertEquals("{\"resourceType\":\"CodeSystem\",\"id\":\"repo/shortName\","
				+ "\"url\":\"code system uri\","
				+ "\"identifier\":{\"use\":\"official\","
				+ "\"system\":\"www.hl7.org\",\"value\":\"OID:1234.1234\"},"
				+ "\"version\":\"2018.01.01\",\"name\":\"Local code system\","
				+ "\"title\":\"title\",\"status\":\"active\","
				+ "\"hierarchyMeaning\":\"is-a\",\"property\":[{\"code\":\"child\","
				+ "\"uri\":\"http://hl7.org/fhir/concept-properties/child\","
				+ "\"description\":\"Child\",\"type\":\"code\"}]}", jsonString);
	}
	
	@Test
	public void summaryCountTest() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
				.status(PublicationStatus.ACTIVE)
				.version("2018.01.01")
				.count(12)
				.build();

		MultiValueMap<String, String> elements = getParametersMap("_summary=count");

		String[] requestedFields = getRequestedFields(elements, "_summary");
		System.out.println("Requested fields: " + Arrays.toString(requestedFields));
		SummaryParameter summaryParameter = SummaryParameter.fromRequestParameter(requestedFields[0]);

		setupSummaryFilter(summaryParameter);

		printJson(codeSystem);
		String jsonString = objectMapper.writeValueAsString(codeSystem);
		assertEquals("{\"resourceType\":\"CodeSystem\",\"id\":\"repo/shortName\","
				+ "\"status\":\"active\",\"count\":12}", jsonString);
	}

	private void setupElementsFilter(String[] requestedFields) {
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
