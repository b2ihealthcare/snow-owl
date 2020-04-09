/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.DelegatingContext.Builder;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.repository.RepositoryTransactionContext;

/**
 * @since 4.5
 */
public interface BranchContext extends RepositoryContext {

	/**
	 * A snapshot state of the branch represented by the {@link #branchPath()}. This is where the request is going to be executed.
	 * 
	 * @return
	 */
	Branch branch();

	/**
	 * The requested branch path. BranchPath modifiers can be present on the value returned by this method. It is recommended to always pass the value
	 * returned by this method to other requests, so they execute on the same requested path.
	 * 
	 * @return
	 * @since 5.9
	 */
	String branchPath();

	@Override
	default Builder<? extends BranchContext> inject() {
		return new DelegatingContext.Builder<BranchContext>(BranchContext.class, this);
	}

	/**
	 * @return <code>true</code> if this context is opened on the {@link Branch#MAIN_PATH}, <code>false</code> otherwise.
	 */
	default boolean isMain() {
		return Branch.MAIN_PATH.equals(branchPath());
	}
	
	default TransactionContext openTransaction(BranchContext context) {
		return openTransaction(context, DatastoreLockContextDescriptions.ROOT);
	}

	default TransactionContext openTransaction(BranchContext context, String parentLockContext) {
		return openTransaction(context, null, null, parentLockContext);
	}

	default TransactionContext openTransaction(BranchContext context, String author, String commitComment, String parentLockContext) {
		return new RepositoryTransactionContext(context, author, commitComment, parentLockContext);
	}
	
}
