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
package com.b2international.snowowl.datastore.internal.branch;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataImpl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.exceptions.BadRequestException;

/**
 * @since 4.1
 */
public class MainBranchImpl extends BranchImpl {

	public static final String TYPE = "MainBranchImpl";
	
	public MainBranchImpl(long baseTimestamp) {
		super(MAIN_PATH, "", baseTimestamp, new MetadataImpl());
	}
	
	public MainBranchImpl(long baseTimestamp, long headTimestamp, Metadata metadata) {
		super(MAIN_PATH, "", baseTimestamp, headTimestamp, metadata);
	}

	@Override
	public InternalBranch withBaseTimestamp(long newBaseTimestamp) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public InternalBranch withDeleted() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected BranchImpl doCreateBranch(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted, Metadata metadata) {
		return new MainBranchImpl(baseTimestamp, headTimestamp, metadata);
	}
	
	@Override
	public Branch parent() {
		return this;
	}
	
	@Override
	public BranchState state() {
		return Branch.BranchState.UP_TO_DATE;
	}
	
	@Override
	public Branch delete() {
		throw new BadRequestException(path() + " cannot be deleted");
	}
	
	@Override
	public Branch rebase(Branch onTopOf, String commitMessage, Runnable postReopen) {
		throw new BadRequestException(path() + " cannot be rebased");
	}
	
	@Override
	public BranchState state(Branch target) {
		throw new UnsupportedOperationException(path() + " cannot compute state compared to target " + target.path());
	}
	
	@Override
	public BranchDocument.Builder toDocument() {
		return super.toDocument().type(TYPE);
	}
	
	static InternalBranch from(BranchDocument doc) {
		return new MainBranchImpl(doc.getBaseTimestamp(), doc.getHeadTimestamp(), doc.getMetadata());
	}
	
}
