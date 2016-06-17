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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import com.b2international.index.WithId;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.google.common.collect.ImmutableList;


/**
 * @since 4.7
 */
public abstract class Revision implements WithId {

//	public static final String ADD_REPLACED_BY_ENTRY_SCRIPT_KEY = "addReplacedByEntryScript";
//	public static final String ADD_REPLACED_BY_ENTRY_SCRIPT = "ctx._source.replacedIns += [branchPath: branch, commitTimestamp: timestamp]";
	public static final String STORAGE_KEY = "storageKey";
	public static final String BRANCH_PATH = "branchPath";
	public static final String COMMIT_TIMESTAMP = "commitTimestamp";

	// move the following fields up to a abstract doc???
	private String _id;
	
	private long storageKey;
	private long commitTimestamp;
	private String branchPath;
	
	private Collection<ReplacedIn> replacedIns = Collections.emptySet();
	
	@Override
	public final void set_id(String _id) {
		this._id = _id;
	}
	
	@Override
	public final String _id() {
		return checkNotNull(_id);
	}
	
	protected final void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	protected final void setCommitTimestamp(long createdTimestamp) {
		this.commitTimestamp = createdTimestamp;
	}
	
	protected final void setStorageKey(long storageKey) {
		this.storageKey = storageKey;
	}
	
	protected final void setReplacedIns(Collection<ReplacedIn> replacedIns) {
		this.replacedIns = replacedIns;
	}
	
	public final long getStorageKey() {
		return storageKey;
	}
	
	public final Collection<ReplacedIn> getReplacedIns() {
		return ImmutableList.copyOf(replacedIns);
	}

	public final String getBranchPath() {
		return branchPath;
	}
	
	public final long getCommitTimestamp() {
		return commitTimestamp;
	}

	public static Expression branchFilter(RevisionBranch branch) {
		return Expressions.builder()
				.must(branchRevisionFilter(branch))
				.mustNot(replacedInFilter(branch))
				.build();
	}

	public static Expression replacedInFilter(RevisionBranch branch) {
		return Expressions.nestedMatch("replacedIns", createBranchSegmentFilter(branch, new ReplacedSegmentFilterBuilder()));
	}

	public static Expression branchRevisionFilter(RevisionBranch branch) {
		return createBranchSegmentFilter(branch, new RevisionSegmentFilterBuilder());
	}

	private static Expression createBranchSegmentFilter(RevisionBranch branch, SegmentFilterBuilder builder) {
		if (branch.parent() != null) {
			final ExpressionBuilder or = Expressions.builder();
			// navigate up the branch hierarchy and add should clauses to the expression tree
			// revisions can match any branch segment but must match at least one path
			RevisionBranch child = null;
			for (RevisionBranch currentParent = branch; currentParent != null; currentParent = currentParent.parent()) {
				or.should(builder.createSegmentFilter(currentParent, child));
				child = currentParent;
			}
			return or.build();
		} else {
			return builder.createSegmentFilter(branch, null);
		}
	}

	private static interface SegmentFilterBuilder {
		
		Expression createSegmentFilter(RevisionBranch parent, RevisionBranch child);
		
	}
	
	private static class RevisionSegmentFilterBuilder implements SegmentFilterBuilder {

		@Override
		public Expression createSegmentFilter(RevisionBranch parent, RevisionBranch child) {
			final Expression currentBranchFilter = Expressions.exactMatch(Revision.BRANCH_PATH, parent.path());
			final Expression commitTimestampFilter = child == null ? timestampFilter(parent) : timestampFilter(parent, child);
			return Expressions.builder()
					.must(currentBranchFilter)
					.must(commitTimestampFilter)
					.build();
		}
		
		/*restricts given branchPath's HEAD to baseTimestamp of child*/
		private static Expression timestampFilter(RevisionBranch parent, RevisionBranch child) {
			return timestampFilter(parent.baseTimestamp(), child.baseTimestamp());
		}
		
		private static Expression timestampFilter(RevisionBranch branch) {
			return timestampFilter(branch.baseTimestamp(), branch.headTimestamp());
		}
		
		private static Expression timestampFilter(long from, long to) {
			return Expressions.matchRange(Revision.COMMIT_TIMESTAMP, from, to);
		}
		
	}
	
	private static class ReplacedSegmentFilterBuilder implements SegmentFilterBuilder {

		@Override
		public Expression createSegmentFilter(RevisionBranch parent, RevisionBranch child) {
			final long maxHead = child != null ? child.baseTimestamp() : Long.MAX_VALUE;
			final long head = Math.min(maxHead, parent.headTimestamp());
			return Expressions.builder()
					.must(Expressions.exactMatch(Revision.BRANCH_PATH, parent.path()))
					.must(Expressions.matchRange(Revision.COMMIT_TIMESTAMP, 0L, head))
					.build();
		}
		
	}

}