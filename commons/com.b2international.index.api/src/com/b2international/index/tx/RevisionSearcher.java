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

import java.io.IOException;

import com.b2international.index.query.Query;

/**
 * @since 4.7
 */
public interface RevisionSearcher {

	/**
	 * Get the latest revision of an object from the index with the given type and storageKey as identifier.
	 * 
	 * @param type
	 *            - the type of the object
	 * @param branchPath
	 *            - the branchPath to restrict the loading of the revision
	 * @param storageKey
	 *            - the storage identifier of the revision
	 * @return the loaded revision object
	 */
	<T extends Revision> T get(Class<T> type, String branchPath, long storageKey);
	
	/**
	 * Execute the given query among all stored items.
	 * 
	 * @param query
	 *            - the query to execute
	 * @return - an {@link Iterable} of matching values
	 * @throws IOException
	 *             - if something goes wrong during the execution of the query
	 */
	<T> Iterable<T> search(Query<T> query) throws IOException;
	
}
