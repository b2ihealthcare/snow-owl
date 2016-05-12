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

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Client-side interface of folder index service.
 * 
 * @since 3.0
 */
public interface IClientFolderIndexService {

	/**
	 * Returns the top level folders.
	 * @param IBranchPath createActivePath
	 * @return list of top level folders
	 */
	public Set<FolderIndexEntry> getTopLevelFolders();
	
	/**
	 * Returns all the components for a given folder
	 * @param folderId
	 * @return
	 */
	public Set<? extends ParentFolderAwareIndexEntry> getComponentsForFolder(final String folderId);
	
	/**
	 * Returns the available published component sets for the current
	 * {@link IBranchPath} in the specified folder. Component sets in sub
	 * folders are included. If the specified folder ID is {@code null} or empty
	 * then all published component sets are returned regardless to their parent folder.
	 * 
	 * @param folderId
	 * @return set of component sets
	 */
	public Set<? extends ParentFolderAwareIndexEntry> getAllPublishedComponentSetsByFolder(@Nullable final String folderId);
	
	
	/**
	 * Returns all the components from the top level
	 * @return
	 */
	public Set<? extends ParentFolderAwareIndexEntry> getTopLevelComponents();

	/**
	 * Returns a folder based on its id
	 * @param folderId
	 * @return
	 */
	public FolderIndexEntry getFolderById(String folderId);

	/**
	 * Returns the direct sub-folders within a folder
	 * @param folderId
	 * @return
	 */
	public Set<FolderIndexEntry> getSubFoldersById(final String folderId);

	/**
	 * Returns all the sub-folders within a folder
	 * @param folderId
	 * @return
	 */
	public Collection<FolderIndexEntry> getAllSubFolders(final String folderId);
}