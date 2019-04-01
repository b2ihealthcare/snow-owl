/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.SnomedClassificationRestRequests.beginClassification;
import static com.b2international.snowowl.snomed.api.rest.SnomedClassificationRestRequests.beginClassificationSave;
import static com.b2international.snowowl.snomed.api.rest.SnomedClassificationRestRequests.getClassificationJobId;
import static com.b2international.snowowl.snomed.api.rest.SnomedClassificationRestRequests.getEquivalentConceptSets;
import static com.b2international.snowowl.snomed.api.rest.SnomedClassificationRestRequests.getRelationshipChanges;
import static com.b2international.snowowl.snomed.api.rest.SnomedClassificationRestRequests.waitForClassificationJob;
import static com.b2international.snowowl.snomed.api.rest.SnomedClassificationRestRequests.waitForClassificationSaveJob;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.changeToDefining;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRelationship;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerRelationship;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChange;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 4.6
 */
public class SnomedClassificationApiTest extends AbstractSnomedApiTest {

	private static final ObjectMapper MAPPER = JsonSupport.getDefaultObjectMapper();
	
	private static int getPersistedInferredRelationshipCount(IBranchPath conceptPath, String conceptId) {
		List<Map<String, Object>> relationships = getComponent(conceptPath, SnomedComponentType.CONCEPT, conceptId, 
				"relationships(\"active\":true,\"characteristicType\":\"" + CharacteristicType.INFERRED_RELATIONSHIP.getConceptId() + "\")")
				.statusCode(200)
				.extract()
				.jsonPath()
				.getList("relationships.items");

		return relationships.size();
	}

	@Test
	public void persistInferredRelationship() throws Exception {
		String parentConceptId = createNewConcept(branchPath);
		String targetConceptId = createNewConcept(branchPath);
		String childConceptId = createNewConcept(branchPath, parentConceptId);

		createNewRelationship(branchPath, parentConceptId, Concepts.MORPHOLOGY, targetConceptId);

		String classificationId = getClassificationJobId(beginClassification(branchPath));
		waitForClassificationJob(branchPath, classificationId)
		.statusCode(200)
		.body("status", equalTo(ClassificationStatus.COMPLETED.name()));

		Collection<RelationshipChange> changes = MAPPER.readValue(getRelationshipChanges(branchPath, classificationId).statusCode(200)
				.extract()
				.asInputStream(), RelationshipChanges.class)
				.getItems();

		Multimap<String, RelationshipChange> changesBySource = Multimaps.index(changes, c -> c.getRelationship().getSourceId());
		Collection<RelationshipChange> parentRelationshipChanges = changesBySource.get(parentConceptId);
		Collection<RelationshipChange> childRelationshipChanges = changesBySource.get(childConceptId);

		// parent concept should have two inferred relationships, one ISA and one MORPHOLOGY, both inferred
		assertEquals(2, parentRelationshipChanges.size());
		// child concept should have two inferred relationships, one ISA and one MORPHOLOGY from parent, both inferred
		assertEquals(2, childRelationshipChanges.size());

		for (RelationshipChange change : parentRelationshipChanges) {
			assertEquals(ChangeNature.NEW, change.getChangeNature());
			switch (change.getRelationship().getTypeId()) {
			case Concepts.IS_A:
				assertEquals(Concepts.ROOT_CONCEPT, change.getRelationship().getDestinationId());
				break;
			case Concepts.MORPHOLOGY:
				assertEquals(targetConceptId, change.getRelationship().getDestinationId());
				break;
			}
		}

		for (RelationshipChange change : childRelationshipChanges) {
			assertEquals(ChangeNature.NEW, change.getChangeNature());
			switch (change.getRelationship().getTypeId()) {
			case Concepts.IS_A:
				assertEquals(parentConceptId, change.getRelationship().getDestinationId());
				break;
			case Concepts.MORPHOLOGY:
				assertEquals(targetConceptId, change.getRelationship().getDestinationId());
				break;
			}
		}

		beginClassificationSave(branchPath, classificationId);
		waitForClassificationSaveJob(branchPath, classificationId)
		.statusCode(200)
		.body("status", equalTo(ClassificationStatus.SAVED.name()));

		assertEquals(2, getPersistedInferredRelationshipCount(branchPath, parentConceptId));
		assertEquals(2, getPersistedInferredRelationshipCount(branchPath, childConceptId));
	}

	@Test
	public void persistRedundantRelationship() throws Exception {
		String parentConceptId = createNewConcept(branchPath);
		String childConceptId = createNewConcept(branchPath, parentConceptId);

		// Add "regular" inferences before running the classification
		createNewRelationship(branchPath, parentConceptId, Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		createNewRelationship(branchPath, childConceptId, Concepts.IS_A, parentConceptId, CharacteristicType.INFERRED_RELATIONSHIP);

		// Add redundant information that should be removed
		createNewRelationship(branchPath, childConceptId, Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);

		String classificationId = getClassificationJobId(beginClassification(branchPath));
		waitForClassificationJob(branchPath, classificationId)
		.statusCode(200)
		.body("status", equalTo(ClassificationStatus.COMPLETED.name()));

		RelationshipChanges changes = MAPPER.readValue(getRelationshipChanges(branchPath, classificationId).statusCode(200)
				.extract()
				.asInputStream(), RelationshipChanges.class);

		assertEquals(1, changes.getTotal());
		RelationshipChange relationshipChange = Iterables.getOnlyElement(changes.getItems());
		assertEquals(ChangeNature.REDUNDANT, relationshipChange.getChangeNature());
		assertEquals(childConceptId, relationshipChange.getRelationship().getSourceId());
		assertEquals(Concepts.IS_A, relationshipChange.getRelationship().getTypeId());
		assertEquals(Concepts.ROOT_CONCEPT, relationshipChange.getRelationship().getDestinationId());

		beginClassificationSave(branchPath, classificationId);
		waitForClassificationSaveJob(branchPath, classificationId)
		.statusCode(200)
		.body("status", equalTo(ClassificationStatus.SAVED.name()));

		assertEquals(1, getPersistedInferredRelationshipCount(branchPath, parentConceptId));
		assertEquals(1, getPersistedInferredRelationshipCount(branchPath, childConceptId));
	}

	@Test
	public void issue_SO_2152_testGroupRenumbering() throws Exception {
		String conceptId = createNewConcept(branchPath);

		// Add "regular" inferences before running the classification
		createNewRelationship(branchPath, conceptId, Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		// Add new relationship to the root as stated
		createNewRelationship(branchPath);
		// Add the same relationship with a different group to the new concept as inferred
		createNewRelationship(branchPath, conceptId, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.INFERRED_RELATIONSHIP, 5);

		String classificationId = getClassificationJobId(beginClassification(branchPath));
		waitForClassificationJob(branchPath, classificationId)
		.statusCode(200)
		.body("status", equalTo(ClassificationStatus.COMPLETED.name()));

		/* 
		 * Expecting lots of changes; all concepts receive the "Part of" relationship because it was added to the root concept, however, the original inferred relationship 
		 * with group 5 should be redundant.
		 */
		RelationshipChanges changes = MAPPER.readValue(getRelationshipChanges(branchPath, classificationId).statusCode(200)
				.extract()
				.asInputStream(), RelationshipChanges.class);

		boolean redundantFound = false;

		for (RelationshipChange relationshipChange : changes.getItems()) {
			ReasonerRelationship relationship = relationshipChange.getRelationship();
			assertEquals(Concepts.PART_OF, relationship.getTypeId());
			assertEquals(Concepts.NAMESPACE_ROOT, relationship.getDestinationId());

			if (ChangeNature.REDUNDANT.equals(relationshipChange.getChangeNature())) {
				assertFalse("Two redundant relationships found in response.", redundantFound);
				assertEquals(5, (int) relationship.getGroup());
				assertEquals(conceptId, relationship.getSourceId());
				redundantFound = true;
			} else {
				assertEquals(0, (int) relationship.getGroup());
			}
		}

		assertTrue("No redundant relationships found in response.", redundantFound);
	}
	
	@Test
	public void issue_SO_1830_testInferredEquivalentConceptParents() throws Exception {
		String parentConceptId = createNewConcept(branchPath);
		String childConceptId = createNewConcept(branchPath, parentConceptId);
		String equivalentConceptId = createNewConcept(branchPath, parentConceptId);

		changeToDefining(branchPath, equivalentConceptId);

		String classificationId = getClassificationJobId(beginClassification(branchPath));
		waitForClassificationJob(branchPath, classificationId)
		.statusCode(200)
		.body("status", equalTo(ClassificationStatus.COMPLETED.name()));

		/* 
		 * Expecting that childConceptId will get two inferred IS A-s pointing to parentConceptId and equivalentConceptId, respectively, 
		 * while parentConceptId and equivalentConceptId each will get a single inferred IS A pointing to the root concept.
		 */
		RelationshipChanges changes = MAPPER.readValue(getRelationshipChanges(branchPath, classificationId).statusCode(200)
				.extract()
				.asInputStream(), RelationshipChanges.class);

		FluentIterable<RelationshipChange> changesIterable = FluentIterable.from(changes.getItems());
		
		assertEquals(4, changes.getTotal());
		assertTrue("All changes should be inferred.", changesIterable.allMatch(relationshipChange -> ChangeNature.NEW.equals(relationshipChange.getChangeNature())));
		
		assertInferredIsAExists(changesIterable, childConceptId, parentConceptId);
		assertInferredIsAExists(changesIterable, childConceptId, equivalentConceptId);
		assertInferredIsAExists(changesIterable, parentConceptId, Concepts.ROOT_CONCEPT);
		assertInferredIsAExists(changesIterable, equivalentConceptId, Concepts.ROOT_CONCEPT);
		
		EquivalentConceptSets equivalentConceptSets = MAPPER.readValue(getEquivalentConceptSets(branchPath, classificationId).statusCode(200)
				.extract()
				.asInputStream(), EquivalentConceptSets.class);
		
		assertEquals(1, equivalentConceptSets.getItems().size());
		
		SnomedConcepts equivalentConceptsInFirstSet = equivalentConceptSets.first().get().getEquivalentConcepts();
		FluentIterable<SnomedConcept> equivalentConceptsIterable = FluentIterable.from(equivalentConceptsInFirstSet);
		
		assertEquals(2, equivalentConceptsInFirstSet.getTotal());
		assertEquivalentConceptPresent(equivalentConceptsIterable, parentConceptId);
		assertEquivalentConceptPresent(equivalentConceptsIterable, equivalentConceptId);
	}

	private static void assertInferredIsAExists(FluentIterable<RelationshipChange> changesIterable, String childConceptId, String parentConceptId) {
		assertTrue("Inferred IS A between " + childConceptId + " and " + parentConceptId + " not found.", 
				changesIterable.anyMatch(relationshipChange -> {
					final ReasonerRelationship relationship = relationshipChange.getRelationship();
					return Concepts.IS_A.equals(relationship.getTypeId())
							&& childConceptId.equals(relationship.getSourceId())
							&& parentConceptId.equals(relationship.getDestinationId())
							&& relationship.getGroup() == 0
							&& relationship.getUnionGroup() == 0
							&& RelationshipModifier.EXISTENTIAL.equals(relationship.getModifier())
							&& CharacteristicType.INFERRED_RELATIONSHIP.equals(relationship.getCharacteristicType());
				}));
	}
	
	private static void assertEquivalentConceptPresent(FluentIterable<SnomedConcept> equivalentConceptsIterable, String conceptId) {
		assertTrue("Equivalent concept with ID " + conceptId + " not found in set.", 
				equivalentConceptsIterable.anyMatch(equivalentConcept -> conceptId.equals(equivalentConcept.getId())));
	}
}
