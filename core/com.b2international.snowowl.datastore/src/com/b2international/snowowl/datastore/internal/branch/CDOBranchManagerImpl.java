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
package com.b2international.snowowl.datastore.internal.branch;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.SortedSet;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataImpl;
import com.b2international.index.BulkIndexWrite;
import com.b2international.index.Index;
import com.b2international.index.IndexWrite;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.BranchMergeException;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionSegment;
import com.b2international.snowowl.core.exceptions.MergeConflictException;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.repository.InternalRepository;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.events.BranchChangedEvent;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.replicate.BranchReplicator;
import com.b2international.snowowl.identity.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSortedSet;

/**
 * {@link BranchManager} implementation based on {@link CDOBranch} functionality.
 *
 * @since 4.1
 */
public final class CDOBranchManagerImpl extends BaseRevisionBranching implements BranchReplicator {

	private static final String CDO_BRANCH_ID = "id";

	private final InternalRepository repository;
	
    public CDOBranchManagerImpl(final InternalRepository repository, ObjectMapper mapper) {
        super(repository.provider(Index.class), mapper);
        this.repository = repository;
    }
    
    @Override
    protected long getMainBranchId() {
    	return 0L;
    }
    
    @Override
    protected long getMainBaseTimestamp() {
    	return repository.getBaseTimestamp(repository.getCdoMainBranch());
    }
    
    @Override
    protected long getMainHeadTimestamp() {
    	return repository.getHeadTimestamp(repository.getCdoMainBranch());
    }
    
    @Override
    public void replicateBranch(final org.eclipse.emf.cdo.common.branch.CDOBranch branch) {
		if (!branch.isMainBranch()) {
			final int cdoBranchId = branch.getID();
			// if content already available with this cdoBranchId then skip
			RevisionBranch existingBranch = getBranch(cdoBranchId);
			
			if (existingBranch == null) {
				try {
					// get existing branch by name, this can be null, if this is the first replication of the branch
					existingBranch = getBranch(branch.getPathName());
				} catch (NotFoundException e) {
					// ignore not found branches
				}
				final long parentCdoBranchId = branch.getBase().getBranch().getID();
				final RevisionBranch parent = getBranch(parentCdoBranchId);
				if (parent == null) {
					throw new SkipBranchException(branch);
				}
				final String name = branch.getName();
				final long baseTimestamp = repository.getBaseTimestamp(branch);
				final long headTimestamp = repository.getHeadTimestamp(branch);
				doReopen(parent.getPath(), name, existingBranch == null ? new MetadataImpl() : existingBranch.metadata(), baseTimestamp, headTimestamp, cdoBranchId);
			}
		}
    }

    public final CDOBranch getCDOBranch(RevisionBranch branch) {
        checkArgument(!branch.isDeleted(), "Deleted branches cannot be retrieved.");
        return loadCDOBranch(branch.getId());
    }

    private RevisionBranch getBranch(long branchId) {
    	return getBranchFromStore(Query.select(RevisionBranch.class).where(Expressions.exactMatch(CDO_BRANCH_ID, branchId)));
    }
    
    private CDOBranch loadCDOBranch(long branchId) {
        return repository.getCdoBranchManager().getBranch((int) branchId);
    }

    @Override
    protected String doRebase(RevisionBranch branch, RevisionBranch onTopOf, String commitMessage, Runnable postReopen) {
		CDOTransaction testTransaction = null;
		CDOTransaction newTransaction = null;
		
		IndexWrite<Void> delete = null;
		
		try {
			
			final String branchName = branch.getName();
			if (branch.getHeadTimestamp() > branch.getBaseTimestamp()) {
				
				testTransaction = applyChangeSet(branch, onTopOf, true);
				
				final RevisionBranch tmpBranch = reopen(onTopOf,
						String.format(RevisionBranch.TEMP_BRANCH_NAME_FORMAT, RevisionBranch.TEMP_PREFIX, branchName, System.currentTimeMillis()), branch.metadata());
				
				final String tmpBranchPath = tmpBranch.getPath();
				delete = prepareDelete(tmpBranchPath);
				
				postReopen.run();
				
				newTransaction = transferChangeSet(testTransaction, tmpBranch);
				commitChanges(branch, tmpBranch, commitMessage, newTransaction);
				RevisionBranch tmpBranchWithChanges = getBranch(tmpBranchPath);
				
				final RevisionBranch rebasedBranch = RevisionBranch.builder()
					.name(branchName)
					.parentPath(onTopOf.getPath())
					.metadata(branch.metadata())
					.id(tmpBranchWithChanges.getId())
					.segments(tmpBranchWithChanges.getSegments())
					.build();
				
				final IndexWrite<Void> replace = prepareReplace(branch.getPath(), rebasedBranch);
				
				final CDOBranch rebasedCDOBranch = getCDOBranch(rebasedBranch);
				rebasedCDOBranch.rename(branchName);
				
				final IndexWrite<Void> updateCommits = writer -> {
					writer.bulkUpdate(Commit.Update.branch(tmpBranchPath, branch.getPath()));
					return null;
				};
				
				BulkIndexWrite<Void> bulkWrite = new BulkIndexWrite<>(replace, delete, updateCommits);
				
				commit(bulkWrite);
				
				delete = null;
				
				sendChangeEvent(branch.getPath()); // Explicit notification (reopen)
				
				return rebasedBranch.getPath();
				
			} else {

				final RevisionBranch rebasedBranch = reopen(onTopOf, branchName, branch.metadata());
				postReopen.run();
				sendChangeEvent(rebasedBranch.getPath()); // Explicit notification (reopen)
				return rebasedBranch.getPath();
				
			}
			
		} finally {
			
			if (delete != null) {
				commit(delete);
			}
			
			LifecycleUtil.deactivate(testTransaction);
			LifecycleUtil.deactivate(newTransaction);
		}
    }

