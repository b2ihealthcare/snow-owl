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
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Meta}
 * @since 6.3
 */
public class MetaTest extends FhirTest {
	
	private Coding tagCoding = Coding.builder().code("TagA").display("TagDisplay").system("TagSystem").build();
	private Coding securityCoding = Coding.builder().code("A").display("display").system("system").build();
	private Meta meta;
	

	@Before
	public void setup() throws Exception {
		
		DateFormat df = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT);
		Date date = df.parse(TEST_DATE_STRING);
		Instant instant = Instant.builder().instant(date).build();
		
		meta = Meta.builder()
				.id("ID")
				.lastUpdated(instant)
				.versionId("VersionID")
				.addExtension(IntegerExtension.builder().url("ExtensionId").value(1).build())
				.addProfile("profile")
				.addSecurity(securityCoding)
				.addTag(tagCoding)
				.build();
	}
	
	@Test
	public void build() throws Exception {
		
		printPrettyJson(meta);
		
		assertEquals("ID", meta.getId());
		assertEquals(new Id("VersionID"), meta.getVersionId());
		assertEquals(tagCoding, meta.getTags().iterator().next());
		assertEquals(new Uri("profile"), meta.getProfiles().iterator().next());
		assertEquals(securityCoding, meta.getSecurities().iterator().next());
		
	}
	
	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(meta));
		assertThat(jsonPath.getString("versionId"), equalTo("VersionID"));
		assertThat(jsonPath.getString("lastUpdated"), equalTo("2018-03-23T07:49:40Z"));
		assertThat(jsonPath.getString("security[0].code"), equalTo("A"));
		assertThat(jsonPath.getString("tag[0].code"), equalTo("TagA"));
		assertThat(jsonPath.getString("profile[0]"), equalTo("profile"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Meta readMeta = objectMapper.readValue(objectMapper.writeValueAsString(meta), Meta.class);
		
		assertEquals("ID", readMeta.getId());
		assertEquals(new Id("VersionID"), readMeta.getVersionId());
		assertEquals(tagCoding, readMeta.getTags().iterator().next());
		assertEquals(new Uri("profile"), readMeta.getProfiles().iterator().next());
		assertEquals(securityCoding, readMeta.getSecurities().iterator().next());
	}

}
