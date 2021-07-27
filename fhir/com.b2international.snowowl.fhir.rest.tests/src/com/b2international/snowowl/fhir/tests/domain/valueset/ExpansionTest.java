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
package com.b2international.snowowl.fhir.tests.domain.valueset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.*;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Expansion}
 * @since 8.0.0
 */
public class ExpansionTest extends FhirTest {
	
	private Expansion expansion;

	@Before
	public void setup() throws Exception {
		
		StringParameter stringParameter = StringParameter.builder()
				.name("stringParamName")
				.value("stringParamValue")
				.build();
				
		UriParameter uriParameter = UriParameter.builder()
			.name("uriParamName")
			.value(new Uri("uriParamValue"))
			.build();
	
		Contains contains = Contains.builder()
			.system("systemUri")
			.isAbstract(true)
			.inactive(false)
			.version("20140131")
			.code("Code")
			.display("displayValue")
			.addDesignation(Designation.builder()
					.language("en-us")
					.value("pt")
					.build())
			.addContains(Contains.builder().build())
			.build();
		
		expansion = Expansion.builder()
				.identifier("identifier")
				.timestamp(TEST_DATE_STRING)
				.total(200)
				.addParameter(stringParameter)
				.addParameter(uriParameter)
				.addContains(contains)
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(expansion);
	}
	
	@SuppressWarnings("rawtypes")
	private void validate(Expansion expansion) {

		assertEquals("identifier", expansion.getIdentifier().getUriValue());
		assertEquals(FhirDates.parseDate(TEST_DATE_STRING), expansion.getTimestamp());
		assertEquals(Integer.valueOf(200), expansion.getTotal());
		
		Collection<Parameter> parameters = expansion.getParameters();
		
		Parameter stringParameter = parameters.stream().filter(p -> p.getType() == FhirDataType.STRING).findFirst().get();
		assertTrue(stringParameter instanceof StringParameter);
		assertEquals("stringParamName", stringParameter.getName());

		Parameter uriParameter = parameters.stream().filter(p -> p.getType() == FhirDataType.URI).findFirst().get();
		assertTrue(uriParameter instanceof UriParameter);
		assertEquals("uriParamName", uriParameter.getName());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(expansion);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(expansion));
		assertThat(jsonPath.getString("identifier"), equalTo("identifier"));
		assertThat(jsonPath.getString("total"), equalTo("200"));
		assertThat(jsonPath.getString("contains[0].system"), equalTo("systemUri"));
		assertThat(jsonPath.getString("parameter[0].name"), containsString("ParamName"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Expansion readExpansion = objectMapper.readValue(objectMapper.writeValueAsString(expansion), Expansion.class);
		validate(readExpansion);
	}

}
