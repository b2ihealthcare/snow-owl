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

import org.eclipse.core.runtime.IStatus;

import com.b2international.commons.StringUtils;
import com.google.common.base.Preconditions;

/**
 * @since 2.9
 */
public class OperationExecutionStatus implements IOperationExecutionStatus {

	private static final long serialVersionUID = -1772989317174147629L;
	
	/**
	 * The severity. One of
	 * <ul>
	 * <li><code>CANCEL</code></li>
	 * <li><code>ERROR</code></li>
	 * <li><code>WARNING</code></li>
	 * <li><code>INFO</code></li>
	 * <li>or <code>OK</code> (0)</li>
	 * </ul>
	 */
	private int severity = OK;
	private int code;
	private String message;
	private String plugin;
	private Throwable exception;
	private String operationTypeName;
	
	public OperationExecutionStatus(String operationTypeName, String plugin, int severity, String message) {
		this(operationTypeName, plugin, severity, message, null);
	}

	public OperationExecutionStatus(String operationTypeName, String plugin, int severity, String message, Throwable exception) {
		Preconditions.checkArgument(!StringUtils.isEmpty(operationTypeName), "Operation type name must not be empty");
		Preconditions.checkArgument(!StringUtils.isEmpty(plugin), "Plugin ID must not be empty");
		Preconditions.checkArgument(!StringUtils.isEmpty(message), "Message must not be empty");
		this.operationTypeName = operationTypeName;
		this.plugin = plugin;
		this.message = message;
		this.exception = exception;
		this.severity = severity;
	}

	@Override
	public IStatus[] getChildren() {
		return new IStatus[0];
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public Throwable getException() {
		return exception;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getPlugin() {
		return plugin;
	}

	@Override
	public int getSeverity() {
		return severity;
	}

	@Override
	public boolean isMultiStatus() {
		return false;
	}
	
	@Override
	public boolean isOK() {
		return severity == OK;
	}

	@Override
	public boolean matches(int severityMask) {
		return (severity & severityMask) != 0;
	}
	
	@Override
	public String getOperationTypeName() {
		return operationTypeName;
	}
	
	protected void setSeverity(int severity) {
		Preconditions.checkArgument(severity == OK || severity == ERROR || severity == WARNING || severity == INFO || severity == CANCEL);
		this.severity = severity;
	}

}