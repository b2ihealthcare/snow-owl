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
package com.b2international.snowowl.datastore.editor.service;

import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;
import com.b2international.snowowl.datastore.editor.notification.EditorSessionValidationCompleteNotificationMessage;
import com.b2international.snowowl.datastore.editor.operation.AbstractOperation;
import com.b2international.snowowl.datastore.editor.operation.status.IOperationExecutionStatus;
import com.b2international.snowowl.datastore.validation.SessionValidationResults;

/**
 * @since 2.9
 */
public interface IEditorService {
	
	/**
	 * Creates a new editor session instance and returns its unique identifier.
	 * 
	 * @param userId
	 * @param editedComponentId
	 * @param branchPathMap
	 * @return the unique identifier of the editor session
	 * @throws EditorSessionInitializationException
	 */
	UUID openSession(String userId, String editedComponentId, IBranchPathMap branchPathMap) throws EditorSessionInitializationException;
	
	/**
	 * Returns the edited component from the editor session identified by the
	 * given UUID.
	 * 
	 * @param sessionId
	 * @return the edited component represented as an {@link IdentifiedBean
	 *         identified bean}
	 */
	IdentifiedBean getEditedComponentBean(UUID sessionId);

	/**
	 * Performs an editor session independent operation.
	 * 
	 * @param operation the session independent operation to perform
	 * @param userId the user ID to impersonate
	 */
	IOperationExecutionStatus executeOperation(AbstractOperation operation, String userId);

	/**
	 * Performs an operation in the session identified by the specified unique ID.
	 * 
	 * @param editorSessionId
	 * @param operation
	 */
	IOperationExecutionStatus executeSessionOperation(UUID editorSessionId, AbstractOperation operation);

	/**
	 * Returns true if the editor session identified by the given UUID has any unsaved changes or
	 * false otherwise.
	 * 
	 * @param sessionId
	 * @return
	 */
	boolean isDirty(UUID sessionId);

	/**
	 * Saves changes in the editor session identified by the specified unique ID.
	 * 
	 * @param sessionId
	 * @param message
	 * @param monitor
	 * @throws SnowowlServiceException 
	 */
	void commit(UUID sessionId, String message, IProgressMonitor monitor) throws SnowowlServiceException;
	
	/**
	 * Validates the contents of the specified session asynchronously and sends
	 * an {@link EditorSessionValidationCompleteNotificationMessage} when the
	 * validation is complete. 
	 * The latest validation results can be retrieved by
	 * calling {@link #getValidationResults(UUID)}.
	 * 
	 * @param sessionId the session ID
	 */
	void validate(UUID sessionId);

	/**
	 * Validates the contents of the specified session, i.e. waits for the
	 * validation to complete before returning. Does not send a notification
	 * message. 
	 * The latest validation results can be retrieved by calling
	 * {@link #getValidationResults(UUID)}.
	 * 
	 * @param sessionId the session ID
	 */
	void validateSync(UUID sessionId);
	
	/**
	 * Returns the validation results for the specified session.
	 * 
	 * @param sessionId the session ID
	 * @return the validation results for the specified session
	 */
	SessionValidationResults getValidationResults(UUID sessionId);

	/**
	 * Closes the editor session instance with the specified unique identifier.
	 * 
	 * @param sessionId
	 * @throws SnowowlServiceException 
	 */
	void closeSession(UUID sessionId);
	
}