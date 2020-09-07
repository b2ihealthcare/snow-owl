/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.branches;

import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.changeCaseSignificance;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewDescription;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewRefSetMember;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewRelationship;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.merge;
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
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
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
		branching.createBranch(a).statusCode(201);

		changeCaseSignificance(branchPath, descriptionId, Concepts.ENTIRE_TERM_CASE_INSENSITIVE); // Parent branch changes to CaseSignificance.CASE_INSENSITIVE
		changeCaseSignificance(a, descriptionId); // Child branch changes to CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE

		Collection<MergeConflict> conflicts = merge(branchPath, a, "Rebased case significance change over case significance change")
				.body("status", equalTo(Merge.Status.CONFLICTS.name()))
				.extract().as(Merge.class)
				.getConflicts();

		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("caseSignificanceId")
				.oldValue(Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE)
				.value(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
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
		branching.createBranch(a).statusCode(201);

		Map<?, ?> changesOnParent = ImmutableMap.builder()
				.put("caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.put("moduleId", Concepts.MODULE_ROOT)
				.put("commitComment", "Changed case significance and module on parent")
				.build();

		Map<?, ?> changesOnBranch = ImmutableMap.builder()
				.put("caseSignificanceId", Concepts.ENTIRE_TERM_CASE_SENSITIVE)
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
				.oldValue(Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE)
				.value(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
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
		branching.createBranch(a).statusCode(201);

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
	public void addedInSourceAndTargetMergeConflict() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		branching.createBranch(a).statusCode(201);

		String descriptionId = createNewDescription(branchPath);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("id", descriptionId)
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", "Synonym of root concept")
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
				.put("caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
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
		branching.createBranch(a).statusCode(201);

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
		branching.createBranch(a).statusCode(201);

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
		branching.createBranch(a).statusCode(201);

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
		branching.createBranch(a).statusCode(201);

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
		branching.createBranch(a).statusCode(201);

		/*
		 * XXX: Creating a new description on the concept itself would result in a DELETED_WHILE_CHANGED conflict;
		 * by referring to it as the type, the deletion will generate a CAUSES_MISSING_REFERENCE conflict instead.
		 */
		createNewDescription(a, Concepts.ROOT_CONCEPT, conceptId);

		IBranchPath b = BranchPathUtils.createPath(branchPath, "b");
		branching.createBranch(b).statusCode(201);

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
		branching.createBranch(a).statusCode(201);

		createNewRelationship(a, Concepts.ROOT_CONCEPT, Concepts.PART_OF, conceptId);

		IBranchPath b = BranchPathUtils.createPath(branchPath, "b");
		branching.createBranch(b).statusCode(201);

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
	
    @Test
	public void rebaseResolvableDescriptionConflictOnTheSameDescription() throws Exception {
		final String conceptA = createNewConcept(branchPath);
		final String descriptionB = createNewDescription(branchPath, conceptA, Concepts.SYNONYM, SnomedApiTestConstants.UK_PREFERRED_MAP);
		
		final IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		branching.createBranch(a).statusCode(201);
		
		Map<?, ?> descriptionBUpdateRequest = ImmutableMap.builder()
				.put("term", "Description B New Term")
				.put("commitComment", "Change description B")
				.build();
		updateComponent(a, SnomedComponentType.DESCRIPTION, descriptionB, descriptionBUpdateRequest).statusCode(204);
		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionB, descriptionBUpdateRequest).statusCode(204);
		
		merge(branchPath, a, "Rebase branch A").body("status", equalTo(Merge.Status.COMPLETED.name()));
		
		// checking duplicate revisions after sync
		getComponent(a, SnomedComponentType.DESCRIPTION, descriptionB).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, conceptA).statusCode(200);
	}
    
    @Test
	public void rebaseResolvableDescriptionConflictOnTwoDifferentDescriptions() throws Exception {
		final String concept = createNewConcept(branchPath);
		final String syn = createNewDescription(branchPath, concept, Concepts.SYNONYM, SnomedApiTestConstants.UK_PREFERRED_MAP);
		final String fsn = createNewDescription(branchPath, concept, Concepts.FULLY_SPECIFIED_NAME, SnomedApiTestConstants.UK_PREFERRED_MAP);
		
		final IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		branching.createBranch(a).statusCode(201);
		
		Map<?, ?> synUpdateRequest = ImmutableMap.builder()
				.put("term", "Updated")
				.put("commitComment", "Change " + syn)
				.build();
		
		updateComponent(a, SnomedComponentType.DESCRIPTION, syn, synUpdateRequest).statusCode(204);
		
		Map<?, ?> fsnUpdateRequest = ImmutableMap.builder()
				.put("term", "Updated")
				.put("commitComment", "Change " + fsn)
				.build();
		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, fsn, fsnUpdateRequest).statusCode(204);
		
		merge(branchPath, a, "Rebase branch A").body("status", equalTo(Merge.Status.COMPLETED.name()));
		
		// checking duplicate revisions after sync
		getComponent(a, SnomedComponentType.DESCRIPTION, syn).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, fsn).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, concept).statusCode(200);
	}

    @Test
   	public void rebaseResolvableIsaRelationshipConflictSameDestination() throws Exception {
   		final String concept = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
   		
   		final IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
   		branching.createBranch(a).statusCode(201);

   		String relationshipOnParent = createNewRelationship(branchPath, concept, Concepts.IS_A, Concepts.TOPLEVEL_METADATA);
   		String relationshipOnChild = createNewRelationship(a, concept, Concepts.IS_A, Concepts.TOPLEVEL_METADATA);
   		
   		merge(branchPath, a, "Rebase branch A").body("status", equalTo(Merge.Status.COMPLETED.name()));
   		
   		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipOnChild).statusCode(200);
   		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipOnParent).statusCode(200);
   		SnomedConcept conceptOnChild = getComponent(a, SnomedComponentType.CONCEPT, concept).statusCode(200).extract().as(SnomedConcept.class);
   		assertThat(conceptOnChild.getStatedParentIdsAsString()).containsOnly(Concepts.ROOT_CONCEPT, Concepts.TOPLEVEL_METADATA);
   		assertThat(conceptOnChild.getParentIdsAsString()).containsOnly(IComponent.ROOT_ID);
   		assertThat(conceptOnChild.getStatedAncestorIdsAsString()).containsOnly(IComponent.ROOT_ID, Concepts.ROOT_CONCEPT);
   		assertThat(conceptOnChild.getAncestorIdsAsString()).isEmpty();
   	}
    
    @Test
   	public void rebaseResolvableIsaRelationshipConflictTwoDifferentDestinations() throws Exception {
   		final String concept = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
   		
   		final IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
   		branching.createBranch(a).statusCode(201);

   		String relationshipOnParent = createNewRelationship(branchPath, concept, Concepts.IS_A, Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE);
   		String relationshipOnChild = createNewRelationship(a, concept, Concepts.IS_A, Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE);
   		
   		merge(branchPath, a, "Rebase branch A").body("status", equalTo(Merge.Status.COMPLETED.name()));
   		
   		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipOnChild).statusCode(200);
   		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipOnParent).statusCode(200);
   		SnomedConcept conceptOnChild = getComponent(a, SnomedComponentType.CONCEPT, concept).statusCode(200).extract().as(SnomedConcept.class);
   		assertThat(conceptOnChild.getStatedParentIdsAsString()).containsOnly(Concepts.ROOT_CONCEPT, Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE, Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE);
   		assertThat(conceptOnChild.getParentIdsAsString()).containsOnly(IComponent.ROOT_ID);
   		assertThat(conceptOnChild.getStatedAncestorIdsAsString()).containsOnly(IComponent.ROOT_ID, Concepts.ROOT_CONCEPT, Concepts.LINKAGE, Concepts.ATTRIBUTE, Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.TOPLEVEL_METADATA);
   		assertThat(conceptOnChild.getAncestorIdsAsString()).isEmpty();
   	}
    
    @Test
	public void rebaseResolvableNonIsaRelationshipConflict() throws Exception {
    	final String concept = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
   		
   		final IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
   		branching.createBranch(a).statusCode(201);

   		String relationshipOnChild = createNewRelationship(a, concept, Concepts.FINDING_SITE, Concepts.TOPLEVEL_METADATA);
   		String relationshipOnParent = createNewRelationship(branchPath, concept, Concepts.FINDING_SITE, Concepts.TOPLEVEL_METADATA);
   		
   		merge(branchPath, a, "Rebase branch A").body("status", equalTo(Merge.Status.COMPLETED.name()));
   		
   		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipOnChild).statusCode(200);
   		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipOnParent).statusCode(200);
   		getComponent(a, SnomedComponentType.CONCEPT, concept).statusCode(200).extract().as(SnomedConcept.class);
	}
       
    @Test
   	public void rebaseResolvableAxiomMemberConflict() throws Exception {
   		final String concept = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
   		
   		final IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
   		branching.createBranch(a).statusCode(201);

   		String axiomOnParent = createNewRefSetMember(branchPath, concept, Concepts.REFSET_OWL_AXIOM, Map.of(SnomedRf2Headers.FIELD_OWL_EXPRESSION, String.format("SubClassOf(:%s :%s)", concept, Concepts.TOPLEVEL_METADATA)));
   		String axiomOnChild = createNewRefSetMember(a, concept, Concepts.REFSET_OWL_AXIOM, Map.of(SnomedRf2Headers.FIELD_OWL_EXPRESSION, String.format("SubClassOf(:%s :%s)", concept, Concepts.TOPLEVEL_METADATA)));
   		
   		merge(branchPath, a, "Rebase branch A").body("status", equalTo(Merge.Status.COMPLETED.name()));
   		
   		getComponent(a, SnomedComponentType.MEMBER, axiomOnChild).statusCode(200);
   		getComponent(a, SnomedComponentType.MEMBER, axiomOnParent).statusCode(200);
   		SnomedConcept conceptOnChild = getComponent(a, SnomedComponentType.CONCEPT, concept).statusCode(200).extract().as(SnomedConcept.class);
   		assertThat(conceptOnChild.getStatedParentIdsAsString()).containsOnly(Concepts.ROOT_CONCEPT, Concepts.TOPLEVEL_METADATA);
   		assertThat(conceptOnChild.getParentIdsAsString()).containsOnly(IComponent.ROOT_ID);
   		assertThat(conceptOnChild.getStatedAncestorIdsAsString()).containsOnly(IComponent.ROOT_ID, Concepts.ROOT_CONCEPT);
   		assertThat(conceptOnChild.getAncestorIdsAsString()).isEmpty();
   	}
    
}
