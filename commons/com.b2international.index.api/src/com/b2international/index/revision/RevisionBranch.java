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

import java.util.Objects;

/**
 * @since 4.7
 */
public class RevisionBranch {
	
	public static final String MAIN_PATH = "MAIN";
	public static final String SEPARATOR = "/";
	
	private final RevisionBranch parent;
	private final String path;
	private final long baseTimestamp;
	private final long headTimestamp;

	public RevisionBranch(RevisionBranch parent, String path, long baseTimestamp, long headTimestamp) {
		this.parent = parent;
		this.path = path;
		this.baseTimestamp = baseTimestamp;
		this.headTimestamp = headTimestamp;
	}

	public String path() {
		return path;
	}
	
	public long baseTimestamp() {
		return baseTimestamp;
	}
	
	public long headTimestamp() {
		return headTimestamp;
	}
	
	public RevisionBranch parent() {
		return parent;
	}

	@Override
	public int hashCode() {
		return Objects.hash(parent, path, baseTimestamp, headTimestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RevisionBranch other = (RevisionBranch) obj;
		return Objects.equals(parent, other.parent) 
			&& Objects.equals(path, other.path)
			&& Objects.equals(baseTimestamp, other.baseTimestamp)
			&& Objects.equals(headTimestamp, other.headTimestamp);
	}
	
}
