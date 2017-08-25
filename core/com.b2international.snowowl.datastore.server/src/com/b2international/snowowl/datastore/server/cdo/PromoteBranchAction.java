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

import static com.google.common.collect.Lists.newArrayList;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.Exceptions;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branch.BranchState;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.BranchNotSynchronizedException;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.cdo.EmptyTransactionAggregatorException;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.server.internal.branch.CDOBranchMerger;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * Promotes changes from any task branch to its parent. 
 */
public class PromoteBranchAction extends AbstractCDOBranchAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(PromoteBranchAction.class);

	private final List<CDOTransaction> transactions = newArrayList();

	private final String commitComment;

	public PromoteBranchAction(final IBranchPathMap branchPathMap, final String userId, final String commitComment) {
		super(branchPathMap, userId, DatastoreLockContextDescriptions.PROMOTE);
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
		
		final BranchState state = branch.state();
		
		// Silently reject an attempt to promote an up-to-date branch 
		if (BranchState.UP_TO_DATE == state) {
			return false;
		}
		
		// Report an exception if this is not a "fast-forward" merge
		if (BranchState.FORWARD != state) {

			final IBranchPath parentBranchPath = taskBranchPath.getParent();
			LOGGER.error("Modifications have been made on the '{}' branch. Promotion canceled for user '{}'.", parentBranchPath,  getUserId());
			throw new BranchNotSynchronizedException(MessageFormat.format("Modifications have been made on the ''{0}'' branch, promotion "
					+ "is not allowed. Please synchronize changes and try again.", parentBranchPath));
		}
		
		return true;
	}
	
	@Override
	protected void apply(final String repositoryId, final IBranchPath taskBranchPath) throws Exception {

		final ICDOConnection connection = getConnection(repositoryId);
		final CDOBranch taskBranch = connection.getBranch(taskBranchPath);

		final IBranchPath parentBranchPath = taskBranchPath.getParent();
		final CDOBranchPoint parentBranchPoint = taskBranch.getBase();
		final CDOBranch parentBranch = parentBranchPoint.getBranch();

		LOGGER.info("Promoting changes from '{}' to '{}' in '{}'...", taskBranchPath, parentBranchPath, connection.getRepositoryName());
		final CDOBranchMerger branchMerger = new CDOBranchMerger(CDOConflictProcessorBroker.INSTANCE.getProcessor(repositoryId));

		try {

			final CDOTransaction transaction = connection.createTransaction(parentBranch);
			transaction.merge(taskBranch.getHead(), taskBranch.getBase(), branchMerger);

			LOGGER.info(MessageFormat.format("Unlinking components in ''{0}''...", connection.getRepositoryName()));
			branchMerger.postProcess(transaction);

			if (transaction.isDirty()) {
				transactions.add(transaction);
			}

		} catch (final ConflictException e) {
			throw new SnowowlServiceException("Error while promoting changes.", e);			
		}
	}

	@Override
	protected void postRun() throws Exception {

		LOGGER.info("Committing changes...");

		try {

			new CDOServerCommitBuilder(getUserId(), commitComment, transactions)
				.parentContextDescription(getLockDescription())
				.commit();
			
			LogUtils.logUserEvent(LOGGER, getUserId(), "Promoting changes finished successfully.");
		} catch (final CommitException e) {

			final RepositoryLockException lockException = Exceptions.extractCause(e, getClass().getClassLoader(), RepositoryLockException.class);
			if (null != lockException) {
				throw e;
			} else {
				throw new SnowowlServiceException("Error while promoting changes.", e);
			}

		} catch (final EmptyTransactionAggregatorException e) {
			throw new EmptyTransactionAggregatorException("Nothing to promote.");
		}
	}
	
	@Override
	protected void cleanUp() {
		for (final CDOTransaction transaction : transactions) {
			transaction.close();
		}
	}
}
