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
import com.b2international.snowowl.core.domain.SetMember;
import com.b2international.snowowl.core.domain.SetMembers;
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 7.7
 */
public interface SetMemberSearchRequestEvaluator {

	public enum OptionKey {

		/**
		 * Search for members in the specified set.
		 */
		SET,
		
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
	 * Evaluate the given search options on the given context and return generic {@link SetMember} instances back in a {@link SetMembers} pageable
	 * resource.
	 * 
	 * @param uri - the code system uri where the search is being evaluated
	 * @param context - the context prepared for the search
	 * @param search - the search filters and options to apply to the code system specific search
	 * @return
	 */
	SetMembers evaluate(CodeSystemURI uri, BranchContext context, Options search);
	
	/**
	 * No-op request evaluator that returns zero results
	 * @since 7.7
	 */
	SetMemberSearchRequestEvaluator NOOP = new SetMemberSearchRequestEvaluator() {
		
		@Override
		public SetMembers evaluate(CodeSystemURI uri, BranchContext context, Options search) {
			return new SetMembers(search.get(OptionKey.LIMIT, Integer.class), 0);
		}
	};

}
