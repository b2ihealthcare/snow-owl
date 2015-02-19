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
package com.b2international.snowowl.api.impl.task.domain;

import java.util.Date;

import com.b2international.snowowl.api.task.domain.ITask;
import com.b2international.snowowl.api.task.domain.TaskState;

/**
 */
public class Task implements ITask {

	private String description;

	private String taskId;
	private Date baseTimestamp;
	private Date lastUpdatedTimestamp;
	private TaskState state = TaskState.NOT_SYNCHRONIZED;

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public Date getBaseTimestamp() {
		return baseTimestamp;
	}

	@Override
	public Date getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}
	
	@Override
	public TaskState getState() {
		return state;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setTaskId(final String taskId) {
		this.taskId = taskId;
	}

	public void setBaseTimestamp(final Date baseTimestamp) {
		this.baseTimestamp = baseTimestamp;
	}

	public void setLastUpdatedTimestamp(final Date lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}
	
	public void setState(TaskState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Task [description=");
		builder.append(description);
		builder.append(", taskId=");
		builder.append(taskId);
		builder.append(", baseTimestamp=");
		builder.append(baseTimestamp);
		builder.append(", lastUpdatedTimestamp=");
		builder.append(lastUpdatedTimestamp);
		builder.append(", state=");
		builder.append(state);
		builder.append("]");
		return builder.toString();
	}
}