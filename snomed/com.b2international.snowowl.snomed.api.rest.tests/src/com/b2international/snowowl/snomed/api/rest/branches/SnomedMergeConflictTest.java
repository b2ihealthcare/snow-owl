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
package com.b2international.snowowl.snomed.api.rest.branches;

import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.changeCaseSignificance;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewDescription;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSetMember;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRelationship;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.merge;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class SnomedMergeConflictTest extends AbstractSnomedApiTest {

	@Test
	public void changedInSourceAndTargetMergeConflict() {
		String descriptionId = createNewDescription(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		changeCaseSignificance(branchPath, descriptionId, CaseSignificance.CASE_INSENSITIVE); // Parent branch changes to CaseSignificance.CASE_INSENSITIVE
		changeCaseSignificance(a, descriptionId); // Child branch changes to CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased case significance change over case significance change")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("caseSignificanceId")
				.oldValue(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId())
				.value(CaseSignificance.CASE_INSENSITIVE.getConceptId())
				.build();

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(descriptionId, conflict.getComponentId());
		assertEquals("description", conflict.getComponentType());
		assertEquals(ConflictType.CONFLICTING_CHANGE, conflict.getType());
		assertEquals(attribute.toDisplayName(), Iterables.getOnlyElement(conflict.getConflictingAttributes()).toDisplayName());
	}

	@Test
	public void changedInSourceAndTargetMultipleConflict() {
		String descriptionId = createNewDescription(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		Map<?, ?> changesOnParent = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("moduleId", Concepts.MODULE_ROOT)
				.put("commitComment", "Changed case significance and module on parent")
				.build();

		Map<?, ?> changesOnBranch = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("moduleId", Concepts.MODULE_SCT_MODEL_COMPONENT)
				.put("commitComment", "Changed case significance and module on branch")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, changesOnParent).statusCode(204);
		updateComponent(a, SnomedComponentType.DESCRIPTION, descriptionId, changesOnBranch).statusCode(204);

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased description changes over conflicting description changes")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(descriptionId, conflict.getComponentId());
		assertEquals("description", conflict.getComponentType());
		assertEquals(ConflictType.CONFLICTING_CHANGE, conflict.getType());

		Map<String, ConflictingAttribute> expectedAttributes = newHashMap();
		expectedAttributes.put("caseSignificanceId", ConflictingAttributeImpl.builder()
				.property("caseSignificanceId")
				.oldValue(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId())
				.value(CaseSignificance.CASE_INSENSITIVE.getConceptId())
				.build());

		expectedAttributes.put("moduleId", ConflictingAttributeImpl.builder()
				.property("moduleId")
				.oldValue(Concepts.MODULE_SCT_CORE)
				.value(Concepts.MODULE_ROOT)
				.build());

		for (ConflictingAttribute attribute : conflict.getConflictingAttributes()) {
			ConflictingAttribute expected = expectedAttributes.remove(attribute.getProperty());
			assertNotNull(expected);
			assertEquals(expected.toDisplayName(), attribute.toDisplayName());
		}

		assertEquals(ImmutableList.of(), ImmutableList.copyOf(expectedAttributes.values()));
	}

	@Test
	public void changedInSourceDetachedInTargetMergeConflict() {
		String memberId = createNewRefSetMember(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		Date nextEffectiveTime = getNextAvailableEffectiveDate(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String nextEffectiveTimeAsString = EffectiveTimes.format(nextEffectiveTime, DateFormats.SHORT);

		Map<?, ?> effectiveTimeUpdateRequest = ImmutableMap.builder()
				.put("effectiveTime", nextEffectiveTimeAsString)
				.put("commitComment", "Updated effective time on reference set member")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, effectiveTimeUpdateRequest, true).statusCode(204);
		deleteComponent(a, SnomedComponentType.MEMBER, memberId, false).statusCode(204);

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased reference set member deletion over effective time update")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(memberId, conflict.getComponentId());
		assertEquals("member", conflict.getComponentType());
		assertEquals(ConflictType.DELETED_WHILE_CHANGED, conflict.getType());

		Map<String, ConflictingAttribute> expectedAttributes = newHashMap();
		expectedAttributes.put("effectiveTime", ConflictingAttributeImpl.builder()
				.property("effectiveTime")
				.value(nextEffectiveTimeAsString)
				.build());

		expectedAttributes.put("released", ConflictingAttributeImpl.builder()
				.property("released")
				.oldValue("false")
				.value("true")
				.build());

		for (ConflictingAttribute attribute : conflict.getConflictingAttributes()) {
			ConflictingAttribute expected = expectedAttributes.remove(attribute.getProperty());
			assertNotNull(expected);
			assertEquals(expected.toDisplayName(), attribute.toDisplayName());
		}

		assertThat(expectedAttributes).isEmpty();
	}

	@Test
	public void changedInTargetDetachedInSourceDescription() {
		String descriptionId = createNewDescription(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, false).statusCode(204); // Parent deletes the description
		changeCaseSignificance(a, descriptionId); // Child branch changes to CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased case significance change over deletion")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("caseSignificanceId")
				.oldValue(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId())
				.value(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.getConceptId())
				.build();

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(descriptionId, conflict.getComponentId());
		assertEquals("description", conflict.getComponentType());
		assertEquals(ConflictType.CHANGED_WHILE_DELETED, conflict.getType());
		assertEquals(attribute.toDisplayName(), Iterables.getOnlyElement(conflict.getConflictingAttributes()).toDisplayName());
	}

	@Test
	public void changedInTargetDetachedInSourceConcept() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204); // Parent deletes the concept
		SnomedRestFixtures.changeToDefining(a, conceptId); // Child branch changes to DefinitionStatus.FULLY_DEFINED

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased definition status change over deletion")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("definitionStatus")
				.oldValue(DefinitionStatus.PRIMITIVE.getConceptId())
				.value(DefinitionStatus.FULLY_DEFINED.getConceptId())
				.build();

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(conceptId, conflict.getComponentId());
		assertEquals("concept", conflict.getComponentType());
		assertEquals(ConflictType.CHANGED_WHILE_DELETED, conflict.getType());
		assertEquals(attribute.toDisplayName(), Iterables.getOnlyElement(conflict.getConflictingAttributes()).toDisplayName());
	}

	@Test
	public void addedInSourceAndTargetMergeConflict() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String descriptionId = createNewDescription(branchPath);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("id", descriptionId)
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", "Synonym of root concept")
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
				.put("caseSignificance", CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE)
				.put("commitComment", "Created new synonym with duplicate SCTID")
				.build();

		createComponent(a, SnomedComponentType.DESCRIPTION, requestBody).statusCode(201);

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased new description over new description with same SCTID")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder().property("id").build();
		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(descriptionId, conflict.getComponentId());
		assertEquals("description", conflict.getComponentType());
		assertEquals(ConflictType.CONFLICTING_CHANGE, conflict.getType());
		assertEquals(attribute.toDisplayName(), Iterables.getOnlyElement(conflict.getConflictingAttributes()).toDisplayName());
	}

	@Test
	public void addedInTargetDetachedInSourceMergeConflict() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		String relationshipId = createNewRelationship(a, Concepts.ROOT_CONCEPT, Concepts.PART_OF, conceptId);

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased new relationship over deleted concept")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("destinationId")
				.value(conceptId)
				.build();
		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(relationshipId, conflict.getComponentId());
		assertEquals("relationship", conflict.getComponentType());
		assertEquals(ConflictType.HAS_MISSING_REFERENCE, conflict.getType());
		assertEquals(attribute.toDisplayName(), Iterables.getOnlyElement(conflict.getConflictingAttributes()).toDisplayName());
	}

	@Test
	public void addedInSourceDetachedInTargetMergeConflict() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, conceptId);
		deleteComponent(a, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased deleted concept over new relationship")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(conceptId, conflict.getComponentId());
		assertEquals("concept", conflict.getComponentType());
		assertEquals(ConflictType.CAUSES_MISSING_REFERENCE, conflict.getType());
	}

	@Test
	public void deleteReferencedComponentOnSourceMergeConflict() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		String memberId = createNewRefSetMember(a, conceptId);

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased new reference set member over deleted referenced component")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("container")
				.value(conceptId)
				.build();

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(memberId, conflict.getComponentId());
		assertEquals("member", conflict.getComponentType());
		assertEquals(ConflictType.HAS_MISSING_REFERENCE, conflict.getType());
		assertEquals(attribute.toDisplayName(), Iterables.getOnlyElement(conflict.getConflictingAttributes()).toDisplayName());
	}

	@Test
	public void deleteReferencedComponentOnTargetMergeConflict() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		createNewRefSetMember(branchPath, conceptId);
		deleteComponent(a, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased deleted referenced component over new reference set member")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(conceptId, conflict.getComponentId());
		assertEquals("concept", conflict.getComponentType());
		assertEquals(ConflictType.CAUSES_MISSING_REFERENCE, conflict.getType());
	}

	@Test
	public void noMergeNewDescriptionToUnrelatedBranch() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		/*
		 * XXX: Creating a new description on the concept itself would result in a DELETED_WHILE_CHANGED conflict;
		 * by referring to it as the type, the deletion will generate a CAUSES_MISSING_REFERENCE conflict instead.
		 */
		createNewDescription(a, Concepts.ROOT_CONCEPT, conceptId);

		IBranchPath b = BranchPathUtils.createPath(branchPath, "b");
		createBranch(b).statusCode(201);

		deleteComponent(b, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);

		Collection<MergeConflict> conflicts = merge(a, b, "Merged new description to unrelated branch")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(conceptId, conflict.getComponentId());
		assertEquals("concept", conflict.getComponentType());
		assertEquals(ConflictType.CAUSES_MISSING_REFERENCE, conflict.getType());
		assertEquals(0, conflict.getConflictingAttributes().size());
	}

	@Test
	public void noMergeNewRelationshipToUnrelatedBranch() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		createNewRelationship(a, Concepts.ROOT_CONCEPT, Concepts.PART_OF, conceptId);

		IBranchPath b = BranchPathUtils.createPath(branchPath, "b");
		createBranch(b).statusCode(201);

		deleteComponent(b, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);

		Collection<MergeConflict> conflicts = merge(a, b, "Merged new relationship to unrelated branch")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		MergeConflict conflict = Iterables.getOnlyElement(conflicts);

		assertEquals(conceptId, conflict.getComponentId());
		assertEquals("concept", conflict.getComponentType());
		assertEquals(ConflictType.CAUSES_MISSING_REFERENCE, conflict.getType());
		assertEquals(0, conflict.getConflictingAttributes().size());
	}

}
