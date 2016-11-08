/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;

import com.b2international.collections.longs.LongSet;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;
import com.b2international.snowowl.datastore.request.RevisionSearchRequest;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.escg.ConceptIdQueryEvaluator2;
import com.b2international.snowowl.snomed.datastore.escg.EscgRewriter;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.dsl.query.RValue;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * @since 4.5
 */
public abstract class SnomedSearchRequest<R> extends RevisionSearchRequest<R> {

	enum OptionKey {
		
		/**
		 * Language reference sets to use
		 */
		LANGUAGE_REFSET,
		
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
		EFFECTIVE_TIME_END
	}
	
	protected SnomedSearchRequest() {}
	
	protected List<String> languageRefSetIds() {
		return getList(OptionKey.LANGUAGE_REFSET, String.class);
	}

	@Override
	protected String getIdField() {
		return "id";
	}
	
	protected final void addModuleClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.MODULE)) {
			queryBuilder.must(SnomedDocument.Expressions.module(getString(OptionKey.MODULE)));
		}
	}

	protected final void addActiveClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.ACTIVE)) {
			queryBuilder.must(SnomedDocument.Expressions.active(getBoolean(OptionKey.ACTIVE)));
		}
	}
	
	protected final void addEffectiveTimeClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.EFFECTIVE_TIME_START) || containsKey(OptionKey.EFFECTIVE_TIME_END)) {
			final long from = containsKey(OptionKey.EFFECTIVE_TIME_START) ? get(OptionKey.EFFECTIVE_TIME_START, Long.class) : 0;
			final long to = containsKey(OptionKey.EFFECTIVE_TIME_END) ? get(OptionKey.EFFECTIVE_TIME_END, Long.class) : Long.MAX_VALUE;
			queryBuilder.must(SnomedDocument.Expressions.effectiveTime(from, to));
		}
	}
	
	/**
	 * @deprecated - use {@link #addEclFilter(BranchContext, ExpressionBuilder, Enum, Function)} instead
	 */
	protected final void addEscgFilter(final BranchContext context, final ExpressionBuilder queryBuilder, Enum<?> key, Function<LongSet, Expression> expressionProvider) {
		if (containsKey(key)) {
			try {
				final String escg = getString(key);
				final RValue expression = context.service(EscgRewriter.class).parseRewrite(escg);
				final LongSet conceptIds = new ConceptIdQueryEvaluator2(context.service(RevisionSearcher.class)).evaluate(expression);
				final Expression conceptFilter = expressionProvider.apply(conceptIds);
				queryBuilder.must(conceptFilter);
			} catch (SyntaxErrorException e) {
				throw new IllegalQueryParameterException(e.getMessage());
			}
		}
	}
	
	protected final void addEclFilter(final BranchContext context, final ExpressionBuilder queryBuilder, Enum<?> eclCapableOptionKey, Function<Collection<String>, Expression> matchingIdsToExpression) {
		if (containsKey(eclCapableOptionKey)) {
			Collection<String> idFilter = getCollection(eclCapableOptionKey, String.class);
			if (idFilter.size() == 1) {
				// if only a single item is available in the typeIdFilter
				final String expression = Iterables.getOnlyElement(idFilter);
				if (!SnomedIdentifiers.isConceptIdentifier(expression)) {
					// and it's not a CONCEPT_ID, then evaluate via SnomedConceptSearchRequest
					// TODO replace sync call to concept search with async promise
					SnomedConcepts matchingConcepts = SnomedRequests.prepareSearchConcept()
						.all()
						.filterByEcl(expression)
						.build()
						.execute(context);
					idFilter = FluentIterable.from(matchingConcepts).transform(IComponent.ID_FUNCTION).toSet();
				}
			}
			queryBuilder.must(matchingIdsToExpression.apply(idFilter));
		}
	}
	
}
