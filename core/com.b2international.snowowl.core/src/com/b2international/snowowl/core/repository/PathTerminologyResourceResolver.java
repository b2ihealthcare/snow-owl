/*
 * Copyright 2020-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.Version;
import com.google.common.primitives.Ints;

/**
 * @since 7.5
 */
@FunctionalInterface
public interface PathTerminologyResourceResolver {

	/**
	 * Returns the closest relative {@link TerminologyResource} for the given reference branch.
	 * 
	 * @param context - the context to use for the resolution of the resource
	 * @param toolingId - the tooling identifier of the requested resource, may not be <code>null</code>
	 * @param referenceBranch - the reference branch, may not be <code>null</code>
	 * 
	 * @return the closest relative {@link TerminologyResource}
	 * 
	 * @throws BadRequestException - if there is no relative {@link TerminologyResource} can be found for the given reference branch
	 */
	<T extends TerminologyResource> T resolve(ServiceProvider context, String toolingId, String referenceBranch);
	
	/**
	 * @since 8.0
	 */
	class Default implements PathTerminologyResourceResolver {
		
		@Override
		public <T extends TerminologyResource> T resolve(ServiceProvider context, String toolingId, String referenceBranch) {
			final Long timestamp;
			
			final String branchWithoutSuffix;
			if (referenceBranch.contains(RevisionIndex.AT_CHAR)) {
				String[] parts = referenceBranch.split(RevisionIndex.AT_CHAR);
				branchWithoutSuffix = parts[0];
				timestamp = Long.valueOf(parts[1]);
			} else {
				branchWithoutSuffix = referenceBranch;
				timestamp = null;
			}
			
			final List<String> ancestorsAndSelf = BranchPathUtils.getAllPaths(branchWithoutSuffix);

			/*
			 * Look for potential resource working paths. Note that if a resource was already upgraded to a newer base version,
			 * it might not appear in this result set.
			 */
			final Map<String, T> resourcesById = ResourceRequests.prepareSearch()
				.all()
				.filterByToolingId(toolingId)
				.filterByBranches(ancestorsAndSelf)
				.buildAsync(timestamp)
				.getRequest()
				.execute(context)
				.stream()
				.filter(TerminologyResource.class::isInstance)
				.collect(Collectors.toMap(t -> t.getId(), t -> (T) t));

			/*
			 * To get a more complete picture, also retrieve the last time any of the paths given above were set as 
			 * a code system working branch, based on version information.
			 */
			final Stream<Version> latestVersionsById = ResourceRequests.prepareSearchVersion()
				.all()
				.filterByToolingId(toolingId)
				.filterByResourceBranchPaths(ancestorsAndSelf)
				.buildAsync(timestamp)
				.getRequest()
				.execute(context)
				.stream()
				// Retain the version with the most recent "created at" timestamp for each encountered resource ID
				.collect(Collectors.groupingBy(
					v -> v.getResourceId(), 
					Collectors.maxBy(Comparator.comparing(v -> v.getCreatedAt()))))
				.values()
				.stream()
				.map(Optional::get);
			
			latestVersionsById.forEachOrdered(v -> {
				final String resourceId = v.getResourceId();
				
				// Ignore if this resource is already known
				if (resourcesById.containsKey(resourceId)) {
					return;
				}
				
				// We need to load this resource using the timestamp retrieved from the version document
				final Long resourceTimestamp = v.getCreatedAt();
				
				final T resourceAtTimestamp = (T) ResourceRequests.prepareGet(resourceId)
					.buildAsync(resourceTimestamp)
					.getRequest()
					.execute(context);
					
				resourcesById.put(resourceId, resourceAtTimestamp);
			});
			
			// Return the resource whose working branch is the closest (prefix) match to the path given
			return resourcesById.values()
				.stream()
				.sorted((t1, t2) -> Ints.compare(ancestorsAndSelf.indexOf(t1.getBranchPath()), ancestorsAndSelf.indexOf(t2.getBranchPath())))
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Terminology Resource", referenceBranch));
		}
	}
}
