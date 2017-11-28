/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.issue;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;

/**
 * @since 6.0
 */
final class ValidationIssueSearchRequest extends SearchIndexResourceRequest<ServiceProvider, ValidationIssues, ValidationIssue> {

	public enum OptionKey {
		/**
		 * Filter matches by rule identifier.
		 */
		RULE_ID,
		
		/**
		 * Filter matches by branch path field.
		 */
		BRANCH_PATH, 
		
		/**
		 * Filter matches by their rule's tooling ID field.
		 */
		TOOLING_ID
	}
	
	@Override
	protected Searcher searcher(ServiceProvider context) {
		return context.service(ValidationRepository.class).searcher();
	}
	
	@Override
	protected Expression prepareQuery(ServiceProvider context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();

		addIdFilter(queryBuilder, ids -> Expressions.matchAny(ValidationIssue.Fields.ID, ids));
		
		if (containsKey(OptionKey.BRANCH_PATH)) {
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.BRANCH_PATH, getCollection(OptionKey.BRANCH_PATH, String.class)));
		}
		
		final Set<String> filterByRuleIds = newHashSet();
		
		if (containsKey(OptionKey.TOOLING_ID)) {
			final Collection<String> toolingIds = getCollection(OptionKey.TOOLING_ID, String.class);
			
			final Set<String> ruleIds = ValidationRequests.rules().prepareSearch()
				.all()
				.filterByToolings(toolingIds)
				.setFields(ValidationRule.Fields.ID)
				.build()
				.execute(context)
				.stream()
				.map(ValidationRule::getId)
				.collect(Collectors.toSet());
			
			if (ruleIds.isEmpty()) {
				queryBuilder.filter(Expressions.matchNone());
			} else {
				filterByRuleIds.addAll(ruleIds);
			}
		}
		
		if (containsKey(OptionKey.RULE_ID)) {
			filterByRuleIds.addAll(getCollection(OptionKey.RULE_ID, String.class));
		}
		
		if (!filterByRuleIds.isEmpty()) {
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.RULE_ID, filterByRuleIds));
		}
		
		return queryBuilder.build();
	}

	@Override
	protected Class<ValidationIssue> getDocumentType() {
		return ValidationIssue.class;
	}

	@Override
	protected ValidationIssues toCollectionResource(ServiceProvider context, Hits<ValidationIssue> hits) {
		return new ValidationIssues(hits.getHits(), hits.getScrollId(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}

	@Override
	protected ValidationIssues createEmptyResult(int limit) {
		return new ValidationIssues(limit, 0);
	}

}
