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
package com.b2international.index.revision;

import com.b2international.index.admin.Administrable;
import com.b2international.index.admin.IndexAdmin;

/**
 * @since 4.7
 */
public interface RevisionIndex extends Administrable<IndexAdmin> {

	/**
	 * A single character that when put at the end of a branchPath argument indicates that the search should execute the query from the perspective of the branch's base point (like ignoring all changes on the branch). 
	 */
	String BASE_REF_CHAR = "^";
	
	/**
	 * Returns the name of the index.
	 * 
	 * @return
	 */
	String name();

	/**
	 * Reads from the index via a {@link RevisionIndexRead read transaction}.
	 * 
	 * @param branchPath
	 * @param read
	 * @return
	 */
	<T> T read(String branchPath, RevisionIndexRead<T> read);

	/**
	 * Writes to this index via an {@link RevisionIndexWrite write transaction}.
	 * 
	 * @param branchPath
	 *            - put all modifications to this branch
	 * @param commitTimestamp
	 *            - all modifications should appear with this timestamp
	 * @param write
	 *            - transactional write operation
	 * @return
	 */
	<T> T write(String branchPath, long commitTimestamp, RevisionIndexWrite<T> write);

	/**
	 * Purges selected revisions from the given branch in this index. When the purge completes only document revisions applicable for the selected
	 * purge strategy remain in each segments of the given branch. This effectively reduces the amount of documents in a single segment to only one.
	 * 
	 * @param branchPath
	 *            - the branch to purge
	 * @param purge
	 *            - the type of purge to execute
	 */
	void purge(String branchPath, Purge purge);

	/**
	 * Compares the given branch with its parent branch as base. The {@link RevisionCompare} response will contain the difference from the branch
	 * compared to its parent. The result might contain new, changed, deleted revision storage keys of any revision.
	 * 
	 * @param branch
	 * @return
	 */
	RevisionCompare compare(String branch);

	/**
	 * 
	 * @param branch
	 * @param limit
	 * @return
	 */
	RevisionCompare compare(String branch, int limit);
	
	/**
	 * Compares the given compare branch with the given base branch. The {@link RevisionCompare} response will contain the difference from the compare
	 * branch compared to the base. The result might contain new, changed, deleted revision storage keys of any revision.
	 * 
	 * @param baseBranch
	 * @param compareBranch
	 * @return
	 */
	RevisionCompare compare(String baseBranch, String compareBranch);
	
	/**
	 * 
	 * @param baseBranch
	 * @param compareBranch
	 * @param limit
	 * @return
	 */
	RevisionCompare compare(String baseBranch, String compareBranch, int limit);
}
