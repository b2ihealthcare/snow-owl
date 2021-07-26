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
package com.b2international.snowowl.fhir.tests.domain;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;
/**
 * Test for validating the {@link Instant} model object.
 * @since 8.0.0
 */
public class InstantTest extends FhirTest {
	
	@Test
	public void buildFromDate() throws Exception {
		Date date = Dates.parse(TEST_DATE_STRING, FhirDates.DATE_TIME_FORMAT);
		Instant instant = Instant.builder().instant(date).build();
		assertEquals("\"2018-03-23T07:49:40Z\"", objectMapper.writeValueAsString(instant));
	}

	@Test
	public void serialize() throws JsonProcessingException {
		
		String time = "2021-06-25T19:33:14.520121Z";
		Instant instant = Instant.builder()
				.instant(time)
				.build();
		
		String serializedString = objectMapper.writeValueAsString(instant);
		assertEquals("\""+ time + "\"", serializedString);
	}
	
	@Test
	public void deserialize() throws JsonProcessingException {
		
		String time = "2021-06-25T19:33:14.520121Z";
		Instant instant = Instant.builder()
				.instant(time)
				.build();
		
		String serializedString = objectMapper.writeValueAsString(instant);
		Instant readInstant = objectMapper.readValue(serializedString, Instant.class);
		System.out.println("Expected: " + time + " actual: " + readInstant.getInstant());
		assertEquals(time, readInstant.getInstant());
	}
	
	
}
