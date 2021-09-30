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

import com.b2international.snowowl.fhir.core.codesystems.*;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Discriminator;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Slicing;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Slicing}
 * @since 8.0.0
 */
public class SlicingTest extends FhirTest {
	
	private Slicing slicing;

	@Before
	public void setup() throws Exception {
		
		slicing = Slicing.builder()
				.ordered(true)
				.rules(SlicingRules.OPEN)
				.addDiscriminator(Discriminator.builder()
						.id("id")
						.path("path")
						.type(DiscriminatorType.VALUE)
						.build())
				.build();
	}
	
	@Test
	public void buildInvalid() {
		
		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error")
				.addLocation("Slicing.rules")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'rules' content is invalid [null]. Violation: may not be null.")
				.build();
			
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Slicing.builder().build();
	}
	
	@Test
	public void build() throws Exception {
		printPrettyJson(slicing);
		validate(slicing);
	}
	
	private void validate(Slicing slicing) {
		assertEquals(true, slicing.getOrdered());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(slicing);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(slicing));
		assertThat(jsonPath.getBoolean("ordered"), equalTo(true));
		assertThat(jsonPath.getString("rules"), equalTo("open"));
		assertThat(jsonPath.getString("discriminator[0].id"), equalTo("id"));
		assertThat(jsonPath.getString("discriminator[0].type"), equalTo("value"));
		assertThat(jsonPath.getString("discriminator[0].path"), equalTo("path"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Slicing readSlicing = objectMapper.readValue(objectMapper.writeValueAsString(slicing), Slicing.class);
		validate(readSlicing);
	}

}
