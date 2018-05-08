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
package com.b2international.snowowl.core.branch;

import java.util.Collection;

import com.b2international.commons.options.Metadata;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;

/**
 * @since 5.0
 */
public final class BranchData implements Branch {

	private static final long serialVersionUID = -3522105063636381152L;
	private final boolean isDeleted;
	private final Metadata metadata;
	private final String name;
	private final String parentPath;
	private final long baseTimestamp;
	private final long headTimestamp;
	private final BranchState state;

	public BranchData(String name, String parentPath, long baseTimestamp, long headTimestamp, BranchState state, boolean isDeleted, Metadata metadata) {
		this.name = name;
		this.parentPath = parentPath;
		this.baseTimestamp = baseTimestamp;
		this.headTimestamp = headTimestamp;
		this.state = state;
		this.isDeleted = isDeleted;
		this.metadata = metadata;
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public Metadata metadata() {
		return metadata;
	}

	@Override
	public String path() {
		return parentPath + Branch.SEPARATOR + name;
	}

	@Override
	public String parentPath() {
		return parentPath;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Branch parent() {
		throw new UnsupportedOperationException();
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
		return state;
	}

	@Override
	public BranchState state(Branch target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canRebase() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canRebase(Branch onTopOf) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Branch rebase(Branch onTopOf, String commitMessage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Branch rebase(Branch onTopOf, String commitMessage, Runnable postReopen) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Branch merge(Branch changesFrom, String commitMessage) throws BranchMergeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Branch createChild(String name) throws AlreadyExistsException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Branch createChild(String name, Metadata metadata) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Branch> children() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Branch reopen() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Branch delete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IBranchPath branchPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Branch withMetadata(Metadata metadata) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(Metadata metadata) {
		throw new UnsupportedOperationException();
	}

}
