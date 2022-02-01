/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceTypeConverter;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.version.Versions;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 8.0
 */
public final class ResourceConverter extends BaseResourceConverter<ResourceDocument, Resource, Resources> {

	public static final String ROOT_LABEL = "Root";
	
	private static final String MISSING_BUNDLE_TEMPLATE = "<bundle %s>";
	
	private final ResourceTypeConverter.Registry converters;

	public ResourceConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
		this.converters = context().service(ResourceTypeConverter.Registry.class);
	}
	
	@Override
	protected RepositoryContext context() {
		return (RepositoryContext) super.context();
	}

	@Override
	protected Resources createCollectionResource(List<Resource> results, String searchAfter, int limit, int total) {
		return new Resources(results, searchAfter, limit, total);
	}

	@Override
	protected Resource toResource(ResourceDocument doc) {
		return converters.toResource(doc);
	}
	
	@Override
	public void expand(List<Resource> results) {
		if (results.isEmpty()) {
			return;
		}

		expandCommits(results);
		expandUpdateAtCommit(results);
		expandResourcePathLabels(results);
		expandVersions(results);
		
		// expand additional fields via pluggable converters
		converters.expand(context(), expand(), locales(), results);
	}

	private void expandUpdateAtCommit(List<Resource> results) {
		if (expand().containsKey(Resource.Expand.UPDATED_AT_COMMIT)) {
			// expand updatedAtCommit object for each resource one-by-one
			results.stream()
				.forEach(res -> {
					RepositoryRequests.commitInfos().prepareSearchCommitInfo()
						.one()
						.filterByBranch(Branch.MAIN_PATH) // all resource commits go to the main branch
						.filterByAffectedComponent(res.getId())
						.sortBy("timestamp:desc")
						.build()
						.execute(context())
						.first()
						.ifPresent(res::setUpdatedAtCommit);
				});
		}
	}

	private void expandResourcePathLabels(List<Resource> results) {
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

	private void expandVersions(List<Resource> results) {
		if (expand().containsKey(TerminologyResource.Expand.VERSIONS)) {
			Options expandOptions = expand().getOptions(TerminologyResource.Expand.VERSIONS);
			// version searches must be performed on individual terminology resources to provide correct results
			results.stream()
				.filter(TerminologyResource.class::isInstance)
				.map(TerminologyResource.class::cast)
				.forEach(res -> {
					Versions versions = ResourceRequests.prepareSearchVersion()
						.filterByResource(res.getResourceURI())
						.setLimit(getLimit(expandOptions))
						.setFields(expandOptions.containsKey("fields") ? expandOptions.getList("fields", String.class) : null)
						.sortBy(expandOptions.containsKey("sort") ? expandOptions.getString("sort") : null)
						.setLocales(locales())
						.build()
						.execute(context());
					res.setVersions(versions);
				});
		}
	}
	
	private void expandCommits(List<Resource> results) {
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
						.setFields(expandOptions.containsKey("fields") ? expandOptions.getList("fields", String.class) : CommitInfo.Fields.DEAFULT_FIELD_SELECTION)
						.sortBy(expandOptions.containsKey("sort") ? expandOptions.getString("sort") : null)
						.setLocales(locales())
						.build(res.getToolingId())
						.execute(context().service(IEventBus.class))
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
	
}
