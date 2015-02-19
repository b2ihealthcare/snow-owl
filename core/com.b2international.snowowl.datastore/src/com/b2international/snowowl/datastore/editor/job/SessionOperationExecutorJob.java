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
package com.b2international.snowowl.datastore.editor.job;

import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.editor.operation.AbstractOperation;
import com.b2international.snowowl.datastore.editor.service.IEditorService;

/**
 * @since 2.9
 */
public final class SessionOperationExecutorJob extends Job {

	private final AbstractOperation operation;
	private final UUID sessionId;
	private final IEditorService editorService;

	public SessionOperationExecutorJob(String name, UUID sessionId, AbstractOperation operation, IEditorService editorService) {
		super(name);
		this.sessionId = sessionId;
		this.operation = operation;
		this.editorService = editorService;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			editorService.executeSessionOperation(sessionId, operation);
		} catch (Exception e) {
			return new Status(IStatus.ERROR, DatastoreActivator.PLUGIN_ID, "Error in session operation executor job.", e);
		}
		return Status.OK_STATUS;
	}
	
}