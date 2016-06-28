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
import java.util.Set;

import com.b2international.index.WithId;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.google.common.base.Objects;


/**
 * @since 4.7
 */
public abstract class Revision implements WithId {

	public static final String STORAGE_KEY = "storageKey";
	public static final String BRANCH_PATH = "branchPath";
	public static final String COMMIT_TIMESTAMP = "commitTimestamp";
	public static final String SEGMENT_ID = "segmentId";
	public static final String REPLACED_INS = "replacedIns";

	private String _id;
	
	private long storageKey;
	private long commitTimestamp;
	private String branchPath;
	
	private int segmentId;
	private Collection<Integer> replacedIns = Collections.emptySet();
	
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
	
	protected final void setReplacedIns(Collection<Integer> replacedIns) {
		this.replacedIns = replacedIns;
	}
	
	protected final void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	
	public final int getSegmentId() {
		return segmentId;
	}
	
	public final long getStorageKey() {
		return storageKey;
	}
	
	public final Collection<Integer> getReplacedIns() {
		return replacedIns;
	}

	public final String getBranchPath() {
		return branchPath;
	}
	
	public final long getCommitTimestamp() {
		return commitTimestamp;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("_id", _id)
				.add(STORAGE_KEY, storageKey)
				.add(Revision.BRANCH_PATH, branchPath)
				.add(Revision.COMMIT_TIMESTAMP, commitTimestamp)
				.add(Revision.SEGMENT_ID, segmentId)
				.add(Revision.REPLACED_INS, replacedIns)
				.toString();
	}

	public static Expression branchFilter(RevisionBranch branch) {
		return Expressions.builder()
				.must(segmentIdFilter(branch.segments()))
				.mustNot(replacedInFilter(branch.segments()))
				.build();
	}

	private static Expression replacedInFilter(Set<Integer> segments) {
		return Expressions.matchAnyInt(Revision.REPLACED_INS, segments);
	}

	private static Expression segmentIdFilter(Set<Integer> segments) {
		return Expressions.matchAnyInt(Revision.SEGMENT_ID, segments);
	}

}