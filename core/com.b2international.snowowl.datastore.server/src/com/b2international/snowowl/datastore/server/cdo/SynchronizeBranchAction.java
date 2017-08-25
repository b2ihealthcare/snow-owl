/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.cdo;

import static com.b2international.commons.ChangeKind.DELETED;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.cdo.ConflictWrapper;
import com.b2international.snowowl.datastore.cdo.ConflictingChange;
import com.b2international.snowowl.datastore.cdo.CustomConflictException;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.events.BranchChangedEvent;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.server.internal.branch.CDOBranchMerger;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * Synchronizes changes with the task branches' parents. 
 */
public class SynchronizeBranchAction extends AbstractCDOBranchAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeBranchAction.class);

	private final List<CDOTransaction> transactions = newArrayList();

	private final String commitComment;

	public SynchronizeBranchAction(final IBranchPathMap branchPathMap, final String userId, final String commitComment) {
		super(branchPathMap, userId, DatastoreLockContextDescriptions.SYNCHRONIZE);
		this.commitComment = commitComment;
	}
	
	@Override
	protected boolean isApplicable(String repositoryId, IBranchPath taskBranchPath) throws Throwable {
		if (!super.isApplicable(repositoryId, taskBranchPath)) {
			return false;
		}
		
		final IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
		final Branch branch = RepositoryRequests.branching()
				.prepareGet(taskBranchPath.getPath())
				.build(repositoryId)
				.execute(eventBus)
				.getSync();
		
		return branch.canRebase();
	}

	@Override
	protected void apply(final String repositoryId, final IBranchPath taskBranchPath) throws Throwable {

		final ICDOConnection connection = getConnection(repositoryId);
		final CDOBranch taskBranch = connection.getBranch(taskBranchPath);
		final IBranchPath parentBranchPath = taskBranchPath.getParent();

		LOGGER.info(MessageFormat.format("Applying changes from ''{0}'' to ''{1}'' in ''{2}''...", 
				parentBranchPath,
				taskBranchPath, 
				connection.getRepositoryName()));

		// Do a test run against the parent branch first
		CDOTransaction testTransaction = connection.createTransaction(parentBranchPath);
		try {
			applyChangeSet(testTransaction, taskBranch);
		} finally {
			if (testTransaction != null) {
				testTransaction.close();
			}
		}
		
		final IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
		final Branch reopenedBranch = RepositoryRequests.branching()
				.prepareReopen(taskBranchPath.getPath())
				.build(repositoryId)
				.execute(eventBus)
				.getSync();

		// At this point, others are free to make changes to the parent after reopening the task branch
		releaseLock(new SingleRepositoryAndBranchLockTarget(repositoryId, parentBranchPath));
		
		// This transaction now holds the actual change set on the reopened child, which has the same name as before
		final CDOTransaction syncTransaction = connection.createTransaction(taskBranchPath); 
		applyChangeSet(syncTransaction, taskBranch);
		
		if (syncTransaction.isDirty()) {
			transactions.add(syncTransaction);
		} else {
			// Explicit notification, let listeners know the new state
			final BranchChangedEvent changeEvent = new BranchChangedEvent(repositoryId, reopenedBranch.path());
			changeEvent.publish(eventBus);
			syncTransaction.close();
		}
	}

	private void applyChangeSet(final CDOTransaction transaction, final CDOBranch taskBranch) throws CustomConflictException {
		
		final String repositoryUuid = transaction.getSession().getRepositoryInfo().getUUID();
		final CDOBranchMerger branchMerger = new CDOBranchMerger(CDOConflictProcessorBroker.INSTANCE.getProcessor(repositoryUuid));

		try {
			
			// XXX: specifying sourceBase instead of defaulting to the computed common ancestor point here
			transaction.merge(taskBranch.getHead(), taskBranch.getBase(), branchMerger);
			LOGGER.info(MessageFormat.format("Post-processing components in ''{0}''...", repositoryUuid));
			branchMerger.postProcess(transaction);
			
		} catch (final ConflictException e) {

			final Set<ConflictWrapper> conflictWrappers = newHashSet(); 

			for (final Conflict cdoConflict : branchMerger.getConflicts().values()) {	
				convertRepresentation(cdoConflict, conflictWrappers);
			}
			
			throw new CustomConflictException("Conflicts detected while synchronizing task", conflictWrappers);
		}
	}

	/**
	 * Converts CDO conflict representations to application-specific ones, and appends them to the specified set if the
	 * conversion was successful.
	 * 
	 * @param conflict the conflict to process (may not be {@code null})
	 * @param conflictSet the set of conflicts to append to (may not be {@code null})
	 */
	private void convertRepresentation(final Conflict conflict, final Set<ConflictWrapper> conflictSet) {	
		checkNotNull(conflict, "CDO conflict to process may not be null.");
		checkNotNull(conflictSet, "Converted conflicts set may not be null.");

		if (conflict instanceof ChangedInSourceAndTargetConflict) {

			final CDORevisionDelta sourceDelta = ((ChangedInSourceAndTargetConflict) conflict).getSourceDelta();
			final CDORevisionDelta targetDelta = ((ChangedInSourceAndTargetConflict) conflict).getTargetDelta();

			final Map<EStructuralFeature, CDOFeatureDelta> sourceDeltaMap = ((InternalCDORevisionDelta) sourceDelta).getFeatureDeltaMap();
			final Map<EStructuralFeature, CDOFeatureDelta> targetDeltaMap = ((InternalCDORevisionDelta) targetDelta).getFeatureDeltaMap();

			for (final EStructuralFeature targetFeature : targetDeltaMap.keySet()) {
				final CDOFeatureDelta sourceFeatureDelta = sourceDeltaMap.get(targetFeature);
				final CDOFeatureDelta targetFeatureDelta = targetDeltaMap.get(targetFeature);

				if (sourceFeatureDelta instanceof CDOSetFeatureDelta && targetFeatureDelta instanceof CDOSetFeatureDelta) {
					final ConflictingChange changeOnSource = new ConflictingChange(sourceDelta.getID(), targetFeature, ((CDOSetFeatureDelta) sourceFeatureDelta).getValue());
					final ConflictingChange changeOnTarget = new ConflictingChange(targetDelta.getID(), targetFeature, ((CDOSetFeatureDelta) targetFeatureDelta).getValue());
					conflictSet.add(new ConflictWrapper(changeOnTarget, changeOnSource));
				}
			}

		} else if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict){	

			final CDORevisionDelta sourceDelta = ((ChangedInSourceAndDetachedInTargetConflict) conflict).getSourceDelta();
			final Map<EStructuralFeature, CDOFeatureDelta> sourceDeltaMap = ((InternalCDORevisionDelta) sourceDelta).getFeatureDeltaMap();

			for (final EStructuralFeature sourceFeature : sourceDeltaMap.keySet()) {
				final CDOFeatureDelta sourceFeatureDelta = sourceDeltaMap.get(sourceFeature);

				if (sourceFeatureDelta instanceof CDOSetFeatureDelta) {
					final ConflictingChange changeOnSource = new ConflictingChange(sourceDelta.getID(), sourceFeature, ((CDOSetFeatureDelta) sourceFeatureDelta).getValue());
					final ConflictingChange changeOnTarget = new ConflictingChange(DELETED, conflict.getID());
					conflictSet.add(new ConflictWrapper(changeOnTarget, changeOnSource));
				}
			}

		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {

			final CDORevisionDelta targetDelta = ((ChangedInTargetAndDetachedInSourceConflict) conflict).getTargetDelta();
			final Map<EStructuralFeature, CDOFeatureDelta> targetDeltaMap = ((InternalCDORevisionDelta) targetDelta).getFeatureDeltaMap();

			for (final EStructuralFeature targetFeature : targetDeltaMap.keySet()) {
				final CDOFeatureDelta targetFeatureDelta = targetDeltaMap.get(targetFeature);

				if (targetFeatureDelta instanceof CDOSetFeatureDelta) {
					final ConflictingChange changeOnSource = new ConflictingChange(DELETED, conflict.getID());
					final ConflictingChange changeOnTarget = new ConflictingChange(targetDelta.getID(), targetFeature, ((CDOSetFeatureDelta) targetFeatureDelta).getValue());
					conflictSet.add(new ConflictWrapper(changeOnTarget, changeOnSource));
				}
			}
		}
	}

	@Override
	protected void postRun() throws CommitException {

		if (CompareUtils.isEmpty(transactions)) {
			return;
		}

		// Implicit branch change notification via committing
		new CDOServerCommitBuilder(getUserId(), commitComment, transactions)
			.parentContextDescription(getLockDescription())
			.commit();
		
		LogUtils.logUserEvent(LOGGER, getUserId(), "Synchronizing changes finished successfully.");
	}
	
	@Override
	protected void cleanUp() {
		for (final CDOTransaction transaction : transactions) {
			transaction.close();
		}
	}
}
