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
package com.b2international.snowowl.datastore.expressions;

import javax.annotation.Nullable;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.users.IClientAuthorizationService;
import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.PermissionIdConstant;
import com.b2international.snowowl.datastore.tasks.Task;
import com.b2international.snowowl.datastore.tasks.TaskManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

/**
 * @since 3.1.0
 */
public class TaskContextTester {

	/**
	 * Sugar for {@link #testForContextId(String, String...)}.
	 * <p>{@code null} or empty iterable is prohibited.
	 */
	public static boolean testForContextId(@Nullable final Iterable<String> contextIds) {
		Preconditions.checkNotNull(contextIds, "contextIds");
		Preconditions.checkArgument(!CompareUtils.isEmpty(contextIds), "empty contextIds");
		return testForContextId(Iterables.get(contextIds, 0), Iterables.toArray(contextIds, String.class));
	}
	
	/**
	 * Returns {@code true} if the current active task (if any) is assigned for authoring in the repository given with its unique UUID.
	 * If there is no active task currently, we check the user for {@link PermissionIdConstant#ADMINISTRATIVE_EDITING} permission. 
	 * If that passes, we return <code>true</code>. 
	 * <Code>Note:</code>
	 * It is advised to first check if user has the {@link PermissionIdConstant#ADMINISTRATIVE_EDITING} permission.
	 * If that passes, you don't need to call this tester.
	 * @param repositoryUuid unique repository ID.
	 * @return {@code true} if current active task context is associated with the argument. Otherwise {@code false}.
	 * 
	 */
	public static boolean testForContextId(final String contextId, final String... otherContextIds) {
		final TaskManager taskManager = getTaskManager();
		if (null == taskManager) {
			return false;
		}
		
		IClientAuthorizationService authorizationService = ApplicationContext.getInstance().getService(IClientAuthorizationService.class);
		boolean authorizedAdminEditing = authorizationService.isAuthorized(new Permission(PermissionIdConstant.ADMINISTRATIVE_EDITING));
		final Task task = taskManager.getActiveTask();
		if (null == task) { //client does not have active task
			return authorizedAdminEditing;
		}
		
		final String currentContextId = task.getContextId();
		boolean matchingContextId = currentContextId.equals(contextId);

		//check equals sequentially instead of wrapping arguments into a set
		if (matchingContextId) {
			return true;
		}

		for (final String otherContextId : otherContextIds) {
			if (currentContextId.equals(otherContextId)) {
				return true;
			}
		}
		
		return authorizedAdminEditing;
	}
	
	/*returns with the task manager from the application context. may return with null when the application is starting.*/
	@Nullable private static TaskManager getTaskManager() {
		return ApplicationContext.getInstance().getService(TaskManager.class);
	}

}