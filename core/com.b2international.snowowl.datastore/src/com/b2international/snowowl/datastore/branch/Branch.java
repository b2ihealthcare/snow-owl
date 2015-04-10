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
package com.b2international.snowowl.datastore.branch;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.regex.Pattern;

/*
 * TODO: branch description
 * TODO: state machine (up-to-date, forward, behind, diverged)
 * TODO: metadata
 */
public class Branch {

    public enum BranchState {
        UP_TO_DATE,
        FORWARD, 
        BEHIND, 
        DIVERGED
    }

    private static final String SEPARATOR = "/";
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_-]{1,50}");

    private Branch parent;
    private String name;
    private long baseTimestamp;
    private long headTimestamp;

    public Branch(Branch parent, String name, long baseTimestamp) {
        this(parent, name, baseTimestamp, parent.headTimestamp());
    }

    protected Branch(Branch parent, String name, long baseTimestamp, long parentHeadTimestamp) {
        checkArgument(VALID_NAME_PATTERN.matcher(name).matches(), "Name '%s' has invalid characters.", name);
        checkArgument(baseTimestamp >= 0L, "Base timestamp may not be negative.");
        checkArgument(baseTimestamp > parentHeadTimestamp, "Base timestamp %s must be greater than parent head timestamp %s.", baseTimestamp, parentHeadTimestamp);

        this.parent = parent;
        this.name = name;
        this.baseTimestamp = baseTimestamp;
        this.headTimestamp = baseTimestamp;
    }

    public String path() {
        return parent.path() + SEPARATOR + name;
    }

    public String name() {
        return name;
    }

    public Branch parent() {
        return parent;
    }

    public long baseTimestamp() {
        return baseTimestamp;
    }

    public long headTimestamp() {
        return headTimestamp;
    }

    public BranchState state() {
    	if (headTimestamp > baseTimestamp && parent().headTimestamp() < baseTimestamp) {
        	return BranchState.FORWARD;
        } else if (headTimestamp == baseTimestamp && parent().headTimestamp() > baseTimestamp) {
        	return BranchState.BEHIND;
        } else if (headTimestamp > baseTimestamp && parent().headTimestamp() > baseTimestamp) {
        	return BranchState.DIVERGED;
        } else {
    	    return BranchState.UP_TO_DATE;
        }
    }
    
    public void handleCommit(long commitTimestamp) {
        checkArgument(commitTimestamp > headTimestamp(), "Commit timestamp %s is before last commit timestamp %s on current branch.", commitTimestamp, headTimestamp());
        headTimestamp = commitTimestamp;
    }

	private void handleMerge(Branch source, long mergeTimestamp) {
		checkArgument(mergeTimestamp > source.headTimestamp(), "Merge timestamp %s is before last commit timestamp %s on source branch.", mergeTimestamp, source.headTimestamp());
		handleCommit(mergeTimestamp);
	}
	
	public void merge(Branch source) {
		checkArgument(!source.equals(this), "Can't merge branch onto itself.");
		checkArgument(source.state() == BranchState.FORWARD, "Only source in the FORWARD state can merged.");
		// TODO: actual merge happens here
		handleMerge(source, headTimestamp + 100L);
	}
	
    @Override
    public int hashCode() {
        return 31 + path().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof Branch)) { return false; }

        Branch other = (Branch) obj;
        return path().equals(other.path());
    }
}
