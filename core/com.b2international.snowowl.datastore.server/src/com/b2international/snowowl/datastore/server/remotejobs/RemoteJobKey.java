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
package com.b2international.snowowl.datastore.server.remotejobs;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 */
public abstract class RemoteJobKey implements ISchedulingRule, Serializable {

	private static final long serialVersionUID = 1L;

	private final String targetNsUri;
	private final IBranchPath targetBranchPath;
	
	public RemoteJobKey(final String targetNsUri, final IBranchPath targetBranchPath) {
		checkNotNull(targetNsUri, "targetNsUri");
		checkNotNull(targetBranchPath, "targetBranchPath");
		this.targetNsUri = targetNsUri;
		this.targetBranchPath = targetBranchPath;
	}

	@Override
	public boolean contains(final ISchedulingRule rule) {
		return isConflicting(rule);
	}

	@Override
	public boolean isConflicting(final ISchedulingRule rule) {
		return equals(rule);
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + targetNsUri.hashCode();
		result = prime * result + targetBranchPath.hashCode();
		return result;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final RemoteJobKey other = (RemoteJobKey) obj;
		return targetNsUri.equals(other.targetNsUri) && targetBranchPath.equals(other.targetBranchPath);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RemoteJobKey [targetNsUri=");
		builder.append(targetNsUri);
		builder.append(", targetBranchPath=");
		builder.append(targetBranchPath);
		builder.append("]");
		return builder.toString();
	}
}