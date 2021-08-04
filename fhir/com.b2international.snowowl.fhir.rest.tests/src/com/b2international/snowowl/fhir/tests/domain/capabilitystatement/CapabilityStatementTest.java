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
package com.b2international.snowowl.fhir.tests.domain.capabilitystatement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.codesystems.*;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.*;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.usagecontext.QuantityUsageContext;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link CapabilityStatement}
 * @since 8.0.0
 */
public class CapabilityStatementTest extends FhirTest {
	
	private CapabilityStatement capabilityStatement;

	@Before
	public void setup() throws Exception {
		
		capabilityStatement = CapabilityStatement.builder("id")
				.addContact(ContactDetail.builder()
						.name("name")
						.addTelecom(ContactPoint.builder()
								.period(Period.builder().build())
								.rank(1)
								.system("system")
								.value("value")
								.build())
						.build())
				.addIdentifier(Identifier.builder()
				.use(IdentifierUse.OFFICIAL)
						.system(new Uri("www.hl7.org"))
						.value("OID:1234.1234")
						.build())
				.addJurisdiction(CodeableConcept.builder()
					.addCoding(Coding.builder()
							.code("codingCode")
							.display("codingDisplay")
							.build())
					.text("codingText")
					.build())
				.addUseContext(QuantityUsageContext.builder()
						.code(Coding.builder()
								.code("coding")
								.display("codingDisplay")
								.build())
						.value(Quantity.builder()
								.code("valueCode")
								.unit("ms")
								.value(Double.valueOf(1))
								.comparator(QuantityComparator.GREATER_THAN)
								.build())
						.id("usageContextId")
						.build())
				.status(PublicationStatus.ACTIVE)
				.copyright("copyright")
				.date(TEST_DATE_STRING)
				.description("description")
				.kind("kind")
				.fhirVersion("fhirVersion")
				.addFormat(new Code("format"))
				.addPatchFormat(new Code("patchFormat"))
				.addInstantiate(new Uri("instantiate"))
				.addImport(new Uri("import"))
				.software(Software.builder()
						.name("softwareName")
						.version("softwareVersion")
						.releaseDate(FhirDates.parseDate(TEST_DATE_STRING))
						.build())
				.implementation(Implementation.builder()
						.custodian(Reference.builder().reference("reference")
								.build())
						.url("impUrl")
						.description("impDescription")
						.build())
				.addImplementationGuide(new Uri("impGuide"))
				.addRest(Rest.builder()
						.mode(RestfulCapabilityMode.CLIENT)
						.documentation("documentation")
						.security(Security.builder()
								.addService(CodeableConcept.builder()
										.addCoding(Coding.builder()
												.code("serviceCode")
												.display("serviceDisplay")
												.build())
										.text("codingTest")
										.build())
								.cors(true)
								.description("serviceDescription")
								.build())
						.build())
				.addMessaging(Messaging.builder()
						.addEndpoint(Endpoint.builder()
								.address("address")
								.protocol(Coding.builder()
										.code("code")
										.display("display")
										.system("system")
										.build())
								.build())
						.documentation("documentation")
						.reliableCache(1)
						.addSupportedMessage(SupportedMessage.builder()
								.definition("definition")
								.mode(EventCapabilityMode.SENDER)
								.build())
						.build())
				.addDocument(Document.builder()
						.documentation("docDocumentation")
						.mode(DocumentMode.CONSUMER)
						.build())
				.build();
		
		applyFilter(capabilityStatement);
	}
	
	@Test
	public void build() throws Exception {
		validate(capabilityStatement);
	}
	
	private void validate(CapabilityStatement capabilityStatement) {
		assertEquals("id", capabilityStatement.getId().getIdValue());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(capabilityStatement);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(capabilityStatement));
		assertThat(jsonPath.getString("id"), equalTo("id"));
	}
	
	@Test
	public void deserialize() throws Exception {
		CapabilityStatement readCapabilityStatement = objectMapper.readValue(objectMapper.writeValueAsString(capabilityStatement), CapabilityStatement.class);
		validate(readCapabilityStatement);
	}

}
