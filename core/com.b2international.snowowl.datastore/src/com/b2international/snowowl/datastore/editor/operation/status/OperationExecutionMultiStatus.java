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
package com.b2international.snowowl.datastore.editor.operation.status;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

/**
 * @since 2.9
 */
public class OperationExecutionMultiStatus extends OperationExecutionStatus {

	private static final long serialVersionUID = 7268704973680787159L;
	
	private List<IOperationExecutionStatus> children = newArrayList();
	
	public OperationExecutionMultiStatus(String operationType, int severity, String message) {
		super(operationType, "<unspecified plugin>", severity, message, null);
	}
	
	public OperationExecutionMultiStatus(String operationType, int severity, String message, Throwable exception) {
		super(operationType, "<unspecified plugin>", severity, message, exception);
	}

	@Override
	public IStatus[] getChildren() {
		return children.toArray(new IStatus[children.size()]);
	}

	@Override
	public boolean isMultiStatus() {
		return true;
	}

	public void add(IOperationExecutionStatus status) {
		if (children.add(status)) {
			int newSev = status.getSeverity();
			if (newSev > getSeverity()) {
				setSeverity(newSev);
			}
		}
	}
}