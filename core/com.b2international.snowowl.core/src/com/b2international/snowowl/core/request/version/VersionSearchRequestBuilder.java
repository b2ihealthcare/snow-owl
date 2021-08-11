/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.version;

import java.time.LocalDate;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchPageableCollectionResourceRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.version.VersionSearchRequest.OptionKey;
import com.b2international.snowowl.core.version.Versions;

/**
 * @since 4.7
 */
public final class VersionSearchRequestBuilder 
		extends SearchPageableCollectionResourceRequestBuilder<VersionSearchRequestBuilder, RepositoryContext, Versions>
 		implements ResourceRepositoryRequestBuilder<Versions> {

	public VersionSearchRequestBuilder() {
		super();
	}

	public VersionSearchRequestBuilder filterByResource(ResourceURI resourceUri) {
		return addOption(OptionKey.RESOURCE, resourceUri == null ? null : resourceUri.toString());
	}
	
	public VersionSearchRequestBuilder filterByResource(String resourceUri) {
		return addOption(OptionKey.RESOURCE, resourceUri);
	}
	
	public VersionSearchRequestBuilder filterByResources(Iterable<String> resourceUris) {
		return addOption(OptionKey.RESOURCE, resourceUris);
	}
	
	public VersionSearchRequestBuilder filterByCreatedAt(Long createdAtFrom, Long createdAtTo) {
		return addOption(OptionKey.CREATED_AT_FROM, createdAtFrom)
				.addOption(OptionKey.CREATED_AT_TO, createdAtTo);
	}
	/**
	 * Filter versions by their version tag.
	 * 
	 * @param version - the version tag to look for.
	 * @return
	 */
	public VersionSearchRequestBuilder filterByVersionId(String version) {
		return addOption(OptionKey.VERSION, version);
	}
	
	/**
	 * Filter versions by their version tag.
	 * 
	 * @param versions - the version tags to look for.
	 * @return
	 */
	public VersionSearchRequestBuilder filterByVersionIds(Iterable<String> versions) {
		return addOption(OptionKey.VERSION, versions);
	}
	
	/**
	 * @param effectiveTime - the {@link LocalDate}'s epoch time in UTC to use to match versions
	 * @return
	 */
	public VersionSearchRequestBuilder filterByEffectiveTime(LocalDate effectiveTime) {
		if (effectiveTime != null) {
			final long effectiveTimeValue = EffectiveTimes.getEffectiveTime(effectiveTime);
			return addOption(OptionKey.EFFECTIVE_TIME_START, effectiveTimeValue).addOption(OptionKey.EFFECTIVE_TIME_END, effectiveTimeValue);
		} else {
			return getSelf();
		}
	}
	
	public VersionSearchRequestBuilder filterByEffectiveTime(long effectiveTimeStart, long effectiveTimeEnd) {
		return addOption(OptionKey.EFFECTIVE_TIME_START, effectiveTimeStart).addOption(OptionKey.EFFECTIVE_TIME_END, effectiveTimeEnd);
	}
	
	public VersionSearchRequestBuilder filterByResourceBranchPath(String resourceBranchPath) {
		return addOption(OptionKey.RESOURCE_BRANCHPATH, resourceBranchPath);
	}
	
	public VersionSearchRequestBuilder filterByResourceBranchPaths(Iterable<String> resourceBranchPaths) {
		return addOption(OptionKey.RESOURCE_BRANCHPATH, resourceBranchPaths);
	}
	
	public VersionSearchRequestBuilder filterByResourceType(String resourceType) {
		return addOption(OptionKey.RESOURCE_TYPE, resourceType);
	}
	
	public VersionSearchRequestBuilder filterByResourceTypes(Iterable<String> resourceTypes) {
		return addOption(OptionKey.RESOURCE_TYPE, resourceTypes);
	}
	
	@Override
	protected SearchResourceRequest<RepositoryContext, Versions> createSearch() {
		return new VersionSearchRequest();
	}

}
