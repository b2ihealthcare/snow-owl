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
package com.b2international.snowowl.datastore.replicate;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

/**
 * @since 4.7
 */
public interface BranchReplicator {
	
	class SkipBranchException extends RuntimeException {
		
		private final CDOBranch branch;

		public SkipBranchException(CDOBranch branch) {
			this.branch = branch;
		}
		
		public CDOBranch getBranch() {
			return branch;
		}
		
	}
	
	/**
	 * Replicates a branch with additional metadata in the local store.
	 * @param branch - the branch to replicate
	 * @throws SkipBranchException - if the branch and all its commits must be skipped, because the branch state is inconsistent
	 */
	void replicateBranch(CDOBranch branch) throws SkipBranchException;
	
}
