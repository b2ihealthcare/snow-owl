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

import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.google.common.primitives.Ints;

/**
 * Wraps a sequence of CDO branch identifiers, forming a path to a physical branch in the repository.
 */
public class CDOBranchPath implements BranchPath {

	private static int[] getBranchIds(final CDOBranch branch) {
		final CDOBranchPoint[] basePath = branch.getBasePath();
		final int[] branchIds = new int[basePath.length];
		branchIds[basePath.length - 1] = branch.getID();

		for (int i = 1; i < basePath.length; i++) {
			branchIds[i - 1] = basePath[i].getBranch().getID();
		}

		return branchIds;
	}

	private final int[] branchIds;

	public CDOBranchPath() {
		this(new int[] { CDOBranch.MAIN_BRANCH_ID });
	}

	public CDOBranchPath(final CDOBranch branch) {
		this(getBranchIds(branch));
	}

	public CDOBranchPath(final int[] branchIds) {
		checkState(branchIds[0] == CDOBranch.MAIN_BRANCH_ID, "First branch ID segment does not match the MAIN branch identifier.");
		this.branchIds = branchIds;
	}

	@Override
	public BranchPath parent() {
		checkState(!isMain(), "Can't return parent for MAIN.");
		return new CDOBranchPath(Arrays.copyOf(branchIds, branchIds.length - 1));
	}

	@Override
	public boolean isMain() {
		return branchIds.length == 1;
	}

	@Override
	public String path() {
		return Ints.join(Branch.SEPARATOR, branchIds);
	}

	@Override
	public String toString() {
		return path();
	}

	@Override
	public int hashCode() {
		return 31 + Arrays.hashCode(branchIds);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false;} 
		if (getClass() != obj.getClass()) { return false; }

		final CDOBranchPath other = (CDOBranchPath) obj;
		return Arrays.equals(branchIds, other.branchIds);
	}
}
