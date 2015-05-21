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
package com.b2international.snowowl.datastore.server.internal.branch;

/**
 * @since 4.1
 */
public class CDOBranchImpl extends BranchImpl implements InternalCDOBasedBranch {

	private int cdoBranchId;

	protected CDOBranchImpl(String name, String parentPath, long baseTimestamp, int cdoBranchId) {
		this(name, parentPath, baseTimestamp, baseTimestamp, cdoBranchId);
	}

	protected CDOBranchImpl(String name, String parentPath, long baseTimestamp, long headTimestamp, int cdoBranchId) {
		this(name, parentPath, baseTimestamp, headTimestamp, false, cdoBranchId);
	}

	protected CDOBranchImpl(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted, int cdoBranchId) {
		super(name, parentPath, baseTimestamp, headTimestamp, deleted);
		this.cdoBranchId = cdoBranchId;
	}
	
	@Override
	protected CDOBranchImpl doCreateBranch(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted) {
		return new CDOBranchImpl(name, parentPath, baseTimestamp, headTimestamp, deleted, cdoBranchId);
	}

	@Override
	public int cdoBranchId() {
		return cdoBranchId;
	}

}
