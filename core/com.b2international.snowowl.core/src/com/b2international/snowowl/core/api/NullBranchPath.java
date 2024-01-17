/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.api;

/**
 * Represents a *NON* existent {@link IBranchPath branch path}. Could be the parent of the {@link IBranchPath#MAIN_BRANCH MAIN top most}
 * branch in the hierarchy. Or could represent a not existing branch path.
 * @see NullBranchPath#INSTANCE
 * @see IBranchPath
 */
public enum NullBranchPath implements IBranchPath {

	/**
	 * The NULL instance.
	 * @see NullBranchPath
	 */
	INSTANCE;
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getPath()
	 */
	@Override
	public String getPath() {
		return IBranchPath.EMPTY_PATH;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getParentPath()
	 */
	@Override
	public String getParentPath() {
		return IBranchPath.EMPTY_PATH;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getOsPath()
	 */
	@Override
	public String getOsPath() {
		return IBranchPath.EMPTY_PATH;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getOsParentPath()
	 */
	@Override
	public String getOsParentPath() {
		return IBranchPath.EMPTY_PATH;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getParent()
	 */
	@Override
	public IBranchPath getParent() {
		return INSTANCE;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#lastSegment()
	 */
	@Override
	public String lastSegment() {
		return IBranchPath.EMPTY_PATH;
	}

}