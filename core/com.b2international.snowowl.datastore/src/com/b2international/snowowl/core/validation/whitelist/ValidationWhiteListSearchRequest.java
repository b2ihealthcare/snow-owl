/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ComponentIdentifier;
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
		COMPONENTENT_IDENTIFIER
		
	}
	
	@Override
	protected Searcher searcher(ServiceProvider context) {
		return context.service(ValidationRepository.class).searcher();
	}
	
	@Override
	protected Expression prepareQuery(ServiceProvider context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, ids -> Expressions.matchAny(ValidationWhiteList.Fields.ID, ids));
		
		if(containsKey(OptionKey.RULE_ID)) {
			Collection<String> ruleIds = getCollection(OptionKey.RULE_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(ValidationWhiteList.Fields.RULE_ID, ruleIds));
		}
		
		if(containsKey(OptionKey.COMPONENTENT_IDENTIFIER)) {
			Set<String> componentIds = getCollection(OptionKey.COMPONENTENT_IDENTIFIER, ComponentIdentifier.class).stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet());
			queryBuilder.filter(Expressions.matchAny(ValidationWhiteList.Fields.COMPONENT_IDENTIFIER, componentIds));
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