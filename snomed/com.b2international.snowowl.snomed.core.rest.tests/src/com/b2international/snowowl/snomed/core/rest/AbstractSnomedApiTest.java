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

import java.util.Map;

import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
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
		return SnomedComponentRestRequests.getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, expand);
	}
	
	protected final SnomedConcept getConcept(String conceptId, String...expand) {
		return assertGetConcept(conceptId, expand)
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
	
	protected final ValidatableResponse assertUpdateConcept(final String conceptId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, update);
	}
	
	protected final void updateConcept(final String conceptId, Map<String, Object> update) {
		assertUpdateConcept(conceptId, update).statusCode(204);
	}
	
	protected final ValidatableResponse assertUpdateDescription(final String descriptionId, Map<String, Object> update) {
		return SnomedComponentRestRequests.updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update);
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
	
}
