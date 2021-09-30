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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.google.common.primitives.Bytes;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link e}
 * @since 8.0.0
 */
public class SignatureTest extends FhirTest {
	
	private Signature signature;
	
	@Before
	public void setup() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		signature = Signature.builder()
				.addType(Coding.builder()
						.code("codingCode")
						.display("codingDisplay")
						.build())
				.contentType(new Code("contentTypeCode"))
				.when(instant)
				.whoUri(new Uri("whoUri"))
				.onBehalfOfUri(new Uri("onBehalfUri"))
				.blob("blob".getBytes())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		
		assertEquals(new Uri("whoUri"), signature.getWhoUri());
		assertEquals(new Code("contentTypeCode"), signature.getContentType());
		assertEquals(new Uri("onBehalfUri"), signature.getOnBehalfOfUri());
		//assertEquals(TEST_DATE_STRING, signature.getWhen().getInstant());
		assertArrayEquals("blob".getBytes(), signature.getBlobBytes());
		
	}
	
	@Test
	public void invalidSignatureReferenceTest() throws Exception {
		
		Issue expectedIssue = validationErrorIssueBuilder.addLocation("Signature.valid")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'valid' content is invalid [false]."
						+ " Violation: Either URI or Reference should be set for the 'who' and 'onBehalfOf' fields.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		DateFormat df = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		Signature.builder()
			.addType(Coding.builder().build())
			.contentType(new Code("contentTypeCode"))
			.when(instant)
			.whoUri(new Uri("whoUri"))
			.whoReference(Reference.builder().reference("reference")
					.identifier(Identifier.builder().build())
					.display("display")
					.build())
			.onBehalfOfUri(new Uri("onBehalfUri"))
			.blob("blob".getBytes())
			.build();
	}
	
	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(signature));
		assertThat(jsonPath.getString("when"), equalTo("2018-03-23T07:49:40Z"));
		assertThat(jsonPath.getString("whoUri"), equalTo("whoUri"));
		assertThat(jsonPath.getString("onBehalfOfUri"), equalTo("onBehalfUri"));
		assertThat(jsonPath.getString("contentType"), equalTo("contentTypeCode"));
		
		assertThat(jsonPath.getString("type[0].code"), equalTo("codingCode"));
		assertThat(jsonPath.getString("type[0].display"), equalTo("codingDisplay"));
		
		List<Byte> blobList = jsonPath.getList("blob");
		byte[] bytes = Bytes.toArray(blobList);
		assertEquals("blob", new String(bytes));
	}
	
	@Test
	public void serializeWithReference() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		Signature signature = Signature.builder()
			.addType(Coding.builder().build())
			.contentType(new Code("contentTypeCode"))
			.when(instant)
			.whoReference(Reference.builder().reference("reference")
					.identifier(Identifier.builder().build())
					.display("display")
					.build())
			.onBehalfOfUri(new Uri("onBehalfUri"))
			.blob("blob".getBytes())
			.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(signature));
		assertThat(jsonPath.getString("when"), equalTo("2018-03-23T07:49:40Z"));
		jsonPath.setRoot("whoReference");
		assertThat(jsonPath.getString("reference"), equalTo("reference"));
		assertThat(jsonPath.getString("identifier.system"), equalTo(null));
		assertThat(jsonPath.getString("display"), equalTo("display"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Signature readRange = objectMapper.readValue(objectMapper.writeValueAsString(signature), Signature.class);
	}
}
