/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.review;

import com.b2international.index.Doc;
import com.b2international.snowowl.core.branch.Branch;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.2
 */
@Doc(type="branchState", nested=true)
public final class BranchState {

	private final String path;
	private final long baseTimestamp;
	private final long headTimestamp;

	public BranchState(final Branch branch) {
		this(branch.path(), branch.baseTimestamp(), branch.headTimestamp());
	}

	@JsonCreator
	BranchState(@JsonProperty("path") final String path, 
			@JsonProperty("baseTimestamp") final long baseTimestamp, 
			@JsonProperty("headTimestamp") final long headTimestamp) {
		this.path = path;
		this.baseTimestamp = baseTimestamp;
		this.headTimestamp = headTimestamp;
	}
	
	@JsonProperty
	public String path() {
		return path;
	}

	@JsonProperty
	public long baseTimestamp() {
		return baseTimestamp;
	}

	@JsonProperty
	public long headTimestamp() {
		return headTimestamp;
	}

	public boolean matches(final Branch branch) {
		return branch.path().equals(path) 
				&& branch.baseTimestamp() == baseTimestamp 
				&& branch.headTimestamp() == headTimestamp;
	}
}
