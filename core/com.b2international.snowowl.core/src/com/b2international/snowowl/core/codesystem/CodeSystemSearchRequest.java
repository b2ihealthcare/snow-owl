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
package com.b2international.snowowl.core.codesystem;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;

/**
 * @since 4.7
 */
final class CodeSystemSearchRequest 
	extends SearchIndexResourceRequest<RepositoryContext, CodeSystems, CodeSystemEntry> 
	implements RepositoryAccessControl {

	private static final long serialVersionUID = 2L;

	CodeSystemSearchRequest() { }

	/**
	 * @since 7.9
	 */
	public enum OptionKey {
		/** Search by specific tooling ID */
		TOOLING_ID,
		/** "Smart" search by name (taking prefixes, stemming, etc. into account) */
		NAME,
		/** Exact match for code system name */
		NAME_EXACT,
	}
	
	@Override
	protected Class<CodeSystemEntry> getDocumentType() {
		return CodeSystemEntry.class;
	}
	
	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, ids -> Expressions.builder()
				.should(CodeSystemEntry.Expressions.shortNames(ids))
				.should(CodeSystemEntry.Expressions.oids(ids))
				.build());
		
		addToolingIdFilter(queryBuilder);
		addNameFilter(queryBuilder);
		addNameExactFilter(queryBuilder);
		
		return queryBuilder.build();
	}

	private void addToolingIdFilter(final ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.TOOLING_ID)) {
			queryBuilder.filter(CodeSystemEntry.Expressions.toolingIds(getCollection(OptionKey.TOOLING_ID, String.class)));
		}
	}

	private void addNameFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.NAME)) {
			final String searchTerm = getString(OptionKey.NAME);
			ExpressionBuilder termFilter = Expressions.builder();
			final List<Expression> disjuncts = newArrayList();
			disjuncts.add(CodeSystemEntry.Expressions.matchNameExact(searchTerm));
			disjuncts.add(CodeSystemEntry.Expressions.matchNameAllTermsPresent(searchTerm));
			disjuncts.add(CodeSystemEntry.Expressions.matchNameAllPrefixesPresent(searchTerm));
			termFilter.should(Expressions.dismax(disjuncts));
			termFilter.should(Expressions.boost(CodeSystemEntry.Expressions.shortName(searchTerm), 1000.0f));
			queryBuilder.must(termFilter.build());
		}
	}
	
	private void addNameExactFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.NAME_EXACT)) {
			queryBuilder.must(CodeSystemEntry.Expressions.matchNameOriginal(getString(OptionKey.NAME_EXACT)));
		}		
	}
	
	@Override
	protected CodeSystems toCollectionResource(RepositoryContext context, Hits<CodeSystemEntry> hits) {
		final CodeSystemConverter converter = new CodeSystemConverter(context, expand(), null);
		return converter.convert(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}
	
	@Override
	protected CodeSystems createEmptyResult(int limit) {
		return new CodeSystems(Collections.emptyList(), null, limit, 0);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}
}
