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

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.Reference;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Binding;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Binding}
 * @since 8.0.0
 */
public class BindingTest extends FhirTest {
	
	private Binding binding;

	@Before
	public void setup() throws Exception {
		
		binding = Binding.builder()
				.id("id")
				.description("bindingDescription")
				.strength("strength")
				.valueSetReference(Reference.builder()
						.reference("reference")
						.build())
				.build();
	}
	
	@Test
	public void buildInvalid() {
		
		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error")
				.addLocation("Binding.valid")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'valid' content is invalid [false]. Violation: Both URI and Reference cannot be set for the 'valueSet' property.")
				.build();
			
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		binding = Binding.builder()
				.id("id")
				.description("bindingDescription")
				.strength("strength")
				.valueSetUri("valueSetUri")
				.valueSetReference(Reference.builder()
						.reference("reference")
						.build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(binding);
	}
	
	private void validate(Binding binding) {
		assertEquals("id", binding.getId());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(binding);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(binding));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("strength"), equalTo("strength"));
		assertThat(jsonPath.getString("description"), equalTo("bindingDescription"));
		assertThat(jsonPath.getString("valueSetReference.reference"), equalTo("reference"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Binding readBinding = objectMapper.readValue(objectMapper.writeValueAsString(binding), Binding.class);
		validate(readBinding);
	}

}
