/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.tx;

import java.util.Collection;

import com.b2international.index.Searchable;
import com.b2international.index.admin.Administrable;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.mapping.MappingProvider;
import com.b2international.index.tx.query.TransactionalQueryBuilder;

/**
 * @since 4.7
 */
public interface TransactionalIndex extends Administrable<IndexAdmin>, MappingProvider, Searchable {

	@Override
	TransactionalQueryBuilder query();

	/**
	 * Adds a revision to the transactional index.
	 * 
	 * @param commitId
	 *            - the commitId this revision belongs to
	 * @param revision
	 *            - the revision
	 */
	void addRevision(int commitId, Revision revision);

	/**
	 * Loads the latest revision of an object from the index with the given type and storageKey as identifier.
	 * 
	 * @param type
	 *            - the type of the object
	 * @param branchPath
	 *            - the branchPath to restrict the loading of the revision
	 * @param storageKey
	 *            - the storage identifier of the revision
	 * @return the loaded revision object
	 */
	<T extends Revision> T loadRevision(Class<T> type, String branchPath, long storageKey);

	/**
	 * Indexes a commit group as parent for all previously added revision (with the given commitId) available for search.
	 * 
	 * @param commitId
	 * @param commitTimestamp
	 * @param branchPath
	 * @param commitMessage
	 */
	void commit(int commitId, long commitTimestamp, String branchPath, String commitMessage);

	/**
	 * Opens a new IndexTransaction with the given id and timestamp.
	 * 
	 * @param commitId
	 * @param commitTimestamp
	 * @param branchPath
	 * @return
	 */
	IndexTransaction transaction(int commitId, long commitTimestamp, String branchPath);

	/**
	 * Update a set of revision's {@link ReplacedIn} entries for the given branchPath with the given commitTimestamp to indicate that a newer revision
	 * is visible from that branch.
	 * 
	 * @param type
	 * @param storageKeys
	 * @param branchPath
	 * @param commitTimestamp
	 */
	<T extends Revision> void updateRevisions(int commitId, String type, Collection<Long> storageKeys, String branchPath, long commitTimestamp);

}