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

import static com.b2international.commons.ChangeKind.ADDED;
import static com.b2international.commons.exceptions.Exceptions.extractCause;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.TaskBranchPathMap;
import com.b2international.snowowl.datastore.cdo.BranchNotSynchronizedException;
import com.b2international.snowowl.datastore.cdo.CDOTransactionAggregator;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.cdo.ConflictWrapper;
import com.b2international.snowowl.datastore.cdo.CustomConflictException;
import com.b2international.snowowl.datastore.cdo.EmptyCDOChangeSetData;
import com.b2international.snowowl.datastore.cdo.EmptyTransactionAggregatorException;
import com.b2international.snowowl.datastore.cdo.ICDOBranchManager;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.delta.BaseToHeadBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.HeadToBaseBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.HeadToTargetBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.SourceToHeadBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.CDOChangeSetDataProvider;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.index.IndexServerServiceManager;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Application service class responsible for handling branch operations, eg.
 * creation, synchronization and promotion of changes.
 * 
 */
public class CDOBranchManager implements ICDOBranchManager {

	@SuppressWarnings("restriction") private static final String CDO_OBJECT_QUERY = org.eclipse.emf.cdo.server.internal.db.SQLQueryHandler.CDO_OBJECT_QUERY;
	private static final Logger LOGGER = LoggerFactory.getLogger(CDOBranchManager.class);

	private final Object branchChangeMutex = new Object();

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOBranchManager#prepare(com.b2international.snowowl.datastore.BranchPathMap, java.lang.String)
	 */
	@Override
	public @Nullable Throwable prepare(final IBranchPathMap branchPathMap, final String userId) {
		checkNotNull(branchPathMap, "Destination branch path map argument cannot be null.");

		try {

			Collections3.forEach(branchPathMap.getLockedEntries().entrySet(), new Procedure<Entry<String, IBranchPath>>() {
				@Override protected void doApply(final Entry<String, IBranchPath> entry) {
					
					final ICDOConnection connection = getConnectionManager().getByUuid(entry.getKey());
					final IBranchPath toBranchPath = branchPathMap.getBranchPath(connection.getUuid());
	
					if (null == toBranchPath) {
						//branch should not be created for task
						return;
					}
					
					if (BranchPathUtils.isMain(toBranchPath)) {
						// No need to prepare MAIN
						return;
					}
	
					final IBranchPath fromBranchPath = toBranchPath.getParent();
					final CDOBranch fromBranch = connection.getBranch(fromBranchPath);
	
					if (null == fromBranch) {
						throw new IllegalStateException(MessageFormat.format("Source branch base ''{0}'' not found on connection ''{1}''.", fromBranchPath.getPath(), connection.getUuid()));
					}
	
					final IIndexUpdater<IIndexEntry> indexService = IndexServerServiceManager.INSTANCE.getIndexService(connection.getUuid());
					
					synchronized (branchChangeMutex) {
						final CDOBranch toBranch = connection.getBranch(toBranchPath);
						final String message = "Changing to " + toBranchPath.getPath() + " in '" + connection.getRepositoryName() + "'...";
						
						LOGGER.info(message);
						LogUtils.logUserEvent(LOGGER, userId, fromBranchPath, message);
	
						if (null != toBranch) {
							indexService.prepare(toBranchPath);
							return;
						} else {
							fromBranch.createBranch(toBranchPath.lastSegment());
						}
					}
					
					indexService.snapshotFor(toBranchPath, false, false);
					indexService.prepare(toBranchPath);
					
				}
			});
			
		} catch (final Throwable t) {
			return t;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOBranchManager#synchronize(com.b2international.snowowl.datastore.BranchPathMap, java.lang.String)
	 */
	@Override
	public @Nullable Throwable synchronize(final TaskBranchPathMap branchPathMap, final String userId) {
		checkNotNull(branchPathMap, "Branch path map argument cannot be null.");
		checkNotNull(userId, "User ID argument cannot be null.");
		
		try {

			//repository UUID and the target branch change set data
			final Map<String, CDOChangeSetData> targetChanges = Maps.newHashMap();

			//calculate target branch changes
			Collections3.forEach(branchPathMap.getLockedEntries().entrySet(), new Procedure<Entry<String, IBranchPath>>() {
				@Override protected void doApply(final Entry<String, IBranchPath> entry) {

					final ICDOConnection connection = getConnectionManager().getByUuid(entry.getKey());
					final String uuid = connection.getUuid();
					final IBranchPath taskBranchPath = branchPathMap.getBranchPath(uuid);
					
					final CDOBranch taskBranch = connection.getBranch(taskBranchPath);

					if (null == taskBranch) {
						targetChanges.put(uuid, EmptyCDOChangeSetData.INSTANCE);
						return;
					}

					final IBranchPath parentBranchPath = taskBranchPath.getParent();
					final CDOBranchPoint mainBranchBasePoint = taskBranch.getBase();

					final long sourceCreationTime = mainBranchBasePoint.getTimeStamp();
					final long targetLastCommitTime = CDOServerUtils.getLastCommitTime(connection.getBranch(parentBranchPath));
					
					if (sourceCreationTime > targetLastCommitTime) {
						targetChanges.put(uuid, EmptyCDOChangeSetData.INSTANCE);
						return;
					}

					LOGGER.info("Obtaining changes in '" + connection.getRepositoryName() + "' from " + taskBranchPath.getParentPath() + " branch...");
					
					final SourceToHeadBranchPointCalculationStrategy strategy = // 
							new SourceToHeadBranchPointCalculationStrategy(connection, parentBranchPath, sourceCreationTime);
					final CDOChangeSetData targetChangeSetData = CDOChangeSetDataProvider.INSTANCE.getChangeSetData(strategy);

					//no changes in target branch, nothing to synchronize 
					if (targetChangeSetData.isEmpty()) {
						targetChanges.put(uuid, EmptyCDOChangeSetData.INSTANCE);
						return;
					}

					targetChanges.put(uuid, targetChangeSetData);
				}
			});

			//repository UUID and the source branch change set data
			final Map<String, CDOChangeSetData> sourceChanges = Maps.newHashMap();

			//calculate source branch changes
			Collections3.forEach(branchPathMap.getLockedEntries().entrySet(), new Procedure<Entry<String, IBranchPath>>() {
				@Override protected void doApply(final Entry<String, IBranchPath> entry) {

					final ICDOConnection connection = getConnectionManager().getByUuid(entry.getKey());
					final String uuid = connection.getUuid();
					final IBranchPath taskBranchPath = branchPathMap.getBranchPath(uuid);


					final CDOBranch taskBranch = connection.getBranch(taskBranchPath);

					if (null == taskBranch) {
						sourceChanges.put(uuid, EmptyCDOChangeSetData.INSTANCE);
						return;
					}

					//no changes on source since branching from ancestor
					if (Long.MIN_VALUE == CDOServerUtils.getLastCommitTime(taskBranch)) {
						sourceChanges.put(uuid, EmptyCDOChangeSetData.INSTANCE);
						return;
					}
					
					LOGGER.info("Obtaining changes in '" + connection.getRepositoryName() + "' from  " + taskBranchPath.getPath() + " branch...");

					final BaseToHeadBranchPointCalculationStrategy strategy = // 
							new BaseToHeadBranchPointCalculationStrategy(connection, taskBranchPath);
					final CDOChangeSetData sourceChangeSetData = CDOChangeSetDataProvider.INSTANCE.getChangeSetData(strategy);

					//no changes in source branch 
					if (sourceChangeSetData.isEmpty()) {
						sourceChanges.put(uuid, EmptyCDOChangeSetData.INSTANCE);
						return;
					}

					sourceChanges.put(uuid, sourceChangeSetData);
				}
			});

			//calculate conflicts if any. interrupt whenever first conflict found
			for (final Entry<String, IBranchPath> entry : branchPathMap.getLockedEntries().entrySet()) {

				final ICDOConnection connection = getConnectionManager().getByUuid(entry.getKey());
				final String uuid = connection.getUuid();
				final IBranchPath sourceBranchPath = branchPathMap.getBranchPath(uuid);
				final CDOBranch sourceBranch = connection.getBranch(sourceBranchPath);

				if (null == sourceBranch) {
					continue;
				}

				final IBranchPath targetBranchPath = sourceBranchPath.getParent();
				final CDOBranch targetBranch = connection.getBranch(targetBranchPath);

				if (null == targetBranch) {
					continue;
				}

				LOGGER.info("Checking conflicts between '" + sourceBranchPath.getPath() + "' and '" + targetBranchPath.getPath() + "' in '" + connection.getRepositoryName() + "'...");
				
				try {
					
					final CDOChangeSetData sourceChangeSetData = sourceChanges.get(uuid);
					final CDOChangeSetData targetChangeSetData = targetChanges.get(uuid);
					checkConflicts(sourceChangeSetData, sourceBranch, targetChangeSetData, targetBranch);
					
				} catch (final CustomConflictException e) {
					return e;
				}
			}

			final Set<CDOTransaction> transactions = Sets.newHashSet();

			Collections3.forEach(branchPathMap.getLockedEntries().entrySet(), new Procedure<Entry<String, IBranchPath>>() {
				@Override protected void doApply(final Entry<String, IBranchPath> entry) {

					final ICDOConnection connection = getConnectionManager().getByUuid(entry.getKey());
					final String uuid = connection.getUuid();
					final IBranchPath taskBranchPath = branchPathMap.getBranchPath(uuid);
					
					final CDOBranch sourceBranch = connection.getBranch(taskBranchPath);
					final CDOBranch targetBranch = connection.getBranch(taskBranchPath.getParent());

					if (null != sourceBranch && null != targetBranch && !isSynchronized(connection, taskBranchPath)) {

						final CDOChangeSetData sourceChangeSetData = sourceChanges.get(uuid);
						final CDOChangeSetData targetChangeSetData = targetChanges.get(uuid);

						// Create an empty, new branch on top with same name, do the same for the index
						final CDOBranch newBranch = targetBranch.createBranch(taskBranchPath.lastSegment());
						IndexServerServiceManager.INSTANCE.getIndexService(uuid).updateSnapshotFor(taskBranchPath, newBranch.getBase().getTimeStamp());

						Set<CDOID> objectsToRemove = Sets.newHashSet();

						//apply changes from branch to new branch (if null -> nothing to merge, the new branch is in sync)
						if (!sourceChangeSetData.isEmpty()) {

							LOGGER.info("Applying changes in '" + connection.getRepositoryName() + "' from '" + taskBranchPath.getParentPath() + "' to '" + taskBranchPath.getPath() + "'...");
							
							/*
							 * it is possible that an object has been detached on the task branch and modified on the main branch. This case is not 
							 * handled as a conflict in the workflow. The objects which reference the detached object will be removed.
							 */
							objectsToRemove = handleDetachedObjects(sourceChangeSetData, sourceBranch, targetChangeSetData, targetBranch);

							final CDOTransaction newBranchTransaction = connection.getSession().openTransaction(newBranch);
							((InternalCDOTransaction) newBranchTransaction).applyChangeSetData(sourceChangeSetData);

							LOGGER.info("Post processing dangling components in '" + connection.getRepositoryName() + "'...");
							CDOConflictProcessorBroker.INSTANCE.detachConflictingObject(objectsToRemove, newBranchTransaction);

							transactions.add(newBranchTransaction);
						}
					}
				}
			});

			try {

				if (!CompareUtils.isEmpty(transactions)) {
					
					final String comment = "Synchronized task branch with parent.";
					final Iterable<CDOCommitInfo> commit = CDOServerUtils.commit(transactions, userId, comment, true, null);
					
					Collections3.forEach(commit, new Procedure<CDOCommitInfo>() {
						@Override protected void doApply(final CDOCommitInfo commitInfo) {

							final ICDOConnection connection = getConnectionManager().get(commitInfo.getBranch());
							final String uuid = connection.getUuid();
							final IBranchPath taskBranchPath = branchPathMap.getBranchPath(uuid);
							
							/* 
							 * Log as a user activity
							 * 
							 * TODO: do we need log messages for all branch synchronization, or just on the task 
							 * (branch path map) level? What does the output look like then?
							 */
							LogUtils.logUserEvent(LOGGER, userId, taskBranchPath, "Synchronizing changes in '" + connection.getRepositoryName() + "' successfully finished.");
						}
					});
				}

			} catch (final CommitException e) {
				return e;
			} finally {

				Collections3.forEach(transactions, new Procedure<Object>() {
					@Override protected void doApply(final Object lifecycle) {
						LifecycleUtil.deactivate(lifecycle);
					}
				});
			}

		} catch (final Throwable t) {
			return t;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOBranchManager#isSynchronized(com.b2international.snowowl.datastore.BranchPathMap)
	 */
	@Override
	public boolean isSynchronized(final TaskBranchPathMap branchPathMap) {
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

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOBranchManager#getLastCommitTime(java.lang.String, com.b2international.snowowl.core.api.IBranchPath)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOBranchManager#promote(com.b2international.snowowl.datastore.BranchPathMap, java.lang.String, java.lang.String)
	 */
	@Override
	public @Nullable Throwable promote(final TaskBranchPathMap branchPathMap, final String userId, final String commitComment) {
		checkNotNull(branchPathMap, "Branch path map argument cannot be null.");
		checkNotNull(userId, "User ID argument cannot be null.");
		
		LOGGER.info("Promoting changes...");
		
		try {

			final Set<CDOTransaction> transactions = Sets.newHashSet();
			for (final Entry<String, IBranchPath> entry : branchPathMap.getLockedEntries().entrySet()) {

				final ICDOConnection connection = getConnectionManager().getByUuid(entry.getKey());
				final IBranchPath sourceBranchPath = branchPathMap.getBranchPath(connection.getUuid());
				final CDOBranch sourceBranch = connection.getBranch(sourceBranchPath);

				if (null == sourceBranch) {
					continue; ///branch does not even exist in the repository
				}

				if (Long.MIN_VALUE == CDOServerUtils.getLastCommitTime(sourceBranch)) {
					continue; //no changes on source, keep iterating
				}
				
				final InternalCDOSession session = (InternalCDOSession) connection.getSession();
				final IBranchPath targetBranchPath = sourceBranchPath.getParent();

				//target branch transaction is the brand new transaction
				final CDOTransaction targetTransaction = connection.createTransaction(targetBranchPath);

				//branch point representing the HEAD of the current branch 
				CDOBranchPoint sourceBranchHead = sourceBranch.getHead();

				final long now = targetTransaction.getLastUpdateTime();

				//current state of the MAIN branch as the target of the promotion process
				final CDOBranchPoint targetBranchHead = targetTransaction.getBranch().getPoint(now);

				//we need to set a timestamp
				if (CDOBranchPoint.UNSPECIFIED_DATE == sourceBranchHead.getTimeStamp()) {
					sourceBranchHead = sourceBranchHead.getBranch().getPoint(now);
				}

				//check branch points
				Preconditions.checkState(!CDOBranchUtil.isContainedBy(sourceBranchHead, targetBranchHead), "Source branch HEAD already contained by target HEAD.");

				final CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(targetBranchHead, sourceBranchHead);

				final CDORevisionAvailabilityInfo ancestorInfo = session.createRevisionAvailabilityInfo(ancestor);
				final CDORevisionAvailabilityInfo targetInfo = session.createRevisionAvailabilityInfo(targetBranchHead);
				final CDORevisionAvailabilityInfo sourceInfo = session.createRevisionAvailabilityInfo(sourceBranchHead);

				final Set<CDOID> ids = session.getSessionProtocol().loadMergeData(targetInfo, sourceInfo, ancestorInfo, null/*, nsURIs restriction???*/);

				session.cacheRevisions(targetInfo);
				session.cacheRevisions(sourceInfo);
				session.cacheRevisions(ancestorInfo);

				final CDOChangeSet targetChanges = createChangeSet(ids, ancestorInfo, targetInfo);

				//this could happen when task are promoted concurrently
				//refer to following use case below:
				//user_1 adds new description D1 to concept C on a branch
				//user_2 adds new description D2 to concept C on a branch
				//both users promotes the changes to the parent branch at the "same time"
				//t1 (thread) stands for user_1
				//t2 (thread) stands for user_2
				//t1 creates a transaction to MAIN HEAD
				//t2 creates a transaction to MAIN HEAD
				//t1 calculates last updated time to MAIN (time_100)
				//t2 is much slower it's waiting for a while or calculating (whatever, assume pseudo-random behavior)
				//t1 calculates source, target and ancestor availability info
				//t1 merges data as it's looking for related CDO IDs
				//t2 still *NOT* set the last update time from MAIN
				//t1 calculates source and target changes
				// - in this context source is the task change set and target is the parent (MAIN) change set
				// - as synchronized task context is the precondition of a promote operation target change set should be empty
				//t1 applies changes to the MAIN transaction and commits changes
				//all session (and views, transaction) will be notified about the changes -> triggers last update time changes on the underlying session as well
				//t2 calculates the last update time from the MAIN transaction (as unspecified is unacceptable) which is from the underlying session
				// - session has been updated due to the successful commit of t1
				//t2 gets last updated time (time_200) [time_200 > time_100]
				//t2 calculates availability info, loads effected CDO object IDs
				//t2 calculates source (task) change set and target (MAIN) change set
				//t2 has a *NOT* empty target change set, as it has been changed meanwhile
				//t2 should *NOT* be able to keep on with the promotion process, as it is not synchronized with parent branch any more.
				//otherwise: it might happen that user_2 will add the D2 to C and D1 as well, but D1 already contained by C concept
				if (!targetChanges.isEmpty()) {

					final String targetPath = sourceBranchPath.getParent().getPath();
					LOGGER.info("Modifications have been made on the '" + targetPath + "' branch. Promotion canceled for '" + userId + "'.");

					return new BranchNotSynchronizedException(
							"Modifications have been made on the '" + targetPath + "' branch, promotion is not allowed. " +
							"Please synchronize changes and try again.");
				}

				final CDOChangeSet sourceChanges = createChangeSet(ids, ancestorInfo, sourceInfo);

				if (!sourceChanges.isEmpty()) {

					//calculate changes between MAIN head and current task
					//NOTE: task could consists of various number of branches with the same name but different IDs
					final CDOChangeSetData sourceChangeSetData = new DefaultCDOMerger.PerFeature.ManyValued().merge(targetChanges, sourceChanges);
					((InternalCDOTransaction) targetTransaction).applyChangeSetData(sourceChangeSetData);

					try {

						//check duplicate CDO resources conflicts
						checkConflicts(targetTransaction);
						transactions.add(targetTransaction);

					} catch (final Throwable t) {
						return t;
					}	
				}
			}

			try {

				LOGGER.info("Committing changes...");
				CDOServerUtils.commit(transactions, userId, commitComment, true, null);

			} catch (final CommitException e) {

				if (!isSynchronized(branchPathMap)) {

					// TODO: figure out how much detail we need in this exception. "Branch X of Repository Y has been modified"?
					return new BranchNotSynchronizedException(
							"Modifications have been made on the parent branch, promotion is not allowed. " +
							"Please synchronize changes and try again.");

				} else {
					
					final RepositoryLockException lockException = extractCause(e, this.getClass().getClassLoader(), RepositoryLockException.class);
					if (null != lockException) {
						//intentionally not log error
						throw e;
					}
					
					return new SnowowlServiceException("Error while promoting changes.", e);
				}

			} catch (final EmptyTransactionAggregatorException e) {
				return new EmptyTransactionAggregatorException("Nothing to promote.");
			} catch (final Throwable t) {
				return t;
			} finally {

				Collections3.forEach(transactions, new Procedure<Object>() {
					@Override protected void doApply(final Object lifecycle) {
						LifecycleUtil.deactivate(lifecycle);
					}
				});
			}

			// TODO: when to report these, how many times?
			
//			LOGGER.info("User " + userId + " has promoted branch " + sourceBranchPath + " content to MAIN branch with the comment: " + commitComment);
//
//			//Log as a user activity
//			LogUtils.logUserEvent(LOGGER, userId, sourceBranchPath, "Promoted changes to MAIN with comment: " + commitComment);

		} catch (final EmptyTransactionAggregatorException e) {
			return new EmptyTransactionAggregatorException("Nothing to promote.");
		} catch (final Throwable t) {
			return t;
		}

		return null;
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
				checkConflicts(transaction);
				transaction.revertChangeSetData(reverseChangeSet);
				
				if (targetTimestamp < minTargetTimestamp) {
					minTargetTimestamp = targetTimestamp;
				}
				
			} catch (final Throwable t) {
				throw new com.b2international.snowowl.core.api.SnowowlRuntimeException(t);
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
	
	private String createCommitComment(long minTargetTimestamp) {
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
	
		final boolean branchSynchronized = CDOUtils.apply(new CDOViewFunction<Boolean, CDOView>(connection) {
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

	/**
	 * If an objects has been deleted on a task branch, and objects that reference this object has been created on the main branch, 
	 * when synchronizing the task, the created objects have to be deleted. This method collects the objects, that should be removed. 
	 * @param taskChangeSetData changes made on the task branch
	 * @param taskBranch task CDO branch.
	 * @param mainChangeSetData changes made on the MAIN branch
	 * @param mainBranch MAIN branch.
	 * @return set of objects that should be removed.
	 */
	private Set<CDOID> handleDetachedObjects(final CDOChangeSetData taskChangeSetData, final CDOBranch taskBranch, final CDOChangeSetData mainChangeSetData, final CDOBranch mainBranch) {

		final ICDOConnection connection = getConnectionManager().get(taskBranch);
		final CDOView mainView = connection.createView(mainBranch);
		final Set<CDOID> objectsToRemove = Sets.newHashSet();

		try {

			for (final CDOIDAndVersion newOnMain : mainChangeSetData.getNewObjects()) {

				final CDOObject newObject = mainView.getObject(newOnMain.getID());
				final ConflictWrapper conflict = CDOConflictProcessorBroker.INSTANCE.checkConflictForNewObjects(taskChangeSetData, newOnMain, mainView);

				/*
				 * XXX: the source is the *parent* branch here; if the synchronization went the opposite way, and a new object on the *parent* would create
				 * an application-level conflict, add it to the objects to remove. This is primarily for eg. SCT reference set members where there is no
				 * strong reference to the component. 
				 */
				if (null != conflict && ADDED.equals(conflict.getChangeOnSource().getAction())) {
					objectsToRemove.add(newObject.cdoID());
				}
			}
			
		} finally {
			LifecycleUtil.deactivate(mainView);
		}

		return objectsToRemove;
	}

	/*calculates the change set between two branch points for the given subset of CDO IDs*/
	private CDOChangeSet createChangeSet(final Set<CDOID> ids, 
			final CDORevisionAvailabilityInfo startInfo, final CDORevisionAvailabilityInfo endInfo) {

		return CDORevisionUtil.createChangeSet(
				startInfo.getBranchPoint(), 
				endInfo.getBranchPoint(),
				CDORevisionUtil.createChangeSetData(ids, startInfo, endInfo));
	}

	/*checks CDO resource collision.*/
	private void checkConflicts(final CDOTransaction transaction) throws SnowowlServiceException {

		final List<CDOIDAndVersion> newIdAndVersions = transaction.getChangeSetData().getNewObjects();

		for (final CDOIDAndVersion newIdAndVersion : newIdAndVersions) {

			final CDORevision revision;

			if (newIdAndVersion instanceof InternalCDORevision) {

				revision = (CDORevision) newIdAndVersion;

			} else {

				revision = Iterables.get(CDOServerUtils.getRevisions(transaction, newIdAndVersion.getID()), 0);

			}

			final Throwable t = CDOServerUtils.checkDuplicateResources(revision, transaction);

			if (null != t) {

				throw new SnowowlServiceException("Promotion failed due to conflicting changes.");

			}

		}

	}

	/**
	 * 
	 * Detects conflicts between the provided changes set data.
	 * 
	 * @param sourceChangeSet changes made on the task branch
	 * @param sourceBranch the task branch.
	 * @param targetChangeSet changes made on the MAIN branch
	 * @param targetBranch the MAIN branch instance.
	 * @throws CustomConflictException if there are conflicts a {@link SnowowlServiceException} is thrown that wraps the details
	 */
	private void checkConflicts(final CDOChangeSetData sourceChangeSet, final CDOBranch sourceBranch, final CDOChangeSetData targetChangeSet, final CDOBranch targetBranch) throws CustomConflictException {

		if (null == sourceChangeSet || EmptyCDOChangeSetData.INSTANCE.equals(sourceChangeSet)) {
			return;
		}

		if (null == targetChangeSet || EmptyCDOChangeSetData.INSTANCE.equals(targetChangeSet)){
			return;
		}			

		final DefaultCDOMerger cdoMerger = new DefaultCDOMerger.PerFeature.ManyValued();

		final Set<ConflictWrapper> snowowlConflicts = new HashSet<ConflictWrapper>(); 

		try {

			cdoMerger.merge(
					CDORevisionUtil.createChangeSet(sourceBranch.getBase(), targetBranch.getHead(), targetChangeSet), 
					CDORevisionUtil.createChangeSet(sourceBranch.getBase(), sourceBranch.getHead(), sourceChangeSet));

		} catch (final ConflictException e) {

			final Map<CDOID, Conflict> cdoConflicts = cdoMerger.getConflicts();

			for (final CDOID cdoId : cdoConflicts.keySet()) {	

				final Conflict cdoConflict = cdoConflicts.get(cdoId);

				final ConflictWrapper conflictWrapper = CDOConflictProcessorBroker.INSTANCE.processConflict(cdoConflict);
				if (null != conflictWrapper) {

					snowowlConflicts.add(conflictWrapper);

				}

			}			
		}

		/*
		 * It is possible that there were no conflicts from the CDO point of view, but there are application-level
		 * conflicts, e.g. a reference set member that references a concept has been added on the branch, while the concept has
		 * been deleted on MAIN; in this case we report a conflict. Note that the opposite situation (new reference set member
		 * added on MAIN, concept deleted completely on task) is accepted, and the concept will remain deleted!
		 */
		final ICDOConnection connection = getConnectionManager().get(sourceBranch);
		CDOView sourceView = null;
		CDOView targetView = null;
		
		try {

			sourceView = connection.createView(sourceBranch);
			targetView = connection.createView(targetBranch);
			
			for (final CDOIDAndVersion newOnSource : sourceChangeSet.getNewObjects()) {
				final ConflictWrapper conflict = CDOConflictProcessorBroker.INSTANCE.checkConflictForNewObjects(targetChangeSet, newOnSource, sourceView);

				if (null != conflict) {
					snowowlConflicts.add(conflict);
				}
			}

			final Map<CDOID, CDORevisionKey> changedComponentsMapping = Maps.uniqueIndex(targetChangeSet.getChangedObjects(), new Function<CDOIDAndVersion, CDOID>() {
				public CDOID apply(final CDOIDAndVersion cdoIdAndVersion) {
					return cdoIdAndVersion.getID();
				}
			});
			
			for (final CDOIDAndVersion detachedOnSource : sourceChangeSet.getDetachedObjects()) {
				final ConflictWrapper conflict = CDOConflictProcessorBroker.INSTANCE.checkConflictForDetachedObjects(changedComponentsMapping, detachedOnSource, sourceView, targetView);

				if (null != conflict) {
					snowowlConflicts.add(conflict);
				}
			}

			
		} finally {
			LifecycleUtil.deactivate(sourceView);
			LifecycleUtil.deactivate(targetView);
		}

		if (!snowowlConflicts.isEmpty()) {
			throw new CustomConflictException("Conflicts detected while synchronizing task", snowowlConflicts);
		}
	}
}