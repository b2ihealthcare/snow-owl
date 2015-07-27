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
package com.b2international.snowowl.datastore.server.internal.review;

import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.server.review.BranchState;

/**
 * @since 4.2
 */
public class BranchStateImpl implements BranchState {

	private final String path;
	private final long baseTimestamp;
	private final long headTimestamp;

	public BranchStateImpl(final Branch branch) {
		this(branch.path(), branch.baseTimestamp(), branch.headTimestamp());
	}

	private BranchStateImpl(final String path, final long baseTimestamp, final long headTimestamp) {
		this.path = path;
		this.baseTimestamp = baseTimestamp;
		this.headTimestamp = headTimestamp;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public long baseTimestamp() {
		return baseTimestamp;
	}

	@Override
	public long headTimestamp() {
		return headTimestamp;
	}

	@Override
	public boolean matches(final Branch branch) {
		return branch.path().equals(path) 
				&& branch.baseTimestamp() == baseTimestamp 
				&& branch.headTimestamp() == headTimestamp;
	}
}
