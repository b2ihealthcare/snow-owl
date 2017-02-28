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
package com.b2international.snowowl.datastore.cdo;

import static com.b2international.commons.exceptions.Exceptions.extractCause;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler3;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;
import org.eclipse.emf.internal.cdo.object.CDOObjectReferenceImpl;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;
import org.eclipse.emf.internal.cdo.view.InternalCDOTransactionWrapper;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.transaction.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils.CDOObjectToCDOIDAdjuster;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Holds parameters related to a server-side commit, and triggers the commit on request.
 * 
 */
@SuppressWarnings("restriction")
public class CDOServerCommitBuilder {

	private static IProgressMonitor nullToNullMonitor(final IProgressMonitor monitor) {
		return (null == monitor) ? new NullProgressMonitor() : monitor;
	}

	/**
	 * In addition to filtering write accesses, also allows to suppress commit notifications and/or drop notifications
	 * for a single session (which may or may not be the original sender).
	 * <p>
	 * This class is non-static to allow direct access to fields in the containing builder class.
	 */
	private class ServerTransactionRepository extends WriteAccessHandlerFilteringRepository {

		private final CDOTransaction transactionToIgnore;

		private ServerTransactionRepository(final InternalRepository delegate, 
				final CDOTransaction transactionToIgnore,
				final boolean notifyWriteAccesshandlers) {

			super(delegate, notifyWriteAccesshandlers);
			this.transactionToIgnore = transactionToIgnore;
		}

		@Override 
		public void sendCommitNotification(final InternalSession sender, final CDOCommitInfo commitInfo) {

			if (sendCommitNotification) {
				final int sessionIdToIgnore = transactionToIgnore.getSessionID();
				final InternalSession sessionToIgnore = getSessionManager().getSession(sessionIdToIgnore);
				final CDOCommitInfo delegate = CDOCommitInfoUtils.removeUuidFromComment(commitInfo);

				super.sendCommitNotification(sessionToIgnore, delegate);
			}
		}
	}

	/**
	 * Customized commit context which allows overriding the committing user identifier and lock reason description.
	 * <p>
	 * This class is non-static to allow direct access to fields in the containing builder class.
	 */
	public final class ServerTransactionCommitContext extends CustomTransactionCommitContext {

		private ServerTransactionCommitContext(final InternalTransaction transaction, final IErrorLoggingStrategy strategy) {
			super(transaction, strategy);
		}

		@Override 
		public String getUserID() { 
			return userId; 
		}

		@Override 
		protected String getParentContextDescription() { 
			return parentContextDescription; 
		}

		public final boolean isCommitNotificationEnabled() {
			return sendCommitNotification;
		}
	}

	/**
	 * Creates 
	 * <p>
	 * This class is non-static to allow direct access to fields in the containing builder class.
	 */
	private class ServerTransaction extends DelegatingTransaction {

		private final CDOTransaction transaction;
		private final InternalRepository repository;

		private ServerTransaction(final InternalTransaction delegate, final CDOTransaction transaction, final InternalRepository repository) {
			super(delegate);
			this.transaction = transaction;
			this.repository = repository;
		}

		@Override 
		public InternalRepository getRepository() {
			return new ServerTransactionRepository(repository, transaction, notifyWriteAccessHandlers);
		}
		
		@Override 
		public ServerTransactionCommitContext createCommitContext() {
			return new ServerTransactionCommitContext(this, FilteringErrorLoggingStrategy.INSTANCE);
		}
	}

	private static class ViewTargetChangedEvent implements CDOViewTargetChangedEvent {

		private final InternalCDOTransaction transaction;
		private final CDOBranchPoint branchPoint;

		private ViewTargetChangedEvent(final InternalCDOTransaction transaction, final CDOBranchPoint branchPoint) {
			this.branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
			this.transaction = transaction;
		}

		@Override 
		public CDOView getSource() { 
			return transaction; 
		}

		@Override 
		public CDOBranchPoint getBranchPoint() { 
			return branchPoint; 
		}
	}

	private static class TransactionFinishedEvent implements CDOTransactionFinishedEvent {

		private final InternalCDOTransaction transaction;
		private final CDOTransactionFinishedEvent.Type type;
		private final Map<CDOID, CDOID> idMappings;

