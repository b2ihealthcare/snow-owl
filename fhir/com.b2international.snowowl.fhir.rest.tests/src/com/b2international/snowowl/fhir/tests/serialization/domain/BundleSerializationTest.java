/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class BundleSerializationTest extends FhirTest {
	
	@Test
	public void bundleTest() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
			.status(PublicationStatus.ACTIVE)
			.name("Local code system")
			.content(CodeSystemContentMode.COMPLETE)
			.url(new Uri("code system uri"))
			.build();
		
		Entry entry = new Entry(new Uri("full Url"), codeSystem);
		
		Bundle bundle = Bundle.builder("bundle_Id?")
			.language("en")
			.total(1)
			.type(BundleType.SEARCHSET)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		applyFilter(bundle);
		
//		printPrettyJson(bundle);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(bundle));
		
		assertThat(jsonPath.getString("resourceType")).isEqualTo("Bundle");
		assertThat(jsonPath.getString("id")).isEqualTo("bundle_Id?");
		assertThat(jsonPath.getString("language")).isEqualTo("en");
		assertThat(jsonPath.getString("type")).isEqualTo("searchset");
		assertThat(jsonPath.getInt("total")).isEqualTo(1);
		
		jsonPath.setRoot("link[0]");
		
		assertThat(jsonPath.getString("relation")).isEqualTo("self");
		assertThat(jsonPath.getString("url")).isEqualTo("http://localhost:8080/snowowl/CodeSystem");
		
		jsonPath.setRoot("entry[0]");
		
		assertThat(jsonPath.getString("fullUrl")).isEqualTo("full Url");
		jsonPath.setRoot("entry[0].resource");
		
		assertThat(jsonPath.getString("resourceType")).isEqualTo("CodeSystem");
		assertThat(jsonPath.getString("id")).isEqualTo("repo/shortName");
		assertThat(jsonPath.getString("url")).isEqualTo("code system uri");
		assertThat(jsonPath.getString("name")).isEqualTo("Local code system");
		assertThat(jsonPath.getString("status")).isEqualTo("active");
		assertThat(jsonPath.getString("content")).isEqualTo("complete");
	}

}
