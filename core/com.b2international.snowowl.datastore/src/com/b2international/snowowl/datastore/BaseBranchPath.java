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
 *
 */
/*default*/ class BaseBranchPath extends BranchPath implements IBaseBranchPath {

	private static final long serialVersionUID = -9220838024236069672L;

	BaseBranchPath(final IBranchPath branchPath) {
		super(check(branchPath).getPath());
	}
	
	@Override
	public String toString() {
		return "BASE PATH: " + super.toString();
	}
	
	private static IBranchPath check(final IBranchPath branchPath) {
		checkArgument(!isMain(checkNotNull(branchPath, "branchPath")), "Base branch path cannot be the MAIN path.");
		return branchPath;
	}

}