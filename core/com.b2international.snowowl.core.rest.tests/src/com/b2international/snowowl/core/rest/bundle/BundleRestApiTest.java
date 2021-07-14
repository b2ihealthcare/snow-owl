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
package com.b2international.snowowl.core.rest.bundle;

import static com.b2international.snowowl.core.rest.BundleApiAssert.*;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemCreated;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.prepareCodeSystemCreateRequestBody;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @since 8.0
 */
public class BundleRestApiTest {
	
	@Test
	public void createBundleNoId() {
		assertCreate(
				Json.object(
						ResourceDocument.Fields.ID, ""
				)
		).statusCode(400).body("violations", hasItem("'id' may not be empty (was '')"));
	}

	@Test
	public void createBundleNoTitle() {
		assertCreate(
				Json.object(
					ResourceDocument.Fields.ID, "b1"))
		.statusCode(400).body("violations", hasItem("'title' may not be empty (was 'null')"));
	}

	@Test
	public void createBundleNoUrl() {
		assertCreate(
				Json.object(
						ResourceDocument.Fields.ID, "b1",
						ResourceDocument.Fields.TITLE, "Bundle title b2"))
		.statusCode(400).body("violations", hasItem("'url' may not be empty (was 'null')"));
	}
	
	@Test
	public void createBundle() throws JsonProcessingException {
		final Json body = prepareCreateRequestBody("b1");
		assertBundleCreated(body);
		assertBundleGet("b1");
	}
	
	@Test
	public void searchByNotExisting() {
		assertBundleSearch(Map.of("id", "not-existing")).and()
			.body("total", equalTo(0))
			.body("items", empty())
			.assertThat();
	}
	
	@Test
	public void serachBundleById() {
		assertBundleCreated(prepareCreateRequestBody("b2"));
		assertBundleSearch(Map.of("id", Set.of("b2"))).and()
			.body("items", hasItem(hasEntry("id", "b2")))
			.assertThat();
	}

	@Test
	public void serachBundleByTitle() {
		assertBundleCreated(prepareCreateRequestBody("b3").with("title", "Unique bundle title"));
		
		//Exact case insensitive match
		assertBundleSearch(Map.of("title", "unique bundle title")).and()
			.body("items", hasItem(hasEntry("id", "b3")))
			.body("items", hasItem(hasEntry("title", "Unique bundle title")))
			.assertThat();

		//Prefix match
		assertBundleSearch(Map.of("title", "uni bun tit")).and()
			.body("items", hasItem(hasEntry("id", "b3")))
			.body("items", hasItem(hasEntry("title", "Unique bundle title")))
			.assertThat();

		//All term match 
		assertBundleSearch(Map.of("title", "bundle unique title")).and()
			.body("items", hasItem(hasEntry("id", "b3")))
			.body("items", hasItem(hasEntry("title", "Unique bundle title")))
			.assertThat();

		//Boolean prefix match
		assertBundleSearch(Map.of("title", "Unique bundle ti")).and()
			.body("items", hasItem(hasEntry("id", "b3")))
			.body("items", hasItem(hasEntry("title", "Unique bundle title")))
			.assertThat();
	}
	
	@Test
	public void expandBundleResources() {
		final String rootBundleId = "rootBundleId";
		final String subBundleId = "subBundleId";
		
		assertBundleCreated(prepareCreateRequestBody(rootBundleId));
		assertBundleCreated(prepareCreateRequestBody(subBundleId, rootBundleId));
		assertCodeSystemCreated(prepareCodeSystemCreateRequestBody("cs1").with("bundleId", rootBundleId));
		assertCodeSystemCreated(prepareCodeSystemCreateRequestBody("cs2").with("bundleId", rootBundleId));
		assertCodeSystemCreated(prepareCodeSystemCreateRequestBody("cs3").with("bundleId", subBundleId));
		
		assertBundleSearch(Map.of("id", Set.of(rootBundleId), "expand", "resources()")).and()
			.body("total", equalTo(1))
			.body("items[0].resources.total", equalTo(3))
			.body("items[0].resources.items", hasItem(hasEntry("id", "cs1")))
			.body("items[0].resources.items", hasItem(hasEntry("id", "cs2")))
			.body("items[0].resources.items", hasItem(hasEntry("id", subBundleId)))
			.assertThat();
	}
	
	@Test
	public void updateBundleTitle() {
		final String id = "b4";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "title", "new bundle title");
	}

	@Test
	public void updateBundleUrl() {
		final String id = "b5";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "url", "new bundle url");
	}

	@Test
	public void updateBundleLanguage() {
		final String id = "b6";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "language", "hu");
	}
	
	@Test
	public void updateBundleDescription() {
		final String id = "b7";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "description", "Bundle `Hungarian` resources");
	}
	
	@Test
	public void updateBundleStaus() {
		final String id = "b8";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "status", "draft");
	}
	
	@Test
	public void updateBundleCopyright() {
		final String id = "b9";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "copyright", "Licensed under the Fictive License 2.0");
	}
	
	@Test
	public void updateBundleOwner() {
		final String id = "b10";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "owner", "B2i");
	}
	
	@Test
	public void updateBundleContact() {
		final String id = "b11";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "contact", "info@b2international.com");
	}
	
	@Test
	public void updateBundleUsage() {
		final String id = "b12";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "usage", "For testing");
	}
	
	@Test
	public void updateBundlePurpose() {
		final String id = "b13";
		assertBundleCreated(prepareCreateRequestBody(id));
		assertUpdateBundleField(id, "purpose", "Test bundle REST API update endpoint");
	}
	
	@Test
	public void updateBundleBundleId() {
		final String id = "b14";
		final String newBundleId = IDs.base64UUID();
		
		assertBundleCreated(prepareCreateRequestBody(id));
		assertBundleCreated(prepareCreateRequestBody(newBundleId));
		
		assertUpdateBundleField(id, "bundleId", newBundleId);
	}
	
	@Test
	public void updateBundleBundleIdNotExist() {
		final String id = "b15";
		assertBundleCreated(prepareCreateRequestBody(id));
		updateBundle(id, Json.object("bundleId", "not-existings-id"))
			.statusCode(400);
	}
}
