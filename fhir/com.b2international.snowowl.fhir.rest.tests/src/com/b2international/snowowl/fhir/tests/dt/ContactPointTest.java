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

import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Period;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for the {@link ContactPoint} data type.
 * @since 8.0.0
 */
public class ContactPointTest extends FhirTest {
	
	private ContactPoint contactPoint;
	
	@Before
	public void setup() throws Exception {
			
		contactPoint = ContactPoint.builder()
				.id("element_id")
				.addExtension(IntegerExtension.builder().url("url").value(1).build())
				.addExtension(IntegerExtension.builder().url("url2").value(2).build())
				.period(Period.builder().build())
				.rank(1)
				.system("system")
				.value("value")
				.use(new Code("use"))
				.build();
	}
	
	@Test
	public void build() {
		
		assertEquals("element_id", contactPoint.getId());
		assertEquals(Integer.valueOf(1), contactPoint.getRank());
		assertEquals(new Code("system"), contactPoint.getSystem());
		assertEquals(new Code("use"), contactPoint.getUse());
		assertEquals("value", contactPoint.getValue());
		assertEquals(new Uri("url"), contactPoint.getExtensions().iterator().next().getUrl());
	}
	
	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(contactPoint));
		assertThat(jsonPath.getString("id"), equalTo("element_id"));
		assertThat(jsonPath.getString("system"), equalTo("system"));
		assertThat(jsonPath.getString("value"), equalTo("value"));
		assertThat(jsonPath.getInt("rank"), equalTo(1));
		assertThat(jsonPath.get("period.start"), equalTo(null));
		assertThat(jsonPath.get("period.end"), equalTo(null));
		assertThat(jsonPath.getString("extension[0].url"), equalTo("url"));
		assertThat(jsonPath.getInt("extension[0].valueInteger"), equalTo(1));
		assertThat(jsonPath.getString("extension[1].url"), equalTo("url2"));
		assertThat(jsonPath.getInt("extension[1].valueInteger"), equalTo(2));
		
	}
	
	@Test
	public void deserialize() throws Exception {
		
		ContactPoint readContactPoint = objectMapper.readValue(objectMapper.writeValueAsString(contactPoint), ContactPoint.class);
		
	}

}
