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
package com.b2international.snowowl.core.request.resource;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.*;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BaseResourceConverter;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.Versions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * @since 8.12
 */
public abstract class BaseMetadataResourceConverter<R extends Resource, CR extends PageableCollectionResource<R>> extends BaseResourceConverter<ResourceDocument, R, CR> {

	public static final String ROOT_LABEL = "Root";
	
	private static final String MISSING_BUNDLE_TEMPLATE = "<bundle %s>";
	
	private final ResourceTypeConverter.Registry converters;
	
	public BaseMetadataResourceConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
		this.converters = context().service(ResourceTypeConverter.Registry.class);
	}
	
	@Override
	protected final RepositoryContext context() {
		return (RepositoryContext) super.context();
	}
	
	@Override
	protected final R toResource(ResourceDocument doc) {
		return (R) converters.toResource(doc);
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	public void expand(List<R> results) {
		expandCommits(results);
		expandUpdateAtCommit(results);
		expandResourcePathLabels(results);
		expandVersions(results, expand(), options -> getLimit(options), locales(), context());
		
		expandDependencyUpgrades(results);
		expandDependencyResources(results);
		
		// expand additional fields via pluggable converters
		converters.expand(context(), expand(), locales(), results);
	}
	
	protected final void expandDependencyResources(List<R> results) {
		if (!expand().containsKey("dependencies_resource")) {
			return;
		}
		
		final Options expandOptions = expand().getOptions("dependencies_resource");
		
		// index result dependencies by their Resource IDs in a Multimap to easily update them later on when we get back resource data
		// FIXME it would be great to fetch the versioned state of the resource but that can only be executed one-by-one, which slows things down
		Multimap<String, Dependency> dependenciesToExpand = HashMultimap.create();
		for (Resource res : results) {
			if (res instanceof TerminologyResource tres) {
				TerminologyResource terminologyResource = (TerminologyResource) res;
				if (!CompareUtils.isEmpty(terminologyResource.getDependencies())) {
					for (Dependency dep : terminologyResource.getDependencies()) {
						dependenciesToExpand.put(dep.getUri().getResourceUri().getResourceId(), dep);
					}
				}
			}
		}
		
		// fetch all referenced resources by their ID for now
		ResourceRequests.prepareSearch()
			.filterByIds(dependenciesToExpand.keySet())
			.setLimit(dependenciesToExpand.keySet().size())
			.setExpand(expandOptions.getOptions("expand"))
			.build()
			.execute(context())
			.forEach(resource -> {
				// update the Multimap by removing all Dependency entries from it that we could handle
				dependenciesToExpand.removeAll(resource.getId()).forEach(dependencyToExpand -> {
					dependencyToExpand.setResource((TerminologyResource) resource);
				});
			});
		
		if (!dependenciesToExpand.isEmpty()) {
			context().log().warn("The following resources could not be expanded for their dependencies: {}", ImmutableSortedSet.copyOf(dependenciesToExpand.keySet()));
		}
	}

	protected final void expandDependencyUpgrades(List<R> results) {
		if (!expand().containsKey("dependencies_upgrades")) {
			return;
		}
		
		final Options expandOptions = expand().getOptions("dependencies_upgrades");
		
		// index result dependencies by their URIs in a Multimap to easily update them later on when we get back newer dependency versions
		Multimap<ResourceURI, Dependency> dependenciesToExpand = HashMultimap.create();
		for (Resource res : results) {
			if (res instanceof TerminologyResource tres) {
				TerminologyResource terminologyResource = (TerminologyResource) res;
				if (!CompareUtils.isEmpty(terminologyResource.getDependencies())) {
					for (Dependency dep : terminologyResource.getDependencies()) {
						dependenciesToExpand.put(dep.getUri().getResourceUri(), dep);
					}
				}
			}
		}
		
		final TreeMultimap<ResourceURI, Version> versionsByResource = TreeMultimap.create(
				Comparator.naturalOrder(), 
				Comparator.comparing(Version::getEffectiveTime));
		
		ResourceRequests.prepareSearchVersion()
			.setLimit(1_000)
			.filterByResources(dependenciesToExpand.keySet().stream().map(ResourceURI::withoutPath).map(ResourceURI::toString).collect(Collectors.toSet()))
			.setExpand(expandOptions.getOptions("expand"))
			.stream(context())
			.flatMap(Versions::stream)
			.forEach(version -> versionsByResource.put(version.getResource(), version));

		for (final Dependency dep : dependenciesToExpand.values()) {
			final ResourceURI uri = dep.getUri().getResourceUri();
			
			final ResourceURI resource = uri.withoutPath();
			final String version = uri.getPath();

			final NavigableSet<Version> candidates = versionsByResource.get(resource);
			
			final Optional<Version> currentExtensionVersion = candidates.stream()
					.filter(v -> v.getVersion().equals(version))
					.findFirst();
			
			final Optional<List<ResourceURI>> upgradeUris = currentExtensionVersion.map(currentVersion -> {
				final SortedSet<Version> upgradeVersions = candidates.tailSet(currentVersion, false);
				return upgradeVersions.stream()
						.map(upgradeVersion -> upgradeVersion.getVersionResourceURI())
						.collect(Collectors.toList());
			});
	
			dep.setUpgrades(upgradeUris.orElseGet(List::of));
		}
	}
	
	protected final void expandVersions(List<R> results, Options expand, Function<Options, Integer> getLimit, List<ExtendedLocale> locales, RepositoryContext context) {
		if (expand.containsKey(TerminologyResource.Expand.VERSIONS)) {
			Options expandOptions = expand.getOptions(TerminologyResource.Expand.VERSIONS);
			// version searches must be performed on individual terminology resources to provide correct results
			results.stream()
				.filter(TerminologyResource.class::isInstance)
				.map(TerminologyResource.class::cast)
				.forEach(res -> {
					Versions versions = ResourceRequests.prepareSearchVersion()
						.filterByResource(res.getResourceURI())
						.setLimit(getLimit.apply(expandOptions))
						.setFields(expandOptions.containsKey(FIELD_OPTION_KEY) ? expandOptions.getList(FIELD_OPTION_KEY, String.class) : null)
						.sortBy(expandOptions.containsKey(SORT_OPTION_KEY) ? expandOptions.getString(SORT_OPTION_KEY) : null)
						.setLocales(locales)
						.build()
						.execute(context);
					res.setVersions(versions);
				});
		}
	}
	
	protected final void expandCommits(List<R> results) {
		if (expand().containsKey(TerminologyResource.Expand.COMMITS)) {
			Options expandOptions = expand().getOptions(TerminologyResource.Expand.COMMITS);
			// commit searches must be performed individually on each resource to provide correct results
			var commitSearchRequests = results.stream()
				.filter(TerminologyResource.class::isInstance)
				.map(TerminologyResource.class::cast)
				.map(res -> {
					return RepositoryRequests.commitInfos().prepareSearchCommitInfo()
						.filterByBranch(res.getBranchPath())
						.filterByTimestamp(
							expandOptions.get(TerminologyResource.Expand.TIMESTAMP_FROM_OPTION_KEY, Long.class), 
							expandOptions.get(TerminologyResource.Expand.TIMESTAMP_TO_OPTION_KEY, Long.class))
						.setLimit(getLimit(expandOptions))
						.setFields(expandOptions.containsKey(FIELD_OPTION_KEY) ? expandOptions.getList(FIELD_OPTION_KEY, String.class) : CommitInfo.Fields.DEFAULT_FIELD_SELECTION)
						.sortBy(expandOptions.containsKey(SORT_OPTION_KEY) ? expandOptions.getString(SORT_OPTION_KEY) : null)
						.setLocales(locales())
						.build(res.getToolingId())
						.executeWithContext(context())
						.then(commits -> {
							res.setCommits(commits);
							return commits;
						});
				})
				.collect(Collectors.toList());
			
			// wait until all search requests resolve, or timeout of 3 minutes reached
			Promise.all(commitSearchRequests).getSync(3, TimeUnit.MINUTES);
		}
	}
	
	protected final void expandUpdateAtCommit(List<R> results) {
		if (expand().containsKey(Resource.Expand.UPDATED_AT_COMMIT)) {
			// expand updatedAtCommit object for each resource one-by-one
			results.stream()
				.forEach(res -> {
					RepositoryRequests.commitInfos().prepareSearchCommitInfo()
						.one()
						.filterByBranch(Branch.MAIN_PATH) // all resource commits go to the main branch
						.filterByAffectedComponent(res.getId())
						.setFields(CommitInfo.Fields.DEFAULT_FIELD_SELECTION)
						.sortBy("timestamp:desc")
						.build()
						.execute(context())
						.first()
						.ifPresent(res::setUpdatedAtCommit);
				});
		}
	}

	protected final void expandResourcePathLabels(List<R> results) {
		if (expand().containsKey(Resource.Expand.RESOURCE_PATH_LABELS)) {
			
			final Set<String> collectionIds = results.stream()
				.map(Resource::getResourcePathSegments)
				.<String>flatMap(List::stream)
				.collect(Collectors.toSet());
			
			collectionIds.remove(IComponent.ROOT_ID);
			
			final Map<String, String> collectionLabelsById = ResourceRequests.prepareSearchCollections()
				.filterByIds(collectionIds)
				.setFields(Resource.Fields.ID, Resource.Fields.TITLE)
				.setLimit(collectionIds.size())
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(b -> b.getId(), b -> b.getTitle()));
			
			collectionLabelsById.put(IComponent.ROOT_ID, ROOT_LABEL);
			
			results.forEach(r -> {
				r.setResourcePathLabels(r.getResourcePathSegments()
					.stream()
					.map(id -> collectionLabelsById.computeIfAbsent(id, key -> String.format(MISSING_BUNDLE_TEMPLATE, key)))
					.collect(Collectors.toList()));
			});
		}
	}

}
