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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for the {@link Identifier} data type.
 * @since 8.0.0
 */
public class IdentiferTest extends FhirTest {
	
	Coding coding = Coding.builder()
			.code("codingCode")
			.display("codingDisplay")
			.build();

	CodeableConcept type = CodeableConcept.builder()
			.addCoding(coding)
			.text("codingText")
			.build();

	private Identifier identifier;
	
	@Before
	public void setup() throws Exception {
		DateFormat df = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT);
		Date startDate = df.parse(TEST_DATE_STRING);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		
		Identifier refIdentifier = Identifier.builder()
				.system("system")
				.build();
		
		Period period = Period.builder().start(startDate).end(cal.getTime()).build();
		
		Reference reference = Reference.builder().reference("reference url")
				.identifier(refIdentifier)
				.display("displayString")
				.build();
		
		identifier = Identifier.builder()
				.use(IdentifierUse.OFFICIAL)
				.type(type)
				.period(period)
				.system("system")
				.value("value")
				.assigner(reference)
				.build();
		
	}
	
	@Test
	public void build() {
		assertEquals(IdentifierUse.OFFICIAL.getCode(), identifier.getUse());
		assertEquals(type, identifier.getType());
		assertEquals(new Uri("system"), identifier.getSystem());
		Reference assigner = identifier.getAssigner();
		
		assertEquals("displayString", assigner.getDisplay());
		assertEquals("reference url", assigner.getReference());
	}
	
	@Test
	public void serialize() throws Exception {
		
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(identifier));
		assertThat(jsonPath.getString("use"), equalTo("official"));
		assertThat(jsonPath.getString("system"), equalTo("system"));
		assertThat(jsonPath.getString("value"), equalTo("value"));
		
		assertThat(jsonPath.getString("type.text"), equalTo("codingText"));
		assertThat(jsonPath.getString("type.coding[0].code"), equalTo("codingCode"));
		assertThat(jsonPath.getString("type.coding[0].display"), equalTo("codingDisplay"));

		assertThat(jsonPath.getString("period.start"), equalTo("2018-03-23T07:49:40.000+0000"));
		assertThat(jsonPath.getString("period.end"), equalTo("2018-03-24T07:49:40.000+0000"));
		assertThat(jsonPath.getString("assigner.reference"), equalTo("reference url"));
		assertThat(jsonPath.getString("assigner.display"), equalTo("displayString"));
		assertThat(jsonPath.getString("assigner.identifier.system"), equalTo("system"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Identifier readIdentifier = objectMapper.readValue(objectMapper.writeValueAsString(identifier), Identifier.class);
		
		assertEquals(IdentifierUse.OFFICIAL.getCode(), readIdentifier.getUse());
		assertEquals("codingText", readIdentifier.getType().getText());
		assertEquals(new Uri("system"), readIdentifier.getSystem());
		Reference assigner = readIdentifier.getAssigner();
		
		assertEquals("displayString", assigner.getDisplay());
		assertEquals("reference url", assigner.getReference());
	}

}
