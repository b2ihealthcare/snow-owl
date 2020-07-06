/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @since 6.0
 */
final class ValidationIssueSearchRequest 
		extends SearchIndexResourceRequest<ServiceProvider, ValidationIssues, ValidationIssue> {

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
		TOOLING_ID,
		
		/**
		 * Filter matches by affected component identifier(s).
		 */
		AFFECTED_COMPONENT_ID,
		
		/**
		 * Filter matches by affected component type(s).
		 */
		AFFECTED_COMPONENT_TYPE,
		
		/**
		 * Filter matches by a single value of affected component label.
		 */
		AFFECTED_COMPONENT_LABEL,
		
		/**
		 * Filter matches by that are whitelisted or not. 
		 */
		WHITELISTED,
		
		/**
		 * Filter matches by details
		 */
		DETAILS
	}
	
	
	ValidationIssueSearchRequest() {}
	
	@Override
	protected Searcher searcher(ServiceProvider context) {
		return context.service(ValidationRepository.class).searcher();
	}
	
	@Override
	protected boolean trackScores() {
		return containsKey(OptionKey.AFFECTED_COMPONENT_LABEL);
	}
	
	@Override
	protected Expression prepareQuery(ServiceProvider context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();

		addIdFilter(queryBuilder, ids -> Expressions.matchAny(ValidationIssue.Fields.ID, ids));
		
		if (containsKey(OptionKey.BRANCH_PATH)) {
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.BRANCH_PATH, getCollection(OptionKey.BRANCH_PATH, String.class)));
		}
		
		Set<String> filterByRuleIds = null;
		
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
				filterByRuleIds = newHashSet(ruleIds);
			}
		}
		
		if (containsKey(OptionKey.RULE_ID)) {
			Set<String> ruleFilter = ImmutableSet.copyOf(getCollection(OptionKey.RULE_ID, String.class));
			if (filterByRuleIds != null) {
				SetView<String> diff = Sets.difference(ruleFilter, filterByRuleIds);
				if (!diff.isEmpty()) {
					throw new BadRequestException("Some of the ruleId filter values '%s' belong to a non-specified toolingId.", diff);
				}
				filterByRuleIds = ruleFilter;
			} else {
				filterByRuleIds = newHashSet(ruleFilter);
			}
		}
		
		if (filterByRuleIds != null) {
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.RULE_ID, filterByRuleIds));
		}
		
		if (containsKey(OptionKey.AFFECTED_COMPONENT_ID)) {
			Collection<String> affectedComponentIds = getCollection(OptionKey.AFFECTED_COMPONENT_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.AFFECTED_COMPONENT_ID, affectedComponentIds));
		}
		
		if (containsKey(OptionKey.AFFECTED_COMPONENT_TYPE)) {
			Collection<Integer> affectedComponentTypes = getCollection(OptionKey.AFFECTED_COMPONENT_TYPE, Short.class).stream().map(Integer::valueOf).collect(Collectors.toSet());
			queryBuilder.filter(Expressions.matchAnyInt(ValidationIssue.Fields.AFFECTED_COMPONENT_TYPE, affectedComponentTypes));
		}
		
		if (containsKey(OptionKey.AFFECTED_COMPONENT_LABEL)) {
			final String searchTerm = getString(OptionKey.AFFECTED_COMPONENT_LABEL);
			if (containsKey(OptionKey.AFFECTED_COMPONENT_ID)) {
				queryBuilder.must(Expressions.matchTextPhrase(ValidationIssue.Fields.AFFECTED_COMPONENT_LABELS, searchTerm));
			} else {
				final List<Expression> disjuncts = newArrayListWithExpectedSize(2);
				disjuncts.add(Expressions.scriptScore(Expressions.matchTextPhrase(ValidationIssue.Fields.AFFECTED_COMPONENT_LABELS, searchTerm), "normalizeWithOffset", ImmutableMap.of("offset", 1)));
				disjuncts.add(Expressions.scriptScore(Expressions.matchTextAll(ValidationIssue.Fields.AFFECTED_COMPONENT_LABELS_PREFIX, searchTerm), "normalizeWithOffset", ImmutableMap.of("offset", 0)));
				queryBuilder.must(
					Expressions.builder()
						.should(Expressions.dismax(disjuncts))
						.should(Expressions.boost(Expressions.exactMatch(ValidationIssue.Fields.AFFECTED_COMPONENT_ID, searchTerm), 1000f))
					.build()
				);
			}
		}
		
		if (containsKey(OptionKey.WHITELISTED)) {
			boolean whitelisted = getBoolean(OptionKey.WHITELISTED);
			queryBuilder.filter(Expressions.match(ValidationIssue.Fields.WHITELISTED, whitelisted));
		}
		
		if (containsKey(OptionKey.DETAILS)) {
			if (!containsKey(OptionKey.TOOLING_ID)) {
				throw new BadRequestException("At least one toolingId is required to be able to filter issues by details.");
			}
			final Collection<String> toolingIds = getCollection(OptionKey.TOOLING_ID, String.class);
			final Options options = getOptions(OptionKey.DETAILS);
			final ExpressionBuilder toolingQuery = Expressions.builder();
			for (String toolingId : toolingIds) {
				ValidationIssueDetailExtensionProvider.INSTANCE.getExtensions()
						.stream()
						.filter(ext -> toolingId.equals(ext.getToolingId()))
						.findFirst()
						.ifPresent(extension -> {
							final ExpressionBuilder extensionQuery = Expressions.builder(); 
							extension.prepareQuery(extensionQuery, options);
							toolingQuery.should(extensionQuery.build());
						});
			}
			// at least one tooling should match
			toolingQuery.setMinimumNumberShouldMatch(1);
			queryBuilder.filter(toolingQuery.build());
		}
		
		return queryBuilder.build();
	}

	@Override
	protected Class<ValidationIssue> getDocumentType() {
		return ValidationIssue.class;
	}

	@Override
	protected ValidationIssues toCollectionResource(ServiceProvider context, Hits<ValidationIssue> hits) {
		return new ValidationIssues(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}

	@Override
	protected ValidationIssues createEmptyResult(int limit) {
		return new ValidationIssues(limit, 0);
	}
	
}
