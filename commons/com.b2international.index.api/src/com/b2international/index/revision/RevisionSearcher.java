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
package com.b2international.index.revision;

import java.io.IOException;

import com.b2international.index.Hits;
import com.b2international.index.DocSearcher;
import com.b2international.index.Searcher;
import com.b2international.index.query.Query;

/**
 * @since 4.7
 */
public interface RevisionSearcher extends Searcher {

	/**
	 * Returns the searcher instance used by this revision searcher.
	 * 
	 * @return
	 */
	DocSearcher searcher();

	/**
	 * Get the latest revision of an object from the index with the given type and storageKey as identifier.
	 * 
	 * @param type
	 *            - the type of the object
	 * @param storageKey
	 *            - the storage identifier of the revision
	 * @return the loaded revision object
	 * @throws IOException
	 */
	<T extends Revision> T get(Class<T> type, long storageKey) throws IOException;

	/**
	 * Gets a bunch of revision for the given type and storage key collection.
	 * 
	 * @param type
	 *            - the type of the object
	 * @param storageKeys
	 *            - the storage identifiers of the revisions
	 * @return the loaded revision objects
	 * @throws IOException
	 */
	<T extends Revision> Iterable<T> get(Class<T> type, Iterable<Long> storageKeys) throws IOException;

	/**
	 * Execute the given query among all stored items.
	 * 
	 * @param query
	 *            - the query to execute
	 * @return - an {@link Iterable} of matching values
	 * @throws IOException
	 *             - if something goes wrong during the execution of the query
	 */
	<T> Hits<T> search(Query<T> query) throws IOException;

	/**
	 * @return the branch where this {@link RevisionSearcher} will execute all read operations
	 */
	String branch();

}
