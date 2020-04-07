/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.commit;

import static com.b2international.snowowl.core.commit.CommitInfoSearchRequest.OptionKey.AFFECTED_COMPONENT_ID;
import static com.b2international.snowowl.core.commit.CommitInfoSearchRequest.OptionKey.AUTHOR;
import static com.b2international.snowowl.core.commit.CommitInfoSearchRequest.OptionKey.BRANCH;
import static com.b2international.snowowl.core.commit.CommitInfoSearchRequest.OptionKey.COMMENT;
import static com.b2international.snowowl.core.commit.CommitInfoSearchRequest.OptionKey.TIME_STAMP_FROM;
import static com.b2international.snowowl.core.commit.CommitInfoSearchRequest.OptionKey.TIME_STAMP_TO;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;

/**
 * @since 5.2
 */
public final class CommitInfoSearchRequestBuilder 
		extends SearchResourceRequestBuilder<CommitInfoSearchRequestBuilder, RepositoryContext, CommitInfos> 
		implements RepositoryRequestBuilder<CommitInfos> {

	CommitInfoSearchRequestBuilder() {}

	public CommitInfoSearchRequestBuilder filterByBranch(final String branch) {
		return addOption(BRANCH, branch);
	}
	
	public CommitInfoSearchRequestBuilder filterByBranches(final Iterable<String> branchPaths) {
		return addOption(BRANCH, branchPaths);
	}

	public CommitInfoSearchRequestBuilder filterByAuthor(final String author) {
		return addOption(AUTHOR, author);
	}

	public CommitInfoSearchRequestBuilder filterByComment(final String comment) {
		return addOption(COMMENT, comment);
	}

	public CommitInfoSearchRequestBuilder filterByTimestamp(final Long timestamp) {
		return filterByTimestamp(timestamp, timestamp);
	}
	
	public CommitInfoSearchRequestBuilder filterByTimestamp(final Long timestampFrom, final Long timestampTo) {
		return addOption(TIME_STAMP_FROM, timestampFrom)
				.addOption(TIME_STAMP_TO, timestampTo);
	}
	
	public CommitInfoSearchRequestBuilder filterByAffectedComponent(final String affectedComponentId) {
		return addOption(AFFECTED_COMPONENT_ID, affectedComponentId);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, CommitInfos> createSearch() {
		return new CommitInfoSearchRequest();
	}

}
