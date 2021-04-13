/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.options.MetadataImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.branch.Branching;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.ImmutableMap;

/**
 * Superclass for branch-isolated Java API testing.
 * 
 * @since 7.6
 */
public abstract class AbstractCoreApiTest {

	private final class CoreApiTestWatcher extends ApiTestWatcher {
		
		@Override
		protected void starting(Description description) {
			System.out.println("===== Start of " + description + " =====");
			branchPath = createTestBranchPath(description);
			createBranchRecursively(branchPath);
		}
		
		public void createBranchRecursively(IBranchPath branchPath) {
			createBranchRecursively(branchPath, ImmutableMap.of());
		}

		public void createBranchRecursively(IBranchPath branchPath, Map<String, Object> metadata) {
			
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
				Map<String, Object> currentMetadata = segmentsToCreate.isEmpty() ? metadata : ImmutableMap.of();
				createBranch(currentPath, currentMetadata);
			}
		}
		
	}

	private final Branching branching = RepositoryRequests.branching();
	
	protected IBranchPath branchPath;

	@Rule 
	public final TestWatcher watcher = new CoreApiTestWatcher();

	protected abstract String getRepositoryId();
	
	protected final IEventBus getBus() {
		return Services.bus();
	}
	
	protected final Branch getBranch(IBranchPath branchPath) {
		try {
			return branching.prepareGet(branchPath.getPath())
					.build(getRepositoryId())
					.execute(getBus())
					.getSync(1, TimeUnit.MINUTES);
		} catch (NotFoundException nfe) {
			return null;
		}
	}
	
	protected final String createBranch(IBranchPath branchPath) {
		return createBranch(branchPath, null);
	}
	
	protected final String createBranch(IBranchPath branchPath, Map<String, Object> metadataMap) {
		return branching.prepareCreate()
			.setMetadata(metadataMap != null ? new MetadataImpl(metadataMap) : null)
			.setParent(branchPath.getParentPath())
			.setName(branchPath.lastSegment())
			.build(getRepositoryId())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
	}
	
	protected final Attachment upload(String attachmentFileName) {
		return Attachment.upload(Services.context(), PlatformUtil.toAbsolutePath(getClass(), attachmentFileName));
	}

}
