/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.util.*;
import java.util.stream.Collectors;

import com.b2international.commons.options.Options;
import com.b2international.snomed.ecl.Ecl;
import com.b2international.snomed.ecl.ecl.EclConceptReference;
import com.b2international.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.ecl.EclParser;
import com.b2international.snowowl.core.request.ecl.AbstractComponentSearchRequestBuilder;
import com.b2international.snowowl.core.request.search.TermFilter;
import com.b2international.snowowl.core.request.search.TermFilterSupport;

/**
 * @since 7.5
 */
public interface ConceptSearchRequestEvaluator {

	public enum OptionKey {

		/**
		 * Explicit ID filter to return all concepts that have any of the given IDs.
		 */
		ID,

		/**
		 * Match concepts that have the specified active status. Accepts a boolean <code>true</code> or <code>false</code> value.
		 */
		ACTIVE,

		/**
		 * A term filter that matches concepts having a term match. The exact semantics of how a term match works depends on the given code system,
		 * but usually it supports exact, partial word and prefix matches.
		 */
		TERM,

		/**
		 * One or more query expressions (defined in the target code system's query language) to include matches.
		 */
		QUERY,

		/**
		 * One or more query expressions (defined in the target code system's query language) to exclude matches from the results.
		 */
		MUST_NOT_QUERY,

		/**
		 * Language locales (tag, Accept-Language header, etc.) to use in order of preference when determining the display label or term for a match.
		 */
		LOCALES,

		/**
		 * Search matches after the specified sort key.
		 */
		AFTER,

		/**
		 * Number of matches to return.
		 */
		LIMIT,
		
		/**
		 * Specific fields to load when requested content (consumers of the API must be familiar with the underlying schema)
		 */
		FIELDS,
		
		/**
		 * Expand additional data requested by the client. If set, implementers should set the {@link Concept#setInternalConcept(Object)} to the
		 * fully loaded internal tooling representation of the code and return it along with the generic {@link Concept} object.
		 */
		EXPAND,
		
		/**
		 * Set the preferred display type to return
		 */
		DISPLAY,
		
		/**
		 * Filters terms by their type.
		 */
		TERM_TYPE,

		/**
		 * Filters concepts by their type.
		 */
		TYPE, 
		
		/**
		 * Filters concepts by their direct parents.
		 */
		PARENT,
		
		/**
		 * Filters concepts by their ancestors (direct or indirect parents).
		 */
		ANCESTOR, 

		/**
		 * Filter by semantic similarity using a query vector
		 */
		KNN,
		
		/**
		 * Filter by semantic similarity using only query vectors based on concept description terms
		 */
		DESCRIPTION_KNN,
		
	}

	/**
	 * Evaluate the given search options on the given context and return generic {@link Concept} instances back in a {@link Concepts} pageable
	 * resource.
	 * 
	 * @param uri
	 *            - the code system uri where the search is being evaluated
	 * @param context
	 *            - the context to perform the search on
	 * @param search
	 *            - the search filters and options to apply to the code system specific search
	 * @return
	 */
	Concepts evaluate(ResourceURI uri, ServiceProvider context, Options search);

	/**
	 * Subclasses may optionally use this method to initialize the common concept model from their tooling specific model.
	 * 
	 * @param codeSystem
	 * @param concept
	 * @param iconId
	 * @param term
	 * @param score
	 * @return
	 */
	default Concept toConcept(ResourceURI codeSystem, IComponent concept, String iconId, String term, Float score) {
		Concept result = new Concept(codeSystem, concept.getComponentType());
		result.setId(concept.getId());
		result.setReleased(concept.isReleased());
		result.setIconId(iconId);
		result.setTerm(term);
		result.setScore(score);
		// treat all concepts active by default, so terminology plugin that does not support statuses can be simplified
		result.setActive(true);
		result.setInternalConcept(concept);
		mapRemainingFields(result, concept);
		return result;
	}

	/**
	 * Maps all remaining fields on the given result {@link Concept} model object based on the tooling specific concept received in the second argument.
	 * 
	 * @param result
	 * @param concept
	 */
	default void mapRemainingFields(Concept result, IComponent concept) {
	}

	/**
	 * Prepares an ID filter from the ID option key only. Use as the default ID filter.
	 * 
	 * @param requestBuilder
	 * @param search
	 */
	default void evaluateIdFilterOptions(SearchResourceRequestBuilder<?, ?, ?> requestBuilder, Options search) {
		if (search.containsKey(OptionKey.ID)) {
			requestBuilder.filterByIds(search.getCollection(OptionKey.ID, String.class));
		}
	}
	
	/**
	 * Prepares an ID filter from the ID, QUERY and MUST_NOT_QUERY option keys. Use only if the underlying tooling does not support any kind of special query parameters and you'd like to handle basic query support for enumerated list of component IDs.
	 * 
	 * @param requestBuilder
	 * @param search
	 */
	default void evaluateIdQueryMustNotQueryOptionsAsIdFilter(SearchResourceRequestBuilder<?, ?, ?> requestBuilder, Options search) {
		if (!search.containsKey(OptionKey.ID) && !search.containsKey(OptionKey.QUERY) && !search.containsKey(OptionKey.MUST_NOT_QUERY)) {
			return;
		}
		
		Set<String> idFilter = newHashSet();
		
		if (search.containsKey(OptionKey.ID)) {
			idFilter.addAll(search.getCollection(OptionKey.ID, String.class));
		}
		
		if (search.containsKey(OptionKey.QUERY)) {
			idFilter.addAll(extractIds(search.getCollection(OptionKey.QUERY, String.class)));
		}
		
		if (search.containsKey(OptionKey.MUST_NOT_QUERY)) {
			idFilter.removeAll(extractIds(search.getCollection(OptionKey.MUST_NOT_QUERY, String.class)));
		}
		
		requestBuilder.filterByIds(idFilter);
	}
	
	default void evaluateTermFilterOptions(TermFilterSupport<?> requestBuilder, Options search) {
		if (search.containsKey(OptionKey.TERM)) {
			requestBuilder.filterByTerm(search.get(OptionKey.TERM, TermFilter.class));
		}
	}
	
	/**
	 * Configures knn filtering if the necessary configuration present in the given search options.
	 * 
	 * @param requestBuilder
	 * @param search
	 */
	default void evaluateKnnFilterOptions(KnnFilterSupport<?> requestBuilder, Options search) {
		if (search.containsKey(OptionKey.KNN)) {
			requestBuilder.filterByKnn(search.get(OptionKey.KNN, KnnFilter.class));
		}
		if (search.containsKey(OptionKey.DESCRIPTION_KNN) && requestBuilder instanceof DescriptionKnnFilterSupport<?>) {
			((DescriptionKnnFilterSupport<?>) requestBuilder).filterByDescriptionKnn(search.get(OptionKey.DESCRIPTION_KNN, KnnFilter.class));
		}
	}
	
	/**
	 * Appends an ECL filter to the given component search request filter when either a QUERY or MUST_NOT_QUERY part is present in the given options.
	 * 
	 * @param context
	 * @param req
	 * @param search
	 */
	default void evaluateQueryOptions(ServiceProvider context, AbstractComponentSearchRequestBuilder<?, ?, ?> req, Options search) {
		if (search == null) {
			return;
		}
		
		if (search.containsKey(OptionKey.QUERY) || search.containsKey(OptionKey.MUST_NOT_QUERY)) {
			StringBuilder query = new StringBuilder();
			
			if (search.containsKey(OptionKey.QUERY)) {
				Collection<String> inclusions = search.getCollection(OptionKey.QUERY, String.class);
				query
					.append("(")
					.append(joinEclExpressions(context, inclusions))
					.append(")");
			} else {
				query.append(Ecl.ANY);
			}
			
			if (search.containsKey(OptionKey.MUST_NOT_QUERY)) {
				Collection<String> exclusions = search.getCollection(OptionKey.MUST_NOT_QUERY, String.class);
				query
					.append(" MINUS (")
					.append(joinEclExpressions(context, exclusions))
					.append(")");
			}
			
			req.filterByEcl(query.toString());
		}
	}

	/**
	 * Join the given list of individual ECL expressions to a single ECL expression. Usually this uses OR boolean operator to generate the final
	 * expression, but some implementations might offer optimized alternatives. When there are more than 100 expressions present in the given
	 * collection the system will try to optimize single ID clauses into a proper ID filter so ECL evaluation is efficient.
	 * 
	 * @param context
	 * @param expressions
	 * @return
	 * @see Ecl#or(Collection)
	 */
	default String joinEclExpressions(ServiceProvider context, Collection<String> expressions) {
		// in case of having more than a hundred individual expressions, try to run an early optimization
		if (expressions.size() > 100) {
			var parser = context.service(EclParser.class);
			final SortedSet<String> singleConceptExpressions = new TreeSet<>();
			final SortedSet<String> singleConceptIds = new TreeSet<>();
			final SortedSet<String> remainingExpressions = new TreeSet<>();
			
			for (String expression : expressions) {
				ExpressionConstraint expressionConstraint = parser.parse(expression, getIgnoredSyntaxErrorCodes());
				Optional<EclConceptReference> eclConceptReference = Ecl.extractEclConceptReference(expressionConstraint);
				if (eclConceptReference.isPresent()) {
					String conceptId = eclConceptReference.get().getId();
					singleConceptExpressions.add(expression);
					singleConceptIds.add(conceptId);
				} else {
					remainingExpressions.add(expression);
				}
			}
			
			if (!singleConceptIds.isEmpty()) {
				final String individualConceptIdsEcl = String.format("(* {{ C id = %s }})", singleConceptIds.stream().collect(Collectors.joining(" ", "(", ")")));
				if (!remainingExpressions.isEmpty()) {
					return String.join(" OR ", individualConceptIdsEcl, Ecl.or(remainingExpressions));
				} else {
					return individualConceptIdsEcl;
				}
			}
			
		}
		return Ecl.or(expressions);
	}
	
	/**
	 * A {@link Set} of syntax error codes to ignore when parsing ECL expressions. By default this method returns an empty set and considers
	 * everything to be fully ECL compatible.
	 * 
	 * @return
	 */
	default Set<String> getIgnoredSyntaxErrorCodes() {
		return Collections.emptySet();
	}

	/**
	 * No-op request evaluator that returns zero results
	 * 
	 * @since 7.5
	 */
	ConceptSearchRequestEvaluator NOOP = new ConceptSearchRequestEvaluator() {

		@Override
		public Concepts evaluate(ResourceURI uri, ServiceProvider context, Options search) {
			return new Concepts(search.get(OptionKey.LIMIT, Integer.class), 0);
		}

	};

	/**
	 * Extract IDs from ID|TERM| like query strings. If the query does not have a PIPE character in it, then treat the entire query as an ID.
	 * 
	 * @since 7.7
	 * @return a collection of extracted IDs
	 * @see Concept#fromConceptString(String)
	 */
	default Collection<String> extractIds(Collection<String> queries) {
		return queries.stream().map(query -> Concept.fromConceptString(query)[0]).collect(Collectors.toList());
	}

}
