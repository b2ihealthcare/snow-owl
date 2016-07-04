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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataHolderImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * @since 4.1
 */
public class BranchImpl extends MetadataHolderImpl implements Branch, InternalBranch {

	private static final Runnable EMPTY_RUNNABLE = new Runnable() {
		@Override public void run() { return; }
	};

    protected BranchManagerImpl branchManager;
    
    private final String name;
    private final String parentPath;
    private final long baseTimestamp;
    private final long headTimestamp;
    private final boolean deleted;
    
    protected BranchImpl(String name, String parentPath, long baseTimestamp) {
    	this(name, parentPath, baseTimestamp, baseTimestamp);
    }
    
    protected BranchImpl(String name, String parentPath, long baseTimestamp, long headTimestamp) {
    	this(name, parentPath, baseTimestamp, headTimestamp, false);
    }
    
    protected BranchImpl(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted) {
        BranchNameValidator.DEFAULT.checkName(name);
        checkArgument(baseTimestamp >= 0L, "Base timestamp may not be negative.");
        checkArgument(headTimestamp >= baseTimestamp, "Head timestamp may not be smaller than base timestamp.");
		this.name = name;
		this.parentPath = parentPath;
		this.baseTimestamp = baseTimestamp;
		this.headTimestamp = headTimestamp;
		this.deleted = deleted;
	}
	
    @Override
	public void setBranchManager(BranchManagerImpl branchManager) {
		this.branchManager = checkNotNull(branchManager, "branchManager");
	}
	
	BranchManagerImpl getBranchManager() {
		return this.branchManager;
	}
	
    @Override
    public InternalBranch withDeleted() {
		return createBranch(name, parentPath, baseTimestamp, headTimestamp, true);
	}

    @Override
    public InternalBranch withBaseTimestamp(long newBaseTimestamp) {
        checkArgument(newBaseTimestamp > baseTimestamp, "New base timestamp may not be smaller or equal than old base timestamp.");
		return createBranch(name, parentPath, newBaseTimestamp, newBaseTimestamp, deleted);
	}
	
    @Override
    public InternalBranch withHeadTimestamp(long newHeadTimestamp) {
		checkArgument(newHeadTimestamp > headTimestamp, "New head timestamp may not be smaller or equal than old head timestamp.");
		return createBranch(name, parentPath, baseTimestamp, newHeadTimestamp, deleted);
	}
    
	private BranchImpl createBranch(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted) {
		final BranchImpl branch = doCreateBranch(name, parentPath, baseTimestamp, headTimestamp, deleted);
		branch.setBranchManager(getBranchManager());
		branch.metadata(metadata());
		return branch;
	}

	protected BranchImpl doCreateBranch(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted) {
		return new BranchImpl(name, parentPath, baseTimestamp, headTimestamp, deleted);
	}
	
	@Override
	public Branch reopen() {
		return branchManager.reopen((InternalBranch) parent(), name, metadata());
	}
	
	@Override
	public Branch delete() {
		return branchManager.delete(this);
	}

	@Override
	public Branch rebase(Branch onTopOf, String commitMessage) {
		return rebase(onTopOf, commitMessage, EMPTY_RUNNABLE);
	}
	
	@Override
	public Branch rebase(Branch onTopOf, String commitMessage, Runnable postReopen) {
		if (canRebase(onTopOf)) {
			return branchManager.rebase(this, (BranchImpl) onTopOf, commitMessage, postReopen);
		} else {
			return this;
		}
	}

	@Override
	public boolean canRebase() {
		return canRebase(state());
	}
	
	@Override
	public boolean canRebase(Branch onTopOf) {
		return canRebase(state(onTopOf));
	}

	private boolean canRebase(final BranchState state) {
		return state == BranchState.BEHIND || state == BranchState.DIVERGED || state == BranchState.STALE;
	}
	
	@Override
	public Branch merge(Branch changesFrom, String commitMessage) throws BranchMergeException {
		if (path().equals(changesFrom.path())) {
			throw new BadRequestException("Can't merge branch '%s' onto itself.", path());
		}
		
		if (changesFrom.state() == BranchState.FORWARD) {
			return branchManager.merge((BranchImpl) changesFrom, this, commitMessage);
		} else {
			throw new BranchMergeException("Only source in the FORWARD state can merged.");
		}
	}

	@Override
	public Branch notifyChanged() {
		return branchManager.sendChangeEvent(this);
	}

	@Override
	public Branch createChild(String name) {
		return createChild(name, null);
	}
	
	@Override
	public Branch createChild(String name, Metadata metadata) {
		BranchNameValidator.DEFAULT.checkName(name);
		return branchManager.createBranch(this, name, metadata);
	}
	
	@Override
	public Collection<? extends Branch> children() {
		return branchManager.getChildren(this);
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public String parentPath() {
		return parentPath;
	}
	
	@Override
	public Branch parent() {
		return branchManager.getBranch(parentPath);
	}
	
    @Override
	public long baseTimestamp() {
        return baseTimestamp;
    }

    @Override
	public long headTimestamp() {
        return headTimestamp;
    }

    @Override
    public boolean isDeleted() {
    	return deleted;
    }

	@Override
	public String path() {
        return parentPath + SEPARATOR + name;
    }
	
	@Override
	public IBranchPath branchPath() {
		return BranchPathUtils.createPath(path());
	}

    @Override
	public BranchState state() {
		return state(parent());
	}
    
    @Override
	public BranchState state(Branch target) {
		if (baseTimestamp() < target.baseTimestamp()) {
        	return BranchState.STALE;
        } else if (headTimestamp > baseTimestamp && target.headTimestamp() < baseTimestamp) {
        	return BranchState.FORWARD;
        } else if (headTimestamp == baseTimestamp && target.headTimestamp() > baseTimestamp) {
        	return BranchState.BEHIND;
        } else if (headTimestamp > baseTimestamp && target.headTimestamp() > baseTimestamp) {
        	return BranchState.DIVERGED;
        } else {
    	    return BranchState.UP_TO_DATE;
        }
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (baseTimestamp ^ (baseTimestamp >>> 32));
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + (int) (headTimestamp ^ (headTimestamp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentPath == null) ? 0 : parentPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		
		if (!(obj instanceof BranchImpl)) {
			return false;
		}
		
		BranchImpl other = (BranchImpl) obj;
		
		if (baseTimestamp != other.baseTimestamp) { return false; }
		if (deleted != other.deleted) { return false; }
		if (headTimestamp != other.headTimestamp) { return false; }
		if (!name.equals(other.name)) { return false; }
		if (!parentPath.equals(other.parentPath)) { return false; }
		
		return true;
	}
}
