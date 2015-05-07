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
package com.b2international.snowowl.api.impl.task;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.impl.task.domain.Task;
import com.b2international.snowowl.api.task.ITaskService;
import com.b2international.snowowl.api.task.domain.ITask;
import com.b2international.snowowl.api.task.domain.ITaskInput;
import com.b2international.snowowl.api.task.domain.TaskState;
import com.b2international.snowowl.api.task.exception.TaskNotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.TaskBranchPathMap;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ICDOBranchActionManager;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.b2international.snowowl.datastore.tasks.TaskScenario;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * TODO: exception handling
 * 
 */
public class AbstractTaskServiceImpl implements ITaskService {

	private static final UserBranchPathMap MAIN_BRANCH_PATH_MAP = new UserBranchPathMap();

	private static final String DEFAULT_REPOSITORY_URL = "";
	private static final TaskScenario DEFAULT_SCENARIO = TaskScenario.SINGLE_AUTHOR_WITH_SINGLE_REVIEWER;

	private static final Function<ITask, String> GET_BRANCH_INFO_ID = new Function<ITask, String>() {
		@Override public String apply(final ITask input) { return input.getTaskId(); }
	};

	private static final Ordering<ITask> BRANCH_INFO_ID_ORDERING = Ordering.natural().onResultOf(GET_BRANCH_INFO_ID);

	private final class TaskConverter implements Function<com.b2international.snowowl.datastore.tasks.Task, ITask> {

		private final long baseLastUpdatedTimestamp;

		private TaskConverter(final long baseLastUpdatedTimestamp) {
			this.baseLastUpdatedTimestamp = baseLastUpdatedTimestamp;
		}

		@Override
		public ITask apply(final com.b2international.snowowl.datastore.tasks.Task input) {
			final Task result = new Task();

			final IBranchPath repositoryBranchPath = input.getTaskBranchPath(handledRepositoryUuid);
			final CDOBranch repositoryBranch = getConnectionManager().getByUuid(handledRepositoryUuid).getBranch(repositoryBranchPath);
			final long branchBaseTimestamp = repositoryBranch.getBase().getTimeStamp();
			final long branchHeadTimestamp = CDOServerUtils.getLastCommitTime(repositoryBranch);

			result.setBaseTimestamp(new Date(branchBaseTimestamp));

			if (branchHeadTimestamp > Long.MIN_VALUE) {
				result.setLastUpdatedTimestamp(new Date(branchHeadTimestamp));
			}

			result.setTaskId(input.getTaskId());
			result.setDescription(input.getDescription());
			if (input.isPromoted()) {
				result.setState(TaskState.PROMOTED);
			} else if (branchBaseTimestamp >= baseLastUpdatedTimestamp) {
				result.setState(TaskState.SYNCHRONIZED);
			}
			return result;
		}
	}

	private static TerminologyRegistryService getRegistryService() {
		return ApplicationContext.getServiceForClass(TerminologyRegistryService.class);
	}

	private static ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getServiceForClass(ICDOConnectionManager.class);
	}

	private static ITaskStateManager getTaskStateManager() {
		return ApplicationContext.getServiceForClass(ITaskStateManager.class);
	}

	private static ICDOBranchActionManager getBranchManager() {
		return ApplicationContext.getServiceForClass(ICDOBranchActionManager.class);
	}

	private static void checkCodeSystemVersionArguments(final String shortName, final String version) {
		checkNotNull(shortName, "Code system short name may not be null.");
		checkNotNull(version, "Code system version may not be null.");
	}

	private static void checkBranchArguments(final String shortName, final String version, final String branchId) {
		checkCodeSystemVersionArguments(shortName, version);
		checkNotNull(branchId, "Branch name may not be null.");
	}

	private final String handledRepositoryUuid;
	private final String taskContextId;

	protected AbstractTaskServiceImpl(final String handledRepositoryUuid, final String taskContextId) {
		this.handledRepositoryUuid = handledRepositoryUuid;
		this.taskContextId = taskContextId;
	}

	@Override
	public List<ITask> getAllTasks(final String shortName, final String version, final boolean includePromoted) {
		checkCodeSystemVersionArguments(shortName, version);

		final ICodeSystem codeSystem = getCodeSystem(shortName);
		final String repositoryUuid = codeSystem.getRepositoryUuid();
		final CDOBranch versionBranch = getVersionBranch(repositoryUuid, version);
		final IBranchPath versionBranchPath = BranchPathUtils.createPath(versionBranch);

		final long baseLastUpdatedTimestamp = CDOServerUtils.getLastCommitTime(versionBranch);
		final List<com.b2international.snowowl.datastore.tasks.Task> datastoreTasks = getTaskStateManager().getTasksByVersionPath(repositoryUuid, versionBranchPath, includePromoted);
		final List<ITask> restTasks = Lists.transform(datastoreTasks, new TaskConverter(baseLastUpdatedTimestamp));

		return BRANCH_INFO_ID_ORDERING.immutableSortedCopy(restTasks);
	}

	@Override
	public ITask getTaskByName(final String shortName, final String version, final String taskId) {
		checkBranchArguments(shortName, version, taskId);

		final ICodeSystem codeSystem = getCodeSystem(shortName);
		final String repositoryUuid = codeSystem.getRepositoryUuid();
		final CDOBranch versionBranch = getVersionBranch(repositoryUuid, version);

		// XXX: See if the CDO branch for the requested task exists, then check if the task state manager knows about it
		getTaskBranch(repositoryUuid, versionBranch, taskId);

		final com.b2international.snowowl.datastore.tasks.Task datastoreTask = getTaskStateManager().getTask(taskId);
		if (null == datastoreTask) {
			throw new TaskNotFoundException(taskId);
		}

		final long baseLastUpdatedTimestamp = CDOServerUtils.getLastCommitTime(versionBranch);
		return new TaskConverter(baseLastUpdatedTimestamp).apply(datastoreTask);
	}

	private CDOBranch getTaskBranch(final String repositoryUuid, final CDOBranch versionBranch, final String branchId) {
		final IBranchPath taskBranchPath = BranchPathUtils.createPath(BranchPathUtils.createPath(versionBranch), branchId);
		final CDOBranch taskBranch = getCdoBranch(repositoryUuid, taskBranchPath);

		if (null == taskBranch) {
			throw new TaskNotFoundException(branchId);
		}

		return taskBranch;
	}

	private ICodeSystem getCodeSystem(final String shortName) {
		final ICodeSystem codeSystem = getRegistryService().getCodeSystemByShortName(MAIN_BRANCH_PATH_MAP, shortName);

		if (null == codeSystem) {
			throw new CodeSystemNotFoundException(shortName);
		} else {
			return codeSystem;
		}
	}

	private CDOBranch getVersionBranch(final String repositoryUuid, final String version) {
		final IBranchPath versionBranchPath = BranchPathUtils.createVersionPath(version);
		final CDOBranch versionBranch = getCdoBranch(repositoryUuid, versionBranchPath);

		if (null == versionBranch) {
			throw new CodeSystemVersionNotFoundException(repositoryUuid);
		} else {
			return versionBranch;
		}
	}

	private CDOBranch getCdoBranch(final String repositoryUuid, final IBranchPath branchPath) {
		return getConnectionManager().getByUuid(repositoryUuid).getBranch(branchPath);
	}

	@Override
	public ITask createTask(final String shortName, final String version, final String taskId, final ITaskInput input, final String userId) {
		checkCodeSystemVersionArguments(shortName, version);
		checkNotNull(taskId, "Task identifier may not be null.");
		checkNotNull(input, "Branch input object may not be null.");

		final ICodeSystem codeSystem = getCodeSystem(shortName);
		final String repositoryUuid = codeSystem.getRepositoryUuid();
		final CDOBranch versionBranch = getVersionBranch(repositoryUuid, version);

		try {
			getTaskBranch(repositoryUuid, versionBranch, taskId);
			throw new AlreadyExistsException("Task", taskId);
		} catch (final TaskNotFoundException ignored) {
			// It is expected that the task branch does not already exist.
		}

		final IBranchPath newBranchPath = BranchPathUtils.createPath(BranchPathUtils.createPath(versionBranch), taskId);
		final TaskBranchPathMap newBranchPathMap = new TaskBranchPathMap(ImmutableMap.of(repositoryUuid, newBranchPath));

		// TODO: the next three calls probably should happen in an operation lock, so that others may not jump in
		if (getTaskStateManager().exists(taskId)) {
			throw new AlreadyExistsException("Task", taskId);
		}

		getBranchManager().prepare(newBranchPathMap, userId);
		getTaskStateManager().createOrUpdate(taskId, false, newBranchPathMap, 
				taskContextId, DEFAULT_REPOSITORY_URL, input.getDescription(), DEFAULT_SCENARIO);

		return getTaskByName(shortName, version, taskId);
	}

	@Override
	public void synchronizeTask(final String shortName, final String version, final String branchId, final String userId) {
		checkBranchArguments(shortName, version, branchId);

		final TaskBranchPathMap branchPathMap = getBranchPathMap(shortName, version, branchId); 
		getBranchManager().synchronize(branchPathMap, userId);
	}

	@Override
	public void promoteTask(final String shortName, final String version, final String branchId, final String userId) {
		checkBranchArguments(shortName, version, branchId);

		final TaskBranchPathMap branchPathMap = getBranchPathMap(shortName, version, branchId); 
		getBranchManager().promote(branchPathMap, userId, MessageFormat.format("Promoted changes made on branch {0}.", branchId));
		getTaskStateManager().setClosed(branchId, userId);
	}

	private TaskBranchPathMap getBranchPathMap(final String shortName, final String version, final String branchId) {

		final ICodeSystem codeSystem = getCodeSystem(shortName);
		final String repositoryUuid = codeSystem.getRepositoryUuid();
		final CDOBranch versionBranch = getVersionBranch(repositoryUuid, version);
		final CDOBranch taskBranch = getTaskBranch(repositoryUuid, versionBranch, branchId);
		final IBranchPath taskBranchPath = BranchPathUtils.createPath(taskBranch);

		final TaskBranchPathMap result = new TaskBranchPathMap(ImmutableMap.of(repositoryUuid, taskBranchPath));
		return result;
	}
}