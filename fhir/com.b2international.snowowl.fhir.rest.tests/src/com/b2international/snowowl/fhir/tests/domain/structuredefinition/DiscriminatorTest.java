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
package com.b2international.snowowl.fhir.tests.domain.structuredefinition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.DiscriminatorType;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Discriminator;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Discriminator}
 * @since 8.0.0
 */
public class DiscriminatorTest extends FhirTest {
	
	private Discriminator discriminator;

	@Before
	public void setup() throws Exception {
		
		discriminator = Discriminator.builder()
			.id("id")
			.path("path")
			.type(DiscriminatorType.VALUE)
			.build();
	}
	
	@Test
	public void build() throws Exception {
		printPrettyJson(discriminator);
		validate(discriminator);
	}
	
	@Test
	public void buildInvalid() {
		
		exception.expect(ValidationException.class);
		exception.expectMessage("2 validation errors");
		Discriminator.builder().build();
	}
	
	@Test
	public void buildWithMissingField() {
		
		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error")
				.addLocation("Discriminator.path")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'path' content is invalid [null]. Violation: may not be null.")
				.build();
			
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Discriminator.builder().type(DiscriminatorType.EXISTS).build();
	}
	
	private void validate(Discriminator discriminator) {
		assertEquals("id", discriminator.getId());
		assertEquals("path", discriminator.getPath());
		assertEquals(DiscriminatorType.VALUE.getCode(), discriminator.getType());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(discriminator);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(discriminator));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("type"), equalTo("value"));
		assertThat(jsonPath.getString("path"), equalTo("path"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Discriminator readDiscriminator = objectMapper.readValue(objectMapper.writeValueAsString(discriminator), Discriminator.class);
		validate(readDiscriminator);
	}

}
