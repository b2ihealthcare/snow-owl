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

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.*;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;

/**
 * @since 4.0
 */
public class SnomedRelationshipApiTest extends AbstractSnomedApiTest {

	private static final String TEMPORAL_CONTEXT = "410510008";
	private static final String FINDING_CONTEXT = "408729009";

	@Test
	public void createRelationshipNonExistentBranch() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on a non-existent branch");
		assertComponentCreatedWithStatus(createPath("MAIN/1998-01-31"), SnomedComponentType.RELATIONSHIP, requestBody, 404)
		.and().body("status", equalTo(404));

	}

	@Test
	public void createRelationshipWithNonExistentSource() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody("1", TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship with a non-existent source ID");		
		assertComponentNotCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, requestBody);
	}

	@Test
	public void createRelationshipWithNonexistentType() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, "2", FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on a non-existent branch");		
		assertComponentNotCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, requestBody);
	}

	@Test
	public void createRelationshipWithNonExistentDestination() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, "3", MODULE_SCT_CORE, "New relationship with a non-existent destination ID");		
		assertComponentNotCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, requestBody);
	}

	@Test
	public void createRelationshipWithNonexistentModule() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, "4", "New relationship with a non-existent module ID");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, requestBody);
	}

	@Test
	public void createRelationship() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, requestBody);
		assertCharacteristicType(createMainPath(), relationshipId, CharacteristicType.STATED_RELATIONSHIP);
	}
	
	@Test
	public void createDuplicateRelationship() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, requestBody);
		
		final Map<Object, Object> dupRequestBody = Maps.<Object, Object>newHashMap(requestBody);
		dupRequestBody.put("id", relationshipId);
		dupRequestBody.put("commitComment", "New duplicate relationship on MAIN");
		assertComponentCreatedWithStatus(createMainPath(), SnomedComponentType.RELATIONSHIP, dupRequestBody, 409);
	}

	@Test
	public void createRelationshipInferred() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, CharacteristicType.INFERRED_RELATIONSHIP, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, requestBody);
		assertCharacteristicType(createMainPath(), relationshipId, CharacteristicType.INFERRED_RELATIONSHIP);
	}

	@Test
	public void deleteRelationship() {
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, requestBody);

		assertRelationshipCanBeDeleted(createMainPath(), relationshipId);
		assertRelationshipNotExists(createMainPath(), relationshipId);
	}

	@Test
	public void inactivateRelationship() {
		final Map<?, ?> createRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, createRequestBody);
		assertComponentActive(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, true);

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated relationship")
				.build();

		assertRelationshipCanBeUpdated(createMainPath(), relationshipId, updateRequestBody);
		assertComponentActive(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, false);
	}
	
	@Test
	public void deleteInactiveNonIsaRelationship() throws Exception {
		deleteInactiveRelationship(TEMPORAL_CONTEXT);
	}
	
	@Test
	public void deleteInactiveIsaRelationship() throws Exception {
		deleteInactiveRelationship(IS_A);
	}

	private void deleteInactiveRelationship(final String relationshipType) {
		givenBranchWithPath(testBranchPath);
		// create relationship on active but released concept
		final Map<?, ?> requestBody = givenRelationshipRequestBody(BLEEDING, relationshipType, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, requestBody);
		assertComponentActive(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipId, true);
		
		final Map<String, Object> inactivationBody = newHashMap();
		inactivationBody.put("active", false);
		inactivationBody.put("commitComment", "Inactivated " + BLEEDING);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, BLEEDING, inactivationBody);
		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, BLEEDING, "active", false);
		assertComponentHasProperty(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipId, "active", false);
		
		// try to delete the relationship
		assertRelationshipCanBeDeleted(testBranchPath, relationshipId);
		assertRelationshipNotExists(testBranchPath, relationshipId);
		// assert that the concept is still exists
		assertConceptExists(testBranchPath, BLEEDING);
	}
	
	@Test
	public void changeRelationshipGroup() {
		final Map<?, ?> createRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, createRequestBody);
		assertComponentActive(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, true);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("group", 99)
				.put("commitComment", "Changed group on relationship")
				.build();
		
		assertRelationshipCanBeUpdated(createMainPath(), relationshipId, updateRequestBody);
		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, "group", 99);
	}
	
	@Test
	public void changeRelationshipGroupToInvalidValue() {
		final Map<?, ?> createRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, createRequestBody);
		assertComponentActive(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, true);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("group", 299)
				.put("commitComment", "Changed group on relationship")
				.build();
		
		assertComponentUpdatedWithStatus(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, updateRequestBody, 400);
		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, "group", 0);
	}
	
	@Test
	public void changeRelationshipUnionGroup() {
		final Map<?, ?> createRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, createRequestBody);
		assertComponentActive(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, true);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("unionGroup", 99)
				.put("commitComment", "Changed union group on relationship")
				.build();
		
		assertRelationshipCanBeUpdated(createMainPath(), relationshipId, updateRequestBody);
		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, "unionGroup", 99);
	}
	
	@Test
	public void changeRelationshipCharacteristicType() {
		final Map<?, ?> createRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, createRequestBody);
		assertComponentActive(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, true);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("characteristicType", CharacteristicType.ADDITIONAL_RELATIONSHIP.name())
				.put("commitComment", "Changed characteristic type on relationship")
				.build();
		
		assertRelationshipCanBeUpdated(createMainPath(), relationshipId, updateRequestBody);
		assertCharacteristicType(createMainPath(), relationshipId, CharacteristicType.ADDITIONAL_RELATIONSHIP);
	}
	
	@Test
	@Ignore("Universal relationship modifier concept not present in minified dataset")
	public void changeRelationshipModifier() {
		final Map<?, ?> createRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, createRequestBody);
		assertComponentActive(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, true);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("modifier", RelationshipModifier.UNIVERSAL.name())
				.put("commitComment", "Changed modifier on relationship")
				.build();
		
		assertRelationshipCanBeUpdated(createMainPath(), relationshipId, updateRequestBody);
		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, relationshipId, "modifier", RelationshipModifier.UNIVERSAL.name());
	}

	@Test
	public void createRelationshipOnNestedBranch() {
		SnomedBranchingApiAssert.givenBranchWithPath(testBranchPath);
		final IBranchPath nestedBranchPath = createNestedBranch(testBranchPath, "a", "b");
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(nestedBranchPath, SnomedComponentType.RELATIONSHIP, requestBody);		

		assertRelationshipExists(nestedBranchPath, relationshipId);
		assertRelationshipNotExists(nestedBranchPath.getParent(), relationshipId);
		assertRelationshipNotExists(nestedBranchPath.getParent().getParent(), relationshipId);
		assertRelationshipNotExists(nestedBranchPath.getParent().getParent().getParent(), relationshipId);
	}

	@Test
	public void deleteRelationshipOnNestedBranch() {
		SnomedBranchingApiAssert.givenBranchWithPath(testBranchPath);
		final IBranchPath nestedBranchPath = createNestedBranch(testBranchPath, "a", "b");
		final Map<?, ?> requestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		final String relationshipId = assertComponentCreated(nestedBranchPath, SnomedComponentType.RELATIONSHIP, requestBody);		

		assertRelationshipCanBeDeleted(nestedBranchPath, relationshipId);
		assertRelationshipNotExists(nestedBranchPath, relationshipId);
		assertRelationshipNotExists(nestedBranchPath.getParent(), relationshipId);
		assertRelationshipNotExists(nestedBranchPath.getParent().getParent(), relationshipId);
		assertRelationshipNotExists(nestedBranchPath.getParent().getParent().getParent(), relationshipId);
	}
	
	@Test
	public void createCyclicIsaRelationship_Self() throws Exception {
		final Map<?, ?> body = givenRelationshipRequestBody(DISEASE, IS_A, DISEASE, MODULE_SCT_CORE, "New cyclic ISA relationship");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, body);
	}
	
	@Test
	public void createInactiveNonIsaRelationship() throws Exception {
		createInactiveRelationship(TEMPORAL_CONTEXT);
	}
	
	@Test
	public void createInactiveIsaRelationship() throws Exception {
		createInactiveRelationship(IS_A);
	}

	private void createInactiveRelationship(final String type) {
		SnomedBranchingApiAssert.givenBranchWithPath(testBranchPath);
		final Builder<String, Object> req = ImmutableMap.builder();
		final Map<String, Object> requestBody = givenRelationshipRequestBody(DISEASE, type, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship on MAIN");
		req.putAll(requestBody);
		req.put("active", false);
		final String relationshipId = assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, req.build());
		assertComponentHasProperty(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipId, "active", false);
	}
	
	private static void assertRelationshipCanBeDeleted(final IBranchPath branchPath, final String relationshipId, final String... segments) {
		assertComponentCanBeDeleted(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId);
	}

	private static void assertRelationshipCanBeUpdated(final IBranchPath branchPath, final String relationshipId, final Map<?, ?> requestBody) {
		assertComponentCanBeUpdated(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody);
	}
	
	private static void assertCharacteristicType(final IBranchPath branchPath, final String relationshipId, final CharacteristicType characteristicType) {
		assertComponentHasProperty(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, "characteristicType", characteristicType.name());
	}
	
}
