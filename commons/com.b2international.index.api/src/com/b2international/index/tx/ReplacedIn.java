/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.tx;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.7
 */
public final class ReplacedIn {

	private final String branchPath;
	private final long commitTimestamp;
	
	@JsonCreator
	ReplacedIn(@JsonProperty("branchPath") String branchPath, @JsonProperty("commitTimestamp") long commitTimestamp) {
		this.branchPath = branchPath;
		this.commitTimestamp = commitTimestamp;
	}
	
	public String getBranchPath() {
		return branchPath;
	}

	public long getCommitTimestamp() {
		return commitTimestamp;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(branchPath, commitTimestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ReplacedIn other = (ReplacedIn) obj;
		return Objects.equals(branchPath, other.branchPath) && Objects.equals(commitTimestamp, other.commitTimestamp); 
	}

}