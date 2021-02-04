/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem.version;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collection;

import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.codesystem.version.CodeSystemVersionSearchRequest.OptionKey;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemVersionSearchRequestBuilder 
		extends SearchResourceRequestBuilder<CodeSystemVersionSearchRequestBuilder, RepositoryContext, CodeSystemVersions>
 		implements RepositoryRequestBuilder<CodeSystemVersions> {

	private String versionId;
	private String parentBranchPath;

	public CodeSystemVersionSearchRequestBuilder() {
		super();
	}

	public CodeSystemVersionSearchRequestBuilder filterByCodeSystemShortName(String codeSystemShortName) {
		return addOption(OptionKey.SHORT_NAME, codeSystemShortName);
	}
	
	public CodeSystemVersionSearchRequestBuilder filterByCodeSystemShortNames(Collection<String> codeSystemShortNames) {
		return addOption(OptionKey.SHORT_NAME, codeSystemShortNames);
	}
	
	/**
	 * Filter versions by their version ID.
	 * @param versionId - the versionId to look for.
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByVersionId(String versionId) {
		this.versionId = versionId;
		return getSelf();
	}
	
	/**
	 * @param effectiveDate - the {@link LocalDate}'s epoch time in UTC to use to match versions
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByEffectiveDate(LocalDate effectiveDate) {
		if (effectiveDate != null) {
			final long effectiveTime = EffectiveTimes.getEffectiveTime(effectiveDate);
			return addOption(OptionKey.EFFECTIVE_TIME_START, effectiveTime).addOption(OptionKey.EFFECTIVE_TIME_END, effectiveTime);
		} else {
			return getSelf();
		}
	}
	
	public CodeSystemVersionSearchRequestBuilder filterByEffectiveDate(long effectiveDateStart, long effectiveDateEnd) {
		return addOption(OptionKey.EFFECTIVE_TIME_START, effectiveDateStart).addOption(OptionKey.EFFECTIVE_TIME_END, effectiveDateEnd);
	}
	
	/**
	 * Returns the code system versions that have matching branch paths
	 * @param branchPath - branch path to filter by
	 */
	public CodeSystemVersionSearchRequestBuilder filterByBranchPath(String branchPath) {
		
		Path path = Paths.get(branchPath);
		if (path.getNameCount() == 0) {
			throw new IllegalArgumentException("Invalid branch path, only root element exists" + branchPath);
		}
		
		parentBranchPath = path.subpath(0, path.getNameCount() - 1).toString();
		versionId = path.getFileName().toString();
		return getSelf();
	}
	
	/**
	 * Returns the code system versions that have matching parent branch paths
	 * @param parentBranchPath - parent branch path to filter by
	 */
	public CodeSystemVersionSearchRequestBuilder filterByParentBranchPath(String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
		return getSelf();
		
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
		final CodeSystemVersionSearchRequest req = new CodeSystemVersionSearchRequest();
		req.setVersionId(versionId);
		req.setParentBranchPath(parentBranchPath);
		return req;
	}
}
