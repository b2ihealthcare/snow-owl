/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.classification;

import static com.b2international.snowowl.snomed.core.rest.SnomedClassificationRestRequests.*;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.reasoner.domain.*;
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
				"relationships(\"active\":true,\"characteristicTypeId\":\"" + Concepts.INFERRED_RELATIONSHIP + "\")")
				.statusCode(200)
				.extract()
				.jsonPath()
				.getList("relationships.items");

		return relationships.size();
	}

	@Test
	public void testClassificationTaskInitialState() {
		
		createNewConcept(branchPath);
		
		String firstJobId = getClassificationJobId(beginClassification(branchPath));
		
		getClassification(branchPath, firstJobId)
			.statusCode(200)
			.body("status", anyOf(equalTo(ClassificationStatus.SCHEDULED.name()), equalTo(ClassificationStatus.RUNNING.name())));
		
		String secondJobId = getClassificationJobId(beginClassification(branchPath));
		
		getClassification(branchPath, secondJobId)
			.statusCode(200)
			.body("status", anyOf(equalTo(ClassificationStatus.SCHEDULED.name()), equalTo(ClassificationStatus.RUNNING.name())));
	
		waitForClassificationJob(branchPath, firstJobId)
			.statusCode(200)
			.body("status", equalTo(ClassificationStatus.COMPLETED.name()));
		
		waitForClassificationJob(branchPath, secondJobId)
			.statusCode(200)
			.body("status", equalTo(ClassificationStatus.COMPLETED.name()));
		
	}
	
	@Test
	public void testClassificationTaskBecomesStale() {
		
		createNewConcept(branchPath);
		
		String firstJobId = getClassificationJobId(beginClassification(branchPath));
		
		waitForClassificationJob(branchPath, firstJobId)
			.statusCode(200)
			.body("status", equalTo(ClassificationStatus.COMPLETED.name()));
		
		createNewConcept(branchPath);
		
		getClassification(branchPath, firstJobId)
			.statusCode(200)
			.body("status", equalTo(ClassificationStatus.STALE.name()));
		
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
	public void persistDataHasValueAxiom_Integer() throws Exception {
		persistDataHasValueAxiom("\"99\"^^xsd:integer", new RelationshipValue(99));
	}
	
	@Test
	public void persistDataHasValueAxiom_Decimal() throws Exception {
		persistDataHasValueAxiom("\"3.6\"^^xsd:decimal", new RelationshipValue(3.6d));
	}
	
	@Test
	public void persistDataHasValueAxiom_String() throws Exception {
		persistDataHasValueAxiom("\"Hello world\"^^xsd:string", new RelationshipValue("Hello world"));
	}

	private void persistDataHasValueAxiom(String owlValueLiteral, RelationshipValue value) throws Exception {
		String parentConceptId = createNewConcept(branchPath);
		String childConceptId = createNewConcept(branchPath, parentConceptId);

		createNewRefSetMember(branchPath, parentConceptId, Concepts.REFSET_OWL_AXIOM, Map.of(
			SnomedRf2Headers.FIELD_OWL_EXPRESSION, "SubClassOf("
			+ ":" + parentConceptId 
			+ " ObjectIntersectionOf(" 
			+ ":" + Concepts.ROOT_CONCEPT
			+ " ObjectSomeValuesFrom(:609096000 "
			+ "DataHasValue(:" + Concepts.MORPHOLOGY + " " + owlValueLiteral + "))))"));
		
		verifyRelationshipValueChanges(parentConceptId, childConceptId, value);
	}
	
	@Test
	public void persistInferredRelationshipWithValue_Integer() throws Exception {
		persistInferredRelationshipWithValue(new RelationshipValue(99));
	}
	
	@Test
	public void persistInferredRelationshipWithValue_Decimal() throws Exception {
		persistInferredRelationshipWithValue(new RelationshipValue(3.6d));
	}
	
	@Test
	public void persistInferredRelationshipWithValue_String() throws Exception {
		persistInferredRelationshipWithValue(new RelationshipValue("Hello world"));
	}

	private void persistInferredRelationshipWithValue(RelationshipValue value) throws Exception {
		String parentConceptId = createNewConcept(branchPath);
		String childConceptId = createNewConcept(branchPath, parentConceptId);
		
		// Add _stated_ relationship with value (unlikely to be encountered in a dataset)
		createNewConcreteValue(branchPath, parentConceptId, Concepts.MORPHOLOGY, value, Concepts.STATED_RELATIONSHIP, 1);
		
		verifyRelationshipValueChanges(parentConceptId, childConceptId, value);
	}

	private void verifyRelationshipValueChanges(String parentConceptId, String childConceptId, RelationshipValue expectedValue) throws Exception {
		String classificationId = getClassificationJobId(beginClassification(branchPath));
		waitForClassificationJob(branchPath, classificationId)
			.statusCode(200)
			.body("status", equalTo(ClassificationStatus.COMPLETED.name()));
		
		InputStream inputStream = getRelationshipChanges(branchPath, classificationId)
			.statusCode(200)
			.extract()
			.asInputStream();
			
		Collection<RelationshipChange> changes = MAPPER.readValue(inputStream, RelationshipChanges.class)
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
					assertEquals(expectedValue, change.getRelationship().getValueAsObject());
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
					assertEquals(expectedValue, change.getRelationship().getValueAsObject());
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
		createNewRelationship(branchPath, parentConceptId, Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
		createNewRelationship(branchPath, childConceptId, Concepts.IS_A, parentConceptId, Concepts.INFERRED_RELATIONSHIP);

		// Add redundant information that should be removed
		createNewRelationship(branchPath, childConceptId, Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);

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
	public void testRedundantRelationshipModuleChange() throws Exception {
		
		final String codeSystemShortName = "SNOMEDCT-CLASSIFY-RSHIPS-MOD";
		createCodeSystem(branchPath, codeSystemShortName).statusCode(201);
		
		String parentConceptId = createNewConcept(branchPath);
		String childConceptId = createNewConcept(branchPath, parentConceptId);
		
		// Add "regular" inferences before running the classification
		createNewRelationship(branchPath, parentConceptId, Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
		createNewRelationship(branchPath, childConceptId, Concepts.IS_A, parentConceptId, Concepts.INFERRED_RELATIONSHIP);

		// Add redundant information that should be removed
		Map<?, ?> relationshipRequestBody = createRelationshipRequestBody(
				childConceptId, Concepts.IS_A, Concepts.ROOT_CONCEPT,
				Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.INFERRED_RELATIONSHIP, 0)
				.with("commitComment", "Created new relationship");

		String redundantRelationshipId = assertCreated(createComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipRequestBody));
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, redundantRelationshipId)
			.statusCode(200)
			.body("active", equalTo(true))
			.body("moduleId", equalTo(Concepts.MODULE_SCT_MODEL_COMPONENT));
		
		// Create version
		String effectiveDate = getNextAvailableEffectiveDateAsString(codeSystemShortName);
		createVersion(codeSystemShortName, "v1", effectiveDate).statusCode(201);
		
		String classificationId = getClassificationJobId(beginClassification(branchPath));
		waitForClassificationJob(branchPath, classificationId)
			.statusCode(200)
			.body("status", equalTo(ClassificationStatus.COMPLETED.name()));

		RelationshipChanges changes = MAPPER.readValue(getRelationshipChanges(branchPath, classificationId).statusCode(200)
				.extract()
				.asInputStream(), RelationshipChanges.class);

		assertEquals(1, changes.getTotal());
		RelationshipChange relationshipChange = Iterables.getOnlyElement(changes);
		assertEquals(ChangeNature.REDUNDANT, relationshipChange.getChangeNature());
		assertEquals(childConceptId, relationshipChange.getRelationship().getSourceId());
		assertEquals(Concepts.IS_A, relationshipChange.getRelationship().getTypeId());
		assertEquals(Concepts.ROOT_CONCEPT, relationshipChange.getRelationship().getDestinationId());
		assertEquals(redundantRelationshipId, relationshipChange.getRelationship().getOriginId());
		
		beginClassificationSave(branchPath, classificationId);
		waitForClassificationSaveJob(branchPath, classificationId)
			.statusCode(200)
			.body("status", equalTo(ClassificationStatus.SAVED.name()));

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, redundantRelationshipId)
			.statusCode(200)
			.body("active", equalTo(false))
			.body("moduleId", equalTo(Concepts.MODULE_SCT_CORE));
		
		assertEquals(1, getPersistedInferredRelationshipCount(branchPath, parentConceptId));
		assertEquals(1, getPersistedInferredRelationshipCount(branchPath, childConceptId));
	}
	
	@Test
	public void issue_SO_2152_testGroupRenumbering() throws Exception {
		String conceptId = createNewConcept(branchPath);

		// Add "regular" inferences before running the classification
		createNewRelationship(branchPath, conceptId, Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
		// Add new relationship to the root as stated
		createNewRelationship(branchPath);
		// Add the same relationship with a different group to the new concept as inferred
		createNewRelationship(branchPath, conceptId, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, Concepts.INFERRED_RELATIONSHIP, 5);

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
							&& Concepts.EXISTENTIAL_RESTRICTION_MODIFIER.equals(relationship.getModifierId())
							&& Concepts.INFERRED_RELATIONSHIP.equals(relationship.getCharacteristicTypeId());
				}));
	}
	
	private static void assertEquivalentConceptPresent(FluentIterable<SnomedConcept> equivalentConceptsIterable, String conceptId) {
		assertTrue("Equivalent concept with ID " + conceptId + " not found in set.", 
				equivalentConceptsIterable.anyMatch(equivalentConcept -> conceptId.equals(equivalentConcept.getId())));
	}
}