		private TransactionFinishedEvent(final InternalCDOTransaction transaction, 
				final CDOTransactionFinishedEvent.Type type, 
				final Map<CDOID, CDOID> idMappings) {

			this.idMappings = idMappings;
			this.type = type;
			this.transaction = transaction;
		}

		@Override 
		public CDOTransactionFinishedEvent.Type getType() { 
			return type; 
		}

		@Override 
		public Map<CDOID, CDOID> getIDMappings() { 
			return idMappings; 
		}

		@Override 
		public CDOView getSource() { 
			return transaction; 
		}
	}

	/**
	 * Carries information for a single aggregated commit.
	 * <p>
	 * This class is non-static to allow direct access to fields in the containing builder class.
	 */
	private class CDOServerCommit {

		private final List<CDOTransaction> transactions = newArrayList(transactionAggregator);
		private final String commitGroupUuid = transactionAggregator.getUuid();

		private final Map<CDOTransaction, InternalSession> impersonatingSessions = newHashMap();
		private final Map<CDOTransaction, ServerTransactionCommitContext> serverContexts = newHashMap();
		private final Map<CDOTransaction, InternalCDOCommitContext> clientContexts = newHashMap();
		private final Map<CDOTransaction, IStoreAccessor> storeAccessors = newHashMap();
		private final Map<CDOTransaction, CDOCommitInfo> commitInfos = newHashMap();

		public Iterable<CDOCommitInfo> commit(final IProgressMonitor monitor) throws CommitException {

			TransactionException commitException = null;
			
			try {

				createImpersonatingSessions();
				createServerContexts();

				try {
					transferWriteOptionLocks();
					transferChangeSets();
					createClientContexts();
					prepareTransactions();
					commitTransactions();
				} catch (final TransactionException e) {
					commitException = e;
					rollbackTransactions(e);
				}

				collectCommitInfos();
				postCommitTransactions();

				if (CompareUtils.isEmpty(commitInfos)) {
					throw new CommitException();
				} else if (Iterables.any(commitInfos.values(), Predicates.instanceOf(FailureCommitInfo.class))) {
					throw new CommitException(commitException);
				} else {
					return commitInfos.values();
				}
			} finally {
				deactivateImpersonatingSessions();
			}
		}

		private void createImpersonatingSessions() {

			for (final CDOTransaction transaction : transactions) {

				final String repositoryId = getRepositoryId(transaction);
				final InternalSession impersonatingSession = CDOUtils.openSession(userId, repositoryId);
				impersonatingSessions.put(transaction, impersonatingSession);
			}
		}

		private String getRepositoryId(final CDOTransaction transaction) {
			return transaction.getSession().getRepositoryInfo().getUUID();
		}

		private void createServerContexts() {

			for (final CDOTransaction transaction : transactions) {

				final InternalSession impersonatingSession = impersonatingSessions.get(transaction);
				final InternalRepository repository = impersonatingSession.getManager().getRepository();
				// XXX: The original transaction serves as a branch point here (always branch HEAD)
				final InternalTransaction impersonatingTransaction = impersonatingSession.openTransaction(InternalSession.TEMP_VIEW_ID, transaction);
				final ServerTransaction serverTransaction = new ServerTransaction(impersonatingTransaction, transaction, repository);
				final ServerTransactionCommitContext serverContext = serverTransaction.createCommitContext();
				transaction.setCommitComment(buildCommitMessage(commitGroupUuid, comment));
				serverContexts.put(transaction, serverContext);
			}
		}

		private String buildCommitMessage(final String uuid, @Nullable final String comment) {
			checkNotNull(uuid, "UUID argument cannot be null.");
			UUID.fromString(uuid); // XXX: Throws IAE if the UUID is not valid
			return uuid + Strings.nullToEmpty(comment);
		}

