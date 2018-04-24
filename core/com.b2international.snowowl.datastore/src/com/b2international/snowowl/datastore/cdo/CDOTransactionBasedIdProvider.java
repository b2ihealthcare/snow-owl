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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.google.common.base.Preconditions;

/**
 * CDO ID provider implementation backed by a {@link CDOTransaction} working
 * on a given {@link CDOBranch branch}.
 */
public class CDOTransactionBasedIdProvider implements CDOIDProvider {

	private final IBranchPath branchPath;
	private final String repositoryUuid;

	public CDOTransactionBasedIdProvider(final CDOBranch branch) {
		this(
				BranchPathUtils.createPath(Preconditions.checkNotNull(branch, "CDO branch argument cannot be null.")),
				ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(branch).getUuid());
	}
	
	public CDOTransactionBasedIdProvider(final IBranchPath branchPath, final String repositoryUuid) {
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null");
		this.repositoryUuid = Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.id.CDOIDProvider#provideCDOID(java.lang.Object)
	 */
	@Override
	public CDOID provideCDOID(final Object idOrObject) {
		
		final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getByUuid(repositoryUuid);
		CDOTransaction transaction = null;
		try {
			transaction = connection.createTransaction(branchPath);
			return ((InternalCDOTransaction) transaction).provideCDOID(idOrObject);
		} finally {
			LifecycleUtil.deactivate(transaction);
		}
	}

}