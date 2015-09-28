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
package com.b2international.snowowl.datastore.index;

import com.b2international.snowowl.datastore.index.mapping.Mappings;

/**
 * Represents a folder as an index entry
 * 
 */
public class FolderIndexEntry extends ParentFolderAwareIndexEntry {

	public static final FolderIndexEntry ROOT_FOLDER = new FolderIndexEntry(Mappings.ROOT_ID_STRING, "<label>", "<parent id>", 0L, true);

	private static final long serialVersionUID = 2798747626697337577L;
	private final boolean hasChildren;

	public FolderIndexEntry(final String id, final String label, String parentId, long storageKey, boolean hasChildren) {
		super(id, label, null, 0.0f, storageKey, parentId);
		this.hasChildren = hasChildren;
	}

	public boolean hasChildren() {
		return hasChildren;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FolderIndexEntry other = (FolderIndexEntry) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
	
}