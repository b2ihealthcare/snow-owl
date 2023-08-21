/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.compare.BranchCompareResult;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.config.IndexConfiguration;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Implementations of this interface can be used to generate
 * {@link TerminologyResourceCompareResult} instances.
 * 
 * @since 9.0
 */
@FunctionalInterface
public interface TerminologyResourceComparer {

	/**
	 * The default implementation of the interface assumes that all revision changes
	 * are related to the primary component of the resource, and so forwards branch
	 * comparison counts unmodified.
	 */
	TerminologyResourceComparer DEFAULT = (context, fromUri, toUri, termType, locales) -> {
		
		final IndexConfiguration indexConfiguration = context.service(RepositoryConfiguration.class).getIndexConfiguration();
		final int termPartitionSize = indexConfiguration.getTermPartitionSize();
		
		final ResourceURIPathResolver pathResolver = context.service(ResourceURIPathResolver.class);
		final List<String> branchPaths = pathResolver.resolve(context, ImmutableList.of(fromUri, toUri));
		final String baseBranch = branchPaths.get(0);
		final String compareBranch = branchPaths.get(1);
		
		final BranchCompareResult compareResult = RepositoryRequests.branching().prepareCompare()
			.setBase(baseBranch)
			.setCompare(compareBranch)
			.setExcludeComponentChanges(true)
			.build()
			.execute(context);
		
		// Record all changed components as a generic "Component change"
		final Set<String> changedComponentIds = compareResult.getChangedComponents()
			.stream()
			.map(ci -> ci.getComponentId())
			.collect(Collectors.toSet());

		final Map<String, String> termsById = newHashMap();
		for (final List<String> batch : Iterables.partition(changedComponentIds, termPartitionSize)) {
			CodeSystemRequests.prepareSearchConcepts()
				.filterByIds(batch)
				.filterByCodeSystemUri(toUri)
				.setPreferredDisplay(termType)
				.setLocales(locales)
				.setLimit(batch.size())
				.buildAsync()
				.execute(context)
				.forEach(c -> termsById.put(c.getId(), c.getTerm()));
		}
		
		final List<TerminologyResourceCompareResultItem> items = compareResult.getChangedComponents()
			.stream()
			.map(ci -> new TerminologyResourceCompareResultItem(
				ci.getComponentId(), // ID 
				termsById.getOrDefault(ci.getComponentId(), ci.getComponentId()), // Label (or ID as the fallback)
				TerminologyResourceCompareChangeKind.COMPONENT_CHANGE // "Change kind"
			))
			.collect(Collectors.toList());

		final TerminologyResourceCompareResult summary = new TerminologyResourceCompareResult(items, fromUri, toUri);
		summary.setNewComponents(compareResult.getTotalNew());
		summary.setChangedComponents(compareResult.getTotalChanged());
		summary.setDeletedComponents(compareResult.getTotalDeleted());
		return summary;
	};
	
	/** 
	 * The "no-op" implementation returns an unpopulated comparison summary.
	 */
	TerminologyResourceComparer NOOP = (context, fromUri, toUri, termType, locales) -> new TerminologyResourceCompareResult(fromUri, toUri);

	/**
	 * Generates a compare result based on the contents of the resource represented
	 * by the two resource URI arguments.
	 * 
	 * @param repositoryContext - the context to use for completing the request (must match the tooling of both resource URIs)
	 * @param fromUri - the resource URI representing the comparison baseline
	 * @param toUri - the resource URI representing the comparison target
	 * @param termType - term type for changed component labels
	 * @param locales - locales to use for changed component labels
	 * @return the populated change summary
	 */
	TerminologyResourceCompareResult compareResource(
		RepositoryContext repositoryContext, 
		ResourceURI fromUri, 
		ResourceURI toUri,
		String termType,
		List<ExtendedLocale> locales);
}
