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

import static com.b2international.snowowl.core.internal.ResourceDocument.Expressions.defaultTitleDisjunctionQuery;
import static com.b2international.snowowl.core.internal.ResourceDocument.Expressions.minShouldMatchTermDisjunctionQuery;
import static com.b2international.snowowl.core.internal.ResourceDocument.Expressions.titleFuzzy;
import static com.b2international.snowowl.core.internal.ResourceDocument.Expressions.matchTitleExact;
import static com.b2international.snowowl.core.internal.ResourceDocument.Expressions.parsedTitle;

import java.util.Collections;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.TermFilter;

/**
 * @since 8.0
 */
final class BundleSearchRequest
	extends SearchIndexResourceRequest<RepositoryContext, Bundles, ResourceDocument>
	implements RepositoryAccessControl {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 8.0
	 */
	public enum OptionKey {
		/** Search bundles by title */
		TITLE,
	}

	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, ResourceDocument.Expressions::ids);
		addTitleFilter(queryBuilder);
		
		return queryBuilder.build();
	}

	private void addTitleFilter(ExpressionBuilder queryBuilder) {
		if (!containsKey(OptionKey.TITLE)) {
			return;
		}
		
		final TermFilter termFilter = get(OptionKey.TITLE, TermFilter.class);
		
		final ExpressionBuilder expressionBuilder = Expressions.builder();
		
		if (termFilter.isFuzzy()) {
			expressionBuilder.must(titleFuzzy(termFilter.getTerm()));
		} else if (termFilter.isExact()) {
			expressionBuilder.must(matchTitleExact(termFilter.getTerm(), termFilter.isCaseSensitive()));
		} else if (termFilter.isParsed()) {
			expressionBuilder.should(parsedTitle(termFilter.getTerm()));
		} else if (termFilter.isAnyMatch()) {
			expressionBuilder.must(minShouldMatchTermDisjunctionQuery(termFilter));
		} else {
			expressionBuilder.must(defaultTitleDisjunctionQuery(termFilter));
		}
		
		expressionBuilder.should(Expressions.boost(ResourceDocument.Expressions.id(termFilter.getTerm()), 1000.0f));
		
		queryBuilder.must(expressionBuilder.build());
	}

	@Override
	protected Bundles toCollectionResource(RepositoryContext context, Hits<ResourceDocument> hits) {
		final BundleConverter converter = new BundleConverter(context, expand(), null);
		return converter.convert(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}

	@Override
	protected Bundles createEmptyResult(int limit) {
		return new Bundles(Collections.emptyList(), null, limit, 0);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}

	@Override
	protected Class<ResourceDocument> getSelect() {
		return ResourceDocument.class;
	}
	
}
