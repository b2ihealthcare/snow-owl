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

import static com.google.common.base.Preconditions.checkArgument;

import com.b2international.index.admin.Administrable;
import com.b2international.index.admin.IndexAdmin;
import com.google.common.base.Strings;

/**
 * @since 4.7
 */
public interface RevisionIndex extends Administrable<IndexAdmin> {

	/**
	 * A single character that when put at the end of a branchPath argument indicates that the search should execute the query from the perspective of the branch's base point (like ignoring all changes on the branch). 
	 */
	String BASE_REF_CHAR = "^";
	
	/**
	 * Three dot characters that represent git diff notation between two branch paths. This kind of path expression will evaluate to the latest version of a given document available in the segments selected by the range described in the expression
	 * (eg. MAIN...MAIN/A would consider revisions available on all segments of MAIN/A since it diverged from MAIN)
	 * https://git-scm.com/docs/git-diff 
	 */
	String REV_RANGE = "...";
	
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

	/**
	 * Returns a single {@link String} that can be used to query revision available on the specified compare path only.
	 * @param base
	 * @param compare
	 * @return
	 */
	static String toRevisionRange(String base, String compare) {
		checkArgument(!Strings.isNullOrEmpty(base));
		checkArgument(!Strings.isNullOrEmpty(compare));
		return String.format("%s%s%s", base, REV_RANGE, compare);
	}
	
	/**
	 * Extracts the branch paths from the given revision range path expression.
	 * @param revisionRangePath
	 * @return
	 * @throws IllegalArgumentException - if the given path cannot be parsed as a revision range path expression
	 */
	static String[] getRevisionRangePaths(final String revisionRangePath) {
		if (!isRevRangePath(revisionRangePath)) {
			throw new IllegalArgumentException(
					String.format("Diff notation ('%s') requires two full branch paths. Got %s.", RevisionIndex.REV_RANGE, revisionRangePath));
		}
		return revisionRangePath.split("\\.\\.\\.");
	}
	
	/**
	 * Returns <code>true</code> if the given branch path can evaluate to its base segments.
	 * @param branchPath
	 * @return
	 */
	static boolean isBaseRefPath(String branchPath) {
		return branchPath.endsWith(BASE_REF_CHAR);
	}
	
	/**
	 * Returns <code>true</code> if the given path is an expression that can evaluate to a revision range rather than select a single branch and its segments.
	 * @param revisionRangePath
	 * @return
	 */
	static boolean isRevRangePath(String revisionRangePath) {
		if (revisionRangePath.contains(REV_RANGE)) {
			String[] branches = revisionRangePath.split("\\.\\.\\.");
			return branches.length == 2 && !Strings.isNullOrEmpty(branches[0]) && !Strings.isNullOrEmpty(branches[1]);
		}
		return false;
	}

}
