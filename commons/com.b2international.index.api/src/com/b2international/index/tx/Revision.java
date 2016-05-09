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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import com.google.common.collect.ImmutableList;


/**
 * @since 4.7
 */
public abstract class Revision {

	public static final String ADD_REPLACED_BY_ENTRY_SCRIPT_KEY = "addReplacedByEntryScript";
	public static final String ADD_REPLACED_BY_ENTRY_SCRIPT = "ctx._source.replacedIns += [branchPath: branch, commitTimestamp: timestamp]";
	public static final String STORAGE_KEY = "storageKey";
	public static final String BRANCH_PATH = "branchPath";
	public static final String COMMIT_TIMESTAMP = "commitTimestamp";

	private long storageKey;
	private long commitTimestamp;
	private String branchPath;
	private Collection<ReplacedIn> replacedIns = newHashSet();
	
	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public void setCommitTimestamp(long createdTimestamp) {
		this.commitTimestamp = createdTimestamp;
	}
	
	public void setStorageKey(long storageKey) {
		this.storageKey = storageKey;
	}
	
	public void setReplacedIns(Collection<ReplacedIn> replacedIns) {
		this.replacedIns = replacedIns;
	}
	
	public long getStorageKey() {
		return storageKey;
	}
	
	public Collection<ReplacedIn> getReplacedIns() {
		return ImmutableList.copyOf(replacedIns);
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public long getCommitTimestamp() {
		return commitTimestamp;
	}

	public static FilterBuilder createBranchFilter(RevisionBranch branch) {
		return FilterBuilders.andFilter(branchRevisionFilter(branch), notReplacedInFilter(branch));
	}

	private static FilterBuilder notReplacedInFilter(RevisionBranch branch) {
		return notFilter(nestedFilter("replacedIns", createBranchSegmentFilter(branch, new ReplacedSegmentFilterBuilder())));
	}

	private static FilterBuilder branchRevisionFilter(RevisionBranch branch) {
		return createBranchSegmentFilter(branchPath, new RevisionSegmentFilterBuilder());
	}

	private static FilterBuilder createBranchSegmentFilter(String branchPath, SegmentFilterBuilder builder) {
		final String[] segments = branchPath.split(Branch.SEPARATOR);
		if (segments.length > 1) {
			final OrFilterBuilder or = orFilter();
			String prev = "";
			for (int i = 0; i < segments.length; i++) {
				final String segment = segments[i];
				// we need the current segment + prevSegment to make it full path and the next one to restrict head timestamp on current based on base of the next one
				String current = "";
				String next = null;
				if (!Branch.MAIN_PATH.equals(segment)) {
					current = prev.concat(Branch.SEPARATOR);
				}
				// if not the last segment, compute next one
				current = current.concat(segment);
				if (!segments[segments.length - 1].equals(segment)) {
					if (!current.endsWith(Branch.SEPARATOR)) {
						next = current.concat(Branch.SEPARATOR);
					}
					next = next.concat(segments[i+1]);
				}
				or.add(builder.createSegmentFilter(current, next));
				prev = current;
			}
			return or;
		} else {
			return builder.createSegmentFilter(branchPath, null);
		}
	}

	private static interface SegmentFilterBuilder {
		
		FilterBuilder createSegmentFilter(RevisionBranch parent, RevisionBranch child);
		
	}
	
	private static class RevisionSegmentFilterBuilder implements SegmentFilterBuilder {

		@Override
		public FilterBuilder createSegmentFilter(RevisionBranch parent, RevisionBranch child) {
			final FilterBuilder currentBranchFilter = termFilter(Revision.BRANCH_PATH, parent.path());
			final FilterBuilder commitTimestampFilter = child == null ? timestampFilter(parent) : timestampFilter(parent, child);
			return andFilter(currentBranchFilter, commitTimestampFilter);
		}
		
		/*restricts given branchPath's HEAD to baseTimestamp of child*/
		private static FilterBuilder timestampFilter(RevisionBranch parent, RevisionBranch child) {
			return timestampFilter(parent.baseTimestamp(), child.baseTimestamp());
		}
		
		private static FilterBuilder timestampFilter(RevisionBranch branch) {
			return timestampFilter(branch.baseTimestamp(), branch.headTimestamp());
		}
		
		private static FilterBuilder timestampFilter(long from, long to) {
			return rangeFilter(Revision.COMMIT_TIMESTAMP).gte(from).lte(to);
		}
		
	}
	
	private static class ReplacedSegmentFilterBuilder implements SegmentFilterBuilder {

		@Override
		public FilterBuilder createSegmentFilter(RevisionBranch parent, RevisionBranch child) {
			final long maxHead = child != null ? child.baseTimestamp() : Long.MAX_VALUE;
			final long head = Math.min(maxHead, parent.headTimestamp());
			return andFilter(termFilter("replacedIns."+Revision.BRANCH_PATH, parent.path()), rangeFilter("replacedIns."+Revision.COMMIT_TIMESTAMP).gte(0L).lte(head));
		}
		
	}
	
}