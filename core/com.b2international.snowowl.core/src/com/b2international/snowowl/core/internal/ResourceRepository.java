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

import com.b2international.index.Index;
import com.b2international.index.revision.*;
import com.b2international.snowowl.core.branch.Branch;

/**
 * @since 8.0
 */
public final class ResourceRepository implements RevisionIndex {

	private final RevisionIndex index;

	public ResourceRepository(RevisionIndex index) {
		this.index = index;
		this.index.admin().create();
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
	
}
