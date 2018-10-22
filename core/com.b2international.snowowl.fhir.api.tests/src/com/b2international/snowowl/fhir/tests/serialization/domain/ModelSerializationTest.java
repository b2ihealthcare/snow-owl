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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
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
		
		String expected = "{\"name\":\"name\","
				+ "\"telecom\":"
					+ "[{\"system\":\"system\","
					+ "\"value\":\"value\","
					+ "\"rank\":1,"
					+ "\"period\":{}}"
					+ "]"
				+ "}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(cd));
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
			.addSecurity(Coding.builder().build())
			.addTag(Coding.builder().build())
			.build();
		
		printPrettyJson(meta);
		
		String expected = "{\"versionId\":\"versionId\","
				+ "\"lastUpdated\":\"2018-03-23T07:49:40Z\","
				+ "\"profile\""
					+ ":[\"profileValue\"],"
				+ "\"security\":[{}],"
				+ "\"tag\":[{}]}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(meta));
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
		
		String expected = "{\"resourceType\":\"OperationOutcome\"," + 
				"\"issue\":[{" + 
					"\"severity\":\"error\"," + 
					"\"code\":\"required\"" + 
					"}]" + 
				"}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(ou));
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
