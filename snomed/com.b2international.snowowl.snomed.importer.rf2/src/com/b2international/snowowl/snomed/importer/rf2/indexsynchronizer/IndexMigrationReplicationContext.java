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
package com.b2international.snowowl.snomed.importer.rf2.indexsynchronizer;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.revision.CDORevision;
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
import org.eclipse.net4j.util.om.monitor.Monitor;

import com.b2international.snowowl.datastore.server.DelegatingTransaction;

/**
 * Replication context the delegates the actual work to the same repository the replications is reading from. 
 * During the replication, the change processors responsible for writing Lucene index documents 
 * are triggered but no actual records are written into the repository via the replaced CommitContext
 */
@SuppressWarnings("restriction")
public class IndexMigrationReplicationContext implements CDOReplicationContext {

	private final long initialLastCommitTime;
	private final int initialBranchId;
	private final InternalSession replicatorSession;

	/**
	 * 
	 * @param initialBranchId
	 * @param initialLastCommitTime
	 * @param session
	 */
	public IndexMigrationReplicationContext(int initialBranchId, long initialLastCommitTime, InternalSession session) {
		this.initialBranchId = initialBranchId;
		this.initialLastCommitTime = initialLastCommitTime;
		this.replicatorSession = session;
	}

	@Override
	public void handleCommitInfo(final CDOCommitInfo commitInfo) {

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
			public void failCommit(long timeStamp) {
				//interrupt the replication process when a commit fails
				throw new RuntimeException("Commit with timestamp " + timeStamp +" failed.  Check the log file for details.");
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
		} finally {
			commitContext.postCommit(success);
			transaction.close();
			StoreThreadLocal.setSession(replicatorSession);
		}
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
		// TODO: index the branches
	}

	@Override
	public boolean handleLockArea(LockArea area) {
		return false;
	}

}
