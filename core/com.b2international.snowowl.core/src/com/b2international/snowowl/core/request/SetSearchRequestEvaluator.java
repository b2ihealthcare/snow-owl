/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.domain.ConceptMapMappings;

/**
 * @since 7.8
 */
public interface SetSearchRequestEvaluator<R> {
	
	public enum OptionKey {

		/**
		 * Search for mappings in the specified set.
		 */
		SET,
		
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
		 * Search for mappings with the specified source tooling id.
		 */
		SOURCE_TOOLING_ID,
		
		/**
		 * Search for mappings with the specified referenced component id(s)
		 */
		REFERENCED_COMPONENT,
		
		/**
		 * Search for mappings with the specified map target id(s)
		 */
		MAP_TARGET,
		
		/**
		 * Matches concept map mappings where either the source or target component id matches the given value.
		 */
		COMPONENT,
		
		/**
		 * Set the preferred display type to return
		 */
		DISPLAY, 
		
		/**
		 * Matches concept map mappings where the mapping's status is either active or inactive based on the given value.
		 */
		ACTIVE,
	}
	
	/**
	 * Evaluate the given search options on the given context and return generic {@link ConceptMapMapping} instances back in a {@link ConceptMapMappings} pageable
	 * resource.
	 * 
	 * @param uri - the code system uri where the search is being evaluated
	 * @param context - the context prepared for the search
	 * @param search - the search filters and options to apply to the code system specific search
	 * @return resource 
	 */
	R evaluate(ResourceURI uri, BranchContext context, Options search);

}
