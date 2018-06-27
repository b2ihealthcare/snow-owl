/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.remotejobs;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.remotejobs.SerializableSchedulingRule;

/**
 * 
 */
public class BranchExclusiveRule implements SerializableSchedulingRule {

	private static final long serialVersionUID = 1L;

	private final String repositoryId;
	private final IBranchPath branchPath;
	
	public BranchExclusiveRule(final String repositoryId, final IBranchPath branchPath) {
		checkNotNull(repositoryId, "repositoryId");
		checkNotNull(branchPath, "branchPath");
		this.repositoryId = repositoryId;
		this.branchPath = branchPath;
	}

	@Override
	public boolean contains(final ISchedulingRule rule) {
		return equals(rule);
	}

	@Override
	public boolean isConflicting(final ISchedulingRule rule) {
		return equals(rule);
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + repositoryId.hashCode();
		result = prime * result + branchPath.hashCode();
		return result;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		
		if (!(obj instanceof BranchExclusiveRule)) { return false; } 
		
		final BranchExclusiveRule other = (BranchExclusiveRule) obj;
		return repositoryId.equals(other.repositoryId) && branchPath.equals(other.branchPath);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BranchExclusiveRule [repositoryId=");
		builder.append(repositoryId);
		builder.append(", branchPath=");
		builder.append(branchPath);
		builder.append("]");
		return builder.toString();
	}
}
