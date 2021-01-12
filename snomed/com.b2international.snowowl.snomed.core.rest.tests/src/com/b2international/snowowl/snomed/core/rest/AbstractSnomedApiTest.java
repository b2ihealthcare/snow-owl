/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;

import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.test.commons.rest.AbstractApiTest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import io.restassured.response.ValidatableResponse;

/**
 * @since 7.3
 */
public abstract class AbstractSnomedApiTest extends AbstractApiTest {

	@Override
	protected String getApiBaseUrl() {
		return SnomedApiTestConstants.SCT_API;
	}
	
	protected final ValidatableResponse assertGetConcept(String conceptId, String...expand) {
		return SnomedComponentRestRequests.getComponent(branchPath.getPath(), SnomedComponentType.CONCEPT, conceptId, expand);
	}
	
	protected final ValidatableResponse assertGetConcept(CodeSystemURI codeSystemURI, String conceptId, String...expand) {
		return SnomedComponentRestRequests.getComponent(codeSystemURI.toString(), SnomedComponentType.CONCEPT, conceptId, expand);
	}
	
	protected final SnomedConcept getConcept(String conceptId, String...expand) {
		return assertGetConcept(conceptId, expand)
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
	}
	
	protected final SnomedConcept getConcept(CodeSystemURI codeSystemURI, String conceptId, String...expand) {
		return assertGetConcept(codeSystemURI, conceptId, expand)
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
	}
	
	protected final ValidatableResponse assertGetDescription(String descriptionId, String...expand) {
		return SnomedComponentRestRequests.getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, expand);
	}
	
	protected final SnomedDescription getDescription(String descriptionId, String...expand) {
		return assertGetDescription(descriptionId, expand)
				.statusCode(200)
				.extract()
				.as(SnomedDescription.class);
	}
	
	protected final ValidatableResponse assertGetRelationship(String relationshipId, String...expand) {
		return SnomedComponentRestRequests.getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, expand);
	}
	
	protected final SnomedRelationship getRelationship(String relationshipId, String...expand) {
		return assertGetRelationship(relationshipId, expand)
				.statusCode(200)
				.extract()
				.as(SnomedRelationship.class);
	}
	
	protected final ValidatableResponse assertUpdateConcept(final String path, final String conceptId, Map<String, Object> update) {
		final Map<String, Object> request = Maps.newHashMap(update);
		if (!request.containsKey("commitComment")) {
			request.put("commitComment", "Update Concept");
		}
		return SnomedComponentRestRequests.updateComponent(path, SnomedComponentType.CONCEPT, conceptId, request);
	}
	
	protected final void updateConcept(final String conceptId, Map<String, Object> update) {
		assertUpdateConcept(branchPath.getPath(), conceptId, update).statusCode(204);
	}
	
	protected final void updateConcept(final CodeSystemURI codeSystemURI, String conceptId, Map<String, Object> update) {
		assertUpdateConcept(codeSystemURI.toString(), conceptId, update).statusCode(204);
	}
	
	protected final ValidatableResponse assertUpdateDescription(final String descriptionId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update);
	}
	
	protected final ValidatableResponse assertUpdateDescription(final CodeSystemURI codeSystemURI, final String descriptionId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(codeSystemURI.toString(), SnomedComponentType.DESCRIPTION, descriptionId, update);
	}
	
	protected final ValidatableResponse assertUpdateDescription(final String path, final String descriptionId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(path, SnomedComponentType.DESCRIPTION, descriptionId, update);
	}
	
	protected final void updateDescription(final String descriptionId, Map<String, Object> update) {
		assertUpdateDescription(descriptionId, update).statusCode(204);
	}
	
	protected final ValidatableResponse assertUpdateRelationship(final String relationshipId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update);
	}
	
	protected final void updateRelationship(final String relationshipId, Map<String, Object> update) {
		assertUpdateRelationship(relationshipId, update).statusCode(204);
	}
	
	protected final String createConcept(CodeSystemURI codeSystemURI, ImmutableMap.Builder<String, Object> body) {
		return lastPathSegment(assertCreateConcept(codeSystemURI, body)
					.assertThat()
					.statusCode(201)
					.extract()
					.header("Location"));
	}
	
	protected final String createConcept(IBranchPath branchPath, ImmutableMap.Builder<String, Object> body) {
		return lastPathSegment(assertCreateConcept(branchPath, body)
					.assertThat()
					.statusCode(201)
					.extract()
					.header("Location"));
	}
	
	protected final ValidatableResponse assertCreateConcept(CodeSystemURI codeSystemURI, ImmutableMap.Builder<String, Object> body) {
		final Map<String, Object> request = Maps.newHashMap(body.build());
		if (!request.containsKey("commitComment")) {
			request.put("commitComment", "New Concept");
		}
		return SnomedComponentRestRequests.createComponent(codeSystemURI.toString(), SnomedComponentType.CONCEPT, request);
	}
	
	protected final ValidatableResponse assertCreateConcept(IBranchPath branchPath, ImmutableMap.Builder<String, Object> body) {
		final Map<String, Object> request = Maps.newHashMap(body.build());
		if (!request.containsKey("commitComment")) {
			request.put("commitComment", "New Concept");
		}
		return SnomedComponentRestRequests.createComponent(branchPath, SnomedComponentType.CONCEPT, request);
	}
	
	protected final SnomedConcepts searchConcept(CodeSystemURI codeSystem, Map<String, Object> filters, int limit) {
		final Map<String, Object> params = ImmutableMap.<String, Object>builder().putAll(filters).put("limit", limit).build();
		return SnomedComponentRestRequests.searchComponent(codeSystem.getUri(), SnomedComponentType.CONCEPT, params)
				.assertThat()
				.statusCode(200)
				.extract()
				.as(SnomedConcepts.class);
	}
	
}
