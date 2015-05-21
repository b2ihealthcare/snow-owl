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
package com.b2international.snowowl.core.api.index;

import java.util.List;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Interface for the index update service. You can add, delete or update the index based on the newly persisted element.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 * 
 */
public interface IIndexUpdater<E extends IIndexEntry> extends IIndexService<E> {
	
	/**
	 * Indexes a new entry, or updates an existing one if present.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param indexMappingStrategy generic strategy which is responsible for mapping to documents to be indexed (may not
	 * be {@code null})
	 */
	void index(final IBranchPath branchPath, final IIndexMappingStrategy indexMappingStrategy);
	
	/**
	 * Removes an entry from the index.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param storageKey the storage key of the document
	 */
	void delete(final IBranchPath branchPath, final long storageKey);

	/**
	 * Commits all pending changes to the index.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 */
	void commit(final IBranchPath branchPath);
	
	/**
	 * Rolls back all pending changes.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 */
	void rollback(final IBranchPath branchPath);
	
	/**
	 * Calling this method will drop all of the documents in the index.
	 * <p>
	 * <b>NOTE:</b> this method will forcefully abort all merges in progress. If other threads are running
	 * {@link #optimize(IBranchPath)} or {@link #index(IBranchPath, IIndexMappingStrategy)} methods, they will receive
	 * an {@link IndexException}.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 */
	void deleteAll(final IBranchPath branchPath);
	
	/**
	 * Signals the index updater that the indexes for the specified branch path should be closed and removed from the disk.
	 * 
	 * @param branchPath
	 */
	void inactiveClose(final IBranchPath branchPath);

	/**
	 * Creates an empty commit with custom metadata set for a given branch index.
	 * 
	 * @param branchPath the branch path
	 * @param cdoBranchPath a sequence of CDO branch identifiers, starting with 0 (MAIN)
	 * @param baseTimestamp
	 */
	void reopen(final IBranchPath branchPath, final int[] cdoBranchPath, final long baseTimestamp);

	/**
	 * Returns with the UUID of the repository which is associated with the current index service. 
	 * @return the repository UUID.
	 */
	String getRepositoryUuid();
	
	/**
	 * Collects a list of absolute file paths that contain index data for the specified branch path. Note that the list
	 * may not be complete to reproduce an index by itself in case of "layered" indexes.
	 * @return the list of files which carry data for this update, or an empty list (never {@code null})
	 */
	List<String> listFiles(final IBranchPath branchPath);
}