		private void transferWriteOptionLocks() throws TransactionException {

			for (final CDOTransaction transaction : transactions) {

				final InternalSession impersonatingSession = impersonatingSessions.get(transaction);
				final InternalRepository repository = impersonatingSession.getManager().getRepository();
				final InternalLockManager lockingManager = repository.getLockingManager();

				final int originalSessionId = transaction.getSessionID();
				final int originalViewId = transaction.getViewID();
				final InternalSession originalSession = repository.getSessionManager().getSession(originalSessionId);
				final InternalView originalOwner = originalSession.getView(originalViewId);
				final InternalTransaction newOwner = serverContexts.get(transaction).getTransaction();

				final Map<CDOID, LockGrade> originalLocks = lockingManager.getLocks(originalOwner);
				final Set<Object> writeOptionKeys = newHashSet();

				for (final Entry<CDOID, LockGrade> lockEntry : originalLocks.entrySet()) {
					if (lockEntry.getValue().isOption()) {
						writeOptionKeys.add(lockingManager.getLockKey(lockEntry.getKey(), transaction.getBranch()));
					}
				}

				if (!writeOptionKeys.isEmpty())
				{
					try {

						// Add a read lock alongside any existing write option lock
						lockObjectsAndNotify(lockingManager, LockType.READ, newOwner, writeOptionKeys, 1000, transaction);

						// Revoke all option locks from the original owner
						unlockObjectsAndNotify(lockingManager, LockType.OPTION, originalOwner, writeOptionKeys, transaction);

						// Add a write option for the new owner
						lockObjectsAndNotify(lockingManager, LockType.OPTION, newOwner, writeOptionKeys, 1000, transaction);

						// Finally, remove the read locks we added first
						unlockObjectsAndNotify(lockingManager, LockType.READ, newOwner, writeOptionKeys, transaction);

					} catch (final InterruptedException e) {
						throw new TransactionException(e);
					}
				}
			}
		}

		private void lockObjectsAndNotify(final InternalLockManager lockingManager, 
				final LockType lockType, 
				final InternalView context, 
				final Set<Object> objectsToLock, 
				final int timeout,
				final CDOTransaction transaction) throws InterruptedException {

			final List<LockState<Object, IView>> newLockStates = lockingManager.lock2(lockType, context, objectsToLock, timeout);
			sendLockNotification(Operation.LOCK, transaction, newLockStates);
		}

		private void unlockObjectsAndNotify(final InternalLockManager lockingManager, 
				final LockType lockType,
				final InternalView context, 
				final Set<Object> objectsToUnlock,
				final CDOTransaction transaction) {

			final List<LockState<Object, IView>> newLockStates = lockingManager.unlock2(lockType, context, objectsToUnlock);
			sendLockNotification(Operation.UNLOCK, transaction, newLockStates);
		}

		private void sendLockNotification(final Operation op, final CDOTransaction transaction, final List<LockState<Object, IView>> newLockStates) {
			final InternalCDOTransactionWrapper wrapper = new InternalCDOTransactionWrapper(transaction);
			wrapper.releaseLockStates(op, Repository.toCDOLockStates(newLockStates), transaction.getSession().getLastUpdateTime());
		}

		private void sendLockNotification(final List<LockState<Object, IView>> newLockStates, 
				final InternalLockManager lockingManager,
				final InternalView context, 
				final Operation operation) {

			final CDOLockState[] newStates = Repository.toCDOLockStates(newLockStates);
			final InternalRepository repository = lockingManager.getRepository();
			final long timeStamp = repository.getTimeStamp();

			final CDOLockChangeInfo info = CDOLockUtil.createLockChangeInfo(timeStamp, context, context.getBranch(), operation, null, newStates);
			repository.getSessionManager().sendLockNotification(context.getSession(), info);
		}

		private void transferChangeSets() {

			for (final CDOTransaction transaction : transactions) {
				final ServerTransactionCommitContext serverContext = serverContexts.get(transaction);
				transferChangeSet(transaction, serverContext);
			}
		}

		private void transferChangeSet(final CDOTransaction transaction, final ServerTransactionCommitContext serverContext) {

			checkNotNull(serverContext, "Commit context argument cannot be null.");
			CDOUtils.check(transaction);

			// Replace all CDO objects with NEW state with the corresponding object IDs
			CDOUtils.adjustRevsions(transaction, CDOObjectToCDOIDAdjuster.INSTANCE);

			final InternalCDORevision[] newRevisions = new InternalCDORevision[transaction.getNewObjects().size()];
			final InternalCDORevisionDelta[] dirtyRevisionDeltas = new InternalCDORevisionDelta[transaction.getLastSavepoint().getRevisionDeltas().size()];
			final CDOID[] detachedObjects = new CDOID[transaction.getDetachedObjects().size()];

			// Populate new revisions (copy all revisions so that the version is not adjusted twice)
			int i = 0;
			for (final CDOObject newObject : transaction.getNewObjects().values()) {
				newRevisions[i++] = (InternalCDORevision) newObject.cdoRevision().copy();
			}

			// Populate revision deltas
			i = 0;
			for (final CDORevisionDelta revisionDelta : transaction.getLastSavepoint().getRevisionDeltas().values()) {
				dirtyRevisionDeltas[i++] = (InternalCDORevisionDelta) revisionDelta;
			}

			// Populate detached object IDs
			i = 0;
			for (final Entry<CDOID, CDOObject> detachedEntry : transaction.getDetachedObjects().entrySet()) {
				detachedObjects[i++] = detachedEntry.getKey();
			}

			serverContext.setNewObjects(newRevisions);
			serverContext.setDirtyObjectDeltas(dirtyRevisionDeltas);
			serverContext.setDetachedObjects(detachedObjects);
			serverContext.setCommitComment(Strings.nullToEmpty(transaction.getCommitComment()));
		}

		private void createClientContexts() {

			for (final CDOTransaction transaction : transactions) {
				final InternalCDOCommitContext clientContext = ((InternalCDOTransaction) transaction).createCommitContext();
				clientContexts.put(transaction, clientContext);
			}
		}

		private void prepareTransactions() throws TransactionException {

			for (final CDOTransaction transaction : transactions) {
				final ServerTransactionCommitContext serverContext = serverContexts.get(transaction);
				prepareTransaction(transaction, serverContext);
			}
		}

		private void prepareTransaction(final CDOTransaction transaction, final ServerTransactionCommitContext serverContext) throws TransactionException {

			checkTransactionParameters(transaction, serverContext);
			final InternalCDOCommitContext clientContext = clientContexts.get(transaction);
			final List<CDOPackageUnit> newPackageUnits = clientContext.getNewPackageUnits();

			try {

				// XXX: This will allocate the store accessor, and places it in StoreThreadLocal; we will store the accessor for each server context 
				serverContext.preWrite();
				final IStoreAccessor accessor = StoreThreadLocal.getAccessor();
				storeAccessors.put(transaction, accessor);

				serverContext.setAutoReleaseLocksEnabled(transaction.options().isAutoReleaseLocksEnabled());
				
				final InternalCDOPackageUnit[] newUnits = new InternalCDOPackageUnit[newPackageUnits.size()];
				for (int i = 0; i < newUnits.length; i++) {
					newUnits[i] = (InternalCDOPackageUnit) newPackageUnits.get(i);
				}

				serverContext.setNewPackageUnits(newUnits);
				serverContext.write(new Monitor());

			} catch (final Throwable t) {
				handleTransactionException(t);
			}

			checkRollbackMessage(serverContext);
		}

		private void commitTransactions() {

			for (final CDOTransaction transaction : transactions) {
				final ServerTransactionCommitContext serverContext = serverContexts.get(transaction);
				final IStoreAccessor storeAccessor = storeAccessors.get(transaction);
				commitTransaction(transaction, serverContext, storeAccessor);
			}
		}

		private void commitTransaction(final CDOTransaction transaction, 
				final ServerTransactionCommitContext serverContext,
				final IStoreAccessor storeAccessor) {

			checkTransactionParameters(transaction, serverContext);
			initStoreThreadLocal(serverContext, storeAccessor);

			try {
				serverContext.commit(new Monitor());
			} catch (final Throwable t) {
				handleTransactionException(t);
			}

			checkRollbackMessage(serverContext);
		}

		private void handleTransactionException(final Throwable t) throws TransactionException {

			// Propagate a TransactionException without changes
			Throwables.propagateIfInstanceOf(t, TransactionException.class);

			// "Wrap" the Throwable in a TransactionException if it is not already an instance of it
			final String rollbackMessage = (null != t.getMessage()) ? t.getMessage() : Throwables.getStackTraceAsString(t);
			throw new TransactionException(rollbackMessage);
		}

		private void checkRollbackMessage(final ServerTransactionCommitContext serverContext) throws TransactionException {

			final String rollbackMessage = serverContext.getRollbackMessage();
			if (null != rollbackMessage) {
				throw new TransactionException(rollbackMessage);
			}
		}

		private void collectCommitInfos() {

			for (final CDOTransaction transaction : transactions) {
				final ServerTransactionCommitContext serverContext = serverContexts.get(transaction);
				final IStoreAccessor storeAccessor = storeAccessors.get(transaction);
				collectCommitInfo(transaction, serverContext, storeAccessor);
			}
		}

