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
package com.b2international.snowowl.core.repository;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DefaultBranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.domain.TransactionContextProvider;

/**
 * @since 4.5
 */
public final class RepositoryBranchContext extends DefaultBranchContext implements TransactionContextProvider {
	
	RepositoryBranchContext(RepositoryContext context, Branch branch, String branchPath) {
		super(context, branch, branchPath);
	}
	
	@Override
	public <T> T service(Class<T> type) {
		if (TransactionContextProvider.class.isAssignableFrom(type)) {
			return type.cast(this);
		}
		return super.service(type);
	}
	
	@Override
	public TransactionContext get(BranchContext context, String author, String commitComment, String parentContextDescription) {
		return new RepositoryTransactionContext(context, author, commitComment, parentContextDescription);
	}
	
}
