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

import static com.b2international.index.query.Expressions.match;
import static com.b2international.index.query.Expressions.matchAnyInt;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.index.Script;
import com.b2international.index.WithHash;
import com.b2international.index.WithId;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;


/**
 * @since 4.7
 */
@Script(name=Revision.UPDATE_REPLACED_INS, script=""
		+ "if (!ctx._source.replacedIns.contains(params.segmentId)) {"
		+ "    ctx._source.replacedIns.add(params.segmentId);"
		+ "}")
public abstract class Revision implements WithId {

	public static class Views {
		
		public static final class StorageKeyAndHash implements WithHash {
			private final long storageKey;
			private String _hash;

			@JsonCreator
			public StorageKeyAndHash(@JsonProperty(Revision.STORAGE_KEY) long storageKey, @JsonProperty(DocumentMapping._HASH) String _hash) {
				this.storageKey = storageKey;
				this._hash = _hash;
			}
			
			public long getStorageKey() {
				return storageKey;
			}
			
			@Override
			public String _hash() {
				return _hash;
			}
			
			@Override
			public void set_hash(String _hash) {
				this._hash = _hash;
			}
			
		}
	}
	
	public static final String STORAGE_KEY = "storageKey";
	public static final String BRANCH_PATH = "branchPath";
	public static final String COMMIT_TIMESTAMP = "commitTimestamp";
	public static final String SEGMENT_ID = "segmentId";
	public static final String REPLACED_INS = "replacedIns";
	
	// scripts
	public static final String UPDATE_REPLACED_INS = "updateReplacedIns";
	
	/**
	 * Revision fields that should not be part of any hash value.
	 */
	public static final Set<String> REV_FIELDS = ImmutableSet.of(BRANCH_PATH, COMMIT_TIMESTAMP, SEGMENT_ID, REPLACED_INS);

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
	@JsonIgnore
	public final String _id() {
		checkState(_id != null, "Partial documents do not have document IDs. Load the entire document or extract the required data from this object.");
		return _id;
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
	public final String toString() {
		return doToString().toString();
	}
	
	protected ToStringHelper doToString() {
		return Objects.toStringHelper(this)
				.add(DocumentMapping._ID, _id)
				.add(STORAGE_KEY, storageKey)
				.add(Revision.BRANCH_PATH, branchPath)
				.add(Revision.COMMIT_TIMESTAMP, commitTimestamp)
				.add(Revision.SEGMENT_ID, segmentId)
				.add(Revision.REPLACED_INS, replacedIns);
	}

	public static Expression branchFilter(RevisionBranch branch) {
		return branchSegmentFilter(branch.segments());
	}

	public static Expression branchSegmentFilter(final Integer segment) {
		return Expressions.builder()
				.filter(match(Revision.SEGMENT_ID, segment))
				.mustNot(match(Revision.REPLACED_INS, segment))
				.build();
	}
	
	public static Expression branchSegmentFilter(final Set<Integer> segments) {
		return Expressions.builder()
				.filter(matchAnyInt(Revision.SEGMENT_ID, segments))
				.mustNot(matchAnyInt(Revision.REPLACED_INS, segments))
				.build();
	}

}