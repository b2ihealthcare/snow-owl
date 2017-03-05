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
package com.b2international.snowowl.datastore.server.cdo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.cdo.CDOTransactionAggregator;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.cdo.ICDOBranchActionManager;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.delta.HeadToBaseBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.HeadToTargetBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.CDOChangeSetDataProvider;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Application service class responsible for handling branch operations, eg. creation, synchronization and promotion of
 * changes.
 */
public class CDOBranchActionManager implements ICDOBranchActionManager {

	@SuppressWarnings("restriction") 
	private static final String CDO_OBJECT_QUERY = org.eclipse.emf.cdo.server.internal.db.SQLQueryHandler.CDO_OBJECT_QUERY;

	@Override
	public @Nullable Throwable prepare(final IBranchPathMap branchPathMap, final String userId) {
		return new PrepareBranchAction(branchPathMap, userId).run();
	}

	@Override
	public @Nullable Throwable synchronize(final IBranchPathMap branchPathMap, final String userId, final String commitComment) {
		return new SynchronizeBranchAction(branchPathMap, userId, commitComment).run();
	}

	@Override
	public @Nullable Throwable promote(final IBranchPathMap branchPathMap, final String userId, final String commitComment) {
		return new PromoteBranchAction(branchPathMap, userId, commitComment).run();
	}

