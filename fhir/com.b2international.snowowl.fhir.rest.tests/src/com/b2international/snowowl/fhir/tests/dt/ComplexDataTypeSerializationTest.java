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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.google.common.primitives.Bytes;

import io.restassured.path.json.JsonPath;

/**
 * 
 * Tests for complex data type serialization
 * 
 * @see https://www.hl7.org/fhir/datatypes.html
 * @since 6.6
 */
public class ComplexDataTypeSerializationTest extends FhirTest {
	
	@Test
	public void narrativeTest() throws Exception {
		Narrative narrative = Narrative.builder()
				.div("<div>This is text</div>")
				.status(NarrativeStatus.GENERATED)
				.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(narrative));
		assertThat(jsonPath.getString("status"), equalTo("generated"));
		assertThat(jsonPath.getString("div"), equalTo("<div>This is text</div>"));
	}
	
	@Test
	public void incorrentNarrativeTest() throws Exception {
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
	public void quantityTest() throws Exception {
		Quantity quantity = Quantity.builder()
			.value(12.3)
			.unit("mg")
			.system("uri:LOINC")
			.code("code")
			.comparator(QuantityComparator.GREATER_OR_EQUAL_TO)
			.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(quantity));
		assertThat(jsonPath.getDouble("value"), equalTo(12.3));
		assertThat(jsonPath.getString("comparator"), equalTo(">="));
		assertThat(jsonPath.getString("unit"), equalTo("mg"));
		assertThat(jsonPath.getString("system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("code"), equalTo("code"));
	}
	
	@Test
	public void incorrectSimpleQuantityTest() throws Exception {
		
		Issue expectedIssue = validationErrorIssueBuilder.addLocation("SimpleQuantity.comparator")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'comparator' content is invalid [Code [codeValue=>=]]. Violation: must be null.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		SimpleQuantity.builder()
			.value(12.3)
			.unit("mg")
			.system("uri:LOINC")
			.code("code")
			.comparator(QuantityComparator.GREATER_OR_EQUAL_TO)
			.build();
	}
	
	@Test
	public void rangeTest() throws Exception {
		
		SimpleQuantity low = SimpleQuantity.builder()
			.value(12.3)
			.unit("mg")
			.system("uri:LOINC")
			.code("code1")
			.build();
		
		SimpleQuantity high = (SimpleQuantity) SimpleQuantity.builder()
			.value(120.3)
			.unit("mg")
			.system("uri:LOINC")
			.code("code1")
			.build();
			
		
		Range range = new Range(low, high);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(range));
		assertThat(jsonPath.getDouble("low.value"), equalTo(12.3));
		assertThat(jsonPath.getString("low.unit"), equalTo("mg"));
		assertThat(jsonPath.getString("low.system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("low.code"), equalTo("code1"));
		assertThat(jsonPath.getDouble("high.value"), equalTo(120.3));
		assertThat(jsonPath.getString("high.unit"), equalTo("mg"));
		assertThat(jsonPath.getString("high.system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("high.code"), equalTo("code1"));
	}
	
	@Test
	public void extensionTest() throws Exception {
		
		@SuppressWarnings("rawtypes")
		Extension extension = IntegerExtension.builder().url("url").value(1).build();
		
		String expected = "{\"url\":\"url\",\"valueInteger\":1}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(extension));
	}
	
	@Test
	public void signatureUriTest() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		Signature signature = Signature.builder()
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
	public void signatureReferenceTest() throws Exception {
		
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
	public void incorrectSignatureReferenceTest() throws Exception {
		
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

}
