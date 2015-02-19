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

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;
import com.b2international.snowowl.datastore.editor.notification.NotificationMessage;
import com.b2international.snowowl.datastore.editor.operation.AbstractOperation;
import com.b2international.snowowl.datastore.editor.operation.status.IOperationExecutionStatus;
import com.b2international.snowowl.datastore.validation.SessionValidationResults;

/**
 * @since 2.9
 */
public interface IEditorSession {
	
	@OverridingMethodsMustInvokeSuper
	public void dispose();

	public boolean isDirty();

	public IOperationExecutionStatus executeSessionOperation(final AbstractOperation operation);

	public void commit(String message, IProgressMonitor monitor) throws SnowowlServiceException;

	public <D extends Serializable> void pushNotification(UUID sessionId, NotificationMessage<D> notification);

	public void loadComponents(CDOEditingContext editingContext);

	public IdentifiedBean getEditedComponentBean();

	public void applySessionOperations() throws SnowowlServiceException;

	public UUID getUuid();

	public String getUserId();

	public IBranchPath getBranchPath();

	public Set<Long> getDeletedObjectStorageKeys();
	
	public SessionValidationResults getValidationResults();

	public abstract void removeValidationResults(Set<String> removedBeanIds);
	
	/**
	 * Returns with the branch path map representing the owner user's current, task aware,
	 * branch configuration.
	 * @return the branch path map.
	 */
	IBranchPathMap getBranchPathMap();

}