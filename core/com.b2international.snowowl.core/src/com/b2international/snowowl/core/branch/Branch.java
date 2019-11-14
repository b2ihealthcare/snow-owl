/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataHolder;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.index.revision.RevisionBranchMergeSource;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a {@link Branch} in a terminology repository. A {@link Branch} can be uniquely identified by using its {@link #path()} and
 * {@link #baseTimestamp()} values.
 * 
 * @since 4.1
 */
public final class Branch implements MetadataHolder, Serializable {

	private static final long serialVersionUID = 1305843991971921932L;

	/**
	 * The path of the main branch.
	 */
	public static final String MAIN_PATH = RevisionBranch.MAIN_PATH;
	
	/**
	 * Segment separator in {@link Branch#path()} values.
	 */
	public static final String SEPARATOR = RevisionBranch.SEPARATOR;
	
	/**
	 * A singleton {@link Joiner} that can be used to concatenate branch path segments into a fully usable branch path.
	 * @see #get(String...)
	 */
	public static final Joiner BRANCH_PATH_JOINER = Joiner.on(SEPARATOR);
	
	/**
	 * @since 6.16
	 */
	public static final class Fields {
		
		public static final String PATH = "path";
		public static final String PARENT_PATH = "parentPath";
		public static final String NAME = "name";
		public static final String BASE_TIMESTAMP = "baseTimestamp";
		public static final String HEAD_TIMESTAMP = "headTimestamp";
		public static final String STATE = "state";
		public static final Set<String> ALL = ImmutableSet.of(
			PATH,
			PARENT_PATH,
			NAME,
			BASE_TIMESTAMP,
			HEAD_TIMESTAMP,
			STATE
		);
	}
	
	public static interface Expand {
		public static final String CHILDREN = "children";
	}
	
	private final long branchId;
	private final boolean isDeleted;
	private final Metadata metadata;
	private final String name;
	private final String parentPath;
	private final long baseTimestamp;
	private final long headTimestamp;
	private final BranchState state;
	private final IBranchPath branchPath;
	private final List<RevisionBranchMergeSource> mergeSources;
	
	private Branches children;
	
	public Branch(RevisionBranch branch, BranchState state, IBranchPath branchPath, List<RevisionBranchMergeSource> mergeSources) {
		this(branch.getId(), branch.getName(), branch.getParentPath(), branch.getBaseTimestamp(), branch.getHeadTimestamp(), branch.isDeleted(), branch.metadata(), state, branchPath, mergeSources);
	}
	
	private Branch(long branchId, String name, String parentPath, long baseTimestamp, long headTimestamp, boolean isDeleted, Metadata metadata, BranchState state, IBranchPath branchPath, List<RevisionBranchMergeSource> mergeSources) {
		this.branchId = branchId;
		this.name = name;
		this.parentPath = parentPath;
		this.baseTimestamp = baseTimestamp;
		this.headTimestamp = headTimestamp;
		this.state = state;
		this.isDeleted = isDeleted;
		this.metadata = metadata;
		this.branchPath = branchPath;
		this.mergeSources = Collections3.toImmutableList(mergeSources);
	}
	
	/**
	 * @return the numeric identifier associated with this branch.
	 */
	public long branchId() {
		return branchId;
	}

	/**
	 * @return whether this branch is deleted or not
	 */
	@JsonProperty
	public boolean isDeleted() {
		return isDeleted;
	}

	@JsonProperty
	@Override
	public Metadata metadata() {
		return metadata;
	}

	/**
	 * @return the unique path of this {@link Branch}.
	 */
	@JsonProperty
	public String path() {
		return Strings.isNullOrEmpty(parentPath) ? name : parentPath + Branch.SEPARATOR + name;
	}

	/**
	 * @return the unique path of the parent of this {@link Branch}.
	 */
	@JsonProperty
	public String parentPath() {
		return parentPath;
	}

	/**
	 * @return the name of the {@link Branch}, which is often the same value as the last segment of the {@link #path()}.
	 */
	@JsonProperty
	public String name() {
		return name;
	}

	/**
	 * Returns the base timestamp value of this {@link Branch}. The base timestamp represents the time when this branch has been created, or branched
	 * of from its {@link #parent()}.
	 * 
	 * @return
	 */
	@JsonProperty
	public long baseTimestamp() {
		return baseTimestamp;
	}

	/**
	 * Returns the head timestamp value for this {@link Branch}. The head timestamp represents the time when the last commit arrived on this
	 * {@link Branch}.
	 * 
	 * @return
	 */
	@JsonProperty
	public long headTimestamp() {
		return headTimestamp;
	}

	/**
	 * Returns the {@link BranchState} of this {@link Branch} compared to its {@link #parent()}. TODO document how BranchState calculation works
	 * 
	 * @return
	 * @see #state(Branch)
	 */
	@JsonProperty
	public BranchState state() {
		return state;
	}

	/**
	 * @return
	 * @deprecated - backward compatible API support, use {@link #path()} instead.
	 */
	public IBranchPath branchPath() {
		return branchPath;
	}

	/**
	 * Returns all child branches of this branch (direct and indirect children both). If not expanded this method returns a <code>null</code> object.
	 * @return
	 */
	public Branches getChildren() {
		return children;
	}
	
	public void setChildren(Branches children) {
		this.children = children;
	}
	
	@JsonProperty
	public List<RevisionBranchMergeSource> mergeSources() {
		return mergeSources;
	}
	
	/**
	 * @param segments - segments to join into a usable branch path string 
	 * @return a full absolute branch path that can be used in {@link Request}s and other services  
	 */
	public static final String get(String...segments) {
		return BRANCH_PATH_JOINER.join(segments);
	}

}
