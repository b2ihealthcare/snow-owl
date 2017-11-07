/*
 * Copyright 2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.reindex;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.DelegatingRepository;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.revision.DelegatingCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils;
import com.b2international.snowowl.datastore.cdo.DelegatingTransaction;
import com.b2international.snowowl.datastore.replicate.BranchReplicator;
import com.b2international.snowowl.datastore.replicate.BranchReplicator.SkipBranchException;
import com.b2international.snowowl.datastore.request.repository.OptimizeRequest;
import com.b2international.snowowl.datastore.request.repository.PurgeRequest;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.google.common.collect.Sets;

/**
 * Replication context the delegates the actual work to the same repository the replications is reading from. 
 * During the replication, the change processors responsible for writing Lucene index documents 
 * are triggered but no actual records are written into the repository via the replaced CommitContext
 */
@SuppressWarnings("restriction")
class IndexMigrationReplicationContext implements CDOReplicationContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexMigrationReplicationContext.class);

	private final RepositoryContext context;
	private final long initialLastCommitTime;
	private final int initialBranchId;
	private final InternalSession replicatorSession;
	
	private TreeMap<Long, CDOBranch> branchesByBasetimestamp = new TreeMap<>();
	
	private int skippedCommits = 0;
	private int processedCommits = 0;
	private long failedCommitTimestamp = -1;
	private final Set<Integer> skippedBranches = Sets.newHashSet();

	private Exception exception;

	private boolean optimize = false;

	IndexMigrationReplicationContext(final RepositoryContext context, final int initialBranchId, final long initialLastCommitTime, final InternalSession session) {
		this.context = context;
		this.initialBranchId = initialBranchId;
		this.initialLastCommitTime = initialLastCommitTime;
		this.replicatorSession = session;
	}

	@Override
	public void handleCommitInfo(final CDOCommitInfo commitInfo) {
		// skip commits by CDO_SYSTEM user
		if (CDOCommitInfoUtils.CDOCommitInfoQuery.EXCLUDED_USERS.contains(commitInfo.getUserID())) {
			return;
		}
		
		if (failedCommitTimestamp != -1) {
			skippedCommits++;
			return;
		}
		
		final long commitTimestamp = commitInfo.getTimeStamp();
		
		Entry<Long, CDOBranch> lastBranchToReplicateBeforeCommit = branchesByBasetimestamp.floorEntry(commitTimestamp);
		if (lastBranchToReplicateBeforeCommit != null) {
			// get the first entry and use the base of that branch for purge
			Entry<Long, CDOBranch> currentBranchToReplicate = branchesByBasetimestamp.firstEntry();
			
			// before creating the branches, execute a purge on the latest segment of the parent branch
			PurgeRequest.builder()
				.setBranchPath(currentBranchToReplicate.getValue().getBase().getBranch().getPathName())
				.build()
				.execute(context);
			
			// replicate all branches created before the current commit in order
			do {
				final CDOBranch branch = currentBranchToReplicate.getValue();
				LOGGER.info("Replicating branch: " + branch.getName() + " at " + branch.getBase().getTimeStamp());
				try {
					context.service(BranchReplicator.class).replicateBranch(branch);
				} catch (SkipBranchException e) {
					LOGGER.warn("Skipping branch with all of its commits: {}", branch.getID());
					skippedBranches.add(branch.getID());
				}
				branchesByBasetimestamp.remove(currentBranchToReplicate.getKey());
				
				// check if there are more branches to create until this point
				currentBranchToReplicate = branchesByBasetimestamp.firstEntry();
			} while (currentBranchToReplicate != null && currentBranchToReplicate.getKey() <= lastBranchToReplicateBeforeCommit.getKey());
			
			if (optimize) {
				optimize();
				optimize = false;
			}
		}
		
		try {
			if (isVersionCommit(commitInfo)) {
				// optimize the index next time we create the version branch
				this.optimize = true;
			}
			
			if (skippedBranches.contains(commitInfo.getBranch().getID())) {
				skippedCommits++;
				return;
			}
			
			LOGGER.info("Replicating commit: " + commitInfo.getComment() + " at " + commitInfo.getBranch().getName() + "@" + commitTimestamp);	
		} catch (DBException e) {
			skippedCommits++;
			
			if (e.getMessage().startsWith("Branch with ID")) {
				LOGGER.warn("Skipping commit with missing branch: " + commitInfo.getComment() + " at " + commitInfo.getBranch().getID() + "@" + commitTimestamp);
				return;
			} else {
				failedCommitTimestamp = commitTimestamp;
				this.exception = e;
			}
		}
		
		final InternalRepository repository = replicatorSession.getManager().getRepository();
		final InternalCDORevisionManager revisionManager = repository.getRevisionManager();
		
		final InternalRepository delegateRepository = new DelegatingRepository() {
			
			@Override
			public InternalCDORevisionManager getRevisionManager() {
				
				return new DelegatingCDORevisionManager() {
				
					@Override
					protected InternalCDORevisionManager getDelegate() {
						return revisionManager;
					}

					@Override
					public EClass getObjectType(CDOID id, CDOBranchManager branchManagerForLoadOnDemand) {
						return revisionManager.getObjectType(id, branchManagerForLoadOnDemand);
					}
					
					/* (non-Javadoc)
					 * @see org.eclipse.emf.cdo.spi.common.revision.DelegatingCDORevisionManager#getRevision(org.eclipse.emf.cdo.common.id.CDOID, org.eclipse.emf.cdo.common.branch.CDOBranchPoint, int, int, boolean)
					 */
					@Override
					public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand) {
						
						//future revisions are hidden (no -1)
						if (branchPoint.getTimeStamp() >= commitInfo.getTimeStamp()) {
							return null;
						}
						
						InternalCDORevision revision = super.getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
						InternalCDORevision copiedRevision = revision.copy();
						
						//we fake later revisions as brand new revision (revised=0)
						if (revision.getRevised() >= commitInfo.getTimeStamp() -1) {
							copiedRevision.setRevised(CDORevision.UNSPECIFIED_DATE);
						}
						return copiedRevision;
					}
					
					/* (non-Javadoc)
					 * @see org.eclipse.emf.cdo.spi.common.revision.DelegatingCDORevisionManager#getRevisionByVersion(org.eclipse.emf.cdo.common.id.CDOID, org.eclipse.emf.cdo.common.branch.CDOBranchVersion, int, boolean)
					 */
					@Override
					public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand) {
						// first revisions are hidden
						if (!(branchVersion instanceof CDORevisionDelta) && branchVersion.getBranch().getID() == commitInfo.getBranch().getID() && branchVersion.getVersion() == CDOBranchVersion.FIRST_VERSION) {
							return null;
						}
						
						InternalCDORevision revisionByVersion = super.getRevisionByVersion(id, branchVersion, referenceChunk, loadOnDemand);
						
						InternalCDORevision copiedRevision = revisionByVersion.copy();
						
						//we fake later revisions as brand new revision
						if (revisionByVersion.getRevised() >= commitInfo.getTimeStamp()-1) {
							copiedRevision.setRevised(CDORevision.UNSPECIFIED_DATE);
						}
						return copiedRevision;
					}
					
				};
			}
			
			@Override
			protected InternalRepository getDelegate() {
				return repository;
			}
			
			@Override
			public void endCommit(long timeStamp) {
				//do nothing
			}
			
			@Override
			public void failCommit(long timestamp) {
				failedCommitTimestamp = timestamp;
				skippedCommits++;
			}
			
			@Override 
			public void sendCommitNotification(final InternalSession sender, final CDOCommitInfo commitInfo) {
				//do nothing, no post commit notifications are expected
			}
		};
		
		// this is not the actual HEAD of the particular branch!!
		CDOBranch branch = commitInfo.getBranch();
		CDOBranchPoint head = branch.getHead();

		InternalTransaction transaction = replicatorSession.openTransaction(InternalSession.TEMP_VIEW_ID, head);
		DelegatingTransaction delegatingTransaction = new DelegatingTransaction(transaction) {

			//Transaction needs to return the delegating repository as well
			@Override
			public InternalRepository getRepository() {
				return delegateRepository;
			}
		};

		NonWritingTransactionCommitContext commitContext = new NonWritingTransactionCommitContext(delegatingTransaction, commitInfo);

		commitContext.preWrite();
		boolean success = false;

		try {
			commitContext.write(new Monitor());
			commitContext.commit(new Monitor());
			success = true;
			processedCommits++;
		} finally {
			commitContext.postCommit(success);
			transaction.close();
			StoreThreadLocal.setSession(replicatorSession);
		}
	}

	private boolean isVersionCommit(final CDOCommitInfo commitInfo) {
		for (CDOIDAndVersion newObject : commitInfo.getNewObjects()) {
			if (newObject instanceof CDORevision) {
				if (TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION.isSuperTypeOf(((CDORevision) newObject).getEClass())) {
					return true;
				}
			}
		}
		for (CDORevisionKey changedObject : commitInfo.getChangedObjects()) {
			if (changedObject instanceof CDORevision) {
				if (TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION.isSuperTypeOf(((CDORevision) changedObject).getEClass())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void optimize() {
		OptimizeRequest.builder().setMaxSegments(8).build().execute(context);
	}

	@Override
	public int getLastReplicatedBranchID() {
		return initialBranchId;
	}

	@Override
	public long getLastReplicatedCommitTime() {
		return initialLastCommitTime;
	}

	@Override
	public String[] getLockAreaIDs() {
		return new String[] {};
	}

	@Override
	public void handleBranch(CDOBranch branch) {
		branchesByBasetimestamp.put(branch.getBase().getTimeStamp(), branch);
	}

	@Override
	public boolean handleLockArea(LockArea area) {
		return false;
	}
	
	public long getFailedCommitTimestamp() {
		return failedCommitTimestamp;
	}
	
	public int getSkippedCommits() {
		return skippedCommits;
	}
	
	public int getProcessedCommits() {
		return processedCommits;
	}
	
	public Exception getException() {
		return exception;
	}

}
