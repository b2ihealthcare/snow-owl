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
package com.b2international.snowowl.datastore.internal.branch;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;
import java.util.regex.Pattern;

import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchMergeException;
import com.b2international.snowowl.datastore.branch.TimestampAuthority;

/**
 * TODO: branch description
 * TODO: metadata
 * TODO: move to internal package
 * @since 4.1
 */
public class BranchImpl implements Branch {

    public enum BranchState {
        UP_TO_DATE,
        FORWARD, 
        BEHIND, 
        DIVERGED, 
        STALE
    }

    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_-]{1,50}");

    private Branch parent;
    private String name;
    private long baseTimestamp;
    private long headTimestamp;
    private boolean deleted;
    
    /*TODO remove field if possible, move to SPI interface*/
    private TimestampAuthority timestampAuthority;
    private BranchManagerImpl branchManager;

    void setTimestampAuthority(TimestampAuthority timestampAuthority) {
		this.timestampAuthority = timestampAuthority;
	}
    
    void setBranchManager(BranchManagerImpl branchManager) {
    	this.branchManager = branchManager;
    }

    public BranchImpl(Branch parent, String name, long baseTimestamp) {
        this(parent, name, baseTimestamp, parent.headTimestamp());
    }

    protected BranchImpl(Branch parent, String name, long baseTimestamp, long parentHeadTimestamp) {
        checkName(name);
        checkArgument(baseTimestamp >= 0L, "Base timestamp may not be negative.");
        checkArgument(baseTimestamp > parentHeadTimestamp, "Base timestamp %s must be greater than parent head timestamp %s.", baseTimestamp, parentHeadTimestamp);

        this.parent = parent;
        this.name = name;
        this.baseTimestamp = baseTimestamp;
        this.headTimestamp = baseTimestamp;
    }

    @Override
	public String path() {
        return parent.path() + SEPARATOR + name;
    }

    @Override
	public String name() {
        return name;
    }

    @Override
	public Branch parent() {
        return parent;
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
	public BranchState state() {
		return state(parent());
	}
    
    @Override
    public void delete() {
    	this.deleted = true;
    }
    
    @Override
    public boolean isDeleted() {
    	return deleted;
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
	public void handleCommit(long commitTimestamp) {
        checkArgument(commitTimestamp > headTimestamp(), "Commit timestamp %s is before last commit timestamp %s on current branch.", commitTimestamp, headTimestamp());
        headTimestamp = commitTimestamp;
    }

	private void handleMerge(Branch source, long mergeTimestamp) {
		checkArgument(mergeTimestamp > source.headTimestamp(), "Merge timestamp %s is before last commit timestamp %s on source branch.", mergeTimestamp, source.headTimestamp());
		handleCommit(mergeTimestamp);
	}
	
	@Override
	public Branch rebase() {
		return rebase(parent());
	}

	@Override
	public Branch rebase(Branch target) {
		final BranchState state = state();
		if (state == BranchState.BEHIND || state == BranchState.DIVERGED) {
			return doRebase(target, state);
		}
		return this;
	}

	private Branch doRebase(Branch target, BranchState originalState) {
		final long newBaseTimestamp = getTimestamp();
		final BranchImpl newBranch = new BranchImpl(target, name(), newBaseTimestamp);
		newBranch.setTimestampAuthority(timestampAuthority);
		if (originalState == BranchState.DIVERGED) {
			// TODO apply commits
			newBranch.handleCommit(getTimestamp());
		}
		return newBranch;
	}

	private long getTimestamp() {
		return timestampAuthority.getTimestamp();
	}
	
	@Override
	public void merge(Branch source) throws BranchMergeException {
		checkArgument(!source.equals(this), "Can't merge branch onto itself.");
		if (source.state() != BranchState.FORWARD) {
			throw new BranchMergeException("Only source in the FORWARD state can merged.");
		}
		// TODO: actual merge happens here
		handleMerge(source, getTimestamp());
	}
	
	@Override
	public Branch createChild(String name) {
		checkName(name);
		return branchManager.createBranch(this, name);
	}
	
    @Override
    public int hashCode() {
        return Objects.hash(path(), baseTimestamp());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof BranchImpl)) { return false; }

        Branch other = (Branch) obj;
        return path().equals(other.path()) && baseTimestamp() == other.baseTimestamp();
    }
    
    private void checkName(String name) {
		checkArgument(VALID_NAME_PATTERN.matcher(name).matches(), "Name '%s' has invalid characters.", name);
	}

}
