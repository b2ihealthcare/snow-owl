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
package com.b2international.snowowl.datastore.server.editor.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;
import com.b2international.snowowl.datastore.editor.notification.BroadcastNotificationMessage;
import com.b2international.snowowl.datastore.editor.notification.NotificationMessage;
import com.b2international.snowowl.datastore.editor.notification.RefreshEditorNotificationMessage;
import com.b2international.snowowl.datastore.editor.operation.AbstractOperation;
import com.b2international.snowowl.datastore.editor.operation.status.IOperationExecutionStatus;
import com.b2international.snowowl.datastore.editor.service.EditorSessionInitializationException;
import com.b2international.snowowl.datastore.editor.service.IEditorService;
import com.b2international.snowowl.datastore.net4j.push.PushConstants;
import com.b2international.snowowl.datastore.server.EditingContextFactory;
import com.b2international.snowowl.datastore.server.editor.operation.executor.AbstractOperationExecutor;
import com.b2international.snowowl.datastore.server.editor.operation.executor.OperationExecutorFactory;
import com.b2international.snowowl.datastore.server.editor.session.EditorSession;
import com.b2international.snowowl.datastore.server.editor.session.EditorSessionLifecycleManager;
import com.b2international.snowowl.datastore.server.net4j.push.PushServerService;
import com.b2international.snowowl.datastore.validation.SessionValidationResults;

/**
 * @since 2.9
 */
public class EditorServerService implements IEditorService, IDisposableService {

	private final EditorSessionLifecycleManager lifecycleManager;
	private final EditingContextFactory contextFactory;
	private final OperationExecutorFactory operationExecutorFactory;

	public EditorServerService(EditorSessionLifecycleManager lifecycleManager, EditingContextFactory contextFactory, OperationExecutorFactory executorFactory) {
		this.lifecycleManager = lifecycleManager;
		this.contextFactory = contextFactory;
		this.operationExecutorFactory = executorFactory;
	}

	public UUID openSession(String userId, String id, IBranchPathMap branchPathMap) throws EditorSessionInitializationException {
		UUID sessionId;
		try {
			sessionId = lifecycleManager.openSession(userId, id, branchPathMap, contextFactory, operationExecutorFactory);
		} catch (EditorSessionInitializationException e) {
			throw e;
		} catch (Exception e) {
			throw new EditorSessionInitializationException(e);
		}
		return sessionId;
	}
	
	@Override
	public IOperationExecutionStatus executeOperation(AbstractOperation operation, String userId) {
		AbstractOperationExecutor<AbstractOperation> executor = operationExecutorFactory.createGlobalOperationExecutor(operation, userId);
		IOperationExecutionStatus status = executor.execute(operation);
		pushNotification(PushConstants.BROADCAST_NOTIFICATION_TOPIC, new BroadcastNotificationMessage(status, "Broadcast operation execution status"));
		return status;
	}

	@Override
	public IOperationExecutionStatus executeSessionOperation(UUID sessionId, AbstractOperation operation) {
		return getSessionChecked(sessionId).executeSessionOperation(operation);
	}

	protected EditorSession<?> getSessionChecked(UUID sessionId) {
		checkNotNull(sessionId, "sessionId");
		EditorSession<?> session = lifecycleManager.getSession(sessionId);
		if (session == null) {
			throw new IllegalArgumentException("Editor session not found for ID: " + sessionId);
		}
		return session;
	}

	@Override
	public boolean isDirty(UUID sessionId) {
		return getSessionChecked(sessionId).isDirty();
	}

	@Override
	public void commit(UUID sessionId, String message, IProgressMonitor monitor) throws SnowowlServiceException {
		getSessionChecked(sessionId).commit(message, monitor);
		pushNotification(sessionId, new RefreshEditorNotificationMessage(null, "Committed changes."));
	}

	protected <D extends Serializable> void pushNotification(UUID sessionId, NotificationMessage<D> notification) {
		try {
			PushServerService.INSTANCE.push(sessionId, notification);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void validate(UUID sessionId) {
		// do nothing by default
		// TODO: refactor to be part of 'validation aware editor service'
	}

	@Override
	public void validateSync(UUID sessionId) {
		// do nothing by default
		// TODO: refactor to be part of 'validation aware editor service'
	}
	
	@Override
	public SessionValidationResults getValidationResults(UUID sessionId) {
		return getSessionChecked(sessionId).getValidationResults();
	}

	public void closeSession(UUID sessionId) {
		lifecycleManager.closeSession(sessionId);
	}

	@Override
	public void dispose() {
		lifecycleManager.dispose();
	}

	public boolean isDisposed() {
		return lifecycleManager.isDisposed();
	}

	@Override
	public IdentifiedBean getEditedComponentBean(UUID sessionId) {
		return getSessionChecked(sessionId).getEditedComponentBean();
	}

}