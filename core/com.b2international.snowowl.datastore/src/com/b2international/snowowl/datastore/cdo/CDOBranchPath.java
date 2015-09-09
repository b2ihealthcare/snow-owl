package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.datastore.branch.Branch;
import com.google.common.primitives.Ints;

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
}
