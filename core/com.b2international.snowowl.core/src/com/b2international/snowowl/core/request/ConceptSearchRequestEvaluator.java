/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.uri.CodeSystemURI;

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
		 * Set the preferred display type to return
		 */
		DISPLAY,
		
		/**
		 * Filters descriptions by their Description Type.
		 */
		DESCRIPTION_TYPE,
	}

	/**
	 * Evaluate the given search options on the given context and return generic {@link Concept} instances back in a {@link Concepts} pageable
	 * resource.
	 * 
	 * @param uri
	 *            - the code system uri where the search is being evaluated
	 * @param context
	 *            - the context prepared for the search
	 * @param search
	 *            - the search filters and options to apply to the code system specific search
	 * @return
	 */
	Concepts evaluate(CodeSystemURI uri, BranchContext context, Options search);

	default Concept toConcept(CodeSystemURI codeSystem, IComponent concept, String iconId, String term) {
		Concept result = new Concept(codeSystem.toString(), concept.getTerminologyComponentId());
		result.setId(concept.getId());
		result.setReleased(concept.isReleased());
		result.setIconId(iconId);
		result.setTerm(term);
		return result;
	}

	default void evaluateIdFilters(SearchResourceRequestBuilder<?, ?, ?> requestBuilder, Options search) {
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
	 * No-op request evaluator that returns zero results
	 * 
	 * @since 7.5
	 */
	ConceptSearchRequestEvaluator NOOP = new ConceptSearchRequestEvaluator() {

		@Override
		public Concepts evaluate(CodeSystemURI uri, BranchContext context, Options search) {
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
