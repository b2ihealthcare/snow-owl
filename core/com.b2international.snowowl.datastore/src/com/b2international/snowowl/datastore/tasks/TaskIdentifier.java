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
package com.b2international.snowowl.datastore.tasks;

import com.b2international.commons.StringUtils;
import com.google.common.base.Preconditions;

/**
 * {@link ITaskIdentifier Task identifier} implementation.
 */
public class TaskIdentifier implements ITaskIdentifier {

	private static final long serialVersionUID = 6760158369891439369L;
	private final String taskId;
	private final String repositoryUrl;

	/**Creates a new {@link ITaskIdentifier task identifier} instance with the given task ID and repository URL arguments.*/
	public static ITaskIdentifier newInstance(final String taskId, final String repositoryUrl) {
		return new TaskIdentifier(Preconditions.checkNotNull(taskId), Preconditions.checkNotNull(repositoryUrl));
	}

	/**
	 * Creates a new instance.
	 * @param taskId the unique task ID.
	 * @param repositoryUrl the task repository URL.
	 */
	private TaskIdentifier(String taskId, String repositoryUrl) {
		this.taskId = Preconditions.checkNotNull(taskId, "Task ID argument cannot be null.");
		this.repositoryUrl = Preconditions.checkNotNull(repositoryUrl, "Task repository URL argument cannot be null.");
		Preconditions.checkState(!StringUtils.isEmpty(taskId), "Task ID argument should be specified.");
		Preconditions.checkState(!StringUtils.isEmpty(repositoryUrl), "Task repository URL argument should be specified.");
	}


	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskIdentifier#getId()
	 */
	@Override
	public String getId() {
		return taskId;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskIdentifier#getRepositoryUrl()
	 */
	@Override
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

}