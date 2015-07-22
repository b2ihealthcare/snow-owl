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

import java.text.MessageFormat;
import java.util.HashSet;
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

		// Does the parent branch exist?
		if (null == parentBranch) {
			return;
		}
		
		LOGGER.info(MessageFormat.format("Applying changes from ''{0}'' to ''{1}'' in ''{2}''...", 
				parentBranchPath,
				taskBranchPath, 
				connection.getRepositoryName()));

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
		
		applyChangeSet(connection, taskBranch, reply.getBranch().branchPath());
	}

	private void applyChangeSet(final ICDOConnection connection, final CDOBranch taskBranch, IBranchPath newTaskBranchPath) throws CustomConflictException {
		
		final CDOBranchMerger branchMerger = new CDOBranchMerger(CDOConflictProcessorBroker.INSTANCE.getProcessor(connection.getUuid()));
		final Set<ConflictWrapper> conflictWrappers = new HashSet<ConflictWrapper>(); 

		try {
			
			final CDOTransaction transaction = connection.createTransaction(newTaskBranchPath);
			transaction.merge(taskBranch.getHead(), branchMerger);

			LOGGER.info(MessageFormat.format("Post-processing components in ''{0}''...", connection.getRepositoryName()));
			branchMerger.postProcess(transaction);

			if (transaction.isDirty()) {
				transactions.add(transaction);
			}

		} catch (final ConflictException e) {

			for (final Conflict cdoConflict : branchMerger.getConflicts().values()) {	
				CDOConflictProcessorBroker.INSTANCE.processConflict(cdoConflict, conflictWrappers);
			}			
		}

		if (!conflictWrappers.isEmpty()) {
			throw new CustomConflictException("Conflicts detected while synchronizing task", conflictWrappers);
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
