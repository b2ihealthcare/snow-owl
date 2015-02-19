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
package com.b2international.snowowl.api.rest.task;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.security.Principal;

import org.springframework.http.ResponseEntity;

import com.b2international.snowowl.api.impl.task.domain.TaskInput;
import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.domain.CollectionResource;
import com.b2international.snowowl.api.rest.util.Responses;
import com.b2international.snowowl.api.task.ITaskService;
import com.b2international.snowowl.api.task.domain.ITask;

/**
 * @since 1.0
 */
public abstract class AbstractTaskRestService extends AbstractRestService {

	protected ITaskService delegate;

	protected CollectionResource<ITask> getTasks(final String shortName, final String version, final Boolean includePromoted) {
		return CollectionResource.of(delegate.getAllTasks(shortName, version, includePromoted));
	}

	protected ResponseEntity<Void> createTask(final String shortName, final String version, final TaskInput taskInput, final Principal principal) {
		final String taskId = taskInput.getTaskId();
		final ITask newBranchInfo = delegate.createTask(shortName, version, taskId, taskInput, principal.getName());
		checkNotNull(newBranchInfo.getTaskId(), "Task ID must be set on branch info");
		return Responses.created(createTaskLocationUri(shortName, version, newBranchInfo.getTaskId())).build();
	}

	/**
	 * @param shortName
	 * @param version
	 * @param taskId
	 * @return
	 */
	protected abstract URI createTaskLocationUri(String shortName, String version, String taskId);

	protected ITask getTaskByName(final String shortName, final String version, final String taskId) {
		return delegate.getTaskByName(shortName, version, taskId);
	}

	protected final void synchronizeTask(final String shortName, final String version, final String taskId, final Principal principal) {
		delegate.synchronizeTask(shortName, version, taskId, principal.getName());
	}

	protected final void promoteTask(final String shortName, final String version, final String taskId, final Principal principal) {
		delegate.promoteTask(shortName, version, taskId, principal.getName());
	}
}