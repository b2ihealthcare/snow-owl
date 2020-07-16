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
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;

/**
 * @since 7.7
 */
public interface QueryOptimizer {

	public enum OptionKey {
		/**
		 * Language locales (tag, Accept-Language header, etc.) to use in order of preference when determining the display label or term for a match.
		 */
		LOCALES,
		
		/**
		 * Expressions used for inclusion of concepts.
		 */
		INCLUSIONS,
		
		/**
		 * Expressions used for excluding concepts from the included set.
		 */
		EXCLUSIONS, 
	}

	/**
	 * Suggests changes to the inclusion (and exclusion) expressions that results in a smaller number of queries, but
	 * evaluates to the same set of concepts for a given (fixed) code system version.
	 * 
	 * @param context - the context prepared for the search
	 * @param params - optimization parameters
	 * @return
	 */
	QueryExpressionDiffs optimize(BranchContext context, Options params);
	
	/**
	 * No-op expression optimizer
	 * @since 7.7
	 */
	QueryOptimizer NOOP = new QueryOptimizer() {
		@Override
		public QueryExpressionDiffs optimize(BranchContext context, Options params) {
			return QueryExpressionDiffs.EMPTY;
		}
	};
}
