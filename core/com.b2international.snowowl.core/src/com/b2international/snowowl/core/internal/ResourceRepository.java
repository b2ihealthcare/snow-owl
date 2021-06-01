/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal;

import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.Index;
import com.b2international.index.revision.*;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.VersionDocument;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 8.0
 */
public final class ResourceRepository implements RevisionIndex {

	private final RevisionIndex index;

	public ResourceRepository(RevisionIndex index) {
		this.index = index;
		this.index.admin().create();
		// prepare precommit hook to delete associated versions when deleting a resource
		hooks().addHook(new ResourceRepositoryPreCommitHook());
	}

	@Override
	public RevisionIndexAdmin admin() {
		return index.admin();
	}

	@Override
	public String name() {
		return index.name();
	}

	public <T> T read(RevisionIndexRead<T> read) {
		return index.read(Branch.MAIN_PATH, read);
	}
	
	@Override
	public <T> T read(String branchPath, RevisionIndexRead<T> read) {
		throw new UnsupportedOperationException("This repository does not support non-MAIN branches, please use #read(RevisionIndexRead<T>)");
	}

	@Override
	public void purge(String branchPath, Purge purge) {
		index.purge(branchPath, purge);
	}

	@Override
	public RevisionCompare compare(String branch) {
		return index.compare(branch);
	}

	@Override
	public RevisionCompare compare(String branch, int limit, boolean excludeComponentChanges) {
		return index.compare(branch, limit, excludeComponentChanges);
	}

	@Override
	public RevisionCompare compare(String baseBranch, String compareBranch) {
		return index.compare(baseBranch, compareBranch);
	}

	@Override
	public RevisionCompare compare(String baseBranch, String compareBranch, int limit, boolean excludeComponentChanges) {
		return index.compare(baseBranch, compareBranch, limit, excludeComponentChanges);
	}

	@Override
	public BaseRevisionBranching branching() {
		return index.branching();
	}

	public StagingArea prepareCommit() {
		return prepareCommit(Branch.MAIN_PATH);
	}
	
	@Override
	public StagingArea prepareCommit(String branchPath) {
		// this repository uses a single MAIN branch only
		if (!Branch.MAIN_PATH.equals(branchPath)) {
			throw new UnsupportedOperationException("This repository does not support non-MAIN branches, please use #prepareCommit()");
		}
		return index.prepareCommit(branchPath);
	}

	@Override
	public Hooks hooks() {
		return index.hooks();
	}

	@Override
	public Index index() {
		return index.index();
	}
	
	private static final class ResourceRepositoryPreCommitHook implements Hooks.PreCommitHook {

		@Override
		public void run(StagingArea staging) {
			RepositoryContext context = (RepositoryContext) staging.getContext();
			
			// stage deletion of all version documents as well when deleting a resource
			final Multimap<String, ResourceDocument> resourceUrisByTooling = HashMultimap.create();
			staging.getRemovedObjects(ResourceDocument.class).forEach(deletedResource -> {
				if (deletedResource.getToolingId() != null) {
					resourceUrisByTooling.put(deletedResource.getToolingId(), deletedResource);
				}
			});

			// perform cleanup per tooling repository
			for (String toolingId : resourceUrisByTooling.keySet()) {
				BaseRevisionBranching branching = context.service(RepositoryManager.class).get(toolingId).service(BaseRevisionBranching.class);
				
				final Set<String> resources = resourceUrisByTooling.get(toolingId).stream().map(ResourceDocument::getResourceURI).map(ResourceURI::toString).collect(Collectors.toSet());
				
				ResourceRequests.prepareSearchVersion()
					.all()
					.filterByResources(resources)
					.build()
					.execute(context)
					.forEach(version -> {
						staging.stageRemove(version.getId(), VersionDocument.builder()
								.id(version.getId())
								.build());
						
						// delete version branch
						branching.delete(version.getBranchPath());
					});
				
				// also delete branch of the code system and all possible task or child branches
				resourceUrisByTooling.get(toolingId)
					.stream()
					.map(ResourceDocument::getBranchPath)
					.forEach(branching::delete);
				
			}
		}
		
	}
	
}
