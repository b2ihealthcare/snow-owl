/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.commons.Pair;
import com.b2international.index.BulkIndexWrite;
import com.b2international.index.IndexWrite;
import com.b2international.index.Writer;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataImpl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.exceptions.MergeConflictException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.events.BranchChangedEvent;
import com.b2international.snowowl.datastore.internal.InternalRepository;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.replicate.BranchReplicator;
import com.b2international.snowowl.identity.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * {@link BranchManager} implementation based on {@link CDOBranch} functionality.
 *
 * @since 4.1
 */
public final class CDOBranchManagerImpl extends BranchManagerImpl implements BranchReplicator {

	private static final String CDO_BRANCH_ID = "cdoBranchId";

	private final InternalRepository repository;
	private final AtomicInteger segmentIds = new AtomicInteger(0);
	private final ObjectMapper mapper;
	
    public CDOBranchManagerImpl(final InternalRepository repository, ObjectMapper mapper) {
        super(repository.getIndex());
        this.repository = repository;
       	
        final CDOBranch cdoMainBranch = repository.getCdoMainBranch();
      	final long baseTimestamp = repository.getBaseTimestamp(cdoMainBranch);
      	// assign first segment to MAIN
      	final int segmentId = segmentIds.getAndIncrement();
		initBranchStore(new CDOMainBranchImpl(baseTimestamp, repository.getHeadTimestamp(cdoMainBranch), new MetadataImpl(), segmentId, ImmutableSet.of(segmentId)));
       	
		int maxExistingSegment = segmentId;
		for (Branch branch : getBranches()) {
			if (branch instanceof InternalCDOBasedBranch) {
				final int branchSegmentId = ((InternalCDOBasedBranch) branch).segmentId();
				if (branchSegmentId > maxExistingSegment) {
					maxExistingSegment = branchSegmentId;
				}
			}
		}
		segmentIds.set(maxExistingSegment+1);
		this.mapper = mapper;
    }
    
    private synchronized Pair<Integer, Integer> nextTwoSegments() {
    	final int newBranchSegment = segmentIds.getAndIncrement();
    	final int newParentSegment = segmentIds.getAndIncrement();
    	return Pair.of(newBranchSegment, newParentSegment);
    }
    
    @Override
    public void replicateBranch(final org.eclipse.emf.cdo.common.branch.CDOBranch branch) {
		if (!branch.isMainBranch()) {
			final int cdoBranchId = branch.getID();
			// if content already available with this cdoBranchId then skip
			Branch existingBranch = getBranch(cdoBranchId);
			
			if (existingBranch == null) {
				try {
					// get existing branch by name, this can be null, if this is the first replication of the branch
					existingBranch = getBranch(branch.getPathName());
				} catch (NotFoundException e) {
					// ignore not found branches
				}
				final int parentCdoBranchId = branch.getBase().getBranch().getID();
				final InternalCDOBasedBranch parent = (InternalCDOBasedBranch) getBranch(parentCdoBranchId);
				if (parent == null) {
					throw new SkipBranchException(branch);
				}
				final String name = branch.getName();
				final long baseTimestamp = repository.getBaseTimestamp(branch);
				final long headTimestamp = repository.getHeadTimestamp(branch);
				doReopen(parent.path(), name, existingBranch == null ? new MetadataImpl() : existingBranch.metadata(), baseTimestamp, headTimestamp, cdoBranchId);
			}
		}
    }

    public final CDOBranch getCDOBranch(Branch branch) {
        checkArgument(!branch.isDeleted(), "Deleted branches cannot be retrieved.");
        final int branchId = ((InternalCDOBasedBranch) branch).cdoBranchId();
        return loadCDOBranch(branchId);
    }

    private Branch getBranch(Integer branchId) {
    	return getBranchFromStore(Query.select(BranchDocument.class).where(Expressions.match(CDO_BRANCH_ID, branchId)));
    }
    
    private CDOBranch loadCDOBranch(Integer branchId) {
        return repository.getCdoBranchManager().getBranch(branchId);
    }

    @Override
    public InternalBranch rebase(InternalBranch branch, InternalBranch onTopOf, String commitMessage, Runnable postReopen) {
		CDOTransaction testTransaction = null;
		CDOTransaction newTransaction = null;
		
		IndexWrite<Void> delete = null;
		
		try {
			
			if (branch.headTimestamp() > branch.baseTimestamp()) {
				
				testTransaction = applyChangeSet(branch, onTopOf, true);
				
				final InternalCDOBasedBranch tmpBranch = (InternalCDOBasedBranch) reopen(onTopOf,
						String.format(Branch.TEMP_BRANCH_NAME_FORMAT, Branch.TEMP_PREFIX, branch.name(), System.currentTimeMillis()), branch.metadata());
				
				delete = prepareDelete(tmpBranch.path());
				
				postReopen.run();
				
				newTransaction = transferChangeSet(testTransaction, tmpBranch);
				final InternalCDOBasedBranch tmpBranchWithChanges = (InternalCDOBasedBranch) commitChanges(branch, tmpBranch, commitMessage, newTransaction);
				
				final CDOBranchImpl rebasedBranch = new CDOBranchImpl(branch.name(), onTopOf.path(), tmpBranchWithChanges.baseTimestamp(), 
						tmpBranchWithChanges.headTimestamp(), branch.metadata(), tmpBranchWithChanges.cdoBranchId(), 
						tmpBranchWithChanges.segmentId(), tmpBranchWithChanges.segments(), tmpBranchWithChanges.parentSegments());
				
				final IndexWrite<Void> replace = prepareReplace(branch.path(), rebasedBranch);
				
				final CDOBranch rebasedCDOBranch = getCDOBranch(rebasedBranch);
				rebasedCDOBranch.rename(branch.name());
				
				BulkIndexWrite<Void> bulkWrite = new BulkIndexWrite<>(replace, delete);
				
				commit(bulkWrite);
				
				delete = null;
				
				sendChangeEvent(branch.path()); // Explicit notification (reopen)
				
				return rebasedBranch;
				
			} else {

				final InternalBranch rebasedBranch = reopen(onTopOf, branch.name(), branch.metadata());
				postReopen.run();
				sendChangeEvent(rebasedBranch.path()); // Explicit notification (reopen)
				return rebasedBranch;
				
			}
			
		} finally {
			
			if (delete != null) {
				commit(delete);
			}
			
			LifecycleUtil.deactivate(testTransaction);
			LifecycleUtil.deactivate(newTransaction);
		}
    }

	private IndexWrite<Void> prepareReplace(final String path, final InternalBranch value) {
		return update(path, BranchDocument.Scripts.REPLACE, ImmutableMap.of("replace", mapper.convertValue(value.toDocument().build(), Map.class)));
	}
	
	private IndexWrite<Void> prepareDelete(final String path) {
		return new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
				index.remove(BranchDocument.class, path);
				return null;
			}
		};
	}
    
    private CDOTransaction transferChangeSet(final CDOTransaction transaction, final InternalBranch rebasedBranch) {
		final ICDOConnection connection = repository.getConnection();
		final CDOBranch rebasedCdoBranch = getCDOBranch(rebasedBranch);
		final CDOTransaction newTransaction = connection.createTransaction(rebasedCdoBranch);
		
		CDOUtils.mergeTransaction(transaction, newTransaction);
		return newTransaction;
	}
    
    @Override
    protected InternalBranch applyChangeSet(InternalBranch from, InternalBranch to, boolean dryRun, boolean isRebase, String commitMessage) {
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
			throw new MergeConflictException(conflicts, String.format("Could not resolve all conflicts while applying changeset on '%s' from '%s'.", to.path(), from.path()));
    	}
    }
    
	private InternalBranch commitChanges(InternalBranch from, InternalBranch to, String commitMessage, CDOTransaction targetTransaction) {
		try { 

            if (targetTransaction.isDirty()) {
    			// FIXME: Using "System" user and "synchronize" description until a more suitable pair can be specified here
            	targetTransaction.setCommitComment(commitMessage);
            	CDOCommitInfo commitInfo = new CDOServerCommitBuilder(User.SYSTEM.getUsername(), commitMessage, targetTransaction)
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
    protected InternalBranch doReopen(InternalBranch parent, String name, Metadata metadata) {
        final CDOBranch childCDOBranch = createCDOBranch(parent, name);
        final CDOBranchPoint[] basePath = childCDOBranch.getBasePath();
        final long timeStamp = basePath[basePath.length - 1].getTimeStamp();
		return doReopen(parent.path(), name, metadata, timeStamp, timeStamp, childCDOBranch.getID());
    }

    private InternalBranch doReopen(final String parentPath, final String name, final Metadata metadata, final long baseTimestamp, final long headTimestamp, final int cdoBranchId) {
    	final Pair<Integer, Integer> nextTwoSegments = nextTwoSegments();
    	return commit(new IndexWrite<InternalBranch>() {
			@Override
			public InternalBranch execute(Writer index) throws IOException {
				final InternalCDOBasedBranch parentBranch = (InternalCDOBasedBranch) index.searcher().get(BranchDocument.class, parentPath).toBranch();
				final Set<Integer> parentSegments = newHashSet();
		    	// all branch should know the segment path to the ROOT
		    	parentSegments.addAll(parentBranch.parentSegments());
		    	parentSegments.addAll(parentBranch.segments());
				
				// the "new" child branch
				final CDOBranchImpl childBranch = new CDOBranchImpl(name, parentPath, baseTimestamp, headTimestamp, metadata, cdoBranchId, nextTwoSegments.getA(), Collections.singleton(nextTwoSegments.getA()), parentSegments);
				create(childBranch).execute(index);
				update(parentPath, BranchDocument.Scripts.WITH_SEGMENTID, ImmutableMap.of("segmentId", nextTwoSegments.getB())).execute(index);
				return childBranch;
			}
		});
    }
    
    private CDOBranch createCDOBranch(InternalBranch parent, String name) {
        return getCDOBranch(parent).createBranch(name);
    }

    /**
     * Notifies this branch manager that a commit has happened on the given cdoBranchId with the given commitTimestamp.
     * @param cdoBranchId
     * @param commitTimestamp
     */
    public final void handleCommit(int cdoBranchId, long commitTimestamp) {
    	handleCommit((InternalBranch) getBranch(cdoBranchId), commitTimestamp);
    }
    
    @Override
    void sendChangeEvent(final String branch) {
    	repository.sendNotification(new BranchChangedEvent(repository.id(), branch));
    }
}
