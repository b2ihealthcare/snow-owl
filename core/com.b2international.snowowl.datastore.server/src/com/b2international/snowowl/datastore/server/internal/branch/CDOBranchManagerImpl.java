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
package com.b2international.snowowl.datastore.server.internal.branch;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.exceptions.MergeConflictException;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOBranchPath;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.events.BranchChangedEvent;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.server.internal.InternalRepository;
import com.b2international.snowowl.datastore.store.Store;
import com.b2international.snowowl.datastore.store.query.QueryBuilder;
import com.google.common.collect.ImmutableSortedSet;

/**
 * {@link BranchManager} implementation based on {@link CDOBranch} functionality.
 *
 * @since 4.1
 */
public class CDOBranchManagerImpl extends BranchManagerImpl {

    private static final String CDO_BRANCH_ID = "cdoBranchId";

	private final InternalRepository repository;
	
    public CDOBranchManagerImpl(final InternalRepository repository, final Store<InternalBranch> branchStore) {
        super(branchStore);
        this.repository = repository;
       	branchStore.configureSearchable(CDO_BRANCH_ID);
       	
       	CDOBranch cdoMainBranch = repository.getCdoMainBranch();
		initBranchStore(new CDOMainBranchImpl(repository.getBaseTimestamp(cdoMainBranch), repository.getHeadTimestamp(cdoMainBranch)));
       	
        registerCommitListener(repository.getCdoRepository());
    }

    @Override
	protected void doInitBranchStore(final InternalBranch main) {
		super.doInitBranchStore(main);
		
		Deque<CDOBranch> workQueue = new ArrayDeque<CDOBranch>();
		workQueue.add(repository.getCdoBranchManager().getMainBranch());
		
		while (!workQueue.isEmpty()) {
			CDOBranch current = workQueue.pollFirst();
			
			if (!current.isMainBranch()) {
				final Branch branch = getBranch(current.getID());

				if (branch == null) {
					long baseTimestamp = repository.getBaseTimestamp(current);
					long headTimestamp = repository.getHeadTimestamp(current);
					registerBranch(new CDOBranchImpl(current.getName(), current.getBase().getBranch().getPathName(), baseTimestamp, headTimestamp, current.getID()));
				}
			}
			
			workQueue.addAll(ImmutableSortedSet.copyOf(current.getBranches()));
		}
	}

    CDOBranch getCDOBranch(Branch branch) {
        checkArgument(!branch.isDeleted(), "Deleted branches cannot be retrieved.");
        final int branchId = ((InternalCDOBasedBranch) branch).cdoBranchId();
        return loadCDOBranch(branchId);
    }

    private Branch getBranch(Integer branchId) {
    	return getBranchFromStore(QueryBuilder.newQuery().match(CDO_BRANCH_ID, branchId.toString()).build());
    }
    
    private CDOBranch loadCDOBranch(Integer branchId) {
        return repository.getCdoBranchManager().getBranch(branchId);
    }

    @Override
    InternalBranch rebase(InternalBranch branch, InternalBranch onTopOf, String commitMessage, Runnable postReopen) {
		CDOTransaction testTransaction = null;
		CDOTransaction newTransaction = null;
		
		try {
			testTransaction = applyChangeSet(branch, onTopOf, true);
			final InternalBranch rebasedBranch = reopen(onTopOf, branch.name(), branch.metadata());
			postReopen.run();
			
			if (branch.headTimestamp() > branch.baseTimestamp()) {
				newTransaction = transferChangeSet(testTransaction, rebasedBranch);
				 // Implicit notification (reopen & commit)
				return commitChanges(branch, rebasedBranch, commitMessage, newTransaction);
			} else {
				return sendChangeEvent(rebasedBranch); // Explicit notification (reopen)
			}
			
		} finally {
			LifecycleUtil.deactivate(testTransaction);
			LifecycleUtil.deactivate(newTransaction);
		}
    }
    
    private CDOTransaction transferChangeSet(final CDOTransaction transaction, final InternalBranch rebasedBranch) {
		final ICDOConnection connection = repository.getConnection();
		final CDOBranch rebasedCdoBranch = getCDOBranch(rebasedBranch);
		final CDOTransaction newTransaction = connection.createTransaction(rebasedCdoBranch);
		
		CDOUtils.mergeTransaction(transaction, newTransaction);
		return newTransaction;
	}
    
    @Override
    InternalBranch applyChangeSet(InternalBranch from, InternalBranch to, boolean dryRun, boolean isRebase, String commitMessage) {
        final CDOTransaction dirtyTransaction = applyChangeSet(from, to, isRebase);
        try {
        	if (dryRun) {
            	return to;
            } else {
            	return commitChanges(from, to, commitMessage, dirtyTransaction);
            }
		} finally {
			LifecycleUtil.deactivate(dirtyTransaction);
		}
    }
    
    private CDOTransaction applyChangeSet(InternalBranch from, InternalBranch to, boolean isRebase) {
    	final CDOBranch sourceBranch = getCDOBranch(from);
    	final CDOBranch targetBranch = getCDOBranch(to);
    	final ICDOConnection connection = repository.getConnection();
    	final CDOBranchMerger merger = new CDOBranchMerger(repository.getConflictProcessor(), isRebase);
    	final CDOTransaction targetTransaction = connection.createTransaction(targetBranch);

    	try {
    		// XXX: specifying sourceBase instead of defaulting to the computed common ancestor point here
    		targetTransaction.merge(sourceBranch.getHead(), sourceBranch.getBase(), merger);
    		merger.postProcess(targetTransaction);
    		return targetTransaction;
    	} catch (CDOMerger.ConflictException e) {
    		CDOView sourceView = connection.createView(sourceBranch);
    		Collection<MergeConflict> conflicts = merger.handleCDOConflicts(sourceView, targetTransaction);
    		LifecycleUtil.deactivate(targetTransaction);
    		LifecycleUtil.deactivate(sourceView);
			throw new MergeConflictException(conflicts, String.format("Could not resolve all conflicts while applying changeset on '%s' from '%s'.", to.path(), from.path()));
    	}
    }
    
	private InternalBranch commitChanges(InternalBranch from, InternalBranch to, String commitMessage, CDOTransaction targetTransaction) {
		try { 

            if (targetTransaction.isDirty()) {
    			// FIXME: Using "System" user and "synchronize" description until a more suitable pair can be specified here
            	targetTransaction.setCommitComment(commitMessage);
            	CDOCommitInfo commitInfo = new CDOServerCommitBuilder(SpecialUserStore.SYSTEM_USER_NAME, commitMessage, targetTransaction)
            			.parentContextDescription(DatastoreLockContextDescriptions.SYNCHRONIZE)
            			.commitOne();
            	
	            return to.withHeadTimestamp(commitInfo.getTimeStamp());
            } else {
            	return to;
            }

        } catch (CommitException e) {
            throw new BranchMergeException("Failed to apply changeset on '%s' from '%s'.", to.path(), from.path(), e);
        }
	}
    
    @Override
    InternalBranch reopen(InternalBranch parent, String name, Metadata metadata) {
        final CDOBranch childCDOBranch = createCDOBranch(parent, name);
        final CDOBranchPoint[] basePath = childCDOBranch.getBasePath();
        final CDOBranchPath cdoBranchPath = new CDOBranchPath(childCDOBranch);

        final long timeStamp = basePath[basePath.length - 1].getTimeStamp();
        repository.getIndexUpdater().reopen(BranchPathUtils.createPath(childCDOBranch), cdoBranchPath);
		return reopen(parent, name, metadata, timeStamp, childCDOBranch.getID());
    }

    private InternalBranch reopen(InternalBranch parent, String name, Metadata metadata, long baseTimestamp, int id) {
        final InternalBranch branch = new CDOBranchImpl(name, parent.path(), baseTimestamp, id);
        branch.metadata(metadata);
        registerBranch(branch);
        return branch;
    }

    private CDOBranch createCDOBranch(InternalBranch parent, String name) {
        return getCDOBranch(parent).createBranch(name);
    }

    @SuppressWarnings("restriction")
    private void registerCommitListener(ICDORepository repository) {
        repository.getRepository().addCommitInfoHandler(new CDOCommitInfoHandler() {
			@Override
            public void handleCommitInfo(CDOCommitInfo commitInfo) {
                if (!(commitInfo instanceof org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo)) {
                    handleCommit((InternalBranch) getBranch(commitInfo.getBranch().getID()), commitInfo.getTimeStamp());
                }
            }
        });
    }
    
    @Override
    InternalBranch sendChangeEvent(final InternalBranch branch) {
    	new BranchChangedEvent(repository.id(), branch).publish(repository.events());
		return super.sendChangeEvent(branch);
    }
}
