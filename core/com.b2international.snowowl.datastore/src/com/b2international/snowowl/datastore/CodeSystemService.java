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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Service for searching and querying among existing code system versions for
 * terminologies and contents. 
 * @deprecated - refactor with ext.management in mind
 */
public interface CodeSystemService {

	/**
	 * Sugar for {@link #isTagged(String, IBranchPath)}.
	 * <br>Returns {@code true} if the branch argument has been tagged in the repository,
	 * otherwise {@code false}.
	 * @see #isTagged(String, IBranchPath)
	 */
	boolean isTagged(final CDOBranch branch);

	/**
	 * Returns {@code true} if the given branch has been tagged in the specified repository. Otherwise returns {@code false}.
	 * <p>More formally, this method returns with {@code true} only and if only a code system exists for the given repository,
	 * and the code system has at least one associated code system version with the same version ID as the last segment of the 
	 * branch path argument. Otherwise returns with {@code false}.
	 * @param repositoryUuid the repository UUID.
	 * @param branchPath the branch to check if tagged or not.
	 * @return {@code true} if the branch is tagged, otherwise {@code false}.
	 */
	boolean isTagged(final String repositoryUuid, final IBranchPath branchPath);

	/**
	 * Returns {@code true} if the given branch path represents a tag in the repository and modifications 
	 * have been made on the version tag. Otherwise returns with {@code false}.
	 * @param repositoryUuid the unique ID of the repository.
	 * @param branchPath the unique path of the branch to check.
	 * @return {@code true} if the branch has been tagged and modifications have been made on it. Otherwise {@code false}.
	 */
	boolean isPatched(final String repositoryUuid, final IBranchPath branchPath);

	/**
	 * Sugar for {@link #getAllTags(String)}.
	 */
	Collection<ICodeSystemVersion> getAllTags(final EPackage ePackage);

	/**
	 * Returns with all registered tags (code system versions) available in a repository given with the unique repository identifier.
	 * <p>A matching code system version is representing a valid tag only and if only the corresponding maintenance branch is available for it.
	 * @param repositoryUuid the unique repository identifier.
	 * @return a collection of code system versions (tags).
	 */
	Collection<ICodeSystemVersion> getAllTags(final String repositoryUuid);
	
	/**
	 * Returns with a list of patched {@link ICodeSystemVersion version}s. Clients are guaranteed that
	 * all {@link ICodeSystemVersion} representing a branch has modifications since it has been created.
	 * @param repositoryUuid the repository UUID.
	 * @return a list of patched tags. Or empty list if no such tags.
	 */
	List<ICodeSystemVersion> getAllPatchedTags(final String repositoryUuid);

	/**
	 * Returns with all tags (code system versions) including the {@link ICodeSystemVersion#LATEST_ENTRY HEAD} for the given repository.
	 * <br>The list of code system versions are sorted via the import date in descending order. HEAD is the first,
	 * then the others.
	 * <p>A matching code system version is representing a valid tag only and if only the corresponding maintenance branch is available for it.
	 * @param repositoryUuid the unique ID of the repository.
	 * @return a list of all available tags including the HEAD.
	 */
	List<ICodeSystemVersion> getAllTagsWithHead(final String repositoryUuid);

	/**
	 * Sugar for {@link #getAllTagsDecorateWithPatched(String)}.
	 */
	Collection<ICodeSystemVersion> getAllTagsDecorateWithPatched(final EPackage ePackage);

	/**
	 * Works exactly as {@link #getAllTags(String)} but the {@link ICodeSystemVersion#isPatched()} information is properly
	 * set on the code system version instances.
	 * @param repositoryUuid the unique repository identifier.
	 * @return a collection of code system versions (tags).
	 */
	Collection<ICodeSystemVersion> getAllTagsDecorateWithPatched(final String repositoryUuid);

	/**
	 * Works exactly as {@link #getAllTagsWithHead(String)} but the {@link ICodeSystemVersion#isPatched()} information is properly
	 * set on the code system version instances.
	 * @param repositoryUuid the unique ID of the repository.
	 * @return a list of all available tags including the HEAD.
	 */
	List<ICodeSystemVersion> getAllTagsWithHeadDecorateWithPatched(final String repositoryUuid);

	/**
	 * Returns with a collection of value sets where the returning collection contains exactly the same number and
	 * same {@link ICodeSystemVersion versions} but the {@link ICodeSystemVersion#isPatched()} property is 
	 * properly configured.
	 * @param repositoryUuid the repository UUID.
	 * @param versions a collection of {@link ICodeSystemVersion versions} to decorate.
	 * @return a collection of {@link ICodeSystemVersion versions} with properly configured {@link ICodeSystemVersion#isPatched() patched} property.
	 */
	<T extends ICodeSystemVersion> Collection<T> decorateWithPatchedFlag(final String repositoryUuid, final Collection<? extends T> versions);

}