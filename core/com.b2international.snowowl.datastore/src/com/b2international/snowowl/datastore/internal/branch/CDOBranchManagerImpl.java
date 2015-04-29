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
package com.b2international.snowowl.datastore.internal.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.branch.BranchMergeException;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.internal.IRepository;
import com.b2international.snowowl.datastore.store.Store;

/**
 * {@link BranchManager} implementation based on {@link CDOBranch} functionality.
 * 
 * @since 4.1
 */
public class CDOBranchManagerImpl extends BranchManagerImpl {

	private static final String CDO_BRANCH_ID = "cdoBranchId";
	private final IRepository repository;
	
	public CDOBranchManagerImpl(final IRepository repository, final Store<BranchImpl> branchStore) {
		super(branchStore, getBasetimestamp(repository.getCdoMainBranch()));
		this.repository = repository;
		registerCommitListener(repository.getCdoRepository());
	}
	
	@Override
	void initMainBranch(BranchImpl main) {
		main.metadata().put(CDO_BRANCH_ID, CDOBranch.MAIN_BRANCH_ID);
		super.initMainBranch(main);
	}
	
	CDOBranch getCDOBranch(Branch branch) {
		final Integer branchId = branch.metadata().getInt(CDO_BRANCH_ID);
		if (branchId != null) {
			return loadCDOBranch(branchId);
		}
		throw new SnowowlRuntimeException("Missing registered CDOBranch identifier for branch at path: " + branch.path());
	}
	
	private CDOBranch loadCDOBranch(Integer branchId) {
		return repository.getCdoBranchManager().getBranch(branchId);
	}

	@Override
	BranchImpl applyChangeSet(BranchImpl target, BranchImpl source, long timestamp, String commitMessage) {
		CDOBranch targetBranch = getCDOBranch(target);
	    CDOBranch sourceBranch = getCDOBranch(source);
		CDOTransaction tx = repository.getConnection().createTransaction(targetBranch);
		
		tx.merge(sourceBranch.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
		CDOCommitInfo commitInfo;
		try {
			tx.setCommitComment(commitMessage);
			commitInfo = tx.commit();
		} catch (CommitException e) {
			throw new BranchMergeException("Failed to apply changeset on '%s' from '%s'.", target.path(), source.path());
		}
		
		return target.withHeadTimestamp(commitInfo.getTimeStamp());
	}
	
	@Override
	BranchImpl reopen(BranchImpl parent, String name, Metadata metadata) {
		final CDOBranch childCDOBranch = createCDOBranch(parent, name);
		metadata.put(CDO_BRANCH_ID, childCDOBranch.getID());
		repository.getIndexUpdater().reopen(BranchPathUtils.createPath(childCDOBranch), childCDOBranch.getBase().getTimeStamp());
		return reopen(parent, name, metadata, getBasetimestamp(childCDOBranch));
	}
	
	private CDOBranch createCDOBranch(BranchImpl parent, String name) {
		return getCDOBranch(parent).createBranch(name);
	}

	private void registerCommitListener(ICDORepository repository) {
		repository.getRepository().addCommitInfoHandler(new CDOCommitInfoHandler() {
			@Override
			public void handleCommitInfo(CDOCommitInfo commitInfo) {
				handleCommit((BranchImpl) getBranch(commitInfo.getBranch().getPathName()), commitInfo.getTimeStamp());
			}
		}); 
	}

	private static long getBasetimestamp(CDOBranch branch) {
		return branch.getBase().getTimeStamp();
	}
}
