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
import java.util.Set;

import com.b2international.commons.options.MetadataHolder;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.events.Request;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a {@link Branch} in a terminology repository. A {@link Branch} can be uniquely identified by using its {@link #path()} and
 * {@link #baseTimestamp()} values.
 * 
 * @since 4.1
 */
public interface Branch extends MetadataHolder, Serializable {
	
	/**
	 * @since 6.16
	 */
	final class Fields {
		
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
			STATE);
			
		}
		
		/**
		 * The path of the main branch.
		 */
		static final String MAIN_PATH = RevisionBranch.MAIN_PATH;
		
		interface Expand {
			static final String CHILDREN = "children";
		}
		
		/**
		 * Segment separator in {@link Branch#path()} values.
		 */
		String SEPARATOR = RevisionBranch.SEPARATOR;
	
	/**
	 * Returns the numeric identifier associated with this branch.
	 * @return
	 */
	long branchId();
	
	/**
	 * A singleton {@link Joiner} that can be used to concatenate branch path segments into a fully usable branch path.
	 * @see #get(String...)
	 */
	Joiner BRANCH_PATH_JOINER = Joiner.on(SEPARATOR);

	/**
	 * Returns the unique path of this {@link Branch}.
	 * 
	 * @return
	 */
	String path();

	/**
	 * Returns the unique path of the parent of this {@link Branch}.
	 * 
	 * @return
	 */
	String parentPath();

	/**
	 * Returns the name of the {@link Branch}, which is often the same value as the last segment of the {@link #path()}.
	 * 
	 * @return
	 */
	String name();

	/**
	 * Returns the base timestamp value of this {@link Branch}. The base timestamp represents the time when this branch has been created, or branched
	 * of from its {@link #parent()}.
	 * 
	 * @return
	 */
	long baseTimestamp();

	/**
	 * Returns the head timestamp value for this {@link Branch}. The head timestamp represents the time when the last commit arrived on this
	 * {@link Branch}.
	 * 
	 * @return
	 */
	long headTimestamp();

	/**
	 * Returns the {@link BranchState} of this {@link Branch} compared to its {@link #parent()}. TODO document how BranchState calculation works
	 * 
	 * @return
	 * @see #state(Branch)
	 */
	RevisionBranch.BranchState state();
	
	/**
	 * @return whether this branch is deleted or not
	 */
	boolean isDeleted();

	/**
	 * @return
	 * @deprecated - use the new {@link Branch} interface instead
	 */
	IBranchPath branchPath();
	
	/**
	 * Returns all child branches of this branch (direct and indirect children both). If not expanded this method returns a <code>null</code> object.
	 * @return
	 */
	Branches getChildren();

	/**
	 * @param segments - segments to join into a usable branch path string 
	 * @return a full absolute branch path that can be used in {@link Request}s and other services  
	 */
	static String get(String...segments) {
		return BRANCH_PATH_JOINER.join(segments);
	}
	
}
