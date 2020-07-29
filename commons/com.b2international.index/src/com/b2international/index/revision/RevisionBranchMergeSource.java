/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.io.Serializable;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 7.0
 */
public final class RevisionBranchMergeSource implements Serializable {
	
	private static final long serialVersionUID = 7947353109442291306L;
	private final long timestamp; 
	private final SortedSet<RevisionBranchPoint> branchPoints;
	private final boolean squash;

	@JsonCreator
	public RevisionBranchMergeSource(
			@JsonProperty("timestamp") long timestamp, 
			@JsonProperty("branchPoints") SortedSet<RevisionBranchPoint> branchPoints,
			@JsonProperty("squash") boolean squash) {
		this.timestamp = timestamp;
		this.branchPoints = branchPoints;
		this.squash = squash;
	}
	
	public boolean isSquash() {
		return squash;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public SortedSet<RevisionBranchPoint> getBranchPoints() {
		return branchPoints;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass()).add("timestamp", timestamp).add("branchPoints", branchPoints).add("squash", squash).toString();
	}
	
}
