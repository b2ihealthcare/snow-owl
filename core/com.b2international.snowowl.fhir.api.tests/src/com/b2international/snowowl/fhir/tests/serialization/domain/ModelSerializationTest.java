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
package com.b2international.snowowl.fhir.tests.serialization.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Period;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.jayway.restassured.path.json.JsonPath;

/**
 * 
 * Test for domain model serialization.
 * @since 6.3
 */
public class ModelSerializationTest extends FhirTest {
	
	private Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
	
	@Test
	public void contactDetailTest() throws Exception {
		
		ContactPoint cp = ContactPoint.builder()
			.period(new Period(null, null))
			.rank(1)
			.system("system")
			.value("value")
			.build();
		
		ContactDetail cd = ContactDetail.builder()
			.name("name")
			.addContactPoint(cp)
			.build();
		
		printPrettyJson(cd);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(cd));
		assertThat(jsonPath.getString("name"), equalTo("name"));
		jsonPath.setRoot("telecom[0]");
		assertThat(jsonPath.getString("system"), equalTo("system"));
		assertThat(jsonPath.getString("period.start"), equalTo(null));
		assertThat(jsonPath.getString("value"), equalTo("value"));
		assertThat(jsonPath.getInt("rank"), equalTo(1));
	}
	
	@Test
	public void metaTest() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		Meta meta = Meta.builder()
			.versionId(new Id("versionId"))
			.lastUpdated(instant)
			.addProfile("profileValue")
			.addSecurity(Coding.builder()
					.code("code").build())
			.addTag(Coding.builder()
					.code("tag").build())
			.build();
		
		printPrettyJson(meta);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(meta));
		assertThat(jsonPath.getString("versionId"), equalTo("versionId"));
		assertThat(jsonPath.getString("lastUpdated"), equalTo("2018-03-23T07:49:40Z"));
		assertThat(jsonPath.getString("security[0].code"), equalTo("code"));
		assertThat(jsonPath.getString("tag[0].code"), equalTo("tag"));
		assertThat(jsonPath.getString("profile[0]"), equalTo("profileValue"));
		
	}
	
	@Test
	public void operationOutcomeTest() throws Exception {
		OperationOutcome ou = OperationOutcome.builder()
				.addIssue(Issue.builder()
						.severity(IssueSeverity.ERROR)
						.code(IssueType.REQUIRED)
						.build())
				.build();
		
		printPrettyJson(ou);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(ou));
		assertThat(jsonPath.getString("resourceType"), equalTo("OperationOutcome"));
		jsonPath.setRoot("issue[0]");
		
		assertThat(jsonPath.getString("severity"), equalTo("error"));
		assertThat(jsonPath.getString("code"), equalTo("required"));
	}
	
	@Test
	public void missingIssueTest() throws Exception {
		
		Issue expectedIssue = builder.addLocation("OperationOutcome.issues")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'issues' content is invalid [[]]. Violation: may not be empty.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		OperationOutcome.builder().build();
	}
	
}
