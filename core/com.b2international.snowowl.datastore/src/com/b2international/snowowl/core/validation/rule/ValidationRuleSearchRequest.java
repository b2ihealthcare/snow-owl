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
package com.b2international.snowowl.core.validation.rule;

import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;

/**
 * @since 6.0
 */
final class ValidationRuleSearchRequest extends SearchIndexResourceRequest<ServiceProvider, ValidationRules, ValidationRule> {

	enum OptionKey {
		/**
		 * Filter matches by severity values.
		 */
		SEVERITY
	}
	
	@Override
	protected Searcher searcher(ServiceProvider context) {
		return context.service(ValidationRepository.class).searcher();
	}
	
	@Override
	protected Expression prepareQuery(ServiceProvider context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();

		if (containsKey(OptionKey.SEVERITY)) {
			final Set<String> values = getCollection(OptionKey.SEVERITY, Severity.class).stream().map(Severity::name).collect(Collectors.toSet());
			queryBuilder.filter(Expressions.matchAny(ValidationRule.Fields.SEVERITY, values));
		}
		
		return queryBuilder.build();
	}

	@Override
	protected Class<ValidationRule> getDocumentType() {
		return ValidationRule.class;
	}

	@Override
	protected ValidationRules toCollectionResource(ServiceProvider context, Hits<ValidationRule> hits) {
		return new ValidationRules(hits.getHits(), hits.getScrollId(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}

	@Override
	protected ValidationRules createEmptyResult(int limit) {
		return new ValidationRules(limit, 0);
	}

}