		private void collectCommitInfo(final CDOTransaction transaction, 
				final ServerTransactionCommitContext serverContext,
				final IStoreAccessor storeAccessor) {

			checkTransactionParameters(transaction, serverContext);
			initStoreThreadLocal(serverContext, storeAccessor);

			final CDOCommitInfo commitInfo = (null == serverContext.getRollbackMessage()) 
					? serverContext.createCommitInfo()
					: serverContext.createFailureCommitInfo();

			commitInfos.put(transaction, commitInfo);
		}

		private void postCommitTransactions() {

			for (final CDOTransaction transaction : transactions) {
				final InternalCDOCommitContext clientContext = clientContexts.get(transaction);
				final ServerTransactionCommitContext serverContext = serverContexts.get(transaction);
				final IStoreAccessor storeAccessor = storeAccessors.get(transaction);
				final CDOCommitInfo commitInfo = commitInfos.get(transaction);

				postCommitTransaction(transaction, clientContext, serverContext, storeAccessor, commitInfo);
			}
		}

		private void postCommitTransaction(final CDOTransaction transaction, 
				final InternalCDOCommitContext clientContext, 
				final ServerTransactionCommitContext serverContext,
				final IStoreAccessor storeAccessor, 
				final CDOCommitInfo commitInfo) {

			checkTransactionParameters(transaction, serverContext);
			initStoreThreadLocal(serverContext, storeAccessor);

			// XXX: Store accessors will be released from the server context at the end of this method, don't use them later
			serverContext.postCommit(null == serverContext.getRollbackMessage());

			final CommitTransactionResult transactionResult = createCommitTransactionResult(transaction, serverContext);
			updateTransactionResult(transaction, clientContext, transactionResult, commitInfo);
		}

		private CommitTransactionResult createCommitTransactionResult(final CDOTransaction transaction, 
				final ServerTransactionCommitContext serverContext) throws TransactionException {

			final CDOBranchPoint branchPoint = checkNotNull(serverContext.getBranchPoint(), "CDO branch point cannot be null.");
			final long previousTimeStamp = serverContext.getPreviousTimeStamp();
			final List<CDOIDReference> xRefIds = serverContext.getXRefs();
			final List<CDOObjectReference> xRefs = Lists.newArrayList();

			if (null != xRefIds) {
				for (final CDOIDReference idReference : xRefIds) {
					xRefs.add(new CDOObjectReferenceImpl(transaction, idReference));
				}
			}

			final CommitTransactionResult transactionResult = new CommitTransactionResult(
					(InternalCDOTransaction) transaction, 
					serverContext.getRollbackMessage(), 
					branchPoint, 
					previousTimeStamp, 
					xRefs);

			for (final Entry<CDOID, CDOID> idEntry : serverContext.getIDMappings().entrySet()) {
				final CDOID oldId = idEntry.getKey();
				final CDOID newId = idEntry.getValue();

				checkState(oldId instanceof CDOIDTemp, "Previous CDO ID was permanent. Old ID: " + oldId + " New ID: " + newId + ".");
				checkState(!(newId instanceof CDOIDTemp), "New CDO ID was temporary. Old ID: " + oldId + " New ID: " + newId + ".");

				transactionResult.addIDMapping(oldId, newId);
			}

			final List<LockState<Object, IView>> postCommitLockStates = serverContext.getPostCommmitLockStates();

			if (!CompareUtils.isEmpty(postCommitLockStates)) {
				final CDOLockState[] newLockStates = Repository.toCDOLockStates(postCommitLockStates);
				transactionResult.setNewLockStates(newLockStates);
			}

			return transactionResult;
		}

