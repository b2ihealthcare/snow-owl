/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collection;
import java.util.Collections;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.core.ecl.EclExpression;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.ecl.Ecl;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * Abstract class for SNOMED CT search requests.
 * @since 4.5
 * @param R - the return type of this request
 * @param D - the document type to search for
 */
public abstract class SnomedSearchRequest<R, D extends SnomedDocument> extends SearchIndexResourceRequest<BranchContext, R, D> {

	enum OptionKey {
		
		/**
		 * Component's released flag to match
		 */
		RELEASED,
		/**
		 * Component status to match
		 */
		ACTIVE,
		
		/**
		 * Component module ID to match
		 */
		MODULE,
		
		/**
		 * Filter components by effective time starting from this value, inclusive.
		 */
		EFFECTIVE_TIME_START,
		
		/**
		 * Filter components by effective time ending with this value, inclusive.
		 */
		EFFECTIVE_TIME_END,
		
		/**
		 * Use this expression form for all ECL evaluations, by default it is set to inferred.
		 */
		ECL_EXPRESSION_FORM
		
	}
	
	protected SnomedSearchRequest() {}
	
	protected final void addReleasedClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.RELEASED)) {
			queryBuilder.filter(SnomedDocument.Expressions.released(getBoolean(OptionKey.RELEASED)));
		}
	}
	
	protected final void addActiveClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.ACTIVE)) {
			queryBuilder.filter(SnomedDocument.Expressions.active(getBoolean(OptionKey.ACTIVE)));
		}
	}
	
	protected final void addEffectiveTimeClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.EFFECTIVE_TIME_START) || containsKey(OptionKey.EFFECTIVE_TIME_END)) {
			final long from = containsKey(OptionKey.EFFECTIVE_TIME_START) ? get(OptionKey.EFFECTIVE_TIME_START, Long.class) : 0;
			final long to = containsKey(OptionKey.EFFECTIVE_TIME_END) ? get(OptionKey.EFFECTIVE_TIME_END, Long.class) : Long.MAX_VALUE;
			queryBuilder.filter(SnomedDocument.Expressions.effectiveTime(from, to));
		}
	}
	
	protected final void addEclFilter(final BranchContext context, final ExpressionBuilder queryBuilder, Enum<?> eclCapableOptionKey, Function<Collection<String>, Expression> matchingIdsToExpression) {
		if (containsKey(eclCapableOptionKey)) {
			// trim all input values before using them
			final Collection<String> optionValues = getCollection(eclCapableOptionKey, String.class);
			addEclFilter(context, queryBuilder, optionValues, matchingIdsToExpression);
		}
	}

	protected final void addEclFilter(BranchContext context, ExpressionBuilder queryBuilder, Collection<String> optionValues, Function<Collection<String>, Expression> matchingIdsToExpression) {
		Collection<String> eclFilter = evaluateEclFilter(context, optionValues);
		if (eclFilter != null) {
			queryBuilder.filter(matchingIdsToExpression.apply(eclFilter));
		}
	}

	protected final Collection<String> evaluateEclFilter(BranchContext context, Collection<String> optionValues) {
		if (optionValues.isEmpty()) {
			return Collections.emptySet();
		}
		Collection<String> idFilter = FluentIterable.from(optionValues).transform(String::trim).toSet();
		if (idFilter.size() == 1) {
			// if only a single item is available in the typeIdFilter
			final String expression = Iterables.getOnlyElement(idFilter);
			if (!SnomedIdentifiers.isConceptIdentifier(expression)) {
				// and it's not a CONCEPT_ID, then evaluate via SnomedConceptSearchRequest

				// unless it is an Any ECL expression, which allows any value
				if (Ecl.ANY.equals(expression)) {
					return null;
				}
				
				// TODO replace sync call to concept search with async promise
				idFilter = EclExpression.of(expression, eclExpressionForm()).resolve(context).getSync();
				if (idFilter.isEmpty()) {
					throw new SearchResourceRequest.NoResultException();
				}
			}
		}
		return idFilter;
	}
	
	protected final String eclExpressionForm() {
		return containsKey(OptionKey.ECL_EXPRESSION_FORM) ? getString(OptionKey.ECL_EXPRESSION_FORM) : Trees.INFERRED_FORM;
	}
	
}
