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

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.ConceptMapMappings;

/**
* @since 7.8
*/
public final class ConceptMapMappingSearchRequest extends SearchResourceRequest<ServiceProvider, ConceptMapMappings> {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected ConceptMapMappings createEmptyResult(int limit) {
		return new ConceptMapMappings(limit, 0);
	}
	
	@Override
	protected ConceptMapMappings doExecute(ServiceProvider context) throws IOException {
		final int limit = limit();
		
		Options options = Options.builder()
				.putAll(options())
				.put(ConceptMapMappingSearchRequestEvaluator.OptionKey.AFTER, searchAfter())
				.put(ConceptMapMappingSearchRequestEvaluator.OptionKey.LIMIT, limit())
				.put(ConceptMapMappingSearchRequestEvaluator.OptionKey.LOCALES, locales())
				.put(SearchResourceRequest.OptionKey.SORT_BY, sortBy())
				.build();
		
		List<ConceptMapMappings> evaluatedMappings = context.service(RepositoryManager.class)
			.repositories()
			.stream()
			.flatMap(repository -> {
				ConceptMapMappingSearchRequestEvaluator evaluator = repository.service(ConceptMapMappingSearchRequestEvaluator.class);
				Set<ResourceURI> targets = evaluator.evaluateSearchTargetResources(context, options);
				return targets.stream()
					.map(uri -> {
						return evaluator.evaluate(uri, context, options);
					});
			})
			.collect(Collectors.toList());
		
		// calculate grand total
		int total = 0;
		for (ConceptMapMappings evaluatedMember : evaluatedMappings) {
			total += evaluatedMember.getTotal();
		}
		
		return new ConceptMapMappings(
			evaluatedMappings.stream().flatMap(ConceptMapMappings::stream).limit(limit).collect(Collectors.toList()), // TODO add manual sorting here if multiple resources have been fetched 
			null /* not supported across resources, TODO support it when a single Conceptmap is being fetched */, 
			limit, 
			total
		);
	}
	
}
