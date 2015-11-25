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
package com.b2international.snowowl.datastore.server.editor.session;

import static com.b2international.commons.exceptions.Exceptions.extractCause;

import java.io.Serializable;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.IPostStoreUpdateListener;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.IPostStoreUpdateManager;
import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;
import com.b2international.snowowl.datastore.editor.job.EditorSessionCommitInfoProcessingJob;
import com.b2international.snowowl.datastore.editor.notification.NotificationMessage;
import com.b2international.snowowl.datastore.editor.notification.OperationExecutionStatusNotificationMessage;
import com.b2international.snowowl.datastore.editor.operation.AbstractOperation;
import com.b2international.snowowl.datastore.editor.operation.status.IOperationExecutionStatus;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.EditingContextFactory;
import com.b2international.snowowl.datastore.server.editor.job.EditorSessionCommitInfoProcessingJobChangeAdapter;
import com.b2international.snowowl.datastore.server.editor.job.JobInitializationException;
import com.b2international.snowowl.datastore.server.editor.operation.executor.AbstractOperationExecutor;
import com.b2international.snowowl.datastore.server.editor.operation.executor.IPostOperationExecutionStartegy;
import com.b2international.snowowl.datastore.server.editor.operation.executor.OperationExecutorFactory;
import com.b2international.snowowl.datastore.server.net4j.push.PushServerService;
import com.b2international.snowowl.datastore.validation.SessionValidationResults;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Generic editor session.
 * 
 * @since 2.9
 */
public abstract class EditorSession<C extends EObject> implements IEditorSession, IPostStoreUpdateListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(EditorSession.class);
	
	protected final UUID uuid;
	protected final String userId;
	protected final IBranchPathMap branchPathMap;
	protected final EditingContextFactory editingContextFactory;
	protected final OperationExecutorFactory executorFactory;
	protected final IPostOperationExecutionStartegy postOperationExecutionStartegy;
	protected final Stack<AbstractOperation> operationStack;
	protected final EditorSessionValidationState validationState;
	
	private final IPostStoreUpdateManager postStoreUpdateManager;
	
	// synchronized set to handle concurrent commit info processing jobs to modify
	private final Set<Long> deletedObjectStorageKeys = Collections.synchronizedSet(Sets.<Long>newHashSet());
	
	// don't use this editing context for anything other than the storeUpdated(...) method
	private final AtomicReference<CDOEditingContext> currentlyCommittingEditingContext = new AtomicReference<CDOEditingContext>();

	public EditorSession(
			final String userId, 
			final IBranchPathMap branchPathMap, 
			final UUID uuid, 
			final EditingContextFactory editingContextFactory, 
			final OperationExecutorFactory executorFactory) {
		
		this.userId = userId;
		this.branchPathMap = branchPathMap;
		this.uuid = uuid;
		this.executorFactory = executorFactory;
		this.editingContextFactory = editingContextFactory;
		operationStack = new Stack<AbstractOperation>();
		validationState = new EditorSessionValidationState();
		postStoreUpdateManager = ApplicationContext.getInstance().getServiceChecked(IPostStoreUpdateManager.class);
		postOperationExecutionStartegy = createPostOperationExecutionStartegy();
	}
	
	@OverridingMethodsMustInvokeSuper
	public void init() {
		CDOEditingContext editingContext = editingContextFactory.createEditingContext(getBranchPath());
		loadComponents(editingContext);
		editingContext.close();
		postStoreUpdateManager.addPostStoreUpdateListener(this, false);
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	public void dispose() {
		LOG.info("["+ userId + ", " + uuid + "] dispose()");
		postStoreUpdateManager.removePostStoreUpdateListener(this);
	}

	@Override
	public boolean isDirty() {
		return !operationStack.isEmpty();
	}
	
	protected abstract Class<C> getEditedComponentType();
	
	@Override
	public void storeUpdated(final CDOCommitInfo commitInfo) {
		LOG.info("["+ userId + ", " + uuid + "] storeUpdated(" + commitInfo + ")");

		if (isEditedComponentDeleted(commitInfo)) {
			pushDeletedComponentNotificationMessage(commitInfo);
			return;
		}
		
		if (commitInfo instanceof CDOSessionInvalidationEvent) {
			final CDOSessionInvalidationEvent invalidationEvent = (CDOSessionInvalidationEvent) commitInfo;
			
			// simply ignore changes made on other branched
			final IBranchPath commitInfoBranchPath = BranchPathUtils.createPath(commitInfo.getBranch());
			if (!commitInfoBranchPath.equals(getBranchPath())) {
				return;
			}
			
			final CDOEditingContext localEditingContext = currentlyCommittingEditingContext.get();
			if (localEditingContext != null && invalidationEvent.getLocalTransaction().equals(localEditingContext.getTransaction())) {
				return;
			}
		}
		
		processCommitInfo(commitInfo);
	}

	private void pushDeletedComponentNotificationMessage(final CDOCommitInfo commitInfo) {
		final Multimap<String, String> deletedCompnentTypeNameToIdMap = HashMultimap.<String, String>create();
		deletedCompnentTypeNameToIdMap.put(getEditedComponentType().getName(), getEditedComponentBean().getId());
		pushNotification(uuid, getDeletedComponentNotificationMessage(deletedCompnentTypeNameToIdMap, commitInfo.getUserID()));
	}

	protected abstract OperationExecutionStatusNotificationMessage getDeletedComponentNotificationMessage(Multimap<String, String> deletedCompnentTypeNameToIdMap, String userId);

	private boolean isEditedComponentDeleted(final CDOCommitInfo commitInfo) {
		if (commitInfo != null) {
			if (getBranchPath().equals(BranchPathUtils.createPath(commitInfo.getBranch()))) {
				final Set<Long> storageKeys = EditorSessionCommitInfoProcessingJob.collectDeletedObjectStorageKeys(commitInfo);
				IdentifiedBean editedComponentBean = getEditedComponentBean();
				if (storageKeys.contains(CDOIDUtils.asLong(editedComponentBean.getStorageKey()))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public synchronized IOperationExecutionStatus executeSessionOperation(final AbstractOperation operation) {
		final AbstractOperationExecutor<AbstractOperation> executor = executorFactory.createSessionOperationExecutor(operation, this);
		final IOperationExecutionStatus status = executor.execute(operation);
		operationStack.push(operation);
		postOperationExecutionStartegy.operationExecuted(operation, status);
		pushNotification(uuid, new OperationExecutionStatusNotificationMessage(status, "Operation execution status"));
		return status;
	}

	@Override
	public synchronized void commit(final String commitMessage, final IProgressMonitor monitor) throws SnowowlServiceException {
		LOG.info("["+ userId + ", " + uuid + "] commit(" + commitMessage + ")");
		currentlyCommittingEditingContext.set(editingContextFactory.createEditingContext(getBranchPath()));
		try {
			for (int retry = CDOEditingContext.COMMIT_STRIKES; retry > 0; retry--) {
				try {
					applyCommitOperations(currentlyCommittingEditingContext.get());
				} catch (final SnowowlServiceException e) {
					currentlyCommittingEditingContext.get().close();
					currentlyCommittingEditingContext.set(null);
					throw e;
				}
				
				try {
					CDOServerUtils.commit(currentlyCommittingEditingContext.get().getTransaction(), userId, commitMessage, monitor).getTimeStamp();
					operationStack.clear();
					deletedObjectStorageKeys.clear();
					loadComponents(currentlyCommittingEditingContext.get());
					return;
				} catch (final CommitException e) {
					
					final RepositoryLockException cause = extractCause(e, this.getClass().getClassLoader(), RepositoryLockException.class);
					
					if (null != cause) {
						
						final CDOTransaction transaction = currentlyCommittingEditingContext.get().getTransaction();
						final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(transaction);
						final String name = connection.getRepositoryName();
						
						final String message = StringUtils.isEmpty(cause.getMessage()) 
								? (name + " is currently locked on '" + BranchPathUtils.createPath(transaction) + "' branch. All kind of modifications are temporary disabled.")
								: cause.getMessage();
						
						final SnowowlServiceException sse = new SnowowlServiceException(message, cause);
						
						throw sse;
						
					}
					
					// sleep for a random time between 100 - 1500 millisecond before the next commit attempt
					final int sleepTime = new Random(UUID.randomUUID().hashCode()).nextInt(15) + 1;
					try {
						Thread.sleep(sleepTime * 100L);
					} catch (final InterruptedException e1) {
						LOG.error(e1.getMessage());
					}
					currentlyCommittingEditingContext.get().close();
					currentlyCommittingEditingContext.set(editingContextFactory.createEditingContext(getBranchPath()));
				}
			}
			throw new SnowowlServiceException("Commit failed after " + CDOEditingContext.COMMIT_STRIKES + " attempts.");
		} finally {
			if (currentlyCommittingEditingContext.get() != null) {
				currentlyCommittingEditingContext.get().close();
				currentlyCommittingEditingContext.set(null);
			}
		}
	}

	@Override
	public <D extends Serializable> void pushNotification(final UUID sessionId, final NotificationMessage<D> notification) {
		try {
			PushServerService.INSTANCE.push(sessionId, notification);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public abstract void loadComponents(CDOEditingContext editingContext);

	@Override
	public abstract IdentifiedBean getEditedComponentBean();
	
	protected abstract EditorSessionCommitInfoProcessingJob createCommitInfoProcessingJob(CDOCommitInfo commitInfo) throws JobInitializationException;
	
	protected abstract ILookupService<String, C, CDOView> createLookupService();
	
	protected abstract IPostOperationExecutionStartegy createPostOperationExecutionStartegy();
	
	protected Optional<C> lookupEditedComponent(final CDOEditingContext editingContext) {
		return Optional.fromNullable(createLookupService().getComponent(getEditedComponentBean().getId(), editingContext.getTransaction()));
	}
	
	private void processCommitInfo(final CDOCommitInfo commitInfo) {
		try {
			final EditorSessionCommitInfoProcessingJob job = createCommitInfoProcessingJob(commitInfo);
			// configure job priorities and schedule as soon as possible
			job.setPriority(Job.INTERACTIVE);
			job.setSystem(true);
			job.addJobChangeListener(new EditorSessionCommitInfoProcessingJobChangeAdapter(this, job.getEditingContext()));
			LOG.info("["+ userId + ", " + uuid + "] processCommitInfo(" + commitInfo + ")");
			job.schedule();
		} catch (final JobInitializationException e) {
			LOG.warn("["+ userId + ", " + uuid + "] Could not initialize processing job for commit info: " + commitInfo, e.getMessage());
			pushDeletedComponentNotificationMessage(commitInfo);
		}
	}
	
	private void applyCommitOperations(final CDOEditingContext editingContext) throws SnowowlServiceException {
		
		synchronized (operationStack) {
			for (final AbstractOperation operation : operationStack) {
				final AbstractOperationExecutor<AbstractOperation> executor = executorFactory.createCommitOperationExecutor(operation, editingContext, getEditedComponentBean(), deletedObjectStorageKeys);
				final IOperationExecutionStatus status = executor.execute(operation);
				handleOperationExecutionError(status);
			}
		}
	}
	
	@Override
	public void applySessionOperations() throws SnowowlServiceException {
		
		synchronized (operationStack) {
			for (final AbstractOperation operation : operationStack) {
				final AbstractOperationExecutor<AbstractOperation> executor = executorFactory.createSessionOperationExecutor(operation, this);
				final IOperationExecutionStatus status = executor.execute(operation);
				handleOperationExecutionError(status);
			}
		}
	}
	
	private void handleOperationExecutionError(final IOperationExecutionStatus status) throws SnowowlServiceException {
		if (status.getSeverity() == IStatus.ERROR || status.getSeverity() == IStatus.CANCEL) {
			// TODO: maybe include human readable details about the failed operation
			throw new SnowowlServiceException(status.getMessage());
		}		
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}
	
	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public Set<Long> getDeletedObjectStorageKeys() {
		return deletedObjectStorageKeys;
	}

	@Override
	public SessionValidationResults getValidationResults() {
		return validationState.getValidationResults();
	}

	@Override
	public void removeValidationResults(Set<String> removedBeanIds) {		
		validationState.removeValidationResults(removedBeanIds);
	}
	
	@Override
	public IBranchPathMap getBranchPathMap() {
		return branchPathMap;
	}
	
	public EditingContextFactory getEditingContextFactory() {
		return editingContextFactory;
	}
	
}