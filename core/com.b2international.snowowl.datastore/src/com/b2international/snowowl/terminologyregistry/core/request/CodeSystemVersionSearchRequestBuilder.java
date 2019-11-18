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
package com.b2international.snowowl.terminologyregistry.core.request;

import java.util.Date;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.request.RepositoryIndexRequestBuilder;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemVersionSearchRequest.OptionKey;

/**
 * @since 4.7
 */
public final class CodeSystemVersionSearchRequestBuilder 
		extends SearchResourceRequestBuilder<CodeSystemVersionSearchRequestBuilder, RepositoryContext, CodeSystemVersions>
 		implements RepositoryIndexRequestBuilder<CodeSystemVersions> {

	CodeSystemVersionSearchRequestBuilder() {
		super();
	}

	/**
	 * Filter version by their associated code system short name (essentially by which code system has the version).
	 * @param codeSystemShortName
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByCodeSystemShortName(String codeSystemShortName) {
		return addOption(OptionKey.CODE_SYSTEM_SHORT_NAME, codeSystemShortName);
	}
	
	/**
	 * Filter version by their associated code system short name (essentially by which code system has the version).
	 * @param codeSystemShortNames
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByCodeSystemShortNames(Iterable<String> codeSystemShortNames) {
		return addOption(OptionKey.CODE_SYSTEM_SHORT_NAME, codeSystemShortNames);
	}
	
	/**
	 * Filter versions by their version ID.
	 * @param versionId - the versionId to look for.
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByVersionId(String versionId) {
		return addOption(OptionKey.VERSION_ID, versionId);
	}
	
	/**
	 * Filter versions by their version ID.
	 * @param versionIds - the versionIds to look for.
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByVersionIds(Iterable<String> versionIds) {
		return addOption(OptionKey.VERSION_ID, versionIds);
	}
	
	/**
	 * Filter versions by their effective date property.
	 * @param effectiveDate
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByEffectiveDate(Date effectiveDate) {
		return addOption(OptionKey.EFFECTIVE_DATE, effectiveDate);
	}
	
	/**
	 * Returns the code system versions that have matching branch paths
	 * @param branchPath - branch path to filter by
	 */
	public CodeSystemVersionSearchRequestBuilder filterByBranchPath(String branchPath) {
		return addOption(OptionKey.BRANCH_PATH, branchPath);
	}
	
	/**
	 * Returns the code system versions that have matching parent branch path.
	 * @param parentBranchPath - parent branch path to filter by
	 */
	public CodeSystemVersionSearchRequestBuilder filterByParentBranchPath(String parentBranchPath) {
		return addOption(OptionKey.PARENT_BRANCH_PATH, parentBranchPath);
	}
	
	/**
	 * Returns the code system versions that have matching parent branch paths
	 * @param parentBranchPath - parent branch path to filter by
	 */
	public CodeSystemVersionSearchRequestBuilder filterByParentBranchPaths(Iterable<String> parentBranchPaths) {
		return addOption(OptionKey.PARENT_BRANCH_PATH, parentBranchPaths);
	}

	/**
	 * Filter versions by created at (formerly import date) using the specified range.
	 * @param fromCreatedAt - the lower bound of the created at date range
	 * @param toCreatedAt - the upper bound of the created at date range
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByCreatedAt(final long fromCreatedAt, final long toCreatedAt) {
		return addOption(OptionKey.CREATED_AT_START, fromCreatedAt).addOption(OptionKey.CREATED_AT_END, toCreatedAt);
	}
	
	/**
	 * Filter versions by created at date (formerly import date) using the specified value.
	 * @param createdAt the exact created at date to match for
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByCreatedAt(final long createdAt) {
		return filterByCreatedAt(createdAt, createdAt);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, CodeSystemVersions> createSearch() {
		return new CodeSystemVersionSearchRequest();
	}

}
