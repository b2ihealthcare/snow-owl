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

import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Period;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for the {@link ContactDetail} data type.
 * @since 8.0.0
 */
public class ContactDetailTest extends FhirTest {
	
	private ContactDetail contactDetail;
	
	@Before
	public void setup() throws Exception {
			
		ContactPoint cp = ContactPoint.builder()
			.period(Period.builder().build())
			.rank(1)
			.system("system")
			.value("value")
			.build();
		
		contactDetail = ContactDetail.builder()
			.name("name")
			.addTelecom(cp)
			.build();
			
	}
	
	@Test
	public void build() {
		validate(contactDetail);
	}
	
	private void validate(ContactDetail contactDetail) {
		assertEquals("name", contactDetail.getName());
		ContactPoint telecom = contactDetail.getTelecoms().iterator().next();
		assertEquals(new Code("system"), telecom.getSystem());
		assertEquals("value", telecom.getValue());
	}

	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(contactDetail));
		assertThat(jsonPath.getString("name"), equalTo("name"));
		jsonPath.setRoot("telecom[0]");
		assertThat(jsonPath.getString("system"), equalTo("system"));
		assertThat(jsonPath.getString("period.start"), equalTo(null));
		assertThat(jsonPath.getString("value"), equalTo("value"));
		assertThat(jsonPath.getInt("rank"), equalTo(1));
		
	}
	
	@Test
	public void deserialize() throws Exception {
		
		ContactDetail readContactDetail = objectMapper.readValue(objectMapper.writeValueAsString(contactDetail), ContactDetail.class);
		validate(readContactDetail);
	}

}
