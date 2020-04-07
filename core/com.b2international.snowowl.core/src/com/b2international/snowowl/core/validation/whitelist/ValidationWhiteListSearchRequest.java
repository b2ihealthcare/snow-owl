/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.whitelist;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * @since 6.1
 */
final class ValidationWhiteListSearchRequest 
		extends SearchIndexResourceRequest<ServiceProvider, ValidationWhiteLists, ValidationWhiteList> {

	enum OptionKey {

		/**
		 * Filter by rule id
		 */
		RULE_ID,
		
		/**
		 * Filter matches by component identifier.
		 */
		COMPONENT_ID,
		
		/**
		 * Filter matches by component type. 
		 */
		COMPONENT_TYPE,
		
		/**
		 * Filter matches by reporter and component identifier
		 */
		TERM, 
		
		/**
		 * Filter matches by reporter
		 */
		REPORTER,
		
		/**
		 * Filter matches created after given time stamp
		 */
		CREATED_START,
		
		/**
		 * Filter matches created before given time stamp
		 */
		CREATED_END
	}
	
	@Override
	protected Searcher searcher(ServiceProvider context) {
		return context.service(ValidationRepository.class).searcher();
	}
	
	@Override
	protected Expression prepareQuery(ServiceProvider context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, ids -> Expressions.matchAny(ValidationWhiteList.Fields.ID, ids));
		
		if (containsKey(OptionKey.TERM)) {
			String searchTerm = getString(OptionKey.TERM);
			
			List<Expression> disjuncts = Lists.newArrayListWithExpectedSize(2);
			disjuncts.add(Expressions.scriptScore(Expressions.matchTextPhrase(ValidationWhiteList.Fields.AFFECTED_COMPONENT_LABELS, searchTerm), "normalizeWithOffset", ImmutableMap.of("offset", 1)));
			disjuncts.add(Expressions.scriptScore(Expressions.matchTextAll(ValidationWhiteList.Fields.AFFECTED_COMPONENT_LABELS_PREFIX, searchTerm), "normalizeWithOffset", ImmutableMap.of("offset", 0)));
			
			queryBuilder.must(
					Expressions.builder()
						.should(Expressions.dismax(disjuncts))
						.should(Expressions.boost(Expressions.matchAny(ValidationWhiteList.Fields.COMPONENT_ID, Collections.singleton(searchTerm)), 1000f))
						.should(Expressions.boost(Expressions.prefixMatch(ValidationWhiteList.Fields.REPORTER, searchTerm), 1000f))
					.build()
			);
		}
		
		if (containsKey(OptionKey.RULE_ID)) {
			Collection<String> ruleIds = getCollection(OptionKey.RULE_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(ValidationWhiteList.Fields.RULE_ID, ruleIds));
		}
		
		if (containsKey(OptionKey.COMPONENT_ID)) {
			Collection<String> componentIds = getCollection(OptionKey.COMPONENT_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(ValidationWhiteList.Fields.COMPONENT_ID, componentIds));
		}
		
		if (containsKey(OptionKey.COMPONENT_TYPE)) {
			Collection<Integer> terminologyComponentIds = getCollection(OptionKey.COMPONENT_TYPE, Short.class).stream().map(Integer::valueOf).collect(Collectors.toSet());
			queryBuilder.filter(Expressions.matchAnyInt(ValidationWhiteList.Fields.TERMINOLOGY_COMPONENT_ID, terminologyComponentIds));
		}
		
		if (containsKey(OptionKey.REPORTER)) {
			Collection<String> reporters = getCollection(OptionKey.REPORTER, String.class);
			queryBuilder.filter(Expressions.matchAny(ValidationWhiteList.Fields.REPORTER, reporters));
		}
		
		if (containsKey(OptionKey.CREATED_START) || containsKey(OptionKey.CREATED_END)) {
			final long createdAfter = containsKey(OptionKey.CREATED_START) ? get(OptionKey.CREATED_START, Long.class) : Long.MIN_VALUE;
			final long createdBefore = containsKey(OptionKey.CREATED_END) ? get(OptionKey.CREATED_END, Long.class) : Long.MAX_VALUE;
			queryBuilder.filter(Expressions.matchRange(ValidationWhiteList.Fields.CREATED_AT, createdAfter, createdBefore));
		}
		
		return queryBuilder.build();
	}
	
	@Override
	protected Class<ValidationWhiteList> getDocumentType() {
		return ValidationWhiteList.class;
	}

	@Override
	protected ValidationWhiteLists toCollectionResource(ServiceProvider context, Hits<ValidationWhiteList> hits) {
		return new ValidationWhiteLists(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}

	@Override
	protected ValidationWhiteLists createEmptyResult(int limit) {
		return new ValidationWhiteLists(limit, 0);
	}

}