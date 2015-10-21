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
package com.b2international.snowowl.datastore;

import static com.b2international.snowowl.datastore.BranchPathUtils.isMain;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.api.IBaseBranchPath;
import com.b2international.snowowl.core.api.IBranchPath;

/**
 * {@link IBaseBranchPath} implementation.
 */
/*default*/ class BaseBranchPath extends BranchPath implements IBaseBranchPath {

	private static final long serialVersionUID = -9220838024236069672L;
	
	private final IBranchPath contextPath;

	BaseBranchPath(final IBranchPath logicalPath, final IBranchPath contextPath) {
		super(checkPath(logicalPath).getPath());
		this.contextPath = checkPath(contextPath);
	}
	
	@Override
	public IBranchPath getContextPath() {
		return contextPath;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((contextPath == null) ? 0 : contextPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseBranchPath other = (BaseBranchPath) obj;
		if (contextPath == null) {
			if (other.contextPath != null)
				return false;
		} else if (!contextPath.equals(other.contextPath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("BASE PATH: %s [%s]", getPath(), getContextPath());
	}
	
	private static IBranchPath checkPath(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		checkArgument(!isMain(branchPath), "Base branch path cannot be the MAIN path.");
		return branchPath;
	}
}
