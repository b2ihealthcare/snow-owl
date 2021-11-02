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
package com.b2international.snowowl.fhir.tests.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.codesystems.ExtensionType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.StringExtension;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Software;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class ExtensionTest extends FhirTest {
	
	@Test
	public void buildInvalid() {
		
		ValidationException exception = assertThrows(ValidationException.class, () -> {
		
			IntegerExtension.builder()
					.url("ID")
					.addExtension(IntegerExtension.builder().url("ID2").value(2).build())
					.value(1)
					.build();
		
		});
		
		assertEquals("1 validation error", exception.getMessage());
		
		Issue expectedIssue = validationErrorIssueBuilder
				.addLocation("IntegerExtension.valid")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'valid' content is invalid [false]. "
						+ "Violation: An extension SHALL have either a value (i.e. a value[x] element) or sub-extensions.")
				.build();
		
		assertThat(exception, FhirExceptionIssueMatcher.issue(expectedIssue));
		
	}
	
	@Test
	public void build() throws Exception {
		
		Extension<Integer> extension = IntegerExtension.builder()
				.url("ID")
				.value(1)
				.build();
		
		printPrettyJson(extension);
		validate(extension);
	}
	
	private void validate(Extension extension) {
		assertEquals(new Uri("ID"), extension.getUrl());
		assertEquals(Integer.valueOf(1), extension.getValue());
		assertEquals(ExtensionType.INTEGER, extension.getExtensionType());
	}
	
	@Test
	public void buildWithSubextension() throws Exception {
		
		Extension<Integer> extension = IntegerExtension.builder()
				.url("ID")
				.addExtension(IntegerExtension.builder().url("ID2").value(2).build())
				.build();
		
		printPrettyJson(extension);
		
		assertEquals(new Uri("ID"), extension.getUrl());
		assertEquals(ExtensionType.INTEGER, extension.getExtensionType());
		Extension subExtension = extension.getExtensions().iterator().next();
		assertEquals(new Uri("ID2"), subExtension.getUrl());
		assertEquals(Integer.valueOf(2), subExtension.getValue());
	}

	@Test
	public void buildWithStringSubextension() throws Exception {
		
		Extension extension = StringExtension.builder()
			.url("topURL")
			.addExtension(StringExtension.builder()
					.url("subURL")
					.value("stringValue")
					.build())
			.build();
		
		printPrettyJson(extension);
		
		assertEquals(new Uri("topURL"), extension.getUrl());
		assertEquals(ExtensionType.STRING, extension.getExtensionType());
		Extension subExtension = extension.getExtensions().iterator().next();
		assertEquals(new Uri("subURL"), subExtension.getUrl());
		assertEquals("stringValue", subExtension.getValue());
	}
	
	@Test
	public void serialize() throws Exception {
		
		Extension<Integer> extension = IntegerExtension.builder().url("ID").value(1).build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(extension));
		
		assertThat(jsonPath.getString("url"), equalTo("ID"));
		assertThat(jsonPath.getInt("valueInteger"), equalTo(1));
	}
	
	
	@Test
	public void deserialize() throws Exception {
		
		Extension<Integer> extension = IntegerExtension.builder().url("ID").value(1).build();
		
		printPrettyJson(extension);
		
		Extension<?> readExtension = objectMapper.readValue(objectMapper.writeValueAsString(extension), Extension.class);
		
		assertTrue(readExtension instanceof IntegerExtension);
		validate(readExtension);
		
	}

}
