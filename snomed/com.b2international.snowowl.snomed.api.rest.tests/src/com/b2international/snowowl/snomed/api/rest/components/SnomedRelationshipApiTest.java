/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.components;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.CharacteristicType;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @since 2.0
 */
public class SnomedRelationshipApiTest extends AbstractSnomedApiTest {

	private static final String DISEASE = "64572001";
	private static final String TEMPORAL_CONTEXT = "410510008";
	private static final String FINDING_CONTEXT = "408729009";

	private Builder<Object, Object> createRequestBuilder(String sourceId, String typeId, String destinationId, String moduleId, String comment) {
		return ImmutableMap.builder()
				.put("sourceId", sourceId)
				.put("typeId", typeId)
				.put("destinationId", destinationId)
				.put("moduleId", moduleId)
				.put("commitComment", comment);
	}
	
	private Map<?, ?> createRequestBody(String sourceId, String typeId, String destinationId, String moduleId, String comment) {
		return createRequestBuilder(sourceId, typeId, destinationId, moduleId, comment).build();
	}
	
	private Map<?, ?> createRequestBody(String sourceId, String typeId, String destinationId, String moduleId, CharacteristicType characteristicType, String comment) {
		return createRequestBuilder(sourceId, typeId, destinationId, moduleId, comment)
			.put("characteristicType", characteristicType.name())
			.build();
	}
	
	@Test
	public void createRelationshipNonExistentBranch() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, "New relationship on a non-existent branch");
		assertComponentCreationStatus("relationships", requestBody, 404, "MAIN", "1998-01-31") // !
		.and()
			.body("status", equalTo(404));
	}
	
	@Test
	public void createRelationshipWithNonExistentSource() {
		final Map<?, ?> requestBody = createRequestBody("1", TEMPORAL_CONTEXT, FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, "New relationship with a non-existent source ID");		
		assertComponentCanNotBeCreated("relationships", requestBody, "MAIN");
	}
	
	@Test
	public void createRelationshipWithNonexistentType() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "2", FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, "New relationship on a non-existent branch");		
		assertComponentCanNotBeCreated("relationships", requestBody, "MAIN");
	}

	@Test
	public void createRelationshipWithNonExistentDestination() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, "3", Concepts.MODULE_SCT_CORE, "New relationship with a non-existent destination ID");		
		assertComponentCanNotBeCreated("relationships", requestBody, "MAIN");
	}

	@Test
	public void createRelationshipWithNonexistentModule() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, "4", "New relationship with a non-existent module ID");
		assertComponentCanNotBeCreated("relationships", requestBody, "MAIN");
	}

	private void assertCharacteristicType(String relationshipId, CharacteristicType characteristicType) {
		assertComponentHasProperty("relationships", relationshipId, "characteristicType", characteristicType.name(), "MAIN");
	}

	@Test
	public void createRelationship() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, "New relationship on MAIN");
		String relationshipId = assertComponentCanBeCreated("relationships", requestBody, "MAIN");
		assertCharacteristicType(relationshipId, CharacteristicType.STATED_RELATIONSHIP);
	}
	
	@Test
	public void createRelationshipInferred() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, CharacteristicType.INFERRED_RELATIONSHIP, "New relationship on MAIN");
		String relationshipId = assertComponentCanBeCreated("relationships", requestBody, "MAIN");
		assertCharacteristicType(relationshipId, CharacteristicType.INFERRED_RELATIONSHIP);
	}
	
	@Test
	public void deleteRelationship() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, "New relationship on MAIN");
		String relationshipId = assertComponentCanBeCreated("relationships", requestBody, "MAIN");

		assertRelationshipCanBeDeleted(relationshipId, "MAIN");
		assertRelationshipNotExists(relationshipId, "MAIN");
	}

	private void assertRelationshipCanBeDeleted(String relationshipId, String... segments) {
		assertComponentCanBeDeleted("relationships", relationshipId, segments);
	}

	private void assertRelationshipCanBeUpdated(String relationshipId, final Map<?, ?> updateRequestBody, String... segments) {
		assertComponentCanBeUpdated("relationships", relationshipId, updateRequestBody, segments);
	}

	@Test
	public void inactivateRelationship() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, "New relationship on MAIN");
		String relationshipId = assertComponentCanBeCreated("relationships", createRequestBody, "MAIN");
		
		final Map<?, ?> updateRequestBody = ImmutableMap.of(
			"active", false,
			"commitComment", "Inactivated relationship"
		);
		
		assertRelationshipCanBeUpdated(relationshipId, updateRequestBody, "MAIN");
		assertRelationshipActive(relationshipId, false);
	}
	
	private void assertRelationshipActive(String relationshipId, boolean active) {
		assertComponentActive("relationships", relationshipId, active, "MAIN");
	}
	
	@Test
	public void createRelationshipOnNestedBranch() {
		createNestedBranch("a", "b");
		
		final Map<?, ?> requestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, "New relationship on MAIN");
		String relationshipId = assertComponentCanBeCreated("relationships", requestBody, "MAIN", branchName, "a", "b");		
		
		assertRelationshipExists(relationshipId, "MAIN", branchName, "a", "b");
		assertRelationshipNotExists(relationshipId, "MAIN", branchName, "a");
		assertRelationshipNotExists(relationshipId, "MAIN", branchName);
		assertRelationshipNotExists(relationshipId, "MAIN");
	}
	
	@Test
	public void deleteRelationshipOnNestedBranch() {
		createNestedBranch("a", "b");
		
		final Map<?, ?> requestBody = createRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, Concepts.MODULE_SCT_CORE, "New relationship on MAIN");
		String relationshipId = assertComponentCanBeCreated("relationships", requestBody, "MAIN", branchName, "a", "b");		

		assertRelationshipCanBeDeleted(relationshipId, "MAIN", branchName, "a", "b");
	}
}
