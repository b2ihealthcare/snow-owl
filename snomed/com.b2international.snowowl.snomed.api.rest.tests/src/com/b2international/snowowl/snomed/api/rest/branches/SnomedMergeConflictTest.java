/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.SCT_API;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeMerged;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertMergeJobFailsWithConflict;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedMergeApiAssert.*;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetApiAssert.updateMemberEffectiveTime;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.Response;

/**
 * @since 4.7
 */
public class SnomedMergeConflictTest extends AbstractSnomedApiTest {

	@Override
	protected IBranchPath createRandomBranchPath() {
		final IBranchPath parentBranch = super.createRandomBranchPath();
		givenBranchWithPath(parentBranch);
		return BranchPathUtils.createPath(parentBranch, "merge-conflict-test");
	}

	/*
	 * The test branch path is MAIN/<random UUID>/merge-conflict-test. Tests should affect <random UUID> and merge-conflict-test branches
	 */
	private void init() {
		testBranchPath = createRandomBranchPath();
		givenBranchWithPath(testBranchPath);
	}
	
	@Test
	public void changedInSourceAndTargetMergeConflict() {
		
		init();
		
		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		final Map<?, ?> changesOnParent = ImmutableMap.builder().put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Changed case significance on parent").build();

		assertDescriptionCanBeUpdated(testBranchPath.getParent(), "D100", changesOnParent);

		final Map<?, ?> changesOnBranch = ImmutableMap.builder().put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Changed case significance on branch").build();

		assertDescriptionCanBeUpdated(testBranchPath, "D100", changesOnBranch);

		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "commit");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("caseSignificance")
				.oldValue(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId())
				.value(CaseSignificance.CASE_INSENSITIVE.getConceptId())
				.build();
		
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("D100"))
				.put("componentType", "Description")
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("type", ConflictType.CONFLICTING_CHANGE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("D100"), 
						"Description", 
						Collections.<ConflictingAttribute>singletonList(attribute), 
						ConflictType.CONFLICTING_CHANGE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}

	@Test
	public void changedInSourceDetachedInTargetMergeConflict() {

		init();

		assertRefsetMemberCreated(testBranchPath, "M1");

		assertRefSetMemberExists(testBranchPath, "M1");
		assertRefSetMemberNotExists(testBranchPath.getParent(), "M1");

		assertBranchCanBeMerged(testBranchPath, "merge branch");

		assertRefSetMemberExists(testBranchPath, "M1");
		assertRefSetMemberExists(testBranchPath.getParent(), "M1");

		givenAuthenticatedRequest(SCT_API).when().get("{path}/members/{memberId}", testBranchPath.getParent().getPath(), symbolicNameMap.get("M1"))
				.then().assertThat().body("effectiveTime", nullValue()).body("released", equalTo(false));

		String effectiveTime = EffectiveTimes.format(new Date(), DateFormats.SHORT);

		updateMemberEffectiveTime(testBranchPath.getParent(), symbolicNameMap.get("M1"), effectiveTime, true);

		givenAuthenticatedRequest(SCT_API).when().get("{path}/members/{memberId}", testBranchPath.getParent().getPath(), symbolicNameMap.get("M1"))
				.then().assertThat().body("effectiveTime", equalTo(effectiveTime)).body("released", equalTo(true));

		assertRefSetMemberCanBeDeleted(testBranchPath, "M1");

		assertRefSetMemberExists(testBranchPath.getParent(), "M1");
		assertRefSetMemberNotExists(testBranchPath, "M1");

		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "commit");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());
		
		List<ConflictingAttribute> attributes = FluentIterable.from(ImmutableList.<ConflictingAttribute>of(
				ConflictingAttributeImpl.builder().property("effectiveTime").value(effectiveTime).build(), 
				ConflictingAttributeImpl.builder().property("released").value("true").oldValue("false").build())
			).toSortedList(ConflictingAttributeImpl.ATTRIBUTE_COMPARATOR);	

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("M1"))
				.put("componentType", "SnomedRefSetMember")
				.put("conflictingAttributes", createAttributesMap(attributes))
				.put("type", ConflictType.DELETED_WHILE_CHANGED.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("M1"), 
						"SnomedRefSetMember", 
						attributes, 
						ConflictType.DELETED_WHILE_CHANGED))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}

	@Test
	public void changedInSourceDetachedInTargetNoMergeConflict() {

		init();

		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description into parent branch");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		assertDescriptionProperty(testBranchPath.getParent(), symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.name());

		final Map<?, ?> changesOnParent = ImmutableMap.builder().put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Changed case significance on parent").build();

		assertDescriptionCanBeUpdated(testBranchPath.getParent(), "D100", changesOnParent);

		assertDescriptionProperty(testBranchPath.getParent(), symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.CASE_INSENSITIVE.name());

		assertDescriptionCanBeDeleted(testBranchPath, "D100");

		assertDescriptionNotExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		assertBranchCanBeMerged(testBranchPath.getParent(), testBranchPath, "commit");

		assertDescriptionNotExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");
	}
	
	@Test
	public void changedInTargetDetachedInSourceMergeConflict() {
		
		init();
		
		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description into parent branch");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");
		
		assertDescriptionProperty(testBranchPath, symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.name());
		
		final Map<?, ?> changesOnBranch = ImmutableMap.builder().put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Changed case significance on branch").build();

		assertDescriptionCanBeUpdated(testBranchPath, "D100", changesOnBranch);
		
		assertDescriptionProperty(testBranchPath, symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name());
	
		assertDescriptionCanBeDeleted(testBranchPath.getParent(), "D100");
		
		assertDescriptionNotExists(testBranchPath.getParent(), "D100");
		assertDescriptionExists(testBranchPath, "D100");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "merge");
	
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("caseSignificance")
				.oldValue(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId())
				.value(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.getConceptId())
				.build();
		
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("D100"))
				.put("componentType", "Description")
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("type", ConflictType.CHANGED_WHILE_DELETED.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("D100"), 
						"Description", 
						Collections.<ConflictingAttribute>singletonList(attribute), 
						ConflictType.CHANGED_WHILE_DELETED))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void addedInSourceAndTargetMergeConflict() {
		
		init();
		
		assertDescriptionCreated(testBranchPath, "D200", ACCEPTABLE_ACCEPTABILITY_MAP);
		
		String descriptionId = symbolicNameMap.get("D200");
		
		assertDescriptionExists(testBranchPath, "D200");
		assertDescriptionNotExists(testBranchPath.getParent(), "D200");
		
		assertDescriptionCreatedWithId(testBranchPath.getParent(), "D300", descriptionId, ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath.getParent(), "D300");

		assertEquals(descriptionId, symbolicNameMap.get("D300"));
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "commit");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder().property("id").build();
		
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", descriptionId)
				.put("componentType", "Description")
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("type", ConflictType.CONFLICTING_CHANGE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						descriptionId, 
						"Description", 
						Collections.<ConflictingAttribute>singletonList(attribute), 
						ConflictType.CONFLICTING_CHANGE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void addedInTargetDetachedInSourceMergeConflict() {
		
		init();
		
		assertConceptCreated(testBranchPath, "C1");

		assertBranchCanBeMerged(testBranchPath, "commit");
		
		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
		
		assertRelationshipCreated(testBranchPath, "R1", Concepts.ROOT_CONCEPT, MORPHOLOGIC_ABNORMALITY, symbolicNameMap.get("C1"));
		
		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");
		
		assertConceptCanBeDeleted(testBranchPath.getParent(), "C1");
		assertConceptNotExists(testBranchPath.getParent(), "C1");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "merge");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());
		
		ConflictingAttribute attribute = ConflictingAttributeImpl.builder().property("type").build();
		
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("R1"))
				.put("componentType", "Relationship")
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("type", ConflictType.HAS_MISSING_REFERENCE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("R1"), 
						"Relationship", 
						Collections.<ConflictingAttribute>singletonList(attribute), 
						ConflictType.HAS_MISSING_REFERENCE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void addedInSourceDetachedInTargetMergeConflict() {
		
		init();
		
		assertConceptCreated(testBranchPath, "C1");

		assertBranchCanBeMerged(testBranchPath, "commit");
		
		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
		
		assertRelationshipCreated(testBranchPath.getParent(), "R1", Concepts.ROOT_CONCEPT, MORPHOLOGIC_ABNORMALITY, symbolicNameMap.get("C1"));
		
		assertRelationshipExists(testBranchPath.getParent(), "R1");
		assertRelationshipNotExists(testBranchPath, "R1");
		
		assertConceptCanBeDeleted(testBranchPath, "C1");
		assertConceptNotExists(testBranchPath, "C1");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "merge");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("C1"))
				.put("componentType", "Concept")
				.put("type", ConflictType.CAUSES_MISSING_REFERENCE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("C1"), 
						"Concept", 
						Collections.<ConflictingAttribute>emptyList(), 
						ConflictType.CAUSES_MISSING_REFERENCE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void deleteReferencedComponentOnSourceMergeConflict() {
		
		init();
		
		assertConceptCreated(testBranchPath, "C1");
		
		assertBranchCanBeMerged(testBranchPath, "merge");
		
		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
		
		assertRefsetMemberCreated(testBranchPath, "M1", symbolicNameMap.get("C1"));
		
		assertRefSetMemberExists(testBranchPath, "M1");
		assertRefSetMemberNotExists(testBranchPath.getParent(), "M1");
		
		assertConceptCanBeDeleted(testBranchPath.getParent(), "C1");
		
		assertConceptNotExists(testBranchPath.getParent(), "C1");
		assertConceptExists(testBranchPath, "C1");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "merge");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder().property("referencedComponent").value(symbolicNameMap.get("C1")).build();
		
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("M1"))
				.put("componentType", "SnomedRefSetMember")
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("type", ConflictType.HAS_MISSING_REFERENCE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("M1"), 
						"SnomedRefSetMember", 
						Collections.<ConflictingAttribute>singletonList(attribute), 
						ConflictType.HAS_MISSING_REFERENCE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void deleteReferencedComponentOnTargetMergeConflict() {
		
		init();
		
		assertConceptCreated(testBranchPath, "C1");
		
		assertBranchCanBeMerged(testBranchPath, "merge");
		
		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
		
		assertRefsetMemberCreated(testBranchPath.getParent(), "M1", symbolicNameMap.get("C1"));
		
		assertRefSetMemberExists(testBranchPath.getParent(), "M1");
		assertRefSetMemberNotExists(testBranchPath, "M1");
		
		assertConceptCanBeDeleted(testBranchPath, "C1");
		
		assertConceptNotExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "merge");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("C1"))
				.put("componentType", "Concept")
				.put("type", ConflictType.CAUSES_MISSING_REFERENCE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("C1"), 
						"Concept",
						Collections.<ConflictingAttribute>emptyList(),
						ConflictType.CAUSES_MISSING_REFERENCE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void differentAcceptabilityForOneDescriptionMergeConflict() throws Exception {
		
		init();
		
		assertDescriptionCreated(testBranchPath, "D1", PREFERRED_ACCEPTABILITY_MAP);
		
		List<String> memberIds = SnomedComponentApiAssert.assertDescriptionExists(testBranchPath, symbolicNameMap.get("D1"), "members()").and().extract().body().path("members.items.id");
		
		assertEquals(1, memberIds.size());
		
		assertBranchCanBeMerged(testBranchPath, "merge");
		
		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D1");

		String newMemberId = UUID.randomUUID().toString();
		
		try (final SnomedEditingContext context = new SnomedEditingContext(testBranchPath)) {
			final SnomedLanguageRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
			member.setUuid(newMemberId);
			member.setActive(true);
			member.setModuleId(Concepts.MODULE_SCT_CORE);
			member.setRefSet(context.lookup(Concepts.REFSET_LANGUAGE_TYPE_UK, SnomedRefSet.class));
			member.setReferencedComponentId(symbolicNameMap.get("D1"));
			member.setAcceptabilityId(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE);
			final Description description = context.lookup(symbolicNameMap.get("D1"), Description.class);
			description.getLanguageRefSetMembers().add(member);
			context.commit("Add member to " + symbolicNameMap.get("D1"));
		}
		
		Collection<Map<String, Object>> members = givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("/{path}/{componentType}?referencedComponentId={componentId}", testBranchPath.getPath(), SnomedComponentType.MEMBER.toLowerCasePlural(), symbolicNameMap.get("D1"))
			.then().extract().body().path("items");
		
		assertEquals(2, members.size());
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath, testBranchPath.getParent(), "merge");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder().property("acceptabilityId").value(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE).build();
			
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", newMemberId)
				.put("componentType", "SnomedLanguageRefSetMember")
				.put("type", ConflictType.CONFLICTING_CHANGE.name())
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("message", MergeConflictImpl.buildDefaultMessage(
						newMemberId, 
						"SnomedLanguageRefSetMember",
						Collections.<ConflictingAttribute>singletonList(attribute),
						ConflictType.CONFLICTING_CHANGE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void inactiveRelationshipDestinationRebaseConflict() {
		
		init();
		
		assertConceptCreated(testBranchPath, "C1");
		
		assertBranchCanBeMerged(testBranchPath, "merge");
		
		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
		
		assertRelationshipCreated(testBranchPath, "R1", Concepts.ROOT_CONCEPT, symbolicNameMap.get("C1"));
		
		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");
		
		final Map<?, ?> changeOnParent = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated concept on parent")
				.build();
		
		assertConceptCanBeUpdated(testBranchPath.getParent(), "C1", changeOnParent);
		
		SnomedComponentApiAssert.assertComponentActive(testBranchPath.getParent(), SnomedComponentType.CONCEPT, symbolicNameMap.get("C1"), false);
		SnomedComponentApiAssert.assertComponentActive(testBranchPath, SnomedComponentType.CONCEPT, symbolicNameMap.get("C1"), true);
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "rebase");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ConflictingAttribute attribute = ConflictingAttributeImpl.builder().property("destinationId").value(symbolicNameMap.get("C1")).build();

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("R1"))
				.put("componentType", "Relationship")
				.put("type", ConflictType.HAS_INACTIVE_REFERENCE.name())
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("R1"), 
						"Relationship",
						Collections.<ConflictingAttribute>singletonList(attribute),
						ConflictType.HAS_INACTIVE_REFERENCE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
}
