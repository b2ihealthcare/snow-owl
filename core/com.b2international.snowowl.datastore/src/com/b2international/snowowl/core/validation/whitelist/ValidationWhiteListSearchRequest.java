/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;

/**
 * @since 6.1
 */
final class ValidationWhiteListSearchRequest extends SearchIndexResourceRequest<ServiceProvider, ValidationWhiteLists, ValidationWhiteList>{

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
		COMPONENT_TYPE
		
	}
	
	@Override
	protected Searcher searcher(ServiceProvider context) {
		return context.service(ValidationRepository.class).searcher();
	}
	
	@Override
	protected Expression prepareQuery(ServiceProvider context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, ids -> Expressions.matchAny(ValidationWhiteList.Fields.ID, ids));
		
		if (containsKey(OptionKey.RULE_ID)) {
			Collection<String> ruleIds = getCollection(OptionKey.RULE_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(ValidationWhiteList.Fields.RULE_ID, ruleIds));
		}
		
		if (containsKey(OptionKey.COMPONENT_ID)) {
			Collection<String> componentIds = getCollection(OptionKey.COMPONENT_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(ValidationWhiteList.Fields.COMPONENT_ID, componentIds));
		}
		
		if (containsKey(OptionKey.COMPONENT_TYPE)) {
			Collection<Integer> terminologyComponentIds = getCollection(OptionKey.COMPONENT_TYPE, Short.class).stream().map(Integer::new).collect(Collectors.toSet());
			queryBuilder.filter(Expressions.matchAnyInt(ValidationWhiteList.Fields.TERMINOLOGY_COMPONENT_ID, terminologyComponentIds));
		}
		
		return queryBuilder.build();
	}

	@Override
	protected Class<ValidationWhiteList> getDocumentType() {
		return ValidationWhiteList.class;
	}

	@Override
	protected ValidationWhiteLists toCollectionResource(ServiceProvider context, Hits<ValidationWhiteList> hits) {
		return new ValidationWhiteLists(hits.getHits(), hits.getScrollId(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}

	@Override
	protected ValidationWhiteLists createEmptyResult(int limit) {
		return new ValidationWhiteLists(limit, 0);
	}

}