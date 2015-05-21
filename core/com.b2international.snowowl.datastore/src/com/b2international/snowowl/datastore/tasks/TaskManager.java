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

import static com.b2international.commons.collections.Collections3.toSet;
import static com.b2international.commons.exceptions.Exceptions.extractCause;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.asList;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.ecore.EPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.TaskBranchPathMap;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.datastore.cdo.CustomConflictException;
import com.b2international.snowowl.datastore.cdo.ICDOBranchActionManager;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.IPostStoreUpdateManager;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.google.common.base.Preconditions;

/**
 * The TaskManager service is responsible of handling all the branch switching.
 * <p>
 * It wraps a {@link ICDOBranchActionManager}, which contains the CDO related logic.
 * </p>
 * 
 */
public class TaskManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);

	private final UserBranchPathMap userBranchPathMap;
	
	private final ListenerList taskListenerList = new ListenerList(ListenerList.IDENTITY);
	
	private Task activeTask;
	
	public TaskManager(final UserBranchPathMap userBranchPathMap) {
		this.userBranchPathMap = userBranchPathMap;
	}

	
	/**
	 * Activate the specified task.
	 * @param task the task to activate.
	 * @param monitor monitor for the task activation process.
	 */
	public void activateTask(final Task task, final IProgressMonitor monitor) {

		Throwable exc = null;
		SetTaskNameDelayedJob setTaskNameDelayedJob = null;

		// Nothing to do if the specified task is already active
		if (task.equals(activeTask)) {
			return;
		}
		
		final String taskId = task.getTaskId();
		
		try {
		
			boolean requireBranchCreation = false;
			
			// Check if a branch needs to be created on any connection
			for (final Entry<String, IBranchPath> entry : task.getTaskBranchPathMap().getLockedEntries().entrySet()) {
				
				final ICDOConnection connection = getConnectionManager().getByUuid(entry.getKey());
				final IBranchPath taskBranchPath = task.getTaskBranchPath(connection.getUuid());
				requireBranchCreation = (null == connection.getBranch(taskBranchPath));
				
				if (requireBranchCreation) {
					break;
				}
			}
			
			if (requireBranchCreation) {
				monitor.beginTask(MessageFormat.format("Setting up infrastructure for Task {0}...", taskId), IProgressMonitor.UNKNOWN);
			} else {
				monitor.beginTask(MessageFormat.format("Activating Task {0}...", taskId), IProgressMonitor.UNKNOWN);
				setTaskNameDelayedJob = new SetTaskNameDelayedJob(monitor, taskId);
				setTaskNameDelayedJob.schedule(SetTaskNameDelayedJob.DELAY_MILLIS);
			}
			
			exc = getBranchManager().prepare(task.getTaskBranchPathMap(), getUserId());
			
			if (null == exc) {
				exc = getTaskStateManager().activateTask(taskId, getUserId());
			}
			
		} catch (final Throwable t) {
			exc = t;
		} finally {
			
			if (null != exc) {
				LOGGER.error(exc.getMessage(), exc);
				throw new SnowowlRuntimeException(exc);
			} else {
				
				//set configuration earlier than active filed setting, as flush may fail
				getClientConfiguration().setLastActiveTaskId(taskId);
				getClientConfiguration().flush();
				
				activeTask = task;
				fireTaskActivated(task);
				getPostStoreUpdateManager().notifyListeners(null);
				LOGGER.info(MessageFormat.format("User {0} has activated Task ''{1}''.", getUserId(), taskId));
			}
			
			// Do not start running if not scheduled yet
			if (null != setTaskNameDelayedJob) {
				setTaskNameDelayedJob.cancel();
			}
			
			monitor.done();
		}
	}

	/**
	 * Deactivate the active task. Does nothing if currently there is no active task.
	 * @param monitor the monitor for the deactivation process.
	 */
	public void deactivateActiveTask(final IProgressMonitor monitor) {
		
		// Nothing to do if there is no active task
		if (null == activeTask) {
			return;
		}

		// If the task has been promoted, there's no need to revive the user's version branches for browsing, as they were used in the promotion
		if (activeTask.isPromoted()) {
			LOGGER.info(MessageFormat.format("User {0} has deactivated Task {1} after promoting the change set.", getUserId(), activeTask.getTaskId()));
			postDeactivateTask();
			return;
		}
		
		Throwable exc = null;
		
		try {
			
			// XXX: userBranchPathMap may be lazy, in which case nothing gets prepared, but this may not be a problem anyway.
			monitor.beginTask(MessageFormat.format("Deactivating Task {0}...", activeTask.getTaskId()), IProgressMonitor.UNKNOWN);
			exc = getBranchManager().prepare(getUserBranchPathMap(), getUserId());
			
			if (null == exc) {
				exc = getTaskStateManager().deactiavteActiveTask(getUserId());
			}
			
		} catch (final Throwable t) {
			exc = t;
		} finally {

			if (null != exc) {
				LOGGER.error(exc.getMessage(), exc);
				throw new SnowowlRuntimeException(exc);
			} else {
				LOGGER.info(MessageFormat.format("User {0} has deactivated Task {1}.", getUserId(), activeTask.getTaskId()));
				postDeactivateTask();
			}
		}
	}

	/**
	 * Get the current active task. If there is no task active, the return value is {@code null}.
	 * @return the active task or {@code null}.
	 */
	@Nullable public Task getActiveTask() {
		return activeTask;
	}

	/**Same as {@link #getActiveBranch(String)}.*/
	public IBranchPath getActiveBranch(final EPackage ePackage) {
		return getActiveBranch(getConnectionManager().get(ePackage).getUuid());
	}
	
	/**
	 * Get the active branch for the specified repository UUID. Can be either the version-specific MAIN branch or a task branch.
	 * @param repositoryId the repository UUID to look for
	 * @return the current branch.
	 */
	public IBranchPath getActiveBranch(final String repositoryId) {
		return getBranchPathMap().getBranchPath(repositoryId);
	}

	/**
	 * Sugar for {@link #setActiveVersion(String, String)}.
	 * @param toolingId the application specific tooling ID. Can be either terminology ID or terminology component ID.
	 * @param the target version. The unique name of the tag.
	 */
	public void setActiveVersionByToolingId(final String toolingId, final String targetVersion) {
		final String repositoryUuid = CodeSystemUtils.getRepositoryUuid(Preconditions.checkNotNull(toolingId, "Tooling ID argument cannot be null."));
		setActiveVersion(repositoryUuid, Preconditions.checkNotNull(targetVersion, "Target version argument cannot be null."));
	}
	
	/**
	 * Sets the user specific active version in a repository.
	 * @param repositoryUuid the unique ID of the repository.
	 * @param targetVersion the target version. The tag name.
	 */
	public void setActiveVersion(final String repositoryUuid, final String targetVersion) {

		final IBranchPath destinationPath = IBranchPath.MAIN_BRANCH.equals(Preconditions.checkNotNull(targetVersion, "Target version argument cannot be null.")) 
				? BranchPathUtils.createMainPath() 
				: BranchPathUtils.createPath(BranchPathUtils.createMainPath(), targetVersion);
		
		getUserBranchPathMap().putBranchPath(Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null."), destinationPath);
		
		//singleton map for triggering index structure initialization on the server side.
		final TaskBranchPathMap triggerMap = new TaskBranchPathMap(Collections.singletonMap(repositoryUuid, destinationPath));
		getBranchManager().prepare(triggerMap, getUserId());

		//notify views, editors, listeners whatever
		getPostStoreUpdateManager().notifyListeners(null);
	}
	
	public IBranchPathMap getBranchPathMap() {
		
		return hasActiveTask() 
				? activeTask.getTaskBranchPathMap() 
				: getUserBranchPathMap();
	}

	/**
	 * @return the user-defined branch path settings 
	 */
	public UserBranchPathMap getUserBranchPathMap() {
		return userBranchPathMap;
	}
	
	/**
	 * Synchronize the active task with its version-specific MAIN branch in a new job. All the changes which have been done on the
	 * version-specific MAIN branch after the creation of the task will be merged. If there is a conflict in these changes, a
	 * {@link ConflictException} will be thrown.
	 * 
	 * @param taskId
	 * @param commitComment 
	 * @param progressMonitor monitor for the process.
	 */
	public void synchronizeTask(String taskId, String commitComment, IProgressMonitor progressMonitor) throws CustomConflictException, SnowowlServiceException {

		Throwable exc = null;
		
		try {
			
			Task taskToSynchronize = getTask(taskId);
			checkState(null != taskToSynchronize, "Task doesn't exist.");
			progressMonitor.beginTask(MessageFormat.format("Synchronizing Task {0}...", taskId), IProgressMonitor.UNKNOWN);
			exc = getBranchManager().synchronize(taskToSynchronize.getTaskBranchPathMap(), getUserId(), commitComment);
			
		} catch (final Throwable t) {
			exc = t;
		} finally {
			
			if (null != exc) {
				
				if (exc instanceof CommitException) {
					
					final RepositoryLockException lockException = extractCause((CommitException) exc, this.getClass().getClassLoader(), RepositoryLockException.class);
					if (null != lockException) {
						//intentionally not log error
						throw new SnowowlServiceException("Cannot perform task synchronization due to repository lock. All kind of modifications are temporary disabled.", lockException);
					}
					
				}
				
				if (exc instanceof CustomConflictException) {
					LOGGER.error(exc.getMessage());
				} else {
					LOGGER.error(exc.getMessage(), exc);
				}
				
				
				if (exc instanceof CustomConflictException) {
					throw (CustomConflictException) exc;
				}
				
				throw new SnowowlServiceException(exc.getMessage(), exc);
			} else {
				/* 
				 * Fire an artificial commit notification to refresh navigator/editor contents.
				 * This is required as it may happen that the underlying backend content does not change, but the indexes
				 * should still be updated.
				 */
				getServiceForClass(IPostStoreUpdateManager.class).notifyListeners(null);
				LOGGER.info(MessageFormat.format("User {0} has synchronized Task {1}.", getUserId(), taskId));
			}
		}
	}

	public void promoteTask(final String taskId, final String commitComment, final IProgressMonitor monitor) throws SnowowlServiceException {
		
		Throwable exc = null;
		
		try {
			
			Task taskToPromote = getTask(taskId);
			checkState(null != taskToPromote, "Task doesn't exist.");
			monitor.beginTask(MessageFormat.format("Promoting changes made in Task {0}...", taskId), IProgressMonitor.UNKNOWN);
			exc = getBranchManager().promote(taskToPromote.getTaskBranchPathMap(), getUserId(), commitComment);

		} catch (final Throwable t) {
			exc = t;
		} finally {
			if (null != exc) {
				
				if (exc instanceof CommitException) {
					
					final RepositoryLockException lockException = extractCause((CommitException) exc, this.getClass().getClassLoader(), RepositoryLockException.class);
					if (null != lockException) {
						//intentionally not log error
						throw new SnowowlServiceException("Cannot perform task promotion due to repository lock. All kind of modifications are temporary disabled.", lockException);
					}
					
				}
				
 				LOGGER.error(exc.getMessage(), exc);
				throw new SnowowlServiceException(exc.getMessage(), exc);
			}
		}
	}

	/**
	 * Retrieves a task using the specified identifier. Returns with {@code null} if the task does not exist yet.
	 * @param taskId the task ID.
	 * @return the task or {@code null} if does not exist.
	 */
	public Task getTask(final String taskId) {
		
		final Task persistedTask = getTaskStateManager().getTask(taskId);
		
		if (null != persistedTask) {
			persistedTask.getTaskBranchPathMap().setParent(getUserBranchPathMap());
		}
		
		return persistedTask;
	}

	public boolean isSynchronized(final String taskId) {
		checkNotNull(taskId, "Task identifier may not be null.");
		return isSynchronized(getTask(taskId));
	}
	
	/**
	 * Returns {@code true} if the specified task is synchronized.
	 * 
	 * @param task the task to check
	 * @return {@code true} if the task is synchronized, {@code false} otherwise.
	 */
	public boolean isSynchronized(final Task task) {
		checkNotNull(task, "The task to check may not be null.");
		return getBranchManager().isSynchronized(task.getTaskBranchPathMap());
	}
	
	/**
	 * @return {@code true} if the user has activated a task, {@code false} otherwise.
	 */
	public boolean hasActiveTask() {
		return null != activeTask;
	}
	
	/**
	 * Returns {@code true} if the currently active task is assignable to any of the given task contexts.
	 * Otherwise {@code false}. Sugar for {@link #hasActiveTaskFor(String, String...)}.
	 * @param taskContextIds the task context IDs to check.
	 * @return {@code true} if the currently active task (if any) can be assigned to any of the given task context 
	 * arguments. Otherwise {@code false}.
	 */
	public boolean hasActiveTaskFor(final Iterable<String> taskContextIds) {
		checkNotNull(taskContextIds, "taskContextIds");
		if (hasActiveTask()) {
			return toSet(taskContextIds).contains(getActivetTaskContextId());
		}
		return false;
	}
	
	/**
	 * Returns with {@code true} if a task is active and the task context ID matches with any of the
	 * given task context IDs argument. Otherwise returns with {@code false}. Also returns with {@code false}
	 * if currently there are no active tasks. 
	 * @param taskContextId the task context ID to match.
	 * @param otherTaskContextIds the other possible task context IDs.
	 * @return {@code true} if the currently active task's context ID matches with the given ones, otherwise {@code false}.
	 */
	public boolean hasActiveTaskFor(final String taskContextId, final String... otherTaskContextIds) {
		checkNotNull(taskContextId, "taskContextId");
		checkNotNull(otherTaskContextIds, "otherTaskContextIds");
		return hasActiveTaskFor(asList(taskContextId, otherTaskContextIds));
	}
	
	public void addTaskListener(final IApplicationTaskListener applicationTaskListener) {
		taskListenerList.add(applicationTaskListener);
	}


	public void removeTaskListener(final IApplicationTaskListener applicationTaskListener) {
		taskListenerList.remove(applicationTaskListener);
	}


	private String getActivetTaskContextId() {
		checkNotNull(activeTask, "activeTask");
		return checkNotNull(activeTask.getTaskContext().getContextId(), "Active task context ID was null.");
	}
	
	private void postDeactivateTask() {
		
		final Task oldTask = activeTask;
		activeTask = null;
		
		fireTaskDeactivated(oldTask);
		getPostStoreUpdateManager().notifyListeners(null);
		getClientConfiguration().clearLastActiveTaskId();
		getClientConfiguration().flush();
	}

	/*returns with the unique ID of the user who is associated with the current task manager instance*/
	private String getUserId() {
		return Preconditions.checkNotNull(Preconditions.checkNotNull(getConnectionManager().getUser(), "User was null for the underlying connection.").getUserName(), "User ID was null.");
	}
	
	/*returns with the branch manager service*/
	private ICDOBranchActionManager getBranchManager() {
		return getServiceForClass(ICDOBranchActionManager.class);
	}
	
	/*returns with the CDO connection manager service.*/
	private ICDOConnectionManager getConnectionManager() {
		return getServiceForClass(ICDOConnectionManager.class);
	}

	/*returns with the post store update manager service*/
	private IPostStoreUpdateManager getPostStoreUpdateManager() {
		return getServiceForClass(IPostStoreUpdateManager.class);
	}
	
	/*returns with the task state manager service*/
	private ITaskStateManager getTaskStateManager() {
		return getServiceForClass(ITaskStateManager.class);
	}

	/*returns with the client configuration service*/
	private ClientPreferences getClientConfiguration() {
		return getServiceForClass(ClientPreferences.class);
	}
	
	private void fireTaskActivated(final Task task) {
		
		final Object[] listeners = taskListenerList.getListeners();
		for (final Object listener : listeners) {
			try {
				((IApplicationTaskListener) listener).taskActivated(task);
			} catch (final Exception e) {
				LOGGER.error("Caught exception while notifying listener of task activation.", e);
				continue;
			}
		}
	}


	private void fireTaskDeactivated(final Task task) {
		
		final Object[] listeners = taskListenerList.getListeners();
		for (final Object listener : listeners) {
			try {
				((IApplicationTaskListener) listener).taskDeactivated(task);
			} catch (final Exception e) {
				LOGGER.error("Caught exception while notifying listener of task deactivation.", e);
				continue;
			}
		}
	}

	private static class SetTaskNameDelayedJob extends Job {

		public static final long DELAY_MILLIS = 500L;
		
		private final IProgressMonitor monitor;
		private final String taskId;
		
		public SetTaskNameDelayedJob(final IProgressMonitor monitor, final String taskId) {
			super("Delayed progress monitor update");
			this.monitor = monitor;
			this.taskId = taskId;
			setUser(false);
			setSystem(true);
			setPriority(Job.INTERACTIVE);
		}
		
		/* 
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(final IProgressMonitor jobProgressMonitor) {
			monitor.setTaskName(MessageFormat.format("Reviving index structures for Task {0}...", taskId));
			return Status.OK_STATUS;
		}
	}

}