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
package com.b2international.snowowl.test.commons;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.branch.Branching;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.test.commons.rest.BranchBase;
import com.google.common.collect.ImmutableMap;

/**
 * Superclass for branch-isolated Java API testing.
 * @since 7.6
 */
@BranchBase(Branch.MAIN_PATH)
public abstract class AbstractCoreApiTest {

	private final class CoreApiTestWatcher extends ApiTestWatcher {
		
		private final Branching branching = RepositoryRequests.branching();

		@Override
		protected void starting(Description description) {
			System.out.println("===== Start of " + description + " =====");
			branchPath = createTestBranchPath(description);
			createBranchRecursively(branchPath);
		}
		
		public void createBranchRecursively(IBranchPath branchPath) {
			createBranchRecursively(branchPath, ImmutableMap.of());
		}

		public void createBranchRecursively(IBranchPath branchPath, Map<?, ?> metadata) {
			
			IBranchPath currentPath = branchPath;
			Branch branch = getBranch(currentPath);

			List<String> segmentsToCreate = newArrayList();

			// Step upwards until we find an existing branch
			while (branch == null) {
				segmentsToCreate.add(segmentsToCreate.size(), currentPath.lastSegment());
				currentPath = currentPath.getParent();
				branch = getBranch(currentPath);
			}

			// Step downwards and create all non-existing segments
			while (!segmentsToCreate.isEmpty()) {
				currentPath = BranchPathUtils.createPath(currentPath, segmentsToCreate.remove(segmentsToCreate.size() - 1));
				Map<?, ?> currentMetadata = segmentsToCreate.isEmpty() ? metadata : ImmutableMap.of();
				createBranch(currentPath, currentMetadata);
			}
		}
		
		public Branch getBranch(IBranchPath branchPath) {
			
			Branch branch = null;
			try {
				branch = branching.prepareGet(branchPath.getPath())
						.build(getRepositoryId())
						.execute(getBus())
						.getSync();
			} catch (NotFoundException nfe) {
				//do nothing
			}
			return branch;
		}
		
		public String createBranch(IBranchPath branchPath, Map<?, ?> metadataMap) {
			
			final Metadata metadata = new MetadataImpl();
			metadataMap.forEach((k, v) -> metadata.put((String) k, v));
			
			return branching.prepareCreate()
				.setMetadata(metadata)
				.setParent(branchPath.getParentPath())
				.setName(branchPath.lastSegment())
				.build(getRepositoryId())
				.execute(getBus())
				.getSync();
		}
	}

	protected IBranchPath branchPath;

	@Rule 
	public final TestWatcher watcher = new CoreApiTestWatcher();

	protected abstract String getRepositoryId();
	
	protected final IEventBus getBus() {
		return Services.bus();
	}

}
