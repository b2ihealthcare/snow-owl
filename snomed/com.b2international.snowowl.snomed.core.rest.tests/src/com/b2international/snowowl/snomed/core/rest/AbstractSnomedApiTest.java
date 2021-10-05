/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;

import java.util.Map;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.test.commons.rest.AbstractApiTest;

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
	
	protected final ValidatableResponse assertGetConcept(ResourceURI codeSystemURI, String conceptId, String...expand) {
		return SnomedComponentRestRequests.getComponent(codeSystemURI.withoutResourceType(), SnomedComponentType.CONCEPT, conceptId, expand);
	}
	
	protected final SnomedConcept getConcept(String conceptId, String...expand) {
		return assertGetConcept(conceptId, expand)
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
	}
	
	protected final SnomedConcept getConcept(ResourceURI codeSystemURI, String conceptId, String...expand) {
		return assertGetConcept(codeSystemURI, conceptId, expand)
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
	}
	
	protected final SnomedDescription getDescription(ResourceURI codeSystemURI, String descriptionId, String...expand) {
		return assertGetDescription(codeSystemURI, descriptionId, expand)
				.statusCode(200)
				.extract()
				.as(SnomedDescription.class);
	}
	
	protected final ValidatableResponse assertGetDescription(ResourceURI codeSystemURI, String descriptionId, String...expand) {
		return SnomedComponentRestRequests.getComponent(codeSystemURI.withoutResourceType(), SnomedComponentType.DESCRIPTION, descriptionId, expand);
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
	
	protected final SnomedRelationship getRelationship(ResourceURI codeSystemURI, String relationshipId, String...expand) {
		return assertGetRelationship(codeSystemURI, relationshipId, expand)
				.statusCode(200)
				.extract()
				.as(SnomedRelationship.class);
	}
	
	protected final ValidatableResponse assertGetRelationship(ResourceURI codeSystemURI, String relationshipId, String...expand) {
		return SnomedComponentRestRequests.getComponent(codeSystemURI.withoutResourceType(), SnomedComponentType.RELATIONSHIP, relationshipId, expand);
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
		return SnomedComponentRestRequests.updateComponent(
			path, 
			SnomedComponentType.CONCEPT, 
			conceptId, 
			Json.assign(
				Json.object("commitComment", "Update Concept"), 
				update
			)
		);
	}
	
	protected final void updateConcept(final String conceptId, Map<String, Object> update) {
		assertUpdateConcept(branchPath.getPath(), conceptId, update).statusCode(204);
	}
	
	protected final void updateConcept(final ResourceURI codeSystemURI, String conceptId, Map<String, Object> update) {
		assertUpdateConcept(codeSystemURI.withoutResourceType(), conceptId, update).statusCode(204);
	}
	
	protected final void updateDescription(final ResourceURI codeSystemURI, String descriptionId, Map<String, Object> update) {
		assertUpdateDescription(codeSystemURI.withoutResourceType(), descriptionId, update).statusCode(204);
	}
	
	protected final void updateDescription(final String descriptionId, Map<String, Object> update) {
		assertUpdateDescription(descriptionId, update).statusCode(204);
	}
	
	protected final ValidatableResponse assertUpdateDescription(final String descriptionId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update);
	}
	
	protected final ValidatableResponse assertUpdateDescription(final String path, final String descriptionId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(path, SnomedComponentType.DESCRIPTION, descriptionId, update);
	}
	
	protected final void updateRelationship(final ResourceURI codeSystemURI, String relationshipId, Map<String, Object> update) {
		assertUpdateRelationship(codeSystemURI.withoutResourceType(), relationshipId, update).statusCode(204);
	}
	
	protected final void updateRelationship(final String relationshipId, Map<String, Object> update) {
		assertUpdateRelationship(relationshipId, update).statusCode(204);
	}
	
	protected final ValidatableResponse assertUpdateRelationship(final String relationshipId, Map<String, Object> update) {
		return assertUpdateRelationship(branchPath.getPath(), relationshipId, update);
	}
	
	protected final ValidatableResponse assertUpdateRelationship(final String path, final String relationshipId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(path, SnomedComponentType.RELATIONSHIP, relationshipId, update);
	}
	
	protected final String createConcept(ResourceURI codeSystemURI, Map<String, Object> body) {
		return assertCreated(assertCreateConcept(codeSystemURI, body));
	}
	
	protected final String createConcept(IBranchPath branchPath, Map<String, Object> body) {
		return assertCreated(assertCreateConcept(branchPath, body));
	}
	
	protected final ValidatableResponse assertCreateConcept(ResourceURI codeSystemURI, Map<String, Object> body) {
		return SnomedComponentRestRequests.createComponent(
			codeSystemURI.withoutResourceType(), 
			SnomedComponentType.CONCEPT, 
			Json.assign(
				Json.object("commitComment", "New Concept"),
				body
			)
		);
	}
	
	protected final ValidatableResponse assertCreateConcept(IBranchPath branchPath, Map<String, Object> body) {
		return SnomedComponentRestRequests.createComponent(
			branchPath, 
			SnomedComponentType.CONCEPT, 
			Json.assign(
				Json.object("commitComment", "New Concept"),
				body
			)
		);
	}
	
	protected final SnomedConcepts searchConcepts(ResourceURI codeSystem, Map<String, Object> filters, int limit) {
		return assertSearchConcepts(codeSystem, filters, limit)
				.assertThat()
				.statusCode(200)
				.extract()
				.as(SnomedConcepts.class);
	}

	protected final ValidatableResponse assertSearchConcepts(ResourceURI codeSystem, Map<String, Object> filters, int limit) {
		return SnomedComponentRestRequests.searchComponent(codeSystem.withoutResourceType(), SnomedComponentType.CONCEPT, Json.assign(filters, Json.object("limit", limit)));
	}
	
	protected final String createDescription(ResourceURI codeSystemURI, Map<String, Object> body) {
		return assertCreated(assertCreateDescription(codeSystemURI, body));
	}
	
	protected final ValidatableResponse assertCreateDescription(ResourceURI codeSystemURI, Map<String, Object> body) {
		return SnomedComponentRestRequests.createComponent(
			codeSystemURI.withoutResourceType(), 
			SnomedComponentType.DESCRIPTION, 
			Json.assign(
				Json.object("commitComment", "New Description"),
				body
			)
		);
	}
	
	protected final String createRelationship(ResourceURI codeSystemURI, Map<String, Object> body) {
		return assertCreated(assertCreateRelationship(codeSystemURI, body));
	}
	
	protected final ValidatableResponse assertCreateRelationship(ResourceURI codeSystemURI, Map<String, Object> body) {
		return SnomedComponentRestRequests.createComponent(
			codeSystemURI.withoutResourceType(), 
			SnomedComponentType.RELATIONSHIP, 
			Json.assign(
				Json.object("commitComment", "New Relationship"),
				body
			)
		);
	}
	
	protected final String createMember(ResourceURI codeSystemURI, Map<String, Object> body) {
		return assertCreated(assertCreateMember(codeSystemURI, body));
	}
	
	protected final ValidatableResponse assertCreateMember(ResourceURI codeSystemURI, Map<String, Object> body) {
		return SnomedComponentRestRequests.createComponent(
			codeSystemURI.withoutResourceType(), 
			SnomedComponentType.MEMBER, 
			Json.assign(
				Json.object("commitComment", "New Member"),
				body
			)	
		);
	}
	
}