		private void updateTransactionResult(final CDOTransaction transaction, 
				final InternalCDOCommitContext clientContext, 
				final CommitTransactionResult result, 
				final CDOCommitInfo commitInfo) {

			try {

				final InternalCDOTransaction internalTransaction = (InternalCDOTransaction) transaction;
				final InternalCDOSession internalSession = (InternalCDOSession) transaction.getSession();

				// Invalidate original transaction's session if a rollback occurred
				if (null != result.getRollbackMessage()) {
					internalSession.invalidate(commitInfo, internalTransaction);
					final IListener[] listeners = transaction.getListeners();
					final Notifier transactionAsNotifier = (Notifier) transaction;
					final IEvent event = new TransactionFinishedEvent(internalTransaction, CDOTransactionFinishedEvent.Type.ROLLED_BACK, Collections.<CDOID,CDOID>emptyMap());
					transactionAsNotifier.fireEvent(event, listeners);
					internalTransaction.invalidate(commitInfo.getBranch(), commitInfo.getTimeStamp(), commitInfo.getChangedObjects(), commitInfo.getDetachedObjects(), null, false);
					return;
				}

				// Initialize wrapper as a workaround to modify non-API behavior 
				final InternalCDOTransactionWrapper wrapper = new InternalCDOTransactionWrapper(transaction);
				final CDOBranch branch = result.getBranch();

				final boolean branchChanged = !ObjectUtil.equals(branch, transaction.getBranch()); 
				if (branchChanged) {
					wrapper.basicSetBranchPoint(branch.getHead());
				}

				for (final CDOPackageUnit newPackageUnit : clientContext.getNewPackageUnits()) {
					((InternalCDOPackageUnit) newPackageUnit).setState(CDOPackageUnit.State.LOADED);
				}

				// Not all revision deltas will have a corresponding dirty object, so a difference needs to be computed
				final Set<CDOID> deltaIds = clientContext.getRevisionDeltas().keySet();
				final Set<CDOID> dirtyIds = clientContext.getDirtyObjects().keySet();
				final SetView<CDOID> deltaIdsOnly = Sets.difference(deltaIds, dirtyIds);

				postCommit(clientContext.getNewObjects(), result);
				postCommit(clientContext.getDirtyObjects(), result);
				postCommitRevisions(deltaIdsOnly, internalTransaction, result);
				adjustRevisionDeltas(clientContext, result);
				removeDetachedObjectsFromView(transaction, clientContext);

				internalSession.invalidate(commitInfo, internalTransaction);

				if (internalSession.isSticky()) {
					final CDOBranchPoint commitBranchPoint = CDOBranchUtil.copyBranchPoint(result);

					for (final CDOObject object : clientContext.getNewObjects().values()) {
						internalSession.setCommittedSinceLastRefresh(object.cdoID(), commitBranchPoint);
					}

					// XXX: Instead of dirty object IDs we refer the revision delta IDs, as dirty is a subset of the delta IDs
					for (final CDOID id : clientContext.getRevisionDeltas().keySet()) {
						internalSession.setCommittedSinceLastRefresh(id, commitBranchPoint);
					}

					for (final CDOID id : clientContext.getDetachedObjects().keySet()) {
						internalSession.setCommittedSinceLastRefresh(id, commitBranchPoint);
					}
				}

				final CDOTransactionHandler2[] handlers = transaction.getTransactionHandlers2();
				for (int i = 0; i < handlers.length; i++) {

					final CDOTransactionHandler2 handler = handlers[i];

					if (handler instanceof CDOTransactionHandler3) {
						final CDOTransactionHandler3 handler3 = (CDOTransactionHandler3) handler;
						handler3.committedTransaction(transaction, clientContext, commitInfo);
					} else {
						handler.committedTransaction(transaction, clientContext);
					}
				}

				wrapper.notifyChangeSubscribers(clientContext);
				wrapper.notifyAdapters(clientContext);
				wrapper.cleanUp(clientContext);

				final Map<CDOID, CDOID> idMappings = result.getIDMappings();
				final IListener[] listeners = transaction.getListeners();
				final Notifier transactionAsNotifier = (Notifier) transaction;

				if (branchChanged) {
					final IEvent event = new ViewTargetChangedEvent(internalTransaction, wrapper.getBranchPoint());
					transactionAsNotifier.fireEvent(event, listeners);
				}

				final IEvent event = new TransactionFinishedEvent(internalTransaction, CDOTransactionFinishedEvent.Type.COMMITTED, idMappings);
				transactionAsNotifier.fireEvent(event, listeners);

				if (null != result.getNewLockStates()) {
					wrapper.releaseLockStates(result);
				}

				/* 
				 * XXX: Explicitly invalidate embedded client transaction, otherwise all known/visited objects sitting in the view's soft reference cache will not be refreshed.
				 * This is required because the sender's view is never refreshed by CDO -- the changes are assumed to be present there in the first place.
				 */ 
				internalTransaction.invalidate(branch, commitInfo.getTimeStamp(), commitInfo.getChangedObjects(), commitInfo.getDetachedObjects(), null, false);

			} catch (final Throwable t) {
				Throwables.propagate(t);
			}
		}

