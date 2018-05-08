/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.options.Metadata;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;


/**
 * @since 4.1
 */
public interface InternalBranch extends Branch {

	void setBranchManager(BranchManager branchManager);
	
	InternalBranch withDeleted();
	
	InternalBranch withBaseTimestamp(long newBaseTimestamp);
	
	InternalBranch withHeadTimestamp(long newHeadTimestamp);
	
	@Override
	InternalBranch withMetadata(Metadata metadata);

	RevisionBranch.Builder toDocument();
	
	static InternalBranch toBranch(RevisionBranch doc) {
		switch (doc.getType()) {
		case BranchImpl.TYPE: return BranchImpl.from(doc);
		case MainBranchImpl.TYPE: return MainBranchImpl.from(doc);
		case CDOBranchImpl.TYPE: return CDOBranchImpl.from(doc);
		case CDOMainBranchImpl.TYPE: return CDOMainBranchImpl.from(doc);
		default: throw new UnsupportedOperationException("TODO implement me for " + doc.getType()); 
		}
	}
	
}
