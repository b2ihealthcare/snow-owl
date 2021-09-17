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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemSearchRequestBuilder;
import com.b2international.snowowl.core.domain.Concepts;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * A generic concept search request that can be executed in any code system using generic query expressions and filters to get back primary
 * components/concepts from that code system.
 * 
 * @since 7.5
 * @see ConceptSearchRequestEvaluator
 * @see ConceptSearchRequestBuilder
 */
public final class ConceptSearchRequest extends SearchResourceRequest<ServiceProvider, Concepts> {

	private static final long serialVersionUID = 1L;

	public enum OptionKey {
		
		/**
		 * Filters concepts by their associated resource.
		 */
		CODESYSTEM,
		
	}
	
	@Override
	protected Concepts createEmptyResult(int limit) {
		return new Concepts(limit, 0);
	}

	@Override
	protected Concepts doExecute(ServiceProvider context) throws IOException {
		final int limit = limit();
		
		Options conceptSearchOptions = Options.builder()
				.putAll(options())
				.put(ConceptSearchRequestEvaluator.OptionKey.ID, componentIds())
				.put(ConceptSearchRequestEvaluator.OptionKey.AFTER, searchAfter())
				.put(ConceptSearchRequestEvaluator.OptionKey.LIMIT, limit)
				.put(ConceptSearchRequestEvaluator.OptionKey.LOCALES, locales())
				.put(ConceptSearchRequestEvaluator.OptionKey.FIELDS, fields())
				.put(ConceptSearchRequestEvaluator.OptionKey.EXPAND, expand())
				.put(SearchResourceRequest.OptionKey.SORT_BY, sortBy())
				.build();
		
		final CodeSystemSearchRequestBuilder codeSystemSearchReq = CodeSystemRequests.prepareSearchCodeSystem()
				.all();
		
		final Map<ResourceURI, ResourceURI> codeSystemResourceFiltersByResource;
		if (containsKey(OptionKey.CODESYSTEM)) {
			// remove path so we can use the code resource URI as key
			codeSystemResourceFiltersByResource = Maps.uniqueIndex(getCollection(OptionKey.CODESYSTEM, ResourceURI.class), uri -> uri.withoutPath()); 
			// for filtering use the keys
			codeSystemSearchReq.filterByIds(codeSystemResourceFiltersByResource.keySet().stream().map(ResourceURI::getResourceId).collect(Collectors.toSet())); 
		} else {
			codeSystemResourceFiltersByResource = Collections.emptyMap();
		}
//				.filterByToolingIds(toolingIds) TODO perform TOOLING filtering
//				.filterByUrls(urls) TODO perform URL filtering
		
		List<Concepts> concepts = codeSystemSearchReq
			.buildAsync()
			.execute(context)
			.stream()
			.map(codeSystem -> {
				final ResourceURI uriToEvaluateOn = codeSystemResourceFiltersByResource.getOrDefault(codeSystem.getResourceURI(), codeSystem.getResourceURI());
				return context.service(RepositoryManager.class).get(codeSystem.getToolingId()).service(ConceptSearchRequestEvaluator.class).evaluate(uriToEvaluateOn, context, conceptSearchOptions);
			})
//			.sorted(comparator) // TODO perform Java SORT on Concept fields
//			.limit(limit)
			.collect(Collectors.toList());
		
		// for single CodeSystem searches, sorting, paging works as it should
		if (concepts.size() == 1) {
			return Iterables.getOnlyElement(concepts);
		}
		
		// otherwise, check if searchAfter was used, as it would return bogus results; it can not be applied across code systems
		if (searchAfter() != null) {
			throw new BadRequestException("searchAfter is not supported in Concept Search API for multiple code systems.");
		}
		
		// calculate grand total
		int total = 0;
		for (Concepts conceptsToAdd : concepts) {
			total += conceptsToAdd.getTotal();
		}
		
		return new Concepts(
			concepts.stream().flatMap(Concepts::stream).limit(limit).collect(Collectors.toList()), // TODO add manual sorting here if multiple resources have been fetched 
			null, /* not supported across codesystems */
			limit, 
			total
		);
	}

}
