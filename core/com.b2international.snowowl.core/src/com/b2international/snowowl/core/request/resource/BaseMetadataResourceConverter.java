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
	public void expand(List<R> results) {
		expandCommits(results);
		expandUpdateAtCommit(results);
		expandResourcePathLabels(results);
		expandVersions(results, expand(), options -> getLimit(options), locales(), context());
		
		expandDependencyUpgrades(results);
		
		// expand additional fields via pluggable converters
		converters.expand(context(), expand(), locales(), results);
	}
	
	protected final void expandDependencyUpgrades(List<R> results) {
		if (!expand().containsKey("dependencies_upgrades")) {
			return;
		}
		
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
			
			final Set<String> bundleIds = results.stream()
				.map(Resource::getResourcePathSegments)
				.<String>flatMap(List::stream)
				.collect(Collectors.toSet());
			
			bundleIds.remove(IComponent.ROOT_ID);
			
			final Map<String, String> bundleLabelsById = ResourceRequests.bundles()
				.prepareSearch()
				.filterByIds(bundleIds)
				.setFields(Resource.Fields.ID, Resource.Fields.TITLE)
				.setLimit(bundleIds.size())
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(b -> b.getId(), b -> b.getTitle()));
			
			bundleLabelsById.put(IComponent.ROOT_ID, ROOT_LABEL);
			
			results.forEach(r -> {
				r.setResourcePathLabels(r.getResourcePathSegments()
					.stream()
					.map(id -> bundleLabelsById.computeIfAbsent(id, key -> String.format(MISSING_BUNDLE_TEMPLATE, key)))
					.collect(Collectors.toList()));
			});
		}
	}

}
