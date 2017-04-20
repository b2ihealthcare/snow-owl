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

import com.b2international.index.Doc;
import com.b2international.index.Script;
import com.b2international.index.WithId;
import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;


/**
 * @since 4.1
 */
@Doc(type = "branch")
@Script(name=InternalBranch.WITH_HEADTIMESTAMP, script="ctx._source.headTimestamp = params.headTimestamp")
@Script(name=InternalBranch.WITH_DELETED, script="ctx._source.deleted = true")
@Script(name=InternalBranch.WITH_METADATA, script="ctx._source.metadata = params.metadata")
@Script(name=InternalBranch.REPLACE, script="ctx._source = params.replace")
public interface InternalBranch extends Branch, WithId {

	String WITH_HEADTIMESTAMP = "withHeadTimestamp";
	String WITH_DELETED = "withDeleted";
	String WITH_METADATA = "withMetadata";
	String REPLACE = "replace";

	void setBranchManager(BranchManager branchManager);
	
	InternalBranch withDeleted();
	
	InternalBranch withBaseTimestamp(long newBaseTimestamp);
	
	InternalBranch withHeadTimestamp(long newHeadTimestamp);
	
	@Override
	InternalBranch withMetadata(Metadata metadata);
	
}
