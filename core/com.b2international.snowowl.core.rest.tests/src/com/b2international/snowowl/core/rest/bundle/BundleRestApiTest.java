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

import static com.b2international.snowowl.core.rest.BunldeApiAssert.assertBundleCreated;
import static com.b2international.snowowl.core.rest.BunldeApiAssert.assertBundleGet;
import static com.b2international.snowowl.core.rest.BunldeApiAssert.assertBundleSearch;
import static com.b2international.snowowl.core.rest.BunldeApiAssert.assertCreate;
import static com.b2international.snowowl.core.rest.BunldeApiAssert.prepareCreateRequestBody;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @since 8.0
 */
public class BundleRestApiTest {
	
	@Test
	public void createBundleNoId() {
		assertCreate(
				Json.object())
		.statusCode(400).body("violations", hasItem("'id' may not be empty (was 'null')"));
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
		assertBundleGet("b1").and()
			.body("id", equalTo("b1"))
			.assertThat();
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
		assertBundleSearch(Map.of("id", List.of("b2"))).and()
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
	
}
