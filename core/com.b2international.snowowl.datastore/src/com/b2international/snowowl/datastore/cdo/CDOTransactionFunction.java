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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Function performing any arbitrary operation on a {@link CDOTransaction transaction}.
 * <p>Clients do not have to take care about closing the underlying transaction after using it
 * as it will be closed automatically.
 *
 */
public abstract class CDOTransactionFunction<T> extends CDOViewFunction<T, CDOTransaction> {

	/**Creates a function that can make any operation in a {@link CDOTransaction transaction} opened on the branch given by its unique {@link IBranchPath branch path}.
	 * @param connection the connection where the transaction has to be opened.*/
	public CDOTransactionFunction(final ICDOConnection connection, final IBranchPath branchPath) {
		super(connection, branchPath);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOViewFunction#init()
	 */
	@Override
	protected CDOTransaction init() {
		return connection.createTransaction(branch);
	}
}