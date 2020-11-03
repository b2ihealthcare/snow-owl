/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.branch;

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.branch.compare.BranchCompareResult;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.TestMethodNameRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 5.9
 */
public class BranchCompareRequestTest {

	@Rule
	public final TestMethodNameRule methodName = new TestMethodNameRule();
	
	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;
	
	private IEventBus bus;
	private String branchPath;
	
	@Before
	public void setup() {
		bus = Services.bus();
		branchPath = createBranch(Branch.MAIN_PATH, methodName.get());
	}

	@Test
	public void compareEmptyBranchWithoutBase() throws Exception {
		final BranchCompareResult compareResult = compare(null, branchPath);
		
		assertThat(compareResult.getCompareBranch()).isEqualTo(branchPath);
		assertThat(compareResult.getBaseBranch()).isEqualTo(Branch.MAIN_PATH);
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}
	
	@Test
	public void compareEmptyBranchWithBase() throws Exception {
		final BranchCompareResult compareResult = compare(Branch.MAIN_PATH, branchPath);
		
		assertThat(compareResult.getCompareBranch()).isEqualTo(branchPath);
		assertThat(compareResult.getBaseBranch()).isEqualTo(Branch.MAIN_PATH);
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}

	@Test
	public void compareBranchWithNewComponents() throws Exception {
		final Set<ComponentIdentifier> newIds = prepareBranchWithNewChanges(branchPath);
		
		final BranchCompareResult compare = compare(null, branchPath);
		assertThat(compare.getNewComponents()).containsAll(newIds);
		assertThat(compare.getChangedComponents()).doesNotContainAnyElementsOf(newIds);
		assertThat(compare.getDeletedComponents()).isEmpty();
	}

	@Test
	public void compareBranchWithChangedComponents() throws Exception {
		final IBranchPath branch = BranchPathUtils.createPath(branchPath);
		final String newConceptId = createNewConcept(branch);
		final SnomedConcept concept = getComponent(branch, SnomedComponentType.CONCEPT, newConceptId).extract().as(SnomedConcept.class);
		
		final String taskBranchPath = createBranch(branchPath, "taskBranch");
		
		SnomedRequests.prepareUpdateConcept(concept.getId())
			.setModuleId(Concepts.MODULE_SCT_MODEL_COMPONENT)
			.build(REPOSITORY_ID, taskBranchPath, RestExtensions.USER, "Change module ID")
			.execute(bus)
			.getSync();
		
		// compare task branch and its parent
		final BranchCompareResult compare = compare(branchPath, taskBranchPath);
		assertThat(compare.getNewComponents()).isEmpty();
		assertThat(compare.getChangedComponents()).contains(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, newConceptId));
		assertThat(compare.getDeletedComponents()).isEmpty();
	}
	
	@Test
	public void compareBranchWithDeletedComponents() throws Exception {
		final Set<ComponentIdentifier> componentIdsToDelete = prepareBranchWithNewChanges(branchPath);
		
		final String taskBranchPath = createBranch(branchPath, "taskBranch");
		
		final ComponentIdentifier concept = componentIdsToDelete.stream()
				.filter(ci -> ci.getTerminologyComponentId() == SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.findFirst()
				.get();
		
		SnomedRequests.prepareDeleteConcept(concept.getComponentId())
			.build(REPOSITORY_ID, taskBranchPath, RestExtensions.USER, "Delete concept on task branch")
			.execute(bus)
			.getSync();
		
		// compare task branch and its parent
		final BranchCompareResult compare = compare(branchPath, taskBranchPath);
		assertThat(compare.getNewComponents()).isEmpty();
		assertThat(compare.getChangedComponents()).isEmpty();
		assertThat(compare.getDeletedComponents()).containsAll(componentIdsToDelete);
		
		assertTrue("branchPath^ expression search should return the original concept state", SnomedRequests.prepareSearchConcept()
			.one()
			.filterById(concept.getComponentId())
			.build(REPOSITORY_ID, taskBranchPath + RevisionIndex.BASE_REF_CHAR)
			.execute(bus)
			.getSync()
			.first()
			.isPresent());
		
		assertTrue("branchPath expression search should return nothing", SnomedRequests.prepareSearchConcept()
			.one()
			.filterById(concept.getComponentId())
			.build(REPOSITORY_ID, taskBranchPath)
			.execute(bus)
			.getSync()
			.first()
			.isEmpty());
	}
	
	@Test
	public void compareBranchWithNewComponentOnExistingComponent() throws Exception {
		final Set<ComponentIdentifier> componentsOnParentBranch = prepareBranchWithNewChanges(branchPath);
		
		final String taskBranchPath = createBranch(branchPath, "taskBranch");
		
		final ComponentIdentifier concept = componentsOnParentBranch.stream()
				.filter(ci -> ci.getTerminologyComponentId() == SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.findFirst()
				.get();
		
		final String newDescriptionId = SnomedRequests.prepareNewDescription()
				.setIdFromNamespace(null)
				.setConceptId(concept.getComponentId())
				.setTerm("New Description")
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.setLanguageCode("en")
				.setTypeId(Concepts.FULLY_SPECIFIED_NAME)
				.build(REPOSITORY_ID, taskBranchPath, RestExtensions.USER, "Create new Description on Concept: " + concept.getComponentId())
				.execute(bus)
				.getSync(1, TimeUnit.MINUTES)
				.getResultAs(String.class);
		final ComponentIdentifier newDescription = ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, newDescriptionId);
		
		final BranchCompareResult compareResult = compare(branchPath, taskBranchPath);
		
		assertThat(compareResult.getNewComponents()).containsOnly(newDescription);
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}
	
	@Test
	public void remoteJobSupportEmptyCompare() throws Exception {
		final BranchCompareResult compareResult = compareOnJob(Branch.MAIN_PATH, branchPath);
		
		assertThat(compareResult.getCompareBranch()).isEqualTo(branchPath);
		assertThat(compareResult.getBaseBranch()).isEqualTo(Branch.MAIN_PATH);
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}

	@Test
	public void remoteJobSupportCompareWithContent() throws Exception {
		final Set<ComponentIdentifier> newIds = prepareBranchWithNewChanges(branchPath);
		
		final BranchCompareResult compare = compareOnJob(null, branchPath);
		assertThat(compare.getNewComponents()).containsAll(newIds);
		assertThat(compare.getChangedComponents()).doesNotContainAnyElementsOf(newIds);
		assertThat(compare.getDeletedComponents()).isEmpty();
	}
	
	private BranchCompareResult compare(String base, String compare) {
		return prepareCompare(base, compare)
				.execute(bus)
				.getSync();
	}

	private AsyncRequest<BranchCompareResult> prepareCompare(String base, String compare) {
		return RepositoryRequests.branching().prepareCompare()
				.setBase(base)
				.setCompare(compare)
				.build(REPOSITORY_ID);
	}
	
	private String createBranch(String parent, String name) {
		return RepositoryRequests.branching().prepareCreate()
				.setParent(parent)
				.setName(name)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
	}
	
	private Set<ComponentIdentifier> prepareBranchWithNewChanges(String branchPath) {
		final IBranchPath branch = BranchPathUtils.createPath(branchPath);
		final String newConceptId = createNewConcept(branch);
		
		final SnomedConcept concept = getComponent(branch, SnomedComponentType.CONCEPT, newConceptId, "descriptions(expand(members())),relationships()")
			.extract().as(SnomedConcept.class);
		final Set<ComponentIdentifier> newIds = newHashSet();
		newIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept.getId()));
		for (SnomedDescription description : concept.getDescriptions()) {
			newIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, description.getId()));
			for (SnomedReferenceSetMember member : description.getMembers()) {
				newIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, member.getId()));
			}
		}
		for (SnomedRelationship relationship : concept.getRelationships()) {
			newIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship.getId()));
		}
		return newIds;
	}
	
	private BranchCompareResult compareOnJob(String base, String compare) {
		final String compareJobId = JobRequests.prepareSchedule()
			.setRequest(prepareCompare(base, compare).getRequest())
			.setUser(RestExtensions.USER)
			.setDescription(String.format("Comparing %s changes", branchPath))
			.buildAsync()
			.execute(bus)
			.getSync();
		
		final RemoteJobEntry job = JobRequests.waitForJob(bus, compareJobId, 100);
		
 		return job.getResultAs(JsonSupport.getDefaultObjectMapper(), BranchCompareResult.class);
	}
	
}
