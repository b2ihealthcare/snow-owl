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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.google.common.collect.ImmutableList;


/**
 * @since 4.1
 */
public class BranchManager {

	private TimestampAuthority clock;
	private Map<String, Branch> branches = newHashMap();

	public BranchManager(TimestampAuthority clock) {
		this.clock = clock;
		initMainBranch();
	}

	private void initMainBranch() {
		final MainBranch main = new MainBranch(clock.getTimestamp());
		main.setBranchManager(this);
		this.branches.put(MainBranch.DEFAULT_PATH, main);
	}
	
	Branch createBranch(Branch parent, String name) {
		final String path = parent.path().concat(Branch.SEPARATOR).concat(name);
		if (getBranchFromStore(path) != null) {
			throw new AlreadyExistsException(Branch.class.getSimpleName(), path);
		}
		final BranchImpl branch = new BranchImpl(parent, name, clock.getTimestamp());
		branch.setTimestampAuthority(clock);
		branch.setBranchManager(this);
		branches.put(path, branch);
		return branch;
	}
	
	public Branch getMainBranch() {
		return getBranch(MainBranch.DEFAULT_PATH);
	}

	public Branch getBranch(String path) {
		final Branch branch = getBranchFromStore(path);
		if (branch == null) {
			throw new NotFoundException(Branch.class.getSimpleName(), path);
		}
		return branch;
	}

	private Branch getBranchFromStore(String path) {
		return branches.get(path);
	}

	public Collection<Branch> getBranches() {
		return ImmutableList.copyOf(branches.values());
	}

}
