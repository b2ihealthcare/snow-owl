/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.collection;

import com.b2international.snowowl.core.collection.TerminologyResourceCollectionSearchRequest.OptionKey;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.resource.BaseTerminologyResourceSearchRequestBuilder;

/**
 * @since 9.0.0
 */
public final class TerminologyResourceCollectionSearchRequestBuilder
		extends BaseTerminologyResourceSearchRequestBuilder<TerminologyResourceCollectionSearchRequestBuilder, TerminologyResourceCollections> {

	public TerminologyResourceCollectionSearchRequestBuilder filterByToolingId(String toolingId) {
		return addOption(OptionKey.TOOLING_ID, toolingId);
	}

	public TerminologyResourceCollectionSearchRequestBuilder filterByToolingIds(Iterable<String> toolingIds) {
		return addOption(OptionKey.TOOLING_ID, toolingIds);
	}
	
	@Override
	protected SearchResourceRequest<RepositoryContext, TerminologyResourceCollections> createSearch() {
		return new TerminologyResourceCollectionSearchRequest();
	}

}
