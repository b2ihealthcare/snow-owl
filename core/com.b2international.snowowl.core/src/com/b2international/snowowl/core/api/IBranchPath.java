/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.api;

import java.io.File;
import java.io.Serializable;

import com.b2international.snowowl.core.branch.BranchPathUtils;

/**
 * Branch path representation.
 * <p>
 * <b>NOTE:&nbsp;</b> This class is not intended to be neither implemented not extended by clients. Use the utility class BranchPathUtils to obtain
 * actual instances. 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IBranchPath extends Serializable {

	/**
	 * The path separator. {@value}.
	 */
	final String SEPARATOR = "/";
	
	/**
	 * The path separator character. {@value}.
	 */
	final char SEPARATOR_CHAR = '/';
	
	/**
	 * The ID or name of the MAIN branch. Value: {@value}.
	 */
	final String MAIN_BRANCH = "MAIN"; 
	
	/**
	 * Empty path.
	 */
	final String EMPTY_PATH = "";
	
	/**
	 * Returns with the fully qualified path uniquely identifying a branch. The given path represents the hierarchy among the branches.
	 * All segments of the path represent the name of a branch. 
	 * <pre>
	 * MAIN&#x2215;VERSION_1&#x2215;TASK_300
	 * </pre> 
	 * @return the branch path.
	 * @see #SEPARATOR
	 */
	String getPath();
	
	/**
	 * Returns with the parent {@link IBranchPath branch path} instance. As the branch path can be stored in a taxonomy 
	 * always exactly one parent branch can be associated with a branch. The MAIN branch is exceptional as it does not have a parent.
	 * @return the path of the parent branch.
	 */
	String getParentPath();
	
	/**
	 * Returns a string representation of this branch path which uses the platform-dependent path separator defined by {@link File}.
	 * This method is like {@link #getPath()} except that the latter always uses the same separator ({@link #SEPARATOR}) regardless of platform.
	 * <p>
	 * This string is suitable for passing to {@link File#File(String)}.
	 * </p>
	 * @return a platform-dependent string representation of this branch path.
	 */
	String getOsPath();
	
	/**
	 * Returns a string representation of this parent branch path which uses the platform-dependent path separator defined by {@link File}.
	 * This method is like {@link #getParentPath()} except that the latter always uses the same separator ({@link #SEPARATOR}) regardless of platform.
	 * <p>
	 * This string is suitable for passing to {@link File#File(String)}.
	 * </p>
	 * @return a platform-dependent string representation of this parent branch path.
	 */
	String getOsParentPath();
	
	/**
	 * Returns with the parent branch path of the current branch path.
	 * @return the parent branch path.
	 */
	IBranchPath getParent();
	
	/**
	 * Returns with the last segment of the current branch path.
	 * <p>If the branch path is:
	 * <pre>
	 * MAIN&#x2215;VERSION_1&#x2215;TASK_300
	 * </pre>
	 * then this method returns with the following:
	 * <pre>
	 * TASK_300
	 * </pre>
	 * This method never returns with {@code null}.
	 * @return the last segment of the current branch path.
	 */
	String lastSegment();

	/**
	 * Helper method to create a child {@link IBranchPath} instance using this instance.
	 * 
	 * @param segment
	 * @return
	 */
	default IBranchPath child(String segment) {
		return BranchPathUtils.createPath(this, segment);
	}
	
	/**
	 * Helper method to create a sibling {@link IBranchPath} instance using this instance's {@link #getParent()}.
	 * @param segment
	 * @return
	 */
	default IBranchPath sibling(String segment) {
		return getParent().child(segment);
	}

}