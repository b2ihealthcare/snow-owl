/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.resource.BaseTerminologyResourceSearchRequest;

/**
 * @since 9.0.0
 */
final class TerminologyResourceCollectionSearchRequest extends BaseTerminologyResourceSearchRequest<TerminologyResourceCollections> {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 9.0.0
	 */
	public enum OptionKey {
		
		/**
		 * Filter matches by their associated toolingId value.
		 */
		TOOLING_ID
		
	}

	@Override
	protected void prepareAdditionalFilters(RepositoryContext context, ExpressionBuilder queryBuilder) {
		super.prepareAdditionalFilters(context, queryBuilder);
		addFilter(queryBuilder, OptionKey.TOOLING_ID, String.class, ResourceDocument.Expressions::toolingIds);
	}
	
	@Override
	protected TerminologyResourceCollections toCollectionResource(RepositoryContext context,
			Hits<ResourceDocument> hits) {
		return new TerminologyResourceCollectionConverter(context, expand(), locales()).convert(hits);
	}

	@Override
	protected TerminologyResourceCollections createEmptyResult(int limit) {
		return new TerminologyResourceCollections(limit, 0);
	}

}
