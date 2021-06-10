/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.bundle;

import com.b2international.snowowl.core.bundle.BundleSearchRequest.OptionKey;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.core.request.TermFilterSupport;

/**
 * @since 8.0
 */
public final class BundleSearchRequestBuilder 
		extends SearchResourceRequestBuilder<BundleSearchRequestBuilder, RepositoryContext, Bundles>
		implements ResourceRepositoryRequestBuilder<Bundles>, TermFilterSupport<BundleSearchRequestBuilder> {

	BundleSearchRequestBuilder() {
		super();
	}

	@Override
	public BundleSearchRequestBuilder filterByTerm(TermFilter termFilter) {
		return addOption(OptionKey.TITLE, termFilter);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, Bundles> createSearch() {
		return new BundleSearchRequest();
	}
}
