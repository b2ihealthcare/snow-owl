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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/**
 * Predicate for comparing branches by their names.
 */
public class BranchNamePredicate implements Predicate<CDOBranch> {
	
	private final String branchName;
	
	/**
	 * Creates a new branch name predicate with the name of the branch.
	 * @param branchName the name of the branch.
	 */
	public BranchNamePredicate(final String branchName) {
		this.branchName = Preconditions.checkNotNull(branchName, "Branch name cannot be null.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.google.common.base.Predicate#apply(java.lang.Object)
	 */
	@Override 
	public boolean apply(final CDOBranch branch) {
		return branchName.equals(Preconditions.checkNotNull(Preconditions.checkNotNull(branch, "CDO branch cannot be null.").getName(), "Branch name cannot be null for branch: " + branch));
	}
}