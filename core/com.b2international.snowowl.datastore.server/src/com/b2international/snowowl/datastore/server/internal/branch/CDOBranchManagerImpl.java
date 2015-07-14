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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.server.branch.Branch;
import com.b2international.snowowl.datastore.server.branch.BranchManager;
import com.b2international.snowowl.datastore.server.branch.BranchMergeException;
import com.b2international.snowowl.datastore.server.internal.IRepository;
import com.b2international.snowowl.datastore.store.Store;

/**
 * {@link BranchManager} implementation based on {@link CDOBranch} functionality.
 *
 * @since 4.1
 */
public class CDOBranchManagerImpl extends BranchManagerImpl {

    private final IRepository repository;
	
    public CDOBranchManagerImpl(final IRepository repository, final Store<InternalBranch> branchStore) {
        super(branchStore, getBasetimestamp(repository.getCdoMainBranch()));
        this.repository = repository;
       	branchStore.configureSearchable(PATH_FIELD);
        registerCommitListener(repository.getCdoRepository());
    }

    @Override
    void initMainBranch(InternalBranch main) {
        super.initMainBranch(new CDOMainBranchImpl(main.baseTimestamp(), main.headTimestamp()));
    }

    CDOBranch getCDOBranch(Branch branch) {
        checkArgument(!branch.isDeleted(), "Deleted branches cannot be ");
        final Integer branchId = ((InternalCDOBasedBranch) branch).cdoBranchId();
        if (branchId != null) {
            return loadCDOBranch(branchId);
        }
        throw new SnowowlRuntimeException("Missing registered CDOBranch identifier for branch at path: " + branch.path());
    }

    private CDOBranch loadCDOBranch(Integer branchId) {
        return repository.getCdoBranchManager().getBranch(branchId);
    }

    @Override
    InternalBranch applyChangeSet(InternalBranch target, InternalBranch source, boolean dryRun, String commitMessage) {
        CDOBranch targetBranch = getCDOBranch(target);
        CDOBranch sourceBranch = getCDOBranch(source);
        CDOTransaction targetTransaction = null;

        try {

            ICDOConnection connection = repository.getConnection();
            targetTransaction = connection.createTransaction(targetBranch);

            CDOBranchMerger merger = new CDOBranchMerger(repository.getConflictProcessor());
            targetTransaction.merge(sourceBranch.getHead(), merger);
            merger.postProcess(targetTransaction);

            targetTransaction.setCommitComment(commitMessage);

            if (!dryRun) {
	            CDOCommitInfo commitInfo = targetTransaction.commit();
	            return target.withHeadTimestamp(commitInfo.getTimeStamp());
            } else {
            	return target;
            }

        } catch (CDOMerger.ConflictException e) {
            throw new BranchMergeException("Could not resolve all conflicts while applying changeset on '%s' from '%s'.", target.path(), source.path(), e);
        } catch (CommitException e) {
            throw new BranchMergeException("Failed to apply changeset on '%s' from '%s'.", target.path(), source.path(), e);
        } finally {
            if (targetTransaction != null) {
                targetTransaction.close();
            }
        }
    }

    @Override
    InternalBranch reopen(InternalBranch parent, String name, Metadata metadata) {
        final CDOBranch childCDOBranch = createCDOBranch(parent, name);
        final CDOBranchPoint[] basePath = childCDOBranch.getBasePath();
        final int[] cdoBranchPath = new int[basePath.length];
        cdoBranchPath[basePath.length - 1] = childCDOBranch.getID();
        
        for (int i = 1; i < basePath.length; i++) {
        	cdoBranchPath[i - 1] = basePath[i].getBranch().getID();
        }

        final long timeStamp = basePath[basePath.length - 1].getTimeStamp();
        repository.getIndexUpdater().reopen(BranchPathUtils.createPath(childCDOBranch), cdoBranchPath, timeStamp);
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
                    handleCommit((InternalBranch) getBranch(commitInfo.getBranch().getPathName()), commitInfo.getTimeStamp());
                }
            }
        });
    }

    private static long getBasetimestamp(CDOBranch branch) {
        return branch.getBase().getTimeStamp();
    }
}