	@Override
	public boolean isSynchronized(final IBranchPathMap branchPathMap) {
		checkNotNull(branchPathMap, "Branch path map argument cannot be null.");

		final ICDOConnectionManager connectionManager = getConnectionManager();

		for (final Entry<String, IBranchPath> entry : branchPathMap.getLockedEntries().entrySet()) {
			final String uuid = entry.getKey();
			final IBranchPath branchPath = entry.getValue();
			if (!isSynchronized(connectionManager.getByUuid(uuid), branchPath)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public long getLastCommitTime(final String repositoryUuid, final IBranchPath branchPath) {

		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");

		final ICDOConnection connection = getConnectionManager().getByUuid(repositoryUuid);
		Preconditions.checkNotNull(connection, "Repository does not exist with UUID: '" + repositoryUuid + "'.");

		final CDOBranch branch = connection.getBranch(branchPath);

		if (null == branch) { //branch does not even exist
			return Long.MIN_VALUE;
		}

		return CDOServerUtils.getLastCommitTime(branch);
	}

	@Override
	public @Nullable Throwable revert(final IBranchPoint branchPoint, final String userId) {
		checkNotNull(branchPoint, "Branch point cannot be null.");
		checkNotNull(userId, "User ID argument cannot be null.");

		final Set<CDOTransaction> transactions = Sets.newHashSet();

		try {

			final ICDOConnectionManager connectionManager = getConnectionManager();

			long minTargetTimestamp = Long.MAX_VALUE;

			final ICDOConnection connection = connectionManager.getByUuid(branchPoint.getUuid());
			final IBranchPath branchPath = branchPoint.getBranchPath();
			final long targetTimestamp = branchPoint.getTimestamp();
			final CDOBranch branch = connection.getBranch(branchPath);

			final IBranchPoint targetBranchPoint = BranchPointUtils.create(connection, branchPath, targetTimestamp);
			final HeadToTargetBranchPointCalculationStrategy strategy = new HeadToTargetBranchPointCalculationStrategy(targetBranchPoint);
			final CDOChangeSetData reverseChangeSet = CDOChangeSetDataProvider.INSTANCE.getChangeSetData(strategy);

			final InternalCDOTransaction transaction = (InternalCDOTransaction) connection.createTransaction(branch);
			transactions.add(transaction);

			try {

				transaction.revertChangeSetData(reverseChangeSet);

				if (targetTimestamp < minTargetTimestamp) {
					minTargetTimestamp = targetTimestamp;
				}

			} catch (final Throwable t) {
				throw new SnowowlRuntimeException(t);
			}

			final String comment = createCommitComment(minTargetTimestamp);

			if (!CompareUtils.isEmpty(transactions)) {
				CDOServerUtils.commit(transactions, userId, comment, true, new NullProgressMonitor());
			}

		} catch (final Throwable t) {
			return t;
		} finally {

			Collections3.forEach(transactions, new Procedure<Object>() {
				@Override protected void doApply(final Object lifecycle) {
					LifecycleUtil.deactivate(lifecycle);
				}
			});
		}

		return null;
	}

	@Override
	public Throwable revertAllChangesOnBranch(final IBranchPath branchPath, final String userId, 
			@Nullable final String commitComment, final String repositoryUuid) {

		return revertAllChangesOnBranchWithContext(branchPath, userId, commitComment, repositoryUuid, DatastoreLockContextDescriptions.ROOT);
	}

	@Override
	public Throwable revertAllChangesOnBranchWithContext(final IBranchPath branchPath, final String userId, 
			@Nullable final String commitComment,
			final String repositoryUuid,
			final String parentContextDescription) {

		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");


		long minTargetTimestamp = Long.MAX_VALUE;
		ICDOTransactionAggregator aggregator = null;

		try {

			final ICDOConnection connection = Preconditions.checkNotNull(
					getConnectionManager().getByUuid(repositoryUuid), 
					"Repository cannot be found with UUID: " + repositoryUuid);

			if (null != connection.getBranch(branchPath)) {

				final IBranchPointCalculationStrategy strategy = new HeadToBaseBranchPointCalculationStrategy(connection, branchPath);
				final CDOChangeSetData changeSetData = CDOChangeSetDataProvider.INSTANCE.getChangeSetData(strategy);

				if (changeSetData.isEmpty()) {
					return null;
				}

				final CDOTransaction transaction = connection.createTransaction(branchPath);
				((InternalCDOTransaction) transaction).revertChangeSetData(changeSetData);

				aggregator = CDOTransactionAggregator.create(transaction);

				final long targetTimestamp = strategy.getTargetBranchPoint().getTimestamp();
				if (targetTimestamp < minTargetTimestamp) {
					minTargetTimestamp = targetTimestamp;
				}

				final String comment;

				if (StringUtils.isEmpty(commitComment)) {

					comment = createCommitComment(minTargetTimestamp);

				} else {

					comment = commitComment;

				}

				new CDOServerCommitBuilder(userId, comment, aggregator)
				.parentContextDescription(parentContextDescription)
				.commit();
			}

		} catch (final Throwable t) {

			return t;

		} finally {

			if (aggregator instanceof CDOTransactionAggregator) {
				((CDOTransactionAggregator) aggregator).dispose();
			}

		}		

		return null;
	}

	private String createCommitComment(final long minTargetTimestamp) {
		return String.format("Reverted to repository state as of %s.", Dates.formatByHostTimeZone(minTargetTimestamp, DateFormats.LONG));
	}

	private boolean isSynchronized(final ICDOConnection connection, final IBranchPath branchPath) {

		final CDOBranch branch = connection.getBranch(branchPath);

		if (null == branch) { //could happen that branch does not exist on the repository yet
			return true;
		}

		if (branch.isMainBranch()) { //synchronized check is meaningless for MAIN branch, return true
			return true;
		}

		final CDOBranch parentBranch = connection.getBranch(branchPath.getParent());

		final boolean branchSynchronized = CDOUtils.apply(new CDOViewFunction<Boolean, CDOView>(connection, branchPath) {
			@Override protected Boolean apply(final CDOView view) {
				final CDOQuery parentMaxCommitTimeQuery = view.createQuery("sql", "SELECT MAX(COMMIT_TIME) FROM CDO_COMMIT_INFOS WHERE BRANCH_ID = " + parentBranch.getID());
				parentMaxCommitTimeQuery.setParameter(CDO_OBJECT_QUERY, false);

				final long parentMax = Iterables.getFirst(parentMaxCommitTimeQuery.getResult(Long.class), Long.MIN_VALUE).longValue();
				final long branchBase = branch.getBase().getTimeStamp();
				return parentMax <= branchBase;
			}
		});

		return branchSynchronized;
	}

	/*returns with the connection manager service.*/
	private ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
	}
}
