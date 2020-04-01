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
package com.b2international.snowowl.test.commons.rest;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.test.commons.ApiTestWatcher;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.base.Joiner;

/**
 * @since 2.0
 */
@BranchBase(Branch.MAIN_PATH)
public abstract class AbstractApiTest {

	public static final Joiner PATH_JOINER = Joiner.on('/');
	
	private final class CustomTestWatcher extends ApiTestWatcher {
		
		private final RepositoryBranchRestRequests branching;

		public CustomTestWatcher(RepositoryBranchRestRequests branching) {
			this.branching = branching;
		}
		
		@Override
		protected void starting(Description description) {
			System.out.println("===== Start of " + description + " =====");
			branchPath = createTestBranchPath(description);
			branching.createBranchRecursively(branchPath);
		}
	}

	protected IBranchPath branchPath;

	protected final IEventBus getBus() {
		return Services.bus();
	}
	
	protected final RepositoryBranchRestRequests branching = new RepositoryBranchRestRequests(getApiBaseUrl());
	
	@Rule 
	public final TestWatcher watcher = new CustomTestWatcher(branching);

	protected abstract String getApiBaseUrl();

}