		private void postCommit(final Map<CDOID, CDOObject> objects, final CommitTransactionResult result) {
			if (!objects.isEmpty()) {
				for (final CDOObject object : objects.values()) {
					CDOStateMachine.INSTANCE.commit((InternalCDOObject) object, result);
				}
			}
		}
		
		/**
		 * Adjusts changed revisions and updates ID mapping if required.
		 * <p>
		 * This method considers and ensures a workaround for cases where new objects are created and added to their
		 * container (CDO resource or CDO object) without making the container itself dirty.
		 */
		private void postCommitRevisions(final Set<CDOID> deltaIdsOnly, final InternalCDOTransaction internalTransaction, 
				final CommitTransactionResult result) {

			if (CompareUtils.isEmpty(deltaIdsOnly)) {
				return;
			}

			final CDOBranch branch = result.getBranch();
			final CDOBranchPoint branchPoint = branch.getPoint(result.getPreviousTimeStamp());
			final List<CDORevision> revisions = CDOUtils.getRevisions(branchPoint, deltaIdsOnly);

			for (final CDORevision revision : revisions) {
				final InternalCDORevision internalRevision  = (InternalCDORevision) revision;
				postCommitRevision(internalRevision, internalTransaction, result);
			}
		}

		private void postCommitRevision(final InternalCDORevision revision, final InternalCDOTransaction internalTransaction,
				final CommitTransactionResult result) {

			revision.adjustForCommit(internalTransaction.getBranch(), result.getTimeStamp());
			revision.adjustReferences(result.getReferenceAdjuster());
			revision.freeze();

			final InternalCDORevisionManager revisionManager = internalTransaction.getSession().getRevisionManager();
			revisionManager.addRevision(revision);
		}

		private void adjustRevisionDeltas(final InternalCDOCommitContext clientContext, final CommitTransactionResult result) {

			for (final CDORevisionDelta delta : clientContext.getRevisionDeltas().values()) {
				((InternalCDORevisionDelta) delta).adjustReferences(result.getReferenceAdjuster());
			}
		}

		private void removeDetachedObjectsFromView(final CDOTransaction transaction, final InternalCDOCommitContext clientContext) {

			for (final CDOID id : clientContext.getDetachedObjects().keySet()) {
				((org.eclipse.emf.internal.cdo.view.AbstractCDOView) transaction).removeObject(id);
			}
		}

		private void checkTransactionParameters(final CDOTransaction transaction, final ServerTransactionCommitContext serverContext) {
			CDOUtils.check(transaction);
			checkNotNull(serverContext, "Commit context argument cannot be null.");
		}

		private void initStoreThreadLocal(final ServerTransactionCommitContext serverContext, final IStoreAccessor storeAccessor) {
			StoreThreadLocal.setAccessor(storeAccessor);
			StoreThreadLocal.setCommitContext(serverContext);
		}

		private void rollbackTransactions(final Throwable t) {
			logError(t);
			final String message = Strings.nullToEmpty(t.getMessage());

			for (final CDOTransaction transaction : transactions) {
				final ServerTransactionCommitContext serverContext = serverContexts.get(transaction);
				final IStoreAccessor storeAccessor = storeAccessors.get(transaction);
				rollbackTransaction(serverContext, storeAccessor, message);
			}
		}

		private void rollbackTransaction(final ServerTransactionCommitContext serverContext, final IStoreAccessor storeAccessor, final String message) {

			try {
				initStoreThreadLocal(serverContext, storeAccessor);
				serverContext.rollback(message);
			} catch (final Throwable e) {
				LOGGER.error("Cannot rollback changes after failed commit.", e);
			} finally {
				final InternalTransaction transaction = serverContext.getTransaction();
				final InternalLockManager lockingManager = transaction.getRepository().getLockingManager();
				final List<LockState<Object, IView>> lockStates = lockingManager.unlock2(transaction);
				sendLockNotification(lockStates, lockingManager, transaction, Operation.UNLOCK);
			}
		}

