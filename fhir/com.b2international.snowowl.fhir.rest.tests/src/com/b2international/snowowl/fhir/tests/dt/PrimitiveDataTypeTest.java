/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Tests for primitive data types
 * 
 * @see https://www.hl7.org/fhir/datatypes.html
 * @since 6.6
 */
public class PrimitiveDataTypeTest extends FhirTest {
	
	@Test
	public void serializeCode() throws Exception {
		Code code = new Code("value");
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(code));
	}
	
	@Test
	public void serializeId() throws Exception {
		Id id = new Id("value");
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(id));
	}
	
	@Test
	public void serializeUri() throws Exception {
		Uri uri = new Uri("value");
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(uri));
	}
	
	@Test
	public void buildInvalidIssue() {
		
		ValidationException exception = assertThrows(ValidationException.class, () -> {
			Instant.builder().build();
		});
		
		assertEquals("1 validation error", exception.getMessage());
		
		Issue expectedIssue = validationErrorIssueBuilder
				.addLocation("Instant.instant")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'instant' content is invalid [null]. Violation: may not be null.")
				.build();
		
		assertThat(exception, FhirExceptionIssueMatcher.issue(expectedIssue));
	}
	
	@Test
	public void serializeInstant() throws JsonProcessingException {
		
		String time = "2021-06-25T19:33:14.520121Z";
		Instant instant = Instant.builder()
				.instant(time)
				.build();
		
		String serializedString = objectMapper.writeValueAsString(instant);
		assertEquals("\""+ time + "\"", serializedString);
	}
	
	@Test
	public void deserializeInstant() throws JsonProcessingException {
		
		String time = "2021-06-25T19:33:14.520121Z";
		Instant instant = Instant.builder()
				.instant(time)
				.build();
		
		String serializedString = objectMapper.writeValueAsString(instant);
		Instant readInstant = objectMapper.readValue(serializedString, Instant.class);
		assertEquals(time, readInstant.getInstant());
	}
	
	@Test
	public void buildInstantFromDate() throws Exception {
		Date date = Dates.parse(TEST_DATE_STRING, FhirConstants.DATE_TIME_FORMAT);
		Instant instant = Instant.builder().instant(date).build();
		assertEquals("\"2018-03-23T07:49:40Z\"", objectMapper.writeValueAsString(instant));
	}

}
