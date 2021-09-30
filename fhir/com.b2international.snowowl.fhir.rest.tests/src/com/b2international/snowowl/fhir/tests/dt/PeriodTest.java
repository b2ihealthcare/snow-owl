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

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.dt.Period;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Tests for {@link Period}.
 * @since 8.0.0
 */
public class PeriodTest extends FhirTest {
	
	private Date startDate;
	private Date endDate;
	private Period period;
	
	@Before
	public void setup() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT);
		startDate = df.parse(TEST_DATE_STRING);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		endDate = cal.getTime();
		
		period = Period.builder().start(startDate).end(endDate).build();
	}
	
	@Test
	public void build() throws ParseException {
		
		assertEquals(startDate, period.getStart());
		assertEquals(endDate, period.getEnd());
	}
	
	@Test
	public void serialize() throws Exception {
		
		String expected = "{\"start\":\"2018-03-23T07:49:40.000+0000\"," + 
				"\"end\":\"2018-03-24T07:49:40.000+0000\"}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(period));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Period readPeriod = objectMapper.readValue(objectMapper.writeValueAsString(period), Period.class);
		assertEquals(startDate, readPeriod.getStart());
		assertEquals(endDate, readPeriod.getEnd());
		
	}
}
