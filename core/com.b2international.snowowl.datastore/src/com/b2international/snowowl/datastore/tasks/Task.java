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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.text.MessageFormat;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.TaskBranchPathMap;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Class for encapsulating the bare minimum information for wrapping a vendor independent task.
 */
public final class Task implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final TaskBranchPathMap taskBranchPathMap;
	private final String taskId;
	private final String description;
	private final String repositoryUrl;
	private final String contextId;
	private final String toolingId;
	private final TaskScenario scenario;
	private final boolean promoted;

	public Task(final String taskId, final boolean promoted, final TaskBranchPathMap taskBranchPathMap, final ITaskContext context, final String repositoryUrl, final String description, final TaskScenario scenario) {
		this.taskId = checkNotNull(taskId, "Task identifier argument cannot be null.");
		this.taskBranchPathMap = checkNotNull(taskBranchPathMap, "Task branch path map argument cannot be null.");
		this.contextId = Preconditions.checkNotNull(context, "Task context argument cannot be null.").getContextId();
		toolingId = context.getToolingId();
		this.repositoryUrl = Preconditions.checkNotNull(repositoryUrl, "Repository URL argument cannot be null.");
		this.description = Strings.nullToEmpty(description);
		this.scenario = checkNotNull(scenario, "Task scenario argument cannot be null.");
		this.promoted = promoted;
	}
	
	// TODO: Consider moving this to a provider if we are calling this frequently
	public String getComponentTargetVersion() {
		
		final String repositoryUuid = CodeSystemUtils.getRepositoryUuid(toolingId);
		
		IBranchPath branchPath = taskBranchPathMap.getBranchPath(repositoryUuid);
		
		/*
		 * Has this repository entry been set to work on a task branch? Step up one level. Note that this may cause problems if the task
		 * repository can assign task identifiers which match version identifiers (ie. a "V1" task on a version named "V1").
		 */
		if (taskId.equals(branchPath.lastSegment())) {
			branchPath = branchPath.getParent();
		}
		
		// If we are already at MAIN, return it, otherwise extract the last segment (which should be the version string)
		if (BranchPathUtils.isMain(branchPath)) {
			return branchPath.getPath();
		} else {
			return branchPath.lastSegment();
		}
	}

	public TaskBranchPathMap getTaskBranchPathMap() {
		return taskBranchPathMap;
	}
	
	public IBranchPath getTaskBranchPath(final String repositoryId) {
		return taskBranchPathMap.getBranchPath(repositoryId);
	}

	public IBranchPath getTaskBranchPath(final EPackage ePackage) {
		return taskBranchPathMap.getBranchPath(ePackage);
	}

	public String getTaskId() {
		return taskId;
	}
	
	/**Returns with the URL of the issue tracking repository. Bugzilla, JIRA, etc.*/
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isPromoted() {
		return promoted;
	}
	
	/**
   * Returns with the {@link ITaskContext context} of the current task.
	 * <p>Clients should consider that this method eagerly creates a new {@link ITaskContext context} instance
	 * when accessed.
	 * @return the context of the current task.
	 */
	public ITaskContext getTaskContext() {
		return TaskContextManager.INSTANCE.createNewById(contextId);
	}
	
	public String getContextId() {
		return contextId;
	}

	public String getToolingId() {
		return toolingId;
	}
	
	public TaskScenario getScenario() {
		return scenario;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 31 + taskId.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final Task other = (Task) obj;
		return Objects.equal(taskId, other.taskId) && Objects.equal(repositoryUrl, other.repositoryUrl);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MessageFormat.format("Task [taskId={0}, taskBranchPathMap={1}, description={2}, promoted={3}]", 
				taskId, taskBranchPathMap, description, promoted);
	}
}