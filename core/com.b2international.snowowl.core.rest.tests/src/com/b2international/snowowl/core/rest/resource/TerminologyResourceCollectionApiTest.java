/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.resource;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.collection.TerminologyResourceCollectionToolingSupport;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.google.common.collect.ImmutableSortedSet;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 9.0
 */
public class TerminologyResourceCollectionApiTest {

	private static final String CHILD_RESOURCE_TYPE = CodeSystem.RESOURCE_TYPE;

	private static final String COLLECTIONS_API = "/collections";
	
	private static final TerminologyResourceCollectionToolingSupport TOOLING_SUPPORT = new TerminologyResourceCollectionToolingSupport() {
		
		@Override
		public String getToolingId() {
			return SnomedTerminologyComponentConstants.TOOLING_ID;
		}
		
		@Override
		public Set<String> getSupportedChildResourceTypes() {
			return ImmutableSortedSet.of(CHILD_RESOURCE_TYPE);
		}
	}; 
	
	@After
	public void after() {
		// make sure we always unregister the custom tooling support implementation
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).unregister(TOOLING_SUPPORT);
	}
	
	@Test
	public void create_UnsupportedChildResourceType() throws Exception {
		assertTerminologyResourceCollectionCreate(CHILD_RESOURCE_TYPE)
			.statusCode(400);
	}
	
	@Test
	public void create() throws Exception {
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).register(TOOLING_SUPPORT);
		
		var collectionId = createTerminologyResourceCollection(CHILD_RESOURCE_TYPE);
		
		assertTerminologyResourceCollectionGet(collectionId)
			.statusCode(200)
			.body("childResourceType", equalTo(CHILD_RESOURCE_TYPE));
	}
	
	private ValidatableResponse assertTerminologyResourceCollectionCreate(String childResourceType) {
		var collectionId = IDs.base62UUID();
		return givenAuthenticatedRequest(COLLECTIONS_API)
			.contentType(ContentType.JSON)
			.body(Json.object(
				"id", collectionId,
				"title", "Title of " + collectionId,
				"url", collectionId,
				"description", "<div>Markdown supported</div>",
				"toolingId", SnomedTerminologyComponentConstants.TOOLING_ID,
				"oid", "oid_" + collectionId,
				"language", "ENG",
				"owner", "owner",
				"contact", "https://b2ihealthcare.com",
				"childResourceType", childResourceType
			))
			.post()
			.then();
	}
	
	private ValidatableResponse assertTerminologyResourceCollectionGet(String collectionId) {
		return givenAuthenticatedRequest(COLLECTIONS_API)
				.accept(ContentType.JSON)
				.get("/{id}", collectionId)
				.then();
	}
	
	private ValidatableResponse assertTerminologyResourceCollectionSearch(Map<String, Object> filters) {
		return givenAuthenticatedRequest(COLLECTIONS_API)
				.accept(ContentType.JSON)
				.queryParams(filters)
				.get()
				.then();
	}
	
	private String createTerminologyResourceCollection(String childResourceType) {
		return assertCreated(assertTerminologyResourceCollectionCreate(childResourceType));
	}
	
}
