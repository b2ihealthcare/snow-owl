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
package com.b2international.snowowl.datastore.index;

import org.apache.lucene.search.IndexSearcher;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Allows executing consistent index searches in a callback object.
 * 
 * @since 4.5
 */
public interface IndexTransactionProvider {

	/**
	 * Opens an {@link IndexSearcher} on the given branch, and calls {@link IndexRead#execute(IndexSearcher)} on the callback
	 * object.
	 * <p>
	 * Resources associated with the {@code IndexSearcher} are released when the method returns, so {@link IndexRead}
	 * implementations should not store or otherwise leak the reference to the searcher.
	 * 
	 * @param <T> the return type of the callback object
	 * @param branchPath the branch to run the search on
	 * @param read the callback object to pass the {@link IndexSearcher} to
	 * @return the value returned from the callback
	 */
	<T> T executeReadTransaction(IBranchPath branchPath, IndexRead<T> read);

}
