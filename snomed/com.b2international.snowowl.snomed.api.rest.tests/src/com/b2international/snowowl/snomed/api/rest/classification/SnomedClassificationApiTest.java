/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.classification;

import static org.junit.Assert.*;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenRelationshipRequestBody;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.classification.ChangeNature;
import com.b2international.snowowl.snomed.api.domain.classification.ClassificationStatus;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jayway.restassured.response.Response;

/**
 * @since 4.6
 */
public class SnomedClassificationApiTest extends AbstractSnomedApiTest {

	@Before
	public void createBranch() {
		givenBranchWithPath(testBranchPath);
	}
	
	@Test
	public void persistInferredRelationship() throws Exception {
		// create a parent concept and a random target concept
		final Map<?, ?> parentBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String parentConcept = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, parentBody);
		final String targetConcept = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, parentBody);
		// add a new stated relationship to the parent concept pointing to the target
		final Map<?, ?> relationshipReq = givenRelationshipRequestBody(parentConcept, Concepts.MORPHOLOGY, targetConcept, Concepts.MODULE_SCT_CORE, "New relationship");
		assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipReq);
		// create a child concept
		final Map<?, ?> body = givenConceptRequestBody(null, parentConcept, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String childConcept = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, body);

		// classify
		final String classificationRunId = waitForClassificationToComplete(testBranchPath.getPath());
		final Multimap<String, Map<String, Object>> relationshipChangesBySourceId = classify(testBranchPath.getPath(), classificationRunId);
		
		// verify a new inferred relationship on child
		final Collection<Map<String, Object>> parentInferredRelationships = relationshipChangesBySourceId.get(parentConcept);
		final Collection<Map<String, Object>> childInferredRelationships = relationshipChangesBySourceId.get(childConcept);
		// parent concept should have two inferred relationships, one ISA and one MORPHOLOGY, both inferred
		assertEquals(2, parentInferredRelationships.size());
		// child concept should have two inferred relationships, one ISA and one MORPHOLOGY from parent, both inferred
		assertEquals(2, childInferredRelationships.size());
		
		// verify inferred relationships for parent
		for (Map<String, Object> relationshipChange : parentInferredRelationships) {
			assertEquals(ChangeNature.INFERRED.name(), relationshipChange.get("changeNature"));
			switch ((String) relationshipChange.get("typeId")) {
			case Concepts.IS_A:
				assertEquals(Concepts.ROOT_CONCEPT, relationshipChange.get("destinationId"));
				break;
			case Concepts.MORPHOLOGY:
				assertEquals(targetConcept, relationshipChange.get("destinationId"));
				break;
			}
		}
		
		// verify inferred relationships for parent
		for (Map<String, Object> relationshipChange : childInferredRelationships) {
			assertEquals(ChangeNature.INFERRED.name(), relationshipChange.get("changeNature"));
			switch ((String) relationshipChange.get("typeId")) {
			case Concepts.IS_A:
				assertEquals(parentConcept, relationshipChange.get("destinationId"));
				break;
			case Concepts.MORPHOLOGY:
				assertEquals(targetConcept, relationshipChange.get("destinationId"));
				break;
			}
		}
		
		// run and save the classification
		waitForClassificationSaveToComplete(testBranchPath.getPath(), classificationRunId);
		
		assertEquals(2, getInferredRelationshipCount(parentConcept));
		assertEquals(2, getInferredRelationshipCount(childConcept));
	}

	private int getInferredRelationshipCount(final String conceptId) {
		final List<Map<String, Object>> relationships = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, conceptId, "relationships(\"active\":true)")
			.and().extract().jsonPath()
			.getList("relationships.items");
		
		int inferredRelationships = 0;
		for (Map<String, Object> relationship : relationships) {
			if (relationship.get("characteristicType").equals(CharacteristicType.INFERRED_RELATIONSHIP.toString())) {
				inferredRelationships++;
			}
		}
		return inferredRelationships;
	}
	
	private Multimap<String, Map<String, Object>> classify(String branch, String classificationRunId) throws Exception {
		// get relationship changes
		final Collection<Map<String, Object>> items = RestExtensions.get(SnomedApiTestConstants.SCT_API, branch, "classifications", classificationRunId, "relationship-changes").body().path("items");
		// index all relationship changes by their source ID
		return Multimaps.index(items, new Function<Map<String, Object>, String>() {
			@Override
			public String apply(Map<String, Object> input) {
				return (String) input.get("sourceId");
			}
		});
	}

	private String waitForClassificationToComplete(String branch) throws InterruptedException {
		final Map<String, Object> classifyReq = ImmutableMap.<String, Object>of("reasonerId", SnomedCoreConfiguration.ELK_REASONER_ID); 
		final Response classificationCreated = RestExtensions.postJson(SnomedApiTestConstants.SCT_API, classifyReq, branch, "classifications");
		classificationCreated.then().statusCode(201);
		final String classificationLocationHeader = RestExtensions.location(classificationCreated);
		final String classificationRunId = RestExtensions.lastPathSegment(classificationLocationHeader);
		// wait for classification to complete, but no more than 1 min
		ClassificationStatus classificationStatus;
		do {
			Thread.sleep(500);
			classificationStatus = ClassificationStatus.valueOf(RestExtensions.get(SnomedApiTestConstants.SCT_API, branch, "classifications", classificationRunId).body().<String>path("status"));
		} while(ClassificationStatus.RUNNING == classificationStatus || ClassificationStatus.SCHEDULED == classificationStatus);
		assertEquals(ClassificationStatus.COMPLETED, classificationStatus);
		return classificationRunId;
	}

	private String waitForClassificationSaveToComplete(String branch, String classificationRunId) throws InterruptedException {
		final Map<String, Object> persistRequest = ImmutableMap.<String, Object>of("status", ClassificationStatus.SAVED.toString()); 
		final Response persistResponse = RestExtensions.putJson(SnomedApiTestConstants.SCT_API, persistRequest, branch, "classifications", classificationRunId);
		persistResponse.then().statusCode(204);
		// wait for persisting relationships to complete, but no more than 1 min
		ClassificationStatus classificationStatus;
		do {
			Thread.sleep(500);
			classificationStatus = ClassificationStatus.valueOf(RestExtensions.get(SnomedApiTestConstants.SCT_API, branch, "classifications", classificationRunId).body().<String>path("status"));
		} while(ClassificationStatus.SAVING_IN_PROGRESS == classificationStatus);
		assertEquals(ClassificationStatus.SAVED, classificationStatus);
		return classificationRunId;
	}
	
	@Test
	public void persistRedundantRelationship() throws Exception {
		// create a parent concept
		final Map<?, ?> conceptReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String concept = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptReq);
		// add two new relationship to the parent as inferred 
		final Map<?, ?> relationshipReq = givenRelationshipRequestBody(concept, Concepts.IS_A, ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, CharacteristicType.INFERRED_RELATIONSHIP, "New inferred relationship");
		final String relationship1Id = assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipReq);
		final String relationship2Id = assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipReq);
		assertComponentExists(testBranchPath, SnomedComponentType.RELATIONSHIP, relationship1Id).body("id", equalTo(relationship1Id));
		assertComponentExists(testBranchPath, SnomedComponentType.RELATIONSHIP, relationship2Id).body("id", equalTo(relationship2Id));
		
		final String classificationRunId = waitForClassificationToComplete(testBranchPath.getPath());
		final Multimap<String, Map<String, Object>> relationshipChangesBySourceId = classify(testBranchPath.getPath(), classificationRunId);
		
		final Collection<Map<String, Object>> conceptRelationshipChanges = relationshipChangesBySourceId.get(concept);
		assertEquals(1, conceptRelationshipChanges.size());
		final Map<String, Object> relationshipChange = Iterables.getOnlyElement(conceptRelationshipChanges);
		assertEquals(ChangeNature.REDUNDANT.name(), relationshipChange.get("changeNature"));
		
		// save the classification
		waitForClassificationSaveToComplete(testBranchPath.getPath(), classificationRunId);
	}
	
	@Test
	public void issue_SO_2152_testGroupRenumbering() throws Exception {
		// create a new concept
		final Map<?, ?> conceptReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptReq);

		// add new relationship to the root as stated, add the same relationship with a different group to the new concept as inferred
		// "associated morphology = SNOMED CT Core module"
		final Map<String, Object> statedRelationshipReq = newHashMap(givenRelationshipRequestBody(ROOT_CONCEPT, 
				Concepts.MORPHOLOGY, 
				Concepts.MODULE_SCT_CORE, 
				Concepts.MODULE_SCT_CORE, 
				CharacteristicType.STATED_RELATIONSHIP, 
				"New stated relationship"));
		
		statedRelationshipReq.put("group", 0);
		assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, statedRelationshipReq);
		
		final Map<String, Object> inferredRelationshipReq = newHashMap(givenRelationshipRequestBody(conceptId, 
				Concepts.MORPHOLOGY, 
				Concepts.MODULE_SCT_CORE, 
				Concepts.MODULE_SCT_CORE, 
				CharacteristicType.INFERRED_RELATIONSHIP, 
				"New inferred relationship"));
		
		inferredRelationshipReq.put("group", 5);
		assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, inferredRelationshipReq);
		
		final String classificationRunId = waitForClassificationToComplete(testBranchPath.getPath());
		final Multimap<String, Map<String, Object>> relationshipChangesBySourceId = classify(testBranchPath.getPath(), classificationRunId);
		
		/* 
		 * Expecting three changes, with the original inferred relationship with group 5 being redundant, and two inferred 
		 * relationships (IS A and associated morphology) from the root concept being added.
		 */
		final Collection<Map<String, Object>> conceptRelationshipChanges = relationshipChangesBySourceId.get(conceptId);
		assertEquals(3, conceptRelationshipChanges.size());
		
		boolean redundantFound = false;
		
		for (Map<String, Object> relationshipChange : conceptRelationshipChanges) {
			if (ChangeNature.REDUNDANT.name().equals(relationshipChange.get("changeNature"))) {
				assertFalse("Two redundant relationships found in response.", redundantFound);
				assertEquals(5, relationshipChange.get("group"));
				redundantFound = true;
			}
		}
		
		assertTrue("No redundant relationships found in response.", redundantFound);
	}
}