	private IndexWrite<Void> prepareDelete(final String path) {
		return index -> {
			index.remove(RevisionBranch.class, path);
			return null;
		};
	}
    
    private CDOTransaction transferChangeSet(final CDOTransaction transaction, final RevisionBranch rebasedBranch) {
		final ICDOConnection connection = repository.getConnection();
		final CDOBranch rebasedCdoBranch = getCDOBranch(rebasedBranch);
		final CDOTransaction newTransaction = connection.createTransaction(rebasedCdoBranch);
		
		CDOUtils.mergeTransaction(transaction, newTransaction);
		return newTransaction;
	}
    
    @Override
    protected String applyChangeSet(RevisionBranch from, RevisionBranch to, boolean dryRun, boolean isRebase, String commitMessage) {
        final CDOTransaction dirtyTransaction = applyChangeSet(from, to, isRebase);
        try {
        	if (dryRun) {
            	return to.getPath();
            } else {
            	commitChanges(from, to, commitMessage, dirtyTransaction);
            	return to.getPath();
            }
		} finally {
			LifecycleUtil.deactivate(dirtyTransaction);
		}
    }
    
    private CDOTransaction applyChangeSet(RevisionBranch from, RevisionBranch to, boolean isRebase) {
    	final CDOBranch sourceBranch = getCDOBranch(from);
    	final CDOBranch targetBranch = getCDOBranch(to);
    	final ICDOConnection connection = repository.getConnection();
    	final CDOBranchMerger merger = new CDOBranchMerger(repository.getConflictProcessor(), sourceBranch, targetBranch, isRebase);
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
			throw new MergeConflictException(conflicts, String.format("Could not resolve all conflicts while applying changeset on '%s' from '%s'.", to.getPath(), from.getPath()));
    	}
    }
    
	private String commitChanges(RevisionBranch from, RevisionBranch to, String commitMessage, CDOTransaction targetTransaction) {
		try { 

            if (targetTransaction.isDirty()) {
    			// FIXME: Using "System" user and "synchronize" description until a more suitable pair can be specified here
            	targetTransaction.setCommitComment(commitMessage);
            	new CDOServerCommitBuilder(User.SYSTEM.getUsername(), commitMessage, targetTransaction)
            			.parentContextDescription(DatastoreLockContextDescriptions.SYNCHRONIZE)
            			.commitOne();
            }
            return to.getPath();
        } catch (CommitException e) {
            throw new BranchMergeException("Failed to apply changeset on '%s' from '%s'.", to.getPath(), from.getPath(), e);
        }
	}
    
    @Override
    protected RevisionBranch doReopen(RevisionBranch parent, String name, Metadata metadata) {
        final CDOBranch childCDOBranch = createCDOBranch(parent, name);
        final CDOBranchPoint[] basePath = childCDOBranch.getBasePath();
        final long timeStamp = basePath[basePath.length - 1].getTimeStamp();
		return doReopen(parent.getPath(), name, metadata, timeStamp, timeStamp, childCDOBranch.getID());
    }

    private RevisionBranch doReopen(final String parentPath, final String name, final Metadata metadata, final long baseTimestamp, final long headTimestamp, final int cdoBranchId) {
    	return commit(index -> {
			final RevisionBranch parentBranch = getBranch(parentPath);
			// the "new" child branch
			final RevisionSegment parentSegment = parentBranch.getSegments().last();
			SortedSet<RevisionSegment> segments = ImmutableSortedSet.<RevisionSegment>naturalOrder()
				.addAll(parentBranch.getSegments().headSet(parentSegment))
				.add(parentSegment.withEnd(baseTimestamp))
				.add(new RevisionSegment(cdoBranchId, baseTimestamp, headTimestamp))
				.build();
			final RevisionBranch childBranch = RevisionBranch.builder()
					.id(cdoBranchId)
					.name(name)
					.parentPath(parentPath)
					.deleted(false)
					.metadata(metadata)
					.segments(segments)
					.build();
			create(childBranch).execute(index);
			return childBranch;
		});
    }
    
    private CDOBranch createCDOBranch(RevisionBranch parent, String name) {
        return getCDOBranch(parent).createBranch(name);
    }

//    /**
//     * Notifies this branch manager that a commit has happened on the given cdoBranchId with the given commitTimestamp.
//     * @param cdoBranchId
//     * @param commitTimestamp
//     */
//    public final void handleCommit(int cdoBranchId, long commitTimestamp) {
//    	handleCommit((InternalBranch) getBranch(cdoBranchId), commitTimestamp);
//    }
    
    @Override
    protected void sendChangeEvent(final String branch) {
    	repository.sendNotification(new BranchChangedEvent(repository.id(), branch));
    }
}
