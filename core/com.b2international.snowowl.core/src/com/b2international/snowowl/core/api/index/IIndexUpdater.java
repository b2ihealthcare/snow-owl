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
	 * Creates a snapshot of the index holding information about the specified branch path's <b>parent</b>, used for starting
	 * a new branch based on existing index data.
	 * 
	 * @param targetBranchPath the branch path to take a snapshot for (may not be {@code null})
	 * @param tag flag indicating whether the snapshot is a tag operation of not. {@code true} if yes. If snapshot is a tag,
	 * then clients can make sure, that index directory will not be cleaned up if any modification has been made in it since the 
	 * tag happened. 
	 * @param shouldOptimizeIndex {@code true} if the index should be optimized otherwise {@code false}. If {@code tag} is {@code false} this argument will be 
	 * ignored.
	 */
	void snapshotFor(final IBranchPath targetBranchPath, final boolean tag, final boolean shouldOptimizeIndex);

	/**
	 * Signals the index updater that the indexes for the specified branch path should be closed and removed from the disk.
	 * 
	 * @param branchPath
	 */
	void inactiveClose(final IBranchPath branchPath);

	/**
	 * Updates the snapshot for a given branch index index.  
	 * @param branchPath the branch path.
	 * @param timestamp the timestamp representing the last modification time.
	 */
	void updateSnapshotFor(final IBranchPath branchPath, final long timestamp);

	/**
	 * Returns with the UUID of the repository which is associated with the current index service. 
	 * @return the repository UUID.
	 */
	String getRepositoryUuid();
	
	/**
	 * Purges the underlying index directory content if unused. Should be called after instantiating index updater.
	 */
	void purge();
	
	/**
	 * Collects a list of absolute file paths that contain index data for the specified branch path. Note that the list
	 * may not be complete to reproduce an index by itself in case of "layered" indexes.
	 * @return the list of files which carry data for this update, or an empty list (never {@code null})
	 */
	List<String> listFiles(final IBranchPath branchPath);
}