/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.japi.branches;

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.compare.CompareResult;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.TestMethodNameRule;

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
		bus = ApplicationContext.getInstance().getService(IEventBus.class);
		branchPath = createBranch(Branch.MAIN_PATH, methodName.get());
	}

	@Test
	public void compareEmptyBranchWithoutBase() throws Exception {
		final CompareResult compareResult = compare(null, branchPath);
		
		assertThat(compareResult.getCompareBranch()).isEqualTo(branchPath);
		assertThat(compareResult.getBaseBranch()).isEqualTo(Branch.MAIN_PATH);
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}
	
	@Test
	public void compareEmptyBranchWithBase() throws Exception {
		final CompareResult compareResult = compare(Branch.MAIN_PATH, branchPath);
		
		assertThat(compareResult.getCompareBranch()).isEqualTo(branchPath);
		assertThat(compareResult.getBaseBranch()).isEqualTo(Branch.MAIN_PATH);
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}

	@Test
	public void compareBranchWithNewComponents() throws Exception {
		final Set<ComponentIdentifier> newIds = prepareBranchWithNewChanges(branchPath);
		
		final CompareResult compare = compare(null, branchPath);
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
			.build(REPOSITORY_ID, taskBranchPath, "info@b2international.com", "Change module ID")
			.execute(bus)
			.getSync();
		
		// compare task branch and its parent
		final CompareResult compare = compare(branchPath, taskBranchPath);
		assertThat(compare.getNewComponents()).isEmpty();
		assertThat(compare.getChangedComponents()).contains(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, newConceptId));
		assertThat(compare.getDeletedComponents()).isEmpty();
	}
	
	@Test
	public void compareBranchWithDeletedComponents() throws Exception {
		final Set<ComponentIdentifier> deletedIds = prepareBranchWithNewChanges(branchPath);
		
		final String taskBranchPath = createBranch(branchPath, "taskBranch");
		
		final ComponentIdentifier concept = deletedIds.stream()
				.filter(ci -> ci.getTerminologyComponentId() == SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.findFirst()
				.get();
		
		SnomedRequests.prepareDeleteConcept(concept.getComponentId())
			.build(REPOSITORY_ID, taskBranchPath, "info@b2international.com", "Delete concept on task branch")
			.execute(bus)
			.getSync();
		
		// compare task branch and its parent
		final CompareResult compare = compare(branchPath, taskBranchPath);
		assertThat(compare.getNewComponents()).isEmpty();
		assertThat(compare.getChangedComponents()).isEmpty();
		assertThat(compare.getDeletedComponents()).containsAll(deletedIds);
	}
	
	@Test
	public void remoteJobSupportEmptyCompare() throws Exception {
		final CompareResult compareResult = compareOnJob(Branch.MAIN_PATH, branchPath);
		
		assertThat(compareResult.getCompareBranch()).isEqualTo(branchPath);
		assertThat(compareResult.getBaseBranch()).isEqualTo(Branch.MAIN_PATH);
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}

	@Test
	public void remoteJobSupportCompareWithContent() throws Exception {
		final Set<ComponentIdentifier> newIds = prepareBranchWithNewChanges(branchPath);
		
		final CompareResult compare = compareOnJob(null, branchPath);
		assertThat(compare.getNewComponents()).containsAll(newIds);
		assertThat(compare.getChangedComponents()).doesNotContainAnyElementsOf(newIds);
		assertThat(compare.getDeletedComponents()).isEmpty();
	}
	
	private RemoteJobEntry waitDone(final String jobId) throws InterruptedException {
		RemoteJobEntry entry = null;
		do {
			Thread.sleep(100);
			entry = get(jobId);
		} while (!entry.isDone());
		return entry;
	}

	private RemoteJobEntry get(final String jobId) {
		return JobRequests.prepareGet(jobId).buildAsync().execute(bus).getSync();
	}
	
	private CompareResult compare(String base, String compare) {
		return prepareCompare(base, compare)
				.execute(bus)
				.getSync();
	}

	private AsyncRequest<CompareResult> prepareCompare(String base, String compare) {
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
	
	private CompareResult compareOnJob(String base, String compare) throws Exception {
		final String compareJobId = JobRequests.prepareSchedule()
			.setRequest(prepareCompare(base, compare).getRequest())
			.setUser("test@b2i.sg")
			.setDescription(String.format("Comparing %s changes", branchPath))
			.buildAsync()
			.execute(bus)
			.getSync();
		
		final RemoteJobEntry job = waitDone(compareJobId);
		
 		return job.getResultAs(JsonSupport.getDefaultObjectMapper(), CompareResult.class);
	}
	
}
