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
package com.b2international.snowowl.api.admin;

import java.util.List;

import com.b2international.snowowl.api.admin.exception.SnapshotCreationException;
import com.b2international.snowowl.api.admin.exception.SnapshotListingException;
import com.b2international.snowowl.api.admin.exception.SnapshotReleaseException;
import com.b2international.snowowl.api.admin.exception.SupportingIndexNotFoundException;
import com.b2international.snowowl.api.admin.exception.SupportingIndexSnapshotNotFoundException;

/**
 * An interface definition for the Supporting Index Service.
 * <p>
 * The following operations are supported:
 * <ul>
 * <li>{@link #getSupportingIndexIds() <em>Retrieve all supporting index identifiers</em>}
 * <li>{@link #getSupportingIndexSnapshotIds(String) <em>Retrieve all snapshot identifiers for an index</em>}
 * <li>{@link #createSupportingIndexSnapshot(String) <em>Create snapshot for an index</em>}
 * <li>{@link #getSupportingIndexFiles(String, String) <em>List contents of an index snapshot</em>}
 * <li>{@link #releaseSupportingIndexSnapshot(String, String) <em>Release resources associated with an index snapshot</em>}
 * </ul>
 * 
 */
public interface ISupportingIndexService {

	/**
	 * Retrieves a list of identifiers for indexes which are storing supplementary information (eg. task state, bookmarks, etc.)
	 * 
	 * @return a list of supporting index service identifiers, in alphabetical order (never {@code null})
	 */
	List<String> getSupportingIndexIds();

	/**
	 * Retrieves a list of snapshot identifiers for the specified supporting index.
	 * 
	 * @param indexId the identifier of the supporting index service (may not be {@code null})
	 * @return a list of snapshot identifiers, from newest to oldest (never {@code null})
	 * @throws SupportingIndexNotFoundException if the specified service identifier does not correspond to any registered index service
	 */
	List<String> getSupportingIndexSnapshotIds(String indexId);

	/**
	 * Creates a new, consistent snapshot for the specified supporting index.
	 * 
	 * @param indexId the identifier of the supporting index service (may not be {@code null})
	 * @return the identifier of the created snapshot
	 * @throws SupportingIndexNotFoundException if the specified service identifier does not correspond to any registered index service
	 * @throws SnapshotCreationException if snapshot creation fails for some reason
	 */
	String createSupportingIndexSnapshot(String indexId);

	/**
	 * Retrieves a list of relative paths to files which make up the given consistent snapshot of a supporting index.
	 * 
	 * @param indexId the identifier of the supporting index service (may not be {@code null})
	 * @param snapshotId the identifier of the snapshot (may not be {@code null})
	 * @return a list of relative paths to files which make up the index snapshot, in alphabetical order (never {@code null})
	 * @throws SupportingIndexNotFoundException if the specified service identifier does not correspond to any registered index service
	 * @throws SupportingIndexSnapshotNotFoundException if the specified snapshot identifier does not correspond to any currently present snapshot
	 * @throws SnapshotListingException if listing snapshot contents fails for some reason
	 */
	List<String> getSupportingIndexFiles(String indexId, String snapshotId);

	/**
	 * Releases an existing, consistent snapshot of the specified supporting index.
	 * 
	 * @param indexId the identifier of the supporting index service (may not be {@code null})
	 * @param snapshotId the identifier of the snapshot to be removed (may not be {@code null})
	 * @throws SupportingIndexNotFoundException if the specified service identifier does not correspond to any registered index service
	 * @throws SupportingIndexSnapshotNotFoundException if the specified snapshot identifier does not correspond to any currently present snapshot
	 * @throws SnapshotReleaseException if releasing the snapshot fails for some reason
	 */
	void releaseSupportingIndexSnapshot(String indexId, String snapshotId);
}