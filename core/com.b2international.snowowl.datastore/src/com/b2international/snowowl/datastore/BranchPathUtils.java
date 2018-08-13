/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.concurrent.TimeUnit;

import com.b2international.commons.collections.BackwardListIterator;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.NullBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
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
			RepositoryRequests.branching()
				.prepareGet(branchPath)
				.build(repositoryUUID)
				.execute(ApplicationContext.getInstance().getService(IEventBus.class))
				.getSync(1000, TimeUnit.MILLISECONDS);
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
		return RepositoryRequests.branching()
				.prepareGet(IBranchPath.MAIN_BRANCH)
				.build(repositoryUUID)
				.execute(ApplicationContext.getInstance().getService(IEventBus.class))
				.getSync(1000, TimeUnit.MILLISECONDS);
	}
	
	private static IBranchPath getOrCache(final IBranchPath branchPath) {
		return BRANCH_PATH_INTERNER.intern(branchPath);
	}
}