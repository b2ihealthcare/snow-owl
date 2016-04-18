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
package com.b2international.snowowl.snomed.api.japi.branches;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.request.Branching;
import com.b2international.snowowl.datastore.server.internal.CDOBasedRepository;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.7
 */
public class SnomedBranchRequestTest {

	private IEventBus bus;
	private CDOBranchManager cdoBranchManager;
	
	@Before
	public void setup() {
		bus = ApplicationContext.getInstance().getService(IEventBus.class);
		cdoBranchManager = getSnomedCdoBranchManager();
	}

	@Test
	public void createTwoBranchesSameTimeWithSameName() throws Exception {
		final Branching branches = SnomedRequests.branching();
		
		// try to create two branches at the same time
		final String branchName = UUID.randomUUID().toString();
		final Promise<Branch> first = branches.prepareCreate().setParent(Branch.MAIN_PATH).setName(branchName).build().execute(bus);
		final Promise<Branch> second = branches.prepareCreate().setParent(Branch.MAIN_PATH).setName(branchName).build().execute(bus);
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
		final Branching branches = SnomedRequests.branching();
		
		// try to create two branches at the same time
		final String branchA = UUID.randomUUID().toString();
		final String branchB = UUID.randomUUID().toString();
		final Promise<Branch> first = branches.prepareCreate().setParent(Branch.MAIN_PATH).setName(branchA).build().execute(bus);
		final Promise<Branch> second = branches.prepareCreate().setParent(Branch.MAIN_PATH).setName(branchB).build().execute(bus);
		final Boolean success = Promise.all(first, second)
			.then(new Function<List<Object>, Boolean>() {
				@Override
				public Boolean apply(List<Object> input) {
					final Branch first = (Branch) input.get(0);
					final Branch second = (Branch) input.get(1);
					return first.name().equals(branchA) && second.name().equals(branchB);
				}
			})
			.fail(new Function<Throwable, Boolean>() {
				@Override
				public Boolean apply(Throwable input) {
					return false;
				}
			})
			.getSync();
		assertTrue(success);
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
		return ((CDOBasedRepository) repositoryManager.get(SnomedDatastoreActivator.REPOSITORY_UUID)).getCdoBranchManager();
	}
	
}
