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
package com.b2international.snowowl.fhir.tests.serialization.domain;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class MetaTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void build() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		Coding tagCoding = Coding.builder().code("TagA").display("TagDisplay").system("TagSystem").build();
		Coding securityCoding = Coding.builder().code("A").display("display").system("system").build();
		
		Meta meta = Meta.builder()
				.id("ID")
				.lastUpdated(instant)
				.versionId("VersionID")
				.addExtension(IntegerExtension.builder().id("test/value").value(1).build())
				.addProfile("profile")
				.addSecurity(securityCoding)
				.addTag(tagCoding)
				.build();
		
		printPrettyJson(meta);
		
		assertEquals("ID", meta.getId());
		assertEquals(new Id("VersionID"), meta.getVersionId());
		assertEquals(tagCoding, meta.getTags().iterator().next());
		assertEquals(new Uri("profile"), meta.getProfiles().iterator().next());
		assertEquals(securityCoding, meta.getSecurities().iterator().next());
		
	}
	
	@Test
	public void deserialize() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		Coding tagCoding = Coding.builder().code("TagA").display("TagDisplay").system("TagSystem").build();
		Coding securityCoding = Coding.builder().code("A").display("display").system("system").build();
		
		Meta meta = Meta.builder()
				.id("ID")
				.lastUpdated(instant)
				.versionId("VersionID")
				.addExtension(IntegerExtension.builder().id("test/value").value(1).build())
				.addProfile("profile")
				.addSecurity(securityCoding)
				.addTag(tagCoding)
				.build();
		
		Meta readMeta = objectMapper.readValue(objectMapper.writeValueAsString(meta), Meta.class);
		
		
	}

}
