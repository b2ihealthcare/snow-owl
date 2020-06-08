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
		 * A term filter that matches concepts having an exact term match regardless of case.
		 */
		TERM_EXACT,

		/**
		 * The minimum number of terms to match.
		 */
		MIN_TERM_MATCH,
		
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
	}

	/**
	 * Evaluate the given search options on the given context and return generic {@link Concept} instances back in a {@link Concepts} pageable
	 * resource.
	 * 
	 * @param uri - the code system uri where the search is being evaluated
	 * @param context - the context prepared for the search
	 * @param search - the search filters and options to apply to the code system specific search
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
	
	/**
	 * No-op request evaluator that returns zero results
	 * @since 7.5
	 */
	ConceptSearchRequestEvaluator NOOP = new ConceptSearchRequestEvaluator() {
		
		@Override
		public Concepts evaluate(CodeSystemURI uri, BranchContext context, Options search) {
			return new Concepts(search.get(OptionKey.LIMIT, Integer.class), 0);
		}
	};
	
}
