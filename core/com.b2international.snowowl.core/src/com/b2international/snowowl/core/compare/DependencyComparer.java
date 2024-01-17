/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.branch.compare.BranchCompareResult;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.google.common.collect.ImmutableList;

/**
 * Implementations of this interface can be used to generate
 * {@link AnalysisCompareResult} instances.
 * 
 * @since 9.0.0
 */
@FunctionalInterface
public interface DependencyComparer {

	/**
	 * The default implementation filters all revision-level changes to find out
	 * component change counts for the provided type.
	 */
	public class Default implements DependencyComparer {

		private final String componentType;

		public Default(final String componentType) {
			this.componentType = componentType;
		}

		@Override
		public AnalysisCompareResult compareResource(
			final RepositoryContext context, 
			final ResourceURIWithQuery fromUri,
			final ResourceURIWithQuery toUri, 
			final boolean includeChanges
		) {
			final ResourceURI fromWithoutQuery = fromUri.getResourceUri();
			final ResourceURI toWithoutQuery = toUri.getResourceUri();
			final ResourceURIPathResolver pathResolver = context.service(ResourceURIPathResolver.class);

			final List<String> branchPaths = pathResolver.resolve(context, ImmutableList.of(fromWithoutQuery, toWithoutQuery));
			final String baseBranch = branchPaths.get(0);
			final String compareBranch = branchPaths.get(1);

			final BranchCompareResult compareResult = RepositoryRequests.branching()
				.prepareCompare()
				.setBase(baseBranch)
				.setCompare(compareBranch)
				.build()
				.execute(context);

			final Collection<ComponentIdentifier> changedComponents = compareResult.getChangedComponents();
			final AnalysisCompareResult summary;

			if (changedComponents.isEmpty()) {

				// No changed components to return with this response
				summary = new AnalysisCompareResult(fromUri, toUri);
				summary.setChangedComponents(0);

			} else {

				final Set<String> changedComponentIds = changedComponents
					.stream()
					.filter(ci -> componentType.equals(ci.getComponentType()))
					.map(ci -> ci.getComponentId())
					.collect(Collectors.toSet());

				if (includeChanges) {

					// Record all relevant changed components as a generic "Component change"
					final List<AnalysisCompareResultItem> items = changedComponents.stream()
						.map(ci -> new AnalysisCompareResultItem(ci.getComponentId(), AnalysisCompareChangeKind.COMPONENT_CHANGE))
						.collect(Collectors.toList());

					summary = new AnalysisCompareResult(items, fromUri, toUri);
					summary.setChangedComponents(items.size());
					
				} else {
					
					// No changed components to return with this response, but set the counter
					summary = new AnalysisCompareResult(fromUri, toUri);
					summary.setChangedComponents(changedComponentIds.size());
				}
			}

			// Populate the other two counters as well
			summary.setNewComponents(getComponentCount(compareResult.getNewComponents()));
			summary.setDeletedComponents(getComponentCount(compareResult.getDeletedComponents()));
			return summary;
		}

		private Integer getComponentCount(final Collection<ComponentIdentifier> componentIdentifiers) {
			return (int) componentIdentifiers.stream()
				.filter(ci -> componentType.equals(ci.getComponentType()))
				.count();
		}
	}

	/** 
	 * The "no-op" implementation returns an unpopulated comparison summary.
	 */
	DependencyComparer NOOP = (context, fromUri, toUri, includeChanges) -> new AnalysisCompareResult(fromUri, toUri);

	/**
	 * Generates a dependency compare result based on the contents of the resource represented
	 * by the two resource URI arguments.
	 * 
	 * @param repositoryContext - the context to use for completing the request
	 * (must match the tooling of both resource URIs)
	 * @param fromUri - the resource URI representing the comparison baseline
	 * @param toUri - the resource URI representing the comparison target
	 * @param includeChanges - set to <code>true</code> when itemized component
	 * changes should be included in the response
	 * @return the populated change summary
	 */
	AnalysisCompareResult compareResource(
		RepositoryContext repositoryContext, 
		ResourceURIWithQuery fromUri, 
		ResourceURIWithQuery toUri,
		boolean includeChanges);
}
