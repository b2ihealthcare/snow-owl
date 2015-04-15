/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.internal.branch;

import com.b2international.snowowl.datastore.branch.Branch;

/**
 * @since 4.1
 */
public class MainBranchImpl extends BranchImpl {

    static final String DEFAULT_PATH = "MAIN";

	MainBranchImpl(BranchManagerImpl branchManager, long baseTimestamp) {
		super(branchManager, DEFAULT_PATH, "", baseTimestamp);
	}
	
	private MainBranchImpl(BranchManagerImpl branchManager, long baseTimestamp, long headTimestamp) {
		super(branchManager, DEFAULT_PATH, "", baseTimestamp, headTimestamp);
	}

	@Override
	BranchImpl withBaseTimestamp(long newBaseTimestamp) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	BranchImpl withDeleted() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	MainBranchImpl withHeadTimestamp(long newHeadTimestamp) {
		return new MainBranchImpl(branchManager, baseTimestamp(), newHeadTimestamp);
	}

	@Override
	public String path() {
		return name();
	}
	
	@Override
	public Branch parent() {
		return this;
	}
	
	@Override
	public BranchState state() {
		return BranchState.UP_TO_DATE;
	}
	
	@Override
	public Branch delete() {
		throw new UnsupportedOperationException(path() + " cannot be deleted");
	}
	
	@Override
	public BranchState state(Branch target) {
		throw new UnsupportedOperationException(path() + " cannot compute state compared to target " + target.path());
	}
	
	@Override
	public Branch rebase(Branch target) {
		throw new UnsupportedOperationException(path() + " cannot be rebased");
	}
}
