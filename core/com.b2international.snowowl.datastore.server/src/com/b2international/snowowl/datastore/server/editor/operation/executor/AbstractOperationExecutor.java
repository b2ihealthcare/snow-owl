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
package com.b2international.snowowl.datastore.server.editor.operation.executor;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.datastore.editor.operation.AbstractOperation;
import com.b2international.snowowl.datastore.editor.operation.status.IOperationExecutionStatus;
import com.b2international.snowowl.datastore.editor.operation.status.OperationExecutionErrorStatus;
import com.b2international.snowowl.datastore.editor.operation.status.OperationExecutionMultiStatus;
import com.b2international.snowowl.datastore.editor.operation.status.OperationExecutionStatus;
import com.b2international.snowowl.datastore.editor.operation.status.OperationExecutionWarningStatus;
import com.google.common.base.Preconditions;

/**
 * @since 2.8
 */
public abstract class AbstractOperationExecutor<O extends AbstractOperation> {

	private final List<IOperationExecutionStatus> statusChildren = newArrayList();

	private final Class<O> operationType;
	private final Set<Long> deletedObjectStorageKeys;

	public AbstractOperationExecutor(Class<O> operationType, Set<Long> deletedObjectStorageKeys) {
		Preconditions.checkNotNull(operationType, "Operation type must not be null");
		Preconditions.checkNotNull(deletedObjectStorageKeys, "Set of deleted object keys must not be null");
		this.operationType = operationType;
		this.deletedObjectStorageKeys = deletedObjectStorageKeys;
	}

	protected Set<Long> getDeletedObjectStorageKeys() {
		return deletedObjectStorageKeys;
	}

	protected void addWarningStatus(String message) {
		statusChildren.add(new OperationExecutionWarningStatus(getOperationTypeName(), message));
	}

	protected void addErrorStatus(Exception exception) {
		statusChildren.add(new OperationExecutionErrorStatus(getOperationTypeName(), exception.getMessage(), exception));
	}

	protected IOperationExecutionStatus getStatus() {
		if (statusChildren.isEmpty()) {
			return createDefaultExecutionStatus("OK");
		} else if (statusChildren.size() == 1) {
			return getSingleExecutionStatus();
		} else {
			return createMultiExecutionStatus("Multi status");
		}
	}

	protected IOperationExecutionStatus createDefaultExecutionStatus(String message) {
		return new OperationExecutionStatus(getOperationTypeName(), "<unspecified plugin>", IStatus.OK, message);
	}

	private IOperationExecutionStatus getSingleExecutionStatus() {
		return statusChildren.get(0);
	}

	private IOperationExecutionStatus createMultiExecutionStatus(String message) {
		OperationExecutionMultiStatus multiStatus = new OperationExecutionMultiStatus(getOperationTypeName(), IStatus.OK, message);
		for (IOperationExecutionStatus status : statusChildren) {
			multiStatus.add(status);
		}
		return multiStatus;
	}

	protected String getOperationTypeName() {
		return operationType.getName();
	}

	public abstract IOperationExecutionStatus execute(O operation);

}