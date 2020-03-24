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
package com.b2international.snowowl.core.branch;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.NullBranchPath;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * {@link IBranchPath} implementation. A branch path uniquely identifies a branch among the branch taxonomy.
 * <p>
 * <b>NOTE:&nbsp;</b>This class is intentionally has default visibility since all instances are created with factory methods 
 * and cached (see: {@link BranchPathUtils}) to avoid deadlocks when synchronizing on it. 
 * @noextend This interface is not intended to be extended by clients.
 */
/*default*/ class BranchPath implements IBranchPath, Serializable {

	private static final long serialVersionUID = 6243933060885036510L;

	private final String path;
	private final String parentPath;
	
	/*default*/ BranchPath(final String path) {
		this.path = trimTrailingSeparator(Preconditions.checkNotNull(path, "Branch path cannot be null."));
		this.parentPath = initParentPath(this.path);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getParentPath()
	 */
	@Override
	public String getParentPath() {
		return parentPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getParent()
	 */
	@Override
	public IBranchPath getParent() {
		return StringUtils.isEmpty(parentPath) ? NullBranchPath.INSTANCE : BranchPathUtils.createPath(parentPath);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getOsPath()
	 */
	@Override
	public String getOsPath() {
		return Joiner.on(File.separatorChar).join(Splitter.on(IBranchPath.SEPARATOR_CHAR).trimResults().split(path));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#getOsParentPath()
	 */
	@Override
	public String getOsParentPath() {
		return Joiner.on(File.separatorChar).join(Splitter.on(IBranchPath.SEPARATOR_CHAR).trimResults().split(parentPath));
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IBranchPath#lastSegment()
	 */
	@Override
	public String lastSegment() {
		if (BranchPathUtils.isMain(this)) {
			return IBranchPath.MAIN_BRANCH;
		}
		
		if (StringUtils.isEmpty(path)) {
			return IBranchPath.EMPTY_PATH;
		}
		
		final Splitter splitter = Splitter.on(IBranchPath.SEPARATOR_CHAR).omitEmptyStrings().trimResults();
		final Iterable<String> segments = splitter.split(path);

		return Iterables.getLast(segments, IBranchPath.EMPTY_PATH);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getPath();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BranchPath other = (BranchPath) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
	/*initialize the parent branch path instance*/
	private static String initParentPath(String path) {
		if (BranchPathUtils.isMain(path)) { //if path is main, return with the empty parent
			return IBranchPath.EMPTY_PATH;
		}
		
		if (StringUtils.isEmpty(path)) {
			return NullBranchPath.INSTANCE.getPath();
		}
		
		checkArgument(path.startsWith(IBranchPath.MAIN_BRANCH), "Path '%s' should start with MAIN", path);
		//if the path has a trailing separator, cut it
		path = trimTrailingSeparator(path);
		
		//get last separator index, should contain at least one
		
		int index = path.lastIndexOf(IBranchPath.SEPARATOR);
		
		if (-1 == index) {
			throw new IllegalArgumentException("Error while specifying parent path for '" + path + "'.");
		}
		
		return path.substring(0, index).trim();
	}
	
	/*trims the trailing separator character from the end of the path.*/
	private static String trimTrailingSeparator(final String path) {
		
		String $ = Strings.nullToEmpty(path);
		
		//if the path has a trailing separator, cut it
		if (IBranchPath.SEPARATOR_CHAR == $.charAt($.length() - 1)) {
			$ = $.substring(0, $.length() - 1);
		}
	
		return $;
		
	}

	// Run instances read from a serialized form through BranchPathUtils 
	private Object readResolve() throws ObjectStreamException {
		return BranchPathUtils.createPath(path);
	}
}