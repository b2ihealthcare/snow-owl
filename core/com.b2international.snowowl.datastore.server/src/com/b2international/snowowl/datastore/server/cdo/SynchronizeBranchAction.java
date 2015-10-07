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
package com.b2international.snowowl.datastore.server.cdo;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ConflictWrapper;
import com.b2international.snowowl.datastore.cdo.CustomConflictException;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.server.events.BranchReply;
import com.b2international.snowowl.datastore.server.events.ReopenBranchEvent;
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
	protected void apply(final String repositoryId, final IBranchPath taskBranchPath) throws Throwable {

		final ICDOConnection connection = getConnectionManager().getByUuid(repositoryId);
		final CDOBranch taskBranch = connection.getBranch(taskBranchPath);

		// Does the task CDO branch exist?
		if (null == taskBranch) {
			return;
		}
		
		final IBranchPath parentBranchPath = taskBranchPath.getParent();
		final CDOBranch parentBranch = taskBranch.getBase().getBranch();

		// Did someone set the main branch as taskBranch ("base of main branch is null")?
		if (null == parentBranch) {
			return;
		}
		
		LOGGER.info(MessageFormat.format("Applying changes from ''{0}'' to ''{1}'' in ''{2}''...", 
				parentBranchPath,
				taskBranchPath, 
				connection.getRepositoryName()));

		// Do a test run against the parent branch first
		CDOTransaction testTransaction = null;
		try {
			testTransaction = applyChangeSet(connection, taskBranch, parentBranchPath);
		} finally {
			if (testTransaction != null) {
				testTransaction.close();
			}
		}
		
		final IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
		final ReopenBranchEvent event = new ReopenBranchEvent(repositoryId, taskBranchPath.getPath());
		final BranchReply reply;
		try {
			reply = event.send(eventBus, BranchReply.class).get();
		} catch (InterruptedException e) {
			throw e;
		} catch (ExecutionException e) {
			throw e.getCause();
		}
		
		// This transaction now holds the actual change set on the reopened child
		final CDOTransaction syncTransaction = applyChangeSet(connection, taskBranch, reply.getBranch().branchPath());
		if (syncTransaction.isDirty()) {
			transactions.add(syncTransaction);
		} else {
			syncTransaction.close();
		}
	}

	private CDOTransaction applyChangeSet(final ICDOConnection connection, final CDOBranch taskBranch, IBranchPath newTaskBranchPath) throws CustomConflictException {
		
		final CDOBranchMerger branchMerger = new CDOBranchMerger(CDOConflictProcessorBroker.INSTANCE.getProcessor(connection.getUuid()));
		CDOTransaction transaction = null;

		try {
			
			transaction = connection.createTransaction(newTaskBranchPath);
			transaction.merge(taskBranch.getHead(), branchMerger);

			LOGGER.info(MessageFormat.format("Post-processing components in ''{0}''...", connection.getRepositoryName()));
			branchMerger.postProcess(transaction);
			
			return transaction;
			
		} catch (final ConflictException e) {

			final Set<ConflictWrapper> conflictWrappers = newHashSet(); 

			for (final Conflict cdoConflict : branchMerger.getConflicts().values()) {	
				CDOConflictProcessorBroker.INSTANCE.processConflict(cdoConflict, conflictWrappers);
			}

			// TODO: Check if we rely on this functionality (ie. skipping certain CDO-level conflicts)
			if (conflictWrappers.isEmpty()) {
				LOGGER.warn("CDO conflicts are present, but were not converted: {}. Continuing.", branchMerger.getConflicts().values());
				return transaction;
			} else {
				if (transaction != null) {
					transaction.close();
				}
				
				throw new CustomConflictException("Conflicts detected while synchronizing task", conflictWrappers);
			}
		}
	}

	@Override
	protected void postRun() throws CommitException {

		if (CompareUtils.isEmpty(transactions)) {
			return;
		}

		try {

			new CDOServerCommitBuilder(getUserId(), commitComment, transactions)
				.parentContextDescription(getLockDescription())
				.commit();

			LogUtils.logUserEvent(LOGGER, getUserId(), "Synchronizing changes finished successfully.");

		} finally {
			for (final CDOTransaction transaction : transactions) {
				transaction.close();
			}
		}
	}
}
