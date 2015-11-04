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
package com.b2international.snowowl.datastore.server.request;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.DefaultBranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.datastore.server.EditingContextFactory;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public class CDOBranchContext extends DefaultBranchContext {

	CDOBranchContext(RepositoryContext context, Branch branch) {
		super(context, branch);
	}
	
	@Override
	public <T> Provider<T> provider(Class<T> type) {
		if (TransactionContext.class.isAssignableFrom(type)) {
			return (Provider<T>) new Provider<TransactionContext>() {
				@Override
				public TransactionContext get() {
					return new CDOTransactionContext(CDOBranchContext.this, service(EditingContextFactory.class).createEditingContext(branch().branchPath()));
				}
			};
		}
		return super.provider(type);
	}

}
