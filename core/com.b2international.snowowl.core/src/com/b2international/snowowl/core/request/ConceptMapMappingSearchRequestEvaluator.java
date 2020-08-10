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
import com.b2international.snowowl.core.domain.ConceptMapMappings;
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 7.8
 */
public interface ConceptMapMappingSearchRequestEvaluator extends SetSearchRequestEvaluator<ConceptMapMappings> {

	/**
	 * No-op request evaluator that returns zero results
	 * @since 7.8
	 */
	ConceptMapMappingSearchRequestEvaluator NOOP = new ConceptMapMappingSearchRequestEvaluator() {
		
		@Override
		public ConceptMapMappings evaluate(CodeSystemURI uri, BranchContext context, Options search) {
			return new ConceptMapMappings(search.get(OptionKey.LIMIT, Integer.class), 0);
		}
	};

}
