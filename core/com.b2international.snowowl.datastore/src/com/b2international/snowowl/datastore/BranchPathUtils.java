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
package com.b2international.snowowl.datastore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.collections.BackwardListIterator;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBaseBranchPath;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.NullBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.tasks.Task;
import com.b2international.snowowl.datastore.tasks.TaskManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Iterables;

/**
 * Utility class for creating {@link IBranchPath branch paths}.
 * @see IBranchPath
 */
public abstract class BranchPathUtils {
	
	/**
	 * Keeps strong references to every created {@link IBranchPath} in this JVM.
	 */
	private static final Interner<IBranchPath> BRANCH_PATH_INTERNER = Interners.newStrongInterner();
			
	/**
	 * Returns with the {@link IBranchPath branch path} instance based on the currently used active branch in a particular repository.
	 * @param ePackage the {@link EPackage} associated with a repository
	 * @return the {@link IBranchPath} instance.
	 * @see TaskManager#getActiveBranch()
	 */
	public static IBranchPath createActivePath(final EPackage ePackage) {
		checkNotNull(ePackage, "EPackage reference may not be null.");
		
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = connectionManager.get(ePackage);
		return BranchPathUtils.createActivePath(connection.getUuid());
	}
	
	/**
	 * Returns with the parent of the active {@link IBranchPath branch path} instance based on the currently used active branch in a particular repository.
	 * If the active path is MAIN then returns the active path itself.
	 * @param ePackage the {@link EPackage} associated with a repository
	 * @return the {@link IBranchPath} instance.
	 * @see TaskManager#getActiveBranch()
	 */
	public static IBranchPath createParentForActivePath(final EPackage ePackage) {
		IBranchPath activePath = createActivePath(ePackage);
		if(!BranchPathUtils.isMain(activePath)) {
			return activePath.getParent();
		}
		return activePath;
	}
	
	public static TaskBranchPathMap createPathForTaskId(final String taskId) {
		final TaskManager taskManager = ApplicationContext.getInstance().getService(TaskManager.class);
		final Task task = taskManager.getTask(taskId);
		return checkNotNull(task, "Task not found for %s", taskId).getTaskBranchPathMap();
	}
	
	/**
	 * Returns with the {@link IBranchPath branch path} instance based on the currently used active branch.
	 * @param repositoryId the repository identifier.
	 * @return the {@link IBranchPath} instance.
	 * @see TaskManager#getActiveBranch()
	 */
	public static IBranchPath createActivePath(final String repositoryId) {
		return getOrCache(ApplicationContext.getInstance().getService(TaskManager.class).getActiveBranch(repositoryId));
	}
	
	
	public static IBranchPath trimTaskPart(IBranchPath branchPath) {
		TaskManager taskManager = ApplicationContext.getInstance().getService(TaskManager.class);
		return taskManager.hasActiveTask() ? branchPath.getParent() : branchPath;
	}
	
	/**
	 * Returns with the branch path representing the MAIN branch.
	 * @return the branch path for the MAIN branch.
	 */
	public static IBranchPath createMainPath() {
		return getOrCache(new BranchPath(IBranchPath.MAIN_BRANCH));
	}

	/**
	 * Returns with the {@link IBranchPath branch path} instance based on the specified branch path.  
	 * @param path the path describing the branch.
	 * @return the {@link IBranchPath} instance.
	 */
	public static IBranchPath createPath(final String path) {
		return getOrCache(new BranchPath(checkNotNull(path, "Path argument cannot be null.")));
	}
	
	/**Sugar for unwrapping a {@link IBaseBranchPath base branch path} into a common {@link IBranchPath branch path} instance.*/
	public static IBranchPath createPath(final IBranchPath branchPath) {
		return checkNotNull(branchPath, "branchPath") instanceof IBaseBranchPath
			? createPath(branchPath.getPath())
			: branchPath;
	}
	
	/**
	 * Returns with the {@link IBranchPath branch path} instance based on the specified CDO based branch.
	 * @param branch the CDO branch.
	 * @return the path for the branch.
	 */
	public static IBranchPath createPath(final CDOBranch branch) {
		return getOrCache(new BranchPath(Preconditions.checkNotNull(branch, "CDO branch argument cannot be null.").getPathName()));
	}
	
	/**
	 * Returns with the {@link IBranchPath path} instance based on the underlying CDO branch extracted from the specified view.
	 * @param view the CDO view.
	 * @return the branch path.
	 */
	public static IBranchPath createPath(final CDOView view) {
		return createPath(CDOUtils.check(view).getBranch());
	}
	
	/**
	 * Returns with the {@link IBranchPath branch path} instance based on the CDO View where this given object lives.
	 * @param object the CDO object. Should not have {@link CDOState#TRANSIENT transient} state.
	 * @return the branch path.
	 */
	public static IBranchPath createPath(final CDOObject object) {
		return createPath(CDOUtils.check(object).cdoView());
	}
	
	/**
	 * Returns a new {@code IBranchPath} instance where the path has this instance's path and the specified segment concatenated. Multiple
	 * separators at the insertion point will be converted to a single separator.
	 * 
	 * @param segmentToAppend the string segment to append to this path
	 * @return the resulting path
	 */
	public static IBranchPath createPath(final IBranchPath branchPath, final String segmentToAppend) {
		checkNotNull(branchPath, "Source branch path may not be null.");
		checkNotNull(segmentToAppend, "Appended segment may not be null.");
		
		final Splitter splitter = Splitter.on(IBranchPath.SEPARATOR_CHAR).omitEmptyStrings().trimResults();
		
		final Iterable<String> sourceSegments = splitter.split(branchPath.getPath());
		final Iterable<String> appendedSegments = splitter.split(segmentToAppend);
		final Iterable<String> allSegments = Iterables.concat(sourceSegments, appendedSegments);
		
		return BranchPathUtils.createPath(Joiner.on(IBranchPath.SEPARATOR_CHAR).join(allSegments));
	}
	
	/**
	 * Converts the given branch path argument into a {@link IBaseBranchPath} instance.
	 * <p>The branch path representing the {@link IBranchPath#MAIN_BRANCH MAIN} branch is prohibited. 
	 * @param logicalPath the branch path to convert. Cannot be the MAIN path.
	 * @param contextPath the branch path to use for opening an index service.
	 * @return the converted branch path representing the base of a particular branch.
	 */
	public static IBranchPath convertIntoBasePath(final IBranchPath logicalPath, final IBranchPath contextPath) {
		checkNotNull(logicalPath, "logicalPath");
		checkNotNull(contextPath, "contextPath");
		checkArgument(!isMain(logicalPath), "Cannot convert MAIN branch path into base path.");
		
		if (logicalPath instanceof IBaseBranchPath) {
			final IBranchPath existingContextPath = ((IBaseBranchPath) logicalPath).getContextPath();
			checkArgument(contextPath.equals(existingContextPath), "Base branch path references a different context path (%s instead of %s).", existingContextPath, contextPath);
			return logicalPath;
		} else {
			return new BaseBranchPath(logicalPath, contextPath);
		}
	}
	
	public static IBranchPath convertIntoBasePath(final IBranchPath logicalPath) {
		checkNotNull(logicalPath, "logicalPath");
		checkArgument(!isMain(logicalPath), "Cannot convert MAIN branch path into base path.");
		return convertIntoBasePath(logicalPath, logicalPath);
	}
	
	/**
	 * Returns with {@code true} if the {@link IBranchPath branch path} argument is assignable to
	 * the {@link IBaseBranchPath} interface. Otherwise {@code false}.
	 * @param branchPath the branch path to check.
	 * @return {@code true} if base path. Otherwise {@code false}.
	 */
	public static boolean isBasePath(final IBranchPath branchPath) {
		return checkNotNull(branchPath, "branchPath") instanceof IBaseBranchPath; 
	}
	
	/**
	 * Returns {@code true} if the specified CDO branch is representing the MAIN branch. 
	 * @param branch the CDO branch to check.
	 * @return {@code true} if the branch path is representing the MAIN, otherwise returns with {@code false}.
	 */
	public static boolean isMain(final CDOBranch branch) {
		return Preconditions.checkNotNull(branch, "CDO branch argument cannot be null.").isMainBranch();
	}
	
	/**
	 * Returns {@code true} if the specified branch path is the MAIN branch path.
	 * @param path the branch patch to check. 
	 * @return {@code true} if the branch path is the MAIN, otherwise returns with {@code false}.
	 */
	public static boolean isMain(final String path) {
		return IBranchPath.MAIN_BRANCH.equals(Strings.nullToEmpty(path).replaceAll(IBranchPath.SEPARATOR, IBranchPath.EMPTY_PATH));
	}

	/**
	 * Returns with {@code true} if the specified branch path is the MAIN branch path. 
	 * @param path the branch path to check whether this is the MAIN or not.
	 * @return {@code true} if the MAIN, otherwise returns with {@code false}.
	 */
	public static boolean isMain(final IBranchPath path) {
		return isMain(Preconditions.checkNotNull(path, "Branch path argument cannot be null.").getPath());
	}
	
	/**
	 * Returns with a iterator of branch path for the given branch path argument.
	 * <br>The iterator traverse the branch path parentage from top to down, hence the first item of the iterator is always the 
	 * {@link IBranchPath#MAIN_BRANCH MAIN branch} then its descendants.  
	 * @param branchPath the branch path.
	 * @return an iterator for traversing the branch hierarchy from top to down.
	 */
	public static Iterator<IBranchPath> topToBottomIterator(final IBranchPath branchPath) {
		
		
		if (isMain(checkNotNull(branchPath, "Branch path argument cannot be null."))) {
			return singletonList(branchPath).iterator();
		}

		checkArgument(!NullBranchPath.INSTANCE.equals(branchPath), "Null branch path is not allowed.");
		
		boolean hasParent = true;
		IBranchPath currentPath = branchPath;

		//list for storing branch path top most first, descendants then
		final List<IBranchPath> $ = newArrayList(currentPath);
		
		while (hasParent) {
			
			final IBranchPath currentParent = currentPath.getParent();
			$.add(currentParent);
			
			if (isMain(currentParent)) {
				hasParent = false;
			} else {
				currentPath = currentParent;
			}
			
		}
		return new BackwardListIterator<IBranchPath>(unmodifiableList($));
	}
	
	/**
	 * Returns with a iterator of branch path for the given branch path argument.
	 * <br>The iterator traverse the branch path parentage from bottom to top, hence the first item of the iterator is always the 
	 * argument then its ancestors (if any) up to the {@link IBranchPath#MAIN_BRANCH}.  
	 * @param branchPath the branch path.
	 * @return an iterator for traversing the branch hierarchy from bottom to top.
	 */
	public static Iterator<IBranchPath> bottomToTopIterator(final IBranchPath branchPath) {
		return new BackwardListIterator<IBranchPath>(newArrayList(topToBottomIterator(checkNotNull(branchPath, "branchPath"))));
	}
	
	/**
	 * Returns true if the branch with the path specified exists within the specified repository.
	 * @param repositoryUUID
	 * @param branchPath
	 * @return true if the branch exists in the repository
	 */
	public static boolean exists(String repositoryUUID, String branchPath) {
		try {
			RepositoryRequests.branching(repositoryUUID).prepareGet(branchPath).executeSync(ApplicationContext.getInstance().getService(IEventBus.class), 1000);
		} catch (NotFoundException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the MAIN branch for the repository specified by it's repository UUID.
	 * @param repositoryUUID
	 * @return
	 */
	public static Branch getMainBranchForRepository(String repositoryUUID) {
		return RepositoryRequests.branching(repositoryUUID).prepareGet(IBranchPath.MAIN_BRANCH).executeSync(ApplicationContext.getInstance().getService(IEventBus.class), 1000);
	}
	
	private static IBranchPath getOrCache(final IBranchPath branchPath) {
		return BRANCH_PATH_INTERNER.intern(branchPath);
	}
}