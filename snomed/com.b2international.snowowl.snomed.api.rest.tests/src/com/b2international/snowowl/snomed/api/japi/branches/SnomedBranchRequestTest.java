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
package com.b2international.snowowl.snomed.api.japi.branches;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.Merge.Status;
import com.b2international.snowowl.datastore.internal.branch.InternalCDOBasedBranch;
import com.b2international.snowowl.datastore.request.Branching;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.Merging;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.server.internal.CDOBasedRepository;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @since 4.7
 */
public class SnomedBranchRequestTest {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;
	
	private static final long POLL_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
	private static final long POLL_INTERVAL = TimeUnit.SECONDS.toMillis(1L);
	
	private IEventBus bus;
	private CDOBranchManager cdoBranchManager;
	
	@Before
	public void setup() {
		bus = ApplicationContext.getInstance().getService(IEventBus.class);
		cdoBranchManager = getSnomedCdoBranchManager();
	}

	@Test
	public void createTwoBranchesSameTimeWithSameName() throws Exception {
		final Branching branches = RepositoryRequests.branching();
		
		// try to create two branches at the same time
		final String branchName = UUID.randomUUID().toString();
		final Promise<Branch> first = branches.prepareCreate().setParent(Branch.MAIN_PATH).setName(branchName).build(REPOSITORY_ID).execute(bus);
		final Promise<Branch> second = branches.prepareCreate().setParent(Branch.MAIN_PATH).setName(branchName).build(REPOSITORY_ID).execute(bus);
		final String error = Promise.all(first, second)
			.then(new Function<List<Object>, String>() {
				@Override
				public String apply(List<Object> input) {
					final Branch first = (Branch) input.get(0);
					final Branch second = (Branch) input.get(1);
					return first.baseTimestamp() == second.baseTimestamp() ? null : "Two branches created with the same name but different baseTimestamp";
				}
			})
			.fail(new Function<Throwable, String>() {
				@Override
				public String apply(Throwable input) {
					return input.getMessage() != null ? input.getMessage() : Throwables.getRootCause(input).getClass().getSimpleName();
				}
			})
			.getSync();
		assertNull(error, error);
		assertEquals(1, getCdoBranches(branchName).size());
	}
	
	@Test
	public void createTwoBranchesSameTimeWithDifferentName() throws Exception {
		final Branching branches = RepositoryRequests.branching();
		
		// try to create two branches at the same time
		final String branchA = UUID.randomUUID().toString();
		final String branchB = UUID.randomUUID().toString();
		final Promise<Branch> first = branches.prepareCreate()
				.setParent(Branch.MAIN_PATH)
				.setName(branchA)
				.build(REPOSITORY_ID)
				.execute(bus);
		
		final Promise<Branch> second = branches.prepareCreate()
				.setParent(Branch.MAIN_PATH)
				.setName(branchB)
				.build(REPOSITORY_ID)
				.execute(bus);
		
		Promise.all(first, second).then(new Function<List<Object>, String>() {
			@Override
			public String apply(List<Object> input) {
				final InternalCDOBasedBranch main = (InternalCDOBasedBranch) branches.prepareGet(Branch.MAIN_PATH)
						.build(REPOSITORY_ID)
						.execute(bus)
						.getSync();
				
				final InternalCDOBasedBranch createdFirst;
				final InternalCDOBasedBranch createdSecond;
				
				if (((Branch) input.get(0)).headTimestamp() > ((Branch) input.get(1)).headTimestamp()) {
					createdFirst = (InternalCDOBasedBranch) input.get(1);
					createdSecond = (InternalCDOBasedBranch) input.get(0);
				} else {
					createdFirst = (InternalCDOBasedBranch) input.get(0);
					createdSecond = (InternalCDOBasedBranch) input.get(1);
				}
				
				assertBranchesCreated(branchA, branchB, ((Branch) input.get(0)), ((Branch) input.get(1)));
				assertBranchSegmentsValid(main, createdFirst, createdSecond);
				return null;
			}
		})
		.getSync();
	}

	@Test
	public void createBranchAndCommitToParent() throws Exception {
		final Branching branches = RepositoryRequests.branching();
		final Merging merges = RepositoryRequests.merging();
		
		final String branchA = UUID.randomUUID().toString();
		final String branchB = UUID.randomUUID().toString();

		final Branch first = branches.prepareCreate()
				.setParent(Branch.MAIN_PATH)
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
				.build(REPOSITORY_ID, first.path(), "user", "Created new concept");
		
		final CommitResult info = conceptRequest.execute(bus).getSync();
		final String conceptId = info.getResultAs(String.class);
		
		Promise<Merge> merge = merges.prepareCreate()
				.setSource(first.path())
				.setTarget(first.parentPath())
				.setUserId(User.SYSTEM.getUsername())
				.setCommitComment("Merging changes")
				.build(REPOSITORY_ID)
				.execute(bus);
		
		final long endTime = System.currentTimeMillis() + POLL_TIMEOUT;
		while (System.currentTimeMillis() < endTime && mergeNotCompleted(merge)) {
			
			try {
				Thread.sleep(POLL_INTERVAL);
			} catch (final InterruptedException e) {
				fail(e.toString());
			}

			merge = merges.prepareGet(merge.getSync().getId())
					.build(REPOSITORY_ID)
					.execute(bus);
		}
		
		final Promise<Branch> second = branches.prepareCreate()
				.setParent(first.parentPath())
				.setName(branchB)
				.build(REPOSITORY_ID)
				.execute(bus);
		
		Promise.all(merge, second).then(new Function<List<Object>, Void>() {
			@Override
			public Void apply(final List<Object> input) {
				final Merge merge = (Merge) input.get(0);
				assertEquals(Status.COMPLETED, merge.getStatus());
				
				final InternalCDOBasedBranch target = (InternalCDOBasedBranch) branches.prepareGet(merge.getTarget())
						.build(REPOSITORY_ID)
						.execute(bus)
						.getSync();
				
				final InternalCDOBasedBranch first = (InternalCDOBasedBranch) branches.prepareGet(merge.getSource())
						.build(REPOSITORY_ID)
						.execute(bus)
						.getSync();
				
				final InternalCDOBasedBranch second = (InternalCDOBasedBranch) input.get(1);
				final InternalCDOBasedBranch createdFirst;
				final InternalCDOBasedBranch createdSecond;
				
				if (first.headTimestamp() > second.headTimestamp()) {
					createdFirst = second;
					createdSecond = first;
				} else {
					createdFirst = first;
					createdSecond = second;
				}
				
				assertBranchesCreated(branchA, branchB, first, second);
				assertBranchSegmentsValid(target, createdFirst, createdSecond);
				return null;
			}
		})
		.getSync();
		
		// Check that the concept is visible on parent
		SnomedRequests.prepareGetConcept(conceptId)
				.build(REPOSITORY_ID, first.parentPath())
				.execute(bus)
				.getSync();
	}

	private boolean mergeNotCompleted(Promise<Merge> merge) {
		final Merge mergeData = merge.getSync();
		final Status mergeStatus = mergeData.getStatus();
		return Status.IN_PROGRESS.equals(mergeStatus) || Status.SCHEDULED.equals(mergeStatus) || Status.CANCEL_REQUESTED.equals(mergeStatus);
	}

	private void assertBranchesCreated(final String branchA, final String branchB, final Branch first, final Branch second) {
		assertEquals(branchA, first.name());
		assertEquals(branchB, second.name());
	}
	
	private void assertBranchSegmentsValid(final InternalCDOBasedBranch parent, final InternalCDOBasedBranch createdFirst, final InternalCDOBasedBranch createdSecond) {
		// All segments of the parent except the last should be present in the second child branch's parentSegment collection
		SortedSet<Integer> parentSegments = Sets.newTreeSet(parent.segments());
		parentSegments.remove(parent.segmentId());

		assertEquals(createdSecond.parentSegments().size(), parentSegments.size());
		assertTrue(createdSecond.parentSegments().containsAll(parentSegments));
		
		// Remove the greatest value to get the first child branch's parentSegments
		parentSegments = parentSegments.headSet(parentSegments.last());

		assertEquals(createdFirst.parentSegments().size(), parentSegments.size());
		assertTrue(createdFirst.parentSegments().containsAll(parentSegments));
	}

	private Set<CDOBranch> getCdoBranches(final String branchName) {
		return FluentIterable.from(newArrayList(cdoBranchManager.getMainBranch().getBranches()))
			.filter(new Predicate<CDOBranch>() {
				@Override
				public boolean apply(CDOBranch input) {
					return input.getName().equals(branchName);
				}
			})
			.toSet();
	}
	
	private CDOBranchManager getSnomedCdoBranchManager() {
		final RepositoryManager repositoryManager = ApplicationContext.getInstance().getService(RepositoryManager.class);
		return ((CDOBasedRepository) repositoryManager.get(REPOSITORY_ID)).getCdoBranchManager();
	}
}
