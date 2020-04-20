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

import java.io.IOException;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * A generic concept search request that can be executed in any code system using generic query expressions and filters to get back primary
 * components/concepts from that code system.
 * 
 * @since 7.5
 * @see ConceptSearchRequestEvaluator
 * @see ConceptSearchRequestBuilder
 */
public final class ConceptSearchRequest extends SearchResourceRequest<BranchContext, Concepts> {

	@Override
	protected Concepts createEmptyResult(int limit) {
		return new Concepts(limit, 0);
	}

	@Override
	protected Concepts doExecute(BranchContext context) throws IOException {
		Options options = Options.builder()
				.putAll(options())
				.put(ConceptSearchRequestEvaluator.OptionKey.ID, componentIds())
				.put(ConceptSearchRequestEvaluator.OptionKey.AFTER, searchAfter())
				.put(ConceptSearchRequestEvaluator.OptionKey.LIMIT, limit())
				.put(ConceptSearchRequestEvaluator.OptionKey.LOCALES, locales())
				.put(SearchResourceRequest.OptionKey.SORT_BY, sortBy())
				.build();
		return context.service(ConceptSearchRequestEvaluator.class).evaluate(context.service(CodeSystemURI.class), context, options);
	}

}
