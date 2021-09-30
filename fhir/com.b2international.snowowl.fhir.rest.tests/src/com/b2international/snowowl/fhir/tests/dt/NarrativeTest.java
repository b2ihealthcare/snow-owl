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
package com.b2international.snowowl.fhir.tests.dt;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Narrative}
 * @since 8.0.0
 */
public class NarrativeTest extends FhirTest {
	
	private Narrative narrative;
	
	@Before
	public void setup() throws Exception {
		
		narrative = Narrative.builder()
				.div("<div>This is text</div>")
				.status(NarrativeStatus.GENERATED)
				.build();
	}
	
	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(narrative));
		assertThat(jsonPath.getString("status"), equalTo("generated"));
		assertThat(jsonPath.getString("div"), equalTo("<div>This is text</div>"));
	}
	
	@Test
	public void build() throws Exception {
		
		printPrettyJson(narrative);
		
		assertEquals("<div>This is text</div>", narrative.getDiv());
		assertEquals(NarrativeStatus.GENERATED.getCode(), narrative.getStatus());
		
	}
	
	@Test
	public void invalidNarrative() throws Exception {
		Issue expectedIssue = validationErrorIssueBuilder.addLocation("Narrative.div")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'div' content is invalid [<div>]. Violation: div content is invalid, minimally should be <div></div>.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));

		Narrative.builder()
			.div("<div>")
			.status(NarrativeStatus.GENERATED)
			.build();
	}
	
	
	@Test
	public void deserialize() throws Exception {
		
		Narrative readNarrative = objectMapper.readValue(objectMapper.writeValueAsString(narrative), Narrative.class);
		
		assertEquals("<div>This is text</div>", readNarrative.getDiv());
		assertEquals(NarrativeStatus.GENERATED.getCode(), readNarrative.getStatus());
	}

}
