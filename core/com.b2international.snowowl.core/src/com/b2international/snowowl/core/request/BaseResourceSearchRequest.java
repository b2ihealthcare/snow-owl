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
package com.b2international.snowowl.core.request;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.ResourceDocument;

/**
 * @since 8.0
 */
public abstract class BaseResourceSearchRequest<R>
	extends SearchIndexResourceRequest<RepositoryContext, R, ResourceDocument>
	implements RepositoryAccessControl {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @since 8.0
	 */
	public enum OptionKey {
		/** Search resources by title */
		TITLE,
		/** "Smart" search by title (taking prefixes, stemming, etc. into account) */
		TITLE_EXACT,
	}
	
	protected final ExpressionBuilder addTitleFilter(ExpressionBuilder queryBuilder) {
		if (!containsKey(OptionKey.TITLE)) {
			return queryBuilder;
		}
		
		final TermFilter termFilter = get(OptionKey.TITLE, TermFilter.class);
		
		final ExpressionBuilder expressionBuilder = Expressions.builder();
		
		if (termFilter.isFuzzy()) {
			expressionBuilder.should(ResourceDocument.Expressions.titleFuzzy(termFilter.getTerm()));
		} else if (termFilter.isExact()) {
			expressionBuilder.should(ResourceDocument.Expressions.matchTitleExact(termFilter.getTerm(), termFilter.isCaseSensitive()));
		} else if (termFilter.isParsed()) {
			expressionBuilder.should(ResourceDocument.Expressions.parsedTitle(termFilter.getTerm()));
		} else if (termFilter.isAnyMatch()) {
			expressionBuilder.should(ResourceDocument.Expressions.minShouldMatchTermDisjunctionQuery(termFilter));
		} else {
			expressionBuilder.should(ResourceDocument.Expressions.defaultTitleDisjunctionQuery(termFilter));
		}
		
		expressionBuilder.should(Expressions.boost(ResourceDocument.Expressions.id(termFilter.getTerm()), 1000.0f));
		return queryBuilder.must(expressionBuilder.build());
	}
	
	protected final void addTitleExactFilter(ExpressionBuilder queryBuilder) {
		addFilter(queryBuilder, OptionKey.TITLE_EXACT, String.class, ResourceDocument.Expressions::titles);
	}

	@Override
	public final String getOperation() {
		return Permission.OPERATION_BROWSE;
	}

	@Override
	protected final Class<ResourceDocument> getSelect() {
		return ResourceDocument.class;
	}
}
