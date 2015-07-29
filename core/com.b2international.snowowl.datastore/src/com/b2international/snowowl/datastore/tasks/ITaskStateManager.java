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

import java.util.List;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.SingleDirectoryIndex;
import com.b2international.snowowl.datastore.TaskBranchPathMap;

/**
 * Keeps track of task promotion status on the server. 
 *
 */
public interface ITaskStateManager extends SingleDirectoryIndex {

	public static final String PROTOCOL_NAME = ITaskStateManager.class.getName(); 
	
	/**
	 * Queries the server for the closed status of the specified task.
	 * 
	 * @param taskId the task identifier to look for
	 * @return {@code true} if the task has already been closed, {@code false} otherwise
	 */
	boolean isClosed(final String taskId);
	
	/**
	 * Queries the server for all closed tasks which have the specified branch path as their parent.
	 * 
	 * @param repositoryUuid the unique identifier of the repository to check
	 * @param versionPath the repository version branch path prefix which the tasks to return should have
	 * @param includePromoted {@code true} if promoted tasks should be added to the list, {@code false} otherwise
	 * @return a list of tasks which have a task branch path that is a child of the specified version path
	 */
	List<Task> getTasksByVersionPath(String repositoryUuid, IBranchPath versionPath, boolean includePromoted);

	/**
	 * Signals that the task has been closed; it is no longer useful to make edits related to this task.
	 * 
	 * @param taskId the task identifier to set as closed
	 * @param initiatingUserId the user who initiated the close (usually a reviewer or the adjudicator of the closed task)
	 */
	void setClosed(final String taskId, final String initiatingUserId);

	/**
	 * Activates the task given by its ID with a user.
	 * <p>Under the hood this method just associates the unique task identifier argument with the user ID on the server side. 
	 * @param taskId the task identifier to activate.
	 * @param userId the user ID.
	 * @return {@code null} if the operation was successful, otherwise it returns with a throwable representing the error
	 * occurred during the task activation process.
	 */
	Throwable activateTask(final String taskId, final String userId);

	/**
	 * Deactivates the activate task with a user. This method has no effect if there are no active tasks associated with the user.
	 * <p>Under the hood this method just cleans up all (if any) task identifier association with the given user ID. 
	 * @param userId the user ID.
	 * @return {@code null} if the operation was successful, otherwise it returns with a throwable representing the error
	 * occurred during the task deactivate process.
	 */
	Throwable deactiavteActiveTask(final String userId);
	
	/**
	 * Returns with the currently activate task identifier associated with the given user.
	 * <br>May return with {@code null} if the user does not working on a task.
	 * @param userId the user ID.
	 * @return the activate task ID. {@code null} if user does not work on a task.
	 */
	@Nullable String getActiveTaskId(final String userId);
	
	/**
	 * Persists the user's version configuration onto the server side.
	 * @param userId the unique ID of the user.
	 * @param branchPathMap the branch path map representing the user's version configuration.
	 * @return returns with {@code null} if the operation was successful. Otherwise it returns with
	 * a throwable representing the error occurred while performing the operation.
	 */
	//TODO move this to another service
	Throwable setUserVersionConfiguration(final String userId, final IBranchPathMap branchPathMap);
	
	/**
	 * Returns with a branch path map representing the actual version configuration for a user. If <b>task aware</b> argument flag is set to {@code true}, then
	 * any active task is associated with a user given the unique user ID argument, the relevant version configuration (with the corresponding branch path)
	 * will be updated based on the configuration tied to the active task. If there are no active task for the user, this method simply 
	 * returns with the user's version configuration. If task <b>task aware</b> argument is {@code false} then any active task for the given user
	 * is ignored.
	 * @param userId the unique user ID.
	 * @return a branch path map representing the most up to date client-side version configuration.
	 */
	//TODO move this to another service
	IBranchPathMap getBranchPathMapConfiguration(final String userId, final boolean taskAware);
	
	/**
	 * 
	 * @param taskId
	 * @param promoted
	 * @param taskBranchPathMap
	 * @param contextId unique ID of the {@link ITaskContext}.
	 * @param repositoryUrl
	 * @param description
	 * @param scenario
	 * @return
	 */
	Task createOrUpdate(final String taskId, final boolean promoted, final IBranchPathMap taskBranchPathMap, final String contextId, final String repositoryUrl, final String description, final TaskScenario scenario);
	
	/**
	 * 
	 * @param taskId
	 * @return
	 */
	TaskBranchPathMap getTaskBranchPathMap(final String taskId);

	/**
	 * 
	 * @param taskId
	 * @return
	 */
	Task getTask(final String taskId);
	
	/**
	 * @param taskId the identifier of the task to check (may not be {@code null})
	 * @return {@code true} if server-side persisted data exists for the specified task, {@code false} otherwise
	 */
	boolean exists(final String taskId);
	
	/**
	 * Broadcasts the change of task state to the given address.
	 * @param address
	 * @param taskId
	 * @param initiatingUserId
	 */
	void broadcastStateChange(final String address, final String taskId, final String initiatingUserId,
			final String trigger);
}