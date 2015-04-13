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
package com.b2international.snowowl.datastore.branch;

/**
 * @since 4.1
 */
public class MainBranch extends BranchImpl {

    private static final long DEFAULT_TIMESTAMP = 0L;
	static final String DEFAULT_PATH = "MAIN";

	// For testing only
	MainBranch() {
		this(DEFAULT_TIMESTAMP);
	}
	
	public MainBranch(long baseTimestamp) {
		super(null, DEFAULT_PATH, baseTimestamp, baseTimestamp - 1L);
	}

	@Override
	public String path() {
		return name();
	}

	@Override
	public MainBranch parent() {
		return this;
	}
	
	@Override
	public BranchState state() {
		return BranchState.UP_TO_DATE;
	}
	
	@Override
	public BranchState state(Branch target) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Branch rebase(Branch target) {
		throw new UnsupportedOperationException(path() + " branch cannot be rebased");
	}
	
}
