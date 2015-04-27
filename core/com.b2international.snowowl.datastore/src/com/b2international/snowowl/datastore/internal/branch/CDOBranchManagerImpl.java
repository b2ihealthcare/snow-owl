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

import java.util.concurrent.ConcurrentMap;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.branch.BranchMergeException;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.internal.IRepository;
import com.google.common.collect.MapMaker;

/**
 * {@link BranchManager} implementation based on {@link CDOBranch} functionality.
 * 
 * @since 4.1
 */
public class CDOBranchManagerImpl extends BranchManagerImpl {

	private final ConcurrentMap<String, Integer> branches = new MapMaker().makeMap();
	
	private final IRepository repository;
	
	public CDOBranchManagerImpl(final IRepository repository) {
		super(getBasetimestamp(repository.getCdoMainBranch()));
		this.repository = repository;
		registerCDOBranch(repository.getCdoMainBranch());
		registerCommitListener(repository.getCdoRepository());
	}
	
	CDOBranch getCDOBranch(Branch branch) {
		final Integer branchId = branches.get(branch.path());
		return loadCDOBranch(branchId);
	}
	
	private CDOBranch loadCDOBranch(Integer branchId) {
		if (branchId != null) {
			return repository.getCdoBranchManager().getBranch(branchId);
		} else {
			return null;
		}
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
	protected BranchImpl reopen(BranchImpl parent, String name) {
		final CDOBranch childCDOBranch = createCDOBranch(parent, name);
		registerCDOBranch(childCDOBranch);
		repository.getIndexUpdater().reopen(BranchPathUtils.createPath(childCDOBranch), childCDOBranch.getBase().getTimeStamp());
		return reopen(parent, name, getBasetimestamp(childCDOBranch));
	}
	
	@Override
	protected void postDelete(BranchImpl branch) {
		super.postDelete(branch);
		branches.remove(branch.path());
	}
	
	private CDOBranch createCDOBranch(BranchImpl parent, String name) {
		return getCDOBranch(parent).createBranch(name);
	}

	private void registerCDOBranch(CDOBranch branch) {
		final String path = branch.getPathName();
		final int branchId = branch.getID();
		branches.put(path, branchId);
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
