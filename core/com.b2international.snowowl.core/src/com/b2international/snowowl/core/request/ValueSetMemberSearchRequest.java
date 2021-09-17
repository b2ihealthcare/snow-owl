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
import com.b2international.snowowl.core.domain.ValueSetMembers;

/**
* @since 7.7
*/
public final class ValueSetMemberSearchRequest extends SearchResourceRequest<ServiceProvider, ValueSetMembers> {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected ValueSetMembers createEmptyResult(int limit) {
		return new ValueSetMembers(limit, 0);
	}
	
	@Override
	protected ValueSetMembers doExecute(ServiceProvider context) throws IOException {
		final int limit = limit();
		
		Options options = Options.builder()
				.putAll(options())
				.put(MemberSearchRequestEvaluator.OptionKey.AFTER, searchAfter())
				.put(MemberSearchRequestEvaluator.OptionKey.LIMIT, limit)
				.put(MemberSearchRequestEvaluator.OptionKey.LOCALES, locales())
				.put(SearchResourceRequest.OptionKey.SORT_BY, sortBy())
				.build();
		
		// extract all ValueSetMemberSearchRequestEvaluator from all connected toolings and determine which ones can handle this request
		List<ValueSetMembers> evaluatedMembers = context.service(RepositoryManager.class)
			.repositories()
			.stream()
			.flatMap(repository -> {
				ValueSetMemberSearchRequestEvaluator evaluator = repository.service(ValueSetMemberSearchRequestEvaluator.class);
				Set<ResourceURI> targets = evaluator.evaluateSearchTargetResources(context, options);
				return targets.stream()
					.map(uri -> {
						return evaluator.evaluate(uri, context, options);
					});
			})
			.collect(Collectors.toList());
		
		// calculate grand total
		int total = 0;
		for (ValueSetMembers evaluatedMember : evaluatedMembers) {
			total += evaluatedMember.getTotal();
		}
		
		return new ValueSetMembers(
			evaluatedMembers.stream().flatMap(ValueSetMembers::stream).limit(limit).collect(Collectors.toList()), // TODO add manual sorting here if multiple resources have been fetched 
			null /* not supported across resources, TODO support it when a single ValueSet is being fetched */, 
			limit, 
			total
		);
	}
	
}