		private void logError(final Throwable t) {
			final RepositoryLockException repositoryLockException = extractCause(t, getClass().getClassLoader(), RepositoryLockException.class);

			if (null != repositoryLockException) {
				LOGGER.info(repositoryLockException.getMessage());
				return;
			}

			LOGGER.info("Performing rollback in backend due to failed commit.");
		}

		private void deactivateImpersonatingSessions() {

			for (final CDOTransaction transaction : transactions) {
				final InternalSession impersonatingSession = impersonatingSessions.get(transaction);
				deactivateImpersonatingSession(impersonatingSession);
			}
		}

		private void deactivateImpersonatingSession(final InternalSession impersonatingSession) {

			if (null != impersonatingSession) {
				LifecycleUtil.deactivate(impersonatingSession);
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CDOServerCommitBuilder.class);

	/** The number of characters in a UUID (32 hex digits and 4 dashes) */
	private static final int UUID_LENGTH = 36;

	private final ICDOTransactionAggregator transactionAggregator;
	private final String userId; 
	private @Nullable final String comment;

	private boolean notifyWriteAccessHandlers = true;
	private boolean sendCommitNotification = true;
	private String parentContextDescription = DatastoreLockContextDescriptions.ROOT;

	/**
	 * Creates a server-side commit builder with the specified arguments.
	 * 
	 * @param userId the unique ID of the user (may not be {@code null}).
	 * @param comment comment for the commit.
	 * @param transactions an {@link Iterable} holding individual {@link CDOTransaction}s to commit (may not be {@code null} or empty).
	 */
	public CDOServerCommitBuilder(final String userId, final @Nullable String comment, final Iterable<CDOTransaction> transactions) {
		this(userId, comment, CDOTransactionAggregator.create(transactions));
	}

	/**
	 * Creates a server-side commit builder with the specified arguments.
	 * 
	 * @param userId the unique ID of the user (may not be {@code null}).
	 * @param comment comment for the commit.
	 * @param firstTransaction the first {@link CDOTransaction} to commit (may not be {@code null}).
	 * @param restTransactions additional {@link CDOTransaction}s to commit (optional).
	 */
	public CDOServerCommitBuilder(final String userId, final @Nullable String comment, final CDOTransaction firstTransaction, final CDOTransaction... restTransactions) {
		this(userId, comment, CDOTransactionAggregator.create(firstTransaction, restTransactions));
	}

	/**
	 * Creates a server-side commit builder with the specified arguments.
	 * 
	 * @param userId the unique ID of the user (may not be {@code null}).
	 * @param comment comment for the commit.
	 * @param transactionAggregator an aggregator encapsulating an arbitrary number of CDO transactions (may not be {@code null} or empty).
	 */
	public CDOServerCommitBuilder(final String userId, final @Nullable String comment, final ICDOTransactionAggregator transactionAggregator) {
		Preconditions.checkNotNull(userId, "User identifier argument cannot be null.");
		CDOTransactionAggregatorUtils.check(transactionAggregator);

		this.transactionAggregator = transactionAggregator;
		this.userId = userId;
		final String truncatedMessage = StringUtils.truncate(comment, 255 - UUID_LENGTH);
		if (!Objects.equals(truncatedMessage, comment)) {
			LOGGER.warn("Truncated commit message (original message: {})", comment);
		}
		this.comment = truncatedMessage;
	}

	public CDOServerCommitBuilder notifyWriteAccessHandlers(final boolean value) {
		notifyWriteAccessHandlers = value;
		return this;
	}

	public CDOServerCommitBuilder sendCommitNotification(final boolean value) {
		sendCommitNotification = value;
		return this;
	}

	public CDOServerCommitBuilder parentContextDescription(final String value) {
		parentContextDescription = value;
		return this;
	}

	public CDOCommitInfo commitOne() throws CommitException {
		return Iterables.getOnlyElement(commit());
	}

	public CDOCommitInfo commitOne(@Nullable final IProgressMonitor monitor) throws CommitException {
		return Iterables.getOnlyElement(commit(monitor));
	}

	public Iterable<CDOCommitInfo> commit() throws CommitException {
		return commit(null);
	}

	public Iterable<CDOCommitInfo> commit(@Nullable final IProgressMonitor monitor) throws CommitException {
		final CDOServerCommit serverCommit = new CDOServerCommit();
		return serverCommit.commit(nullToNullMonitor(monitor));
	}
}