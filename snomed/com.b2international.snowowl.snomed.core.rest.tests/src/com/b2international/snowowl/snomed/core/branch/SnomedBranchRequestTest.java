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
package com.b2international.snowowl.snomed.core.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.SortedSet;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionSegment;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.branch.Branching;
import com.b2international.snowowl.core.branch.Merging;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.TestMethodNameRule;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public class SnomedBranchRequestTest {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;
	
	@Rule
	public TestMethodNameRule methodName = new TestMethodNameRule(); 
	
	private IEventBus bus;
	private String branchPath;
	
	@Before
	public void setup() {
		bus = Services.bus();
		branchPath = RepositoryRequests.branching().prepareCreate()
				.setParent(Branch.MAIN_PATH)
				.setName(methodName.get())
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
	}
	
	@Test
	public void createTwoBranchesSameTimeWithSameName() throws Exception {
		final Branching branches = RepositoryRequests.branching();
		
		// try to create two branches at the same time
		final String branchName = UUID.randomUUID().toString();
		final Promise<String> first = branches.prepareCreate().setParent(branchPath).setName(branchName).build(REPOSITORY_ID).execute(bus);
		final Promise<String> second = branches.prepareCreate().setParent(branchPath).setName(branchName).build(REPOSITORY_ID).execute(bus);
		final boolean sameBaseTimestamp = Promise.all(first, second)
			.then(input -> {
				final Branch first1 = branches.prepareGet((String) input.get(0)).build(REPOSITORY_ID).execute(bus).getSync();
				final Branch second1 = branches.prepareGet((String) input.get(1)).build(REPOSITORY_ID).execute(bus).getSync();
				return first1.baseTimestamp() == second1.baseTimestamp(); 
			})
			// if the API throws an error, then rethrow anything that is not an AlreadyExistsException, which indicates slow execution, but not failure
			.fail(e -> {
				if (e instanceof AlreadyExistsException) {
					return true;
				} else {
					throw new RuntimeException(e);
				}
			})
			.getSync();
		// fail the test only when the API managed to create two branches with different base timestamp which should not happen
		if (!sameBaseTimestamp) {
			fail("Two branches created with the same name but different baseTimestamp");
		}
	}
	
	@Test
	public void createTwoBranchesSameTimeWithDifferentName() throws Exception {
		final Branching branches = RepositoryRequests.branching();
		
		// try to create two branches at the same time
		final String branchA = UUID.randomUUID().toString();
		final String branchB = UUID.randomUUID().toString();
		final Promise<String> first = branches.prepareCreate()
				.setParent(branchPath)
				.setName(branchA)
				.build(REPOSITORY_ID)
				.execute(bus);
		
		final Promise<String> second = branches.prepareCreate()
				.setParent(branchPath)
				.setName(branchB)
				.build(REPOSITORY_ID)
				.execute(bus);
		
		Promise.all(first, second).then(input -> {
			
			final Branch firstBranch = branches.prepareGet((String) input.get(0)).build(REPOSITORY_ID).execute(bus).getSync();
			final Branch secondBranch = branches.prepareGet((String) input.get(1)).build(REPOSITORY_ID).execute(bus).getSync();
			
			assertBranchesCreated(branchA, branchB, firstBranch, secondBranch);
			assertBranchSegmentsValid(branchPath, firstBranch.path(), secondBranch.path());
			return null;
		})
		.getSync();
	}

	@Test
	public void createBranchAndCommitToParent() throws Exception {
		final Branching branches = RepositoryRequests.branching();
		final Merging merges = RepositoryRequests.merging();
		
		final String branchA = UUID.randomUUID().toString();
		final String branchB = UUID.randomUUID().toString();

		final String first = branches.prepareCreate()
				.setParent(branchPath)
				.setName(branchA)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
		
		final SnomedDescriptionCreateRequestBuilder fsnBuilder = SnomedRequests.prepareNewDescription()
				.setIdFromNamespace(SnomedIdentifiers.INT_NAMESPACE)
				.setModuleId(Concepts.MODULE_ROOT)
				.setTerm("FSN " + branchA)
				.setTypeId(Concepts.FULLY_SPECIFIED_NAME)
				.setAcceptability(ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		final SnomedDescriptionCreateRequestBuilder ptBuilder = SnomedRequests.prepareNewDescription()
				.setIdFromNamespace(SnomedIdentifiers.INT_NAMESPACE)
				.setModuleId(Concepts.MODULE_ROOT)
				.setTerm("PT " + branchA)
				.setTypeId(Concepts.SYNONYM)
				.setAcceptability(ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		final AsyncRequest<CommitResult> conceptRequest = SnomedRequests.prepareNewConcept()
				.setModuleId(Concepts.MODULE_ROOT)
				.setIdFromNamespace(SnomedIdentifiers.INT_NAMESPACE)
				.addParent(Concepts.ROOT_CONCEPT)
				.addDescription(fsnBuilder)
				.addDescription(ptBuilder)
				.build(REPOSITORY_ID, first, "user", "Created new concept");
		
		final CommitResult info = conceptRequest.execute(bus).getSync();
		final String conceptId = info.getResultAs(String.class);
		
		final String firstParentPath = BranchPathUtils.createPath(first).getParentPath();
		final Request<ServiceProvider, Merge> mergeRequest = merges.prepareCreate()
				.setSource(first)
				.setTarget(firstParentPath)
				.setUserId(User.SYSTEM.getUsername())
				.setCommitComment("Merging changes")
				.build(REPOSITORY_ID)
				.getRequest();
		
		final String mergeJobId = JobRequests.prepareSchedule()
			.setDescription("Merging changes")
			.setRequest(mergeRequest)
			.setUser(User.SYSTEM.getUsername())
			.buildAsync()
			.execute(bus)
			.getSync();
		
		final RemoteJobEntry mergeJobResult = JobRequests.waitForJob(bus, mergeJobId);
		final Merge merge = mergeJobResult.getResultAs(JsonSupport.getDefaultObjectMapper(), Merge.class);
		
		assertEquals(true, merge.getConflicts().isEmpty());
		
		String second = branches.prepareCreate()
				.setParent(firstParentPath)
				.setName(branchB)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();

		final Branch sourceBranch = branches.prepareGet(merge.getSource()).build(REPOSITORY_ID).execute(bus).getSync();
		final Branch secondBranch = branches.prepareGet(second).build(REPOSITORY_ID).execute(bus).getSync();
		
		assertBranchesCreated(branchA, branchB, sourceBranch, secondBranch);
		assertBranchSegmentsValid(merge.getTarget(), sourceBranch.path(), secondBranch.path());
			
		// Check that the concept is visible on parent
		SnomedRequests.prepareGetConcept(conceptId)
				.build(REPOSITORY_ID, firstParentPath)
				.execute(bus)
				.getSync();
		
	}

	private void assertBranchesCreated(final String branchA, final String branchB, final Branch first, final Branch second) {
		assertEquals(branchA, first.name());
		assertEquals(branchB, second.name());
	}
	
	// assert the low level segments
	private void assertBranchSegmentsValid(final String parentPath, final String createdFirstPath, final String createdSecondPath) {
	
		BaseRevisionBranching branching = ApplicationContext.getServiceForClass(RepositoryManager.class).get(REPOSITORY_ID).service(BaseRevisionBranching.class);
		
		RevisionBranch parent = branching.getBranch(parentPath);
		RevisionBranch createdFirst = branching.getBranch(createdFirstPath);
		RevisionBranch createdSecond = branching.getBranch(createdSecondPath);

		SortedSet<RevisionSegment> firstParentSegments = createdFirst.getParentSegments();
		SortedSet<RevisionSegment> secondParentSegments = createdSecond.getParentSegments();
		
		// verify that first and second has a common parent start address (END address is their base timestamp, so that will be different)
		assertEquals(firstParentSegments.last().getStartAddress(), secondParentSegments.last().getStartAddress());
		assertNotEquals(firstParentSegments.last().getEndAddress(), secondParentSegments.last().getEndAddress());
		
		// and parent-parent-segments of first and second are equal
		assertEquals(firstParentSegments.headSet(firstParentSegments.last()), secondParentSegments.headSet(secondParentSegments.last()));
	}

}
