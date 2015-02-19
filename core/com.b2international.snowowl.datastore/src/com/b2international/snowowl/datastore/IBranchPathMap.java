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
package com.b2international.snowowl.datastore;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * 
 */
public interface IBranchPathMap extends Serializable {

	/**
	 * Returns the branch path for the specified repository UUID.
	 * <p>
	 * May return either {@code null} or  {@code MAIN} if the repository did not have any branch path set or the branch path is not known.
	 * 
	 * @param repositoryId the repository UUID to look up
	 * @return the associated branch path
	 */
	IBranchPath getBranchPath(String repositoryId);

	/**
	 * Returns the branch path for the specified package.
	 * <p>
	 * May return either {@code null} or  {@code MAIN} if the repository did not have any branch path set or the branch path is not known.
	 *
	 * @param repositoryId the repository UUID to look up
	 * @return the associated branch path
	 */
	IBranchPath getBranchPath(EPackage ePackage);

	/**
	 * Returns a {@link Map} view of this {@link IBranchPathMap} with the supplied keys. An attempt will be made to create entries for all keys in
	 * the given repository identifier key set. 
	 * 
	 * @param repositoryIds the key set to populate when creating the map
	 * @return a map containing all entries of an {@link IBranchPathMap}, regardless of where the entry has been set (on this instance or any ancestor)
	 */
	Map<String, IBranchPath> asMap(Set<String> repositoryIds);

	/**
	 * @return a map containing entries on this {@link IBranchPathMap} instance which are not allowed to change, or an empty map if no such
	 * entries can be found
	 */
	Map<String, IBranchPath> getLockedEntries();
}