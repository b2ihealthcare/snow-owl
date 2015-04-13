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
package com.b2international.snowowl.api.task;

import java.util.List;

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.task.domain.ITask;
import com.b2international.snowowl.api.task.domain.ITaskInput;
import com.b2international.snowowl.api.task.exception.TaskCreationException;
import com.b2international.snowowl.api.task.exception.TaskNotFoundException;
import com.b2international.snowowl.api.task.exception.TaskPromotionException;
import com.b2international.snowowl.api.task.exception.TaskSynchronizationConflictException;
import com.b2international.snowowl.api.task.exception.TaskSynchronizationException;

/**
 * Implementations of this interface facilitate task-based editing of terminology content.
 * <p>
 * The service uses opaque task identifiers which may come from external workflow/issue management systems like JIRA or
 * Bugzilla.
 */
public interface ITaskService {

	/**
	 * Retrieves all tasks for the specified code system version.
	 * 
	 * @param shortName       the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param version         the code system version identifier to look for, eg. "{@code 2014-07-31}" (may not be {@code null})
	 * @param includePromoted {@code true} if promoted and closed tasks should be added to the list, {@code false} otherwise
	 * 
	 * @return a list of tasks created for editing the specified code system version, ordered by task identifier (never {@code null})
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 */
	List<ITask> getAllTasks(String shortName, String version, boolean includePromoted);

	/**
	 * Retrieves a single task by name, if it exists.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param version   the code system version identifier to look for, eg. "{@code 2014-07-31}" (may not be {@code null})
	 * @param taskId    the identifier of the task to look for, eg. "{@code 1432}" (may not be {@code null})
	 * 
	 * @return the task for the specified code system, version and identifier
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given code 
	 *                                            system version
	 */
	ITask getTaskByName(String shortName, String version, String taskId);

	/**
	 * Creates a new editing task for a code system.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param version   the code system version identifier to look for, eg. "{@code 2014-07-31}" (may not be {@code null})
	 * @param taskId    the identifier of the task to create, eg. "{@code 1432}" (may not be {@code null})
	 * @param input     additional information of the task to be created (may not be {@code null})
	 * @param userId    the identifier of the user requesting task creation (may not be {@code null})
	 * 
	 * @return the created task
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 * @throws DuplicateTaskException             if the task identifier is already assigned to an existing task
	 * @throws TaskCreationException              if any other error occurs while creating the task
	 */
	ITask createTask(String shortName, String version, String taskId, ITaskInput input, String userId);

	/**
	 * Makes changes that happened on the task's parent branch available on the task branch, without losing changes that
	 * happened on the task itself.
	 * <p>
	 * This is a long-running operation; a call to this method not return until fully completed.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param version   the code system version identifier to look for, eg. "{@code 2014-07-31}" (may not be {@code null})
	 * @param taskId    the identifier of the task to synchronize, eg. "{@code 1432}" (may not be {@code null})
	 * @param userId    the identifier of the user requesting task synchronization (may not be {@code null})
	 * 
	 * @throws CodeSystemNotFoundException          if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException   if a code system version for the code system with the given identifier
	 *                                              is not registered
	 * @throws TaskNotFoundException                if the task identifier does not correspond to a task for the given code 
	 *                                              system version
	 * @throws TaskSynchronizationConflictException if synchronization can not be done as conflicting changes were made
	 *                                              on this branch
	 * @throws TaskSynchronizationException         if any other error occurs while synchronizing the task
	 */
	void synchronizeTask(String shortName, String version, String taskId, String userId);

	/**
	 * Makes changes that happened on the task permanent on the parent version. The task to promote must be
	 * synchronized.
	 * <p>
	 * This is a long-running operation; a call to this method will not return until fully completed.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param version   the code system version identifier to look for, eg. "{@code 2014-07-31}" (may not be {@code null})
	 * @param taskId    the identifier of the task to promote, eg. "{@code 1432}" (may not be {@code null})
	 * @param userId    the identifier of the user requesting task promotion (may not be {@code null})
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given code 
	 *                                            system version
	 * @throws TaskSynchronizationException       if the task is not synchronized to the parent version
	 * @throws TaskPromotionException             if any other error occurs while promoting the task
	 */
	void promoteTask(String shortName, String version, String taskId, String userId);
}
