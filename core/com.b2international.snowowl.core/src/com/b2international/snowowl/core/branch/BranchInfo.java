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
package com.b2international.snowowl.core.branch;

import java.io.Serializable;

import com.b2international.index.revision.RevisionBranch.BranchState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.15.0
 */
public final class BranchInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String path;
	private BranchState state;
	private long baseTimestamp;
	private long headTimestamp;
	
	@JsonCreator
	public BranchInfo(@JsonProperty("path") String path, 
			@JsonProperty("state") BranchState state, 
			@JsonProperty("baseTimestamp") long baseTimestamp, 
			@JsonProperty("headTimestamp") long headTimestamp) {
		this.path = path;
		this.state = state;
		this.baseTimestamp = baseTimestamp;
		this.headTimestamp = headTimestamp;
	}
	
	public String getPath() {
		return path;
	}
	
	public BranchState getState() {
		return state;
	}
	
	public long getBaseTimestamp() {
		return baseTimestamp;
	}
	
	public long getHeadTimestamp() {
		return headTimestamp;
	}
	
}
