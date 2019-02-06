/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.request;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.datastore.request.RepositoryIndexRequestBuilder;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTasks;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationSearchRequest.OptionKey;

/**
 * @since 7.0
 */
public final class ClassificationSearchRequestBuilder 
		extends SearchResourceRequestBuilder<ClassificationSearchRequestBuilder, RepositoryContext, ClassificationTasks> 
		implements RepositoryIndexRequestBuilder<ClassificationTasks> {

	ClassificationSearchRequestBuilder() {}

	public ClassificationSearchRequestBuilder filterByUserId(final String userId) {
		return addOption(OptionKey.USER_ID, userId);
	}

	public ClassificationSearchRequestBuilder filterByBranch(final String branch) {
		return addOption(OptionKey.BRANCH, branch);
	}

	public ClassificationSearchRequestBuilder filterByBranch(final Iterable<String> branches) {
		return addOption(OptionKey.BRANCH, branches);
	}

	public ClassificationSearchRequestBuilder filterByCreatedAfter(final Long afterInclusive) {
		return addOption(OptionKey.CREATED_AFTER, afterInclusive);
	}

	public ClassificationSearchRequestBuilder filterByCreatedBefore(final Long beforeExclusive) {
		return addOption(OptionKey.CREATED_BEFORE, beforeExclusive);
	}

	public ClassificationSearchRequestBuilder filterByStatus(final ClassificationStatus status) {
		return addOption(OptionKey.STATUS, status);
	}

	public ClassificationSearchRequestBuilder filterByStatus(final Iterable<ClassificationStatus> statuses) {
		return addOption(OptionKey.STATUS, statuses);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, ClassificationTasks> createSearch() {
		return new ClassificationSearchRequest();
	}
}
