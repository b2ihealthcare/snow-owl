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
package com.b2international.snowowl.fhir.tests.serialization.dt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
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
	
	private Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
	
	@Test
	public void codingTest() throws Exception {
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://www.whocc.no/atc")
			.version("20180131")
			.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(coding));
		assertThat(jsonPath.getString("code")).isEqualTo("1234");
		assertThat(jsonPath.getString("system")).isEqualTo("http://www.whocc.no/atc");
		assertThat(jsonPath.getString("version")).isEqualTo("20180131");
	}
	
	@Test
	public void invalidSnomedVersionCodingTest() throws Exception {
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		
		Coding.builder()
				.code("1234")
				.system("http://snomed.info/sct")
				.version("20180131")
				.build();
	}
	
	@Test
	public void invalidCodingTest() throws Exception {
		
		Issue expectedIssue = builder.addLocation("Coding.code.codeValue")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code.codeValue' content is invalid []. "
						+ "Violation: must match \"[^\\s]+([\\s]?[^\\s]+)*\".")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Coding.builder()
			.code("")
			.system("http://www.whocc.no/atc")
			.version("20180131")
			.build();
	}
	
	@Test
	public void codeableConceptTest() throws Exception {
		
		Coding coding = Coding.builder()
				.code("1234")
				.system("http://www.whocc.no/atc")
				.version("20180131")
				.build();
		
		CodeableConcept cc = CodeableConcept.builder()
				.addCoding(coding)
				.text("text")
				.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(cc));
		assertThat(jsonPath.getString("text")).isEqualTo("text");
		assertThat(jsonPath.getString("coding[0].code")).isEqualTo("1234");
		assertThat(jsonPath.getString("coding[0].system")).isEqualTo("http://www.whocc.no/atc");
		assertThat(jsonPath.getString("coding[0].version")).isEqualTo("20180131");
		
	}
	
	@Test
	public void narrativeTest() throws Exception {
		Narrative narrative = Narrative.builder()
				.div("<div>This is text</div>")
				.status(NarrativeStatus.GENERATED)
				.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(narrative));
		assertThat(jsonPath.getString("status")).isEqualTo("generated");
		assertThat(jsonPath.getString("div")).isEqualTo("<div>This is text</div>");
	}
	
	@Test
	public void incorrentNarrativeTest() throws Exception {
		Issue expectedIssue = builder.addLocation("Narrative.div")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'div' content is invalid [<div>]. Violation: div content is invalid, minimally should be <div></div>.")
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
		assertThat(jsonPath.getDouble("value")).isEqualTo(12.3);
		assertThat(jsonPath.getString("comparator")).isEqualTo(">=");
		assertThat(jsonPath.getString("unit")).isEqualTo("mg");
		assertThat(jsonPath.getString("system")).isEqualTo("uri:LOINC");
		assertThat(jsonPath.getString("code")).isEqualTo("code");
	}
	
	@Test
	public void incorrectSimpleQuantityTest() throws Exception {
		
		Issue expectedIssue = builder.addLocation("SimpleQuantity.comparator")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'comparator' content is invalid [Code [codeValue=>=]]. Violation: must be null.")
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
	public void periodTest() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		Date startDate = df.parse(TEST_DATE_STRING);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		
		Period period = new Period(startDate, cal.getTime());
		
		String expected = "{\"start\":\"2018-03-23T07:49:40.000+00:00\"," + 
				"\"end\":\"2018-03-24T07:49:40.000+00:00\"}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(period));
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
		assertThat(jsonPath.getDouble("low.value")).isEqualTo(12.3d);
		assertThat(jsonPath.getString("low.unit")).isEqualTo("mg");
		assertThat(jsonPath.getString("low.system")).isEqualTo("uri:LOINC");
		assertThat(jsonPath.getString("low.code")).isEqualTo("code1");
		assertThat(jsonPath.getDouble("high.value")).isEqualTo(120.3);
		assertThat(jsonPath.getString("high.unit")).isEqualTo("mg");
		assertThat(jsonPath.getString("high.system")).isEqualTo("uri:LOINC");
		assertThat(jsonPath.getString("high.code")).isEqualTo("code1");
	}
	
	@Test
	public void referenceTest() throws Exception {
		
		Identifier identifier = Identifier.builder()
			.system("system")
			.build();
		
		Reference reference = new Reference("reference url", identifier, "displayString");
		
		String expected = "{\"reference\":\"reference url\"," + 
				"\"identifier\":{\"system\":\"system\"}," + 
				"\"display\":\"displayString\"}";
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(reference));
		assertThat(jsonPath.getString("reference")).isEqualTo("reference url");
		assertThat(jsonPath.getString("identifier.system")).isEqualTo("system");
		assertThat(jsonPath.getString("display")).isEqualTo("displayString");
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(reference));
	}
	
	@Test
	public void extensionTest() throws Exception {
		
		@SuppressWarnings("rawtypes")
		Extension extension = new IntegerExtension("url", 1);
		
		String expected = "{\"url\":\"url\",\"valueInteger\":1}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(extension));
	}
	
	@Test
	public void contactPointTest() throws Exception {
		
		ContactPoint cp = ContactPoint.builder()
				.id("element_id")
				.addExtension(new IntegerExtension("url", 1))
				.addExtension(new IntegerExtension("url2", 2))
				.period(new Period(null, null))
				.rank(1)
				.system("system")
				.value("value")
				.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(cp));
		assertThat(jsonPath.getString("id")).isEqualTo("element_id");
		assertThat(jsonPath.getString("system")).isEqualTo("system");
		assertThat(jsonPath.getString("value")).isEqualTo("value");
		assertThat(jsonPath.getInt("rank")).isEqualTo(1);
		assertThat(jsonPath.<Date>get("period.start")).isNull();
		assertThat(jsonPath.<Date>get("period.end")).isNull();
		assertThat(jsonPath.getString("extension[0].url")).isEqualTo("url");
		assertThat(jsonPath.getInt("extension[0].valueInteger")).isEqualTo(1);
		assertThat(jsonPath.getString("extension[1].url")).isEqualTo("url2");
		assertThat(jsonPath.getInt("extension[1].valueInteger")).isEqualTo(2);
	}
	
	@Test
	public void identifierTest() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		Date startDate = df.parse(TEST_DATE_STRING);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		
		Identifier refIdentifier = Identifier.builder()
				.system("system")
				.build();
		
		Period period = new Period(startDate, cal.getTime());
		
		Reference reference = new Reference("reference url", refIdentifier, "displayString");
		
		Coding coding = Coding.builder()
			.code("codingCode")
			.display("codingDisplay")
			.build();
		
		CodeableConcept codeableConcept = CodeableConcept.builder()
				.addCoding(coding)
				.text("codingText")
				.build();
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.type(codeableConcept)
			.period(period)
			.system("system")
			.value("value")
			.assigner(reference)
			.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(identifier));
		assertThat(jsonPath.getString("use")).isEqualTo("official");
		assertThat(jsonPath.getString("system")).isEqualTo("system");
		assertThat(jsonPath.getString("value")).isEqualTo("value");
		
		assertThat(jsonPath.getString("type.text")).isEqualTo("codingText");
		assertThat(jsonPath.getString("type.coding[0].code")).isEqualTo("codingCode");
		assertThat(jsonPath.getString("type.coding[0].display")).isEqualTo("codingDisplay");

		assertThat(jsonPath.getString("period.start")).isEqualTo("2018-03-23T07:49:40.000+00:00");
		assertThat(jsonPath.getString("period.end")).isEqualTo("2018-03-24T07:49:40.000+00:00");
		assertThat(jsonPath.getString("assigner.reference")).isEqualTo("reference url");
		assertThat(jsonPath.getString("assigner.display")).isEqualTo("displayString");
		assertThat(jsonPath.getString("assigner.identifier.system")).isEqualTo("system");
	}
	
	@Test
	public void signatureUriTest() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
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
		assertThat(jsonPath.getString("when")).isEqualTo("2018-03-23T07:49:40Z");
		assertThat(jsonPath.getString("whoUri")).isEqualTo("whoUri");
		assertThat(jsonPath.getString("onBehalfOfUri")).isEqualTo("onBehalfUri");
		assertThat(jsonPath.getString("contentType")).isEqualTo("contentTypeCode");
		
		assertThat(jsonPath.getString("type[0].code")).isEqualTo("codingCode");
		assertThat(jsonPath.getString("type[0].display")).isEqualTo("codingDisplay");
		
		List<Byte> blobList = jsonPath.getList("blob");
		byte[] bytes = Bytes.toArray(blobList);
		assertEquals("blob", new String(bytes));

	}
	
	@Test
	public void signatureReferenceTest() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		Signature signature = Signature.builder()
			.addType(Coding.builder().build())
			.contentType(new Code("contentTypeCode"))
			.when(instant)
			.whoReference(new Reference("reference", Identifier.builder().build(), "display"))
			.onBehalfOfUri(new Uri("onBehalfUri"))
			.blob("blob".getBytes())
			.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(signature));
		assertThat(jsonPath.getString("when")).isEqualTo("2018-03-23T07:49:40Z");
		jsonPath.setRoot("whoReference");
		assertThat(jsonPath.getString("reference")).isEqualTo("reference");
		assertThat(jsonPath.getString("identifier.system")).isEqualTo(null);
		assertThat(jsonPath.getString("display")).isEqualTo("display");
	}
	
	@Test
	public void incorrectSignatureReferenceTest() throws Exception {
		
		Issue expectedIssue = builder.addLocation("Signature.valid")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'valid' content is invalid [false]."
						+ " Violation: Either URI or Reference should be set for the 'who' and 'onBehalfOf' fields.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		Signature.builder()
			.addType(Coding.builder().build())
			.contentType(new Code("contentTypeCode"))
			.when(instant)
			.whoUri(new Uri("whoUri"))
			.whoReference(new Reference("reference", Identifier.builder().build(), "display"))
			.onBehalfOfUri(new Uri("onBehalfUri"))
			.blob("blob".getBytes())
			.build();
	}

}
