/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.request.Branching;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.compare.CompareResult;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * @since 5.9
 */
public class BranchCompareRequestTest {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;
	
	private IEventBus bus;
	
	@Before
	public void setup() {
		bus = ApplicationContext.getInstance().getService(IEventBus.class);
	}
	
	@Test
	public void compareEmptyBranchWithoutBase() throws Exception {
		final Branching branches = RepositoryRequests.branching();
		final Branch branchA = branches.prepareCreate()
			.setParent(Branch.MAIN_PATH)
			.setName("compareEmptyBranchWithoutBase")
			.build(REPOSITORY_ID)
			.execute(bus)
			.getSync();
		
		final CompareResult compareResult = branches.prepareCompare().setCompare(branchA.path()).build(REPOSITORY_ID).execute(bus).getSync();
		assertThat(compareResult.getCompareBranch()).isEqualTo(branchA.path());
		assertThat(compareResult.getBaseBranch()).isEqualTo(Branch.MAIN_PATH);
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}
	
}
