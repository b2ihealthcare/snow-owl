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
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.core.request.version.VersionSearchRequest.OptionKey;
import com.b2international.snowowl.core.version.Versions;

/**
 * @since 4.7
 */
public final class VersionSearchRequestBuilder 
		extends SearchResourceRequestBuilder<VersionSearchRequestBuilder, ServiceProvider, Versions>
 		implements SystemRequestBuilder<Versions> {

	public VersionSearchRequestBuilder() {
		super();
	}

	public VersionSearchRequestBuilder filterByResource(ResourceURI resourceUri) {
		return addOption(OptionKey.RESOURCE, resourceUri);
	}
	
	public VersionSearchRequestBuilder filterByResources(Iterable<ResourceURI> resourceUris) {
		return addOption(OptionKey.RESOURCE, resourceUris);
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
	public VersionSearchRequestBuilder filterByVersionId(Iterable<String> versions) {
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
	
	@Override
	protected SearchResourceRequest<ServiceProvider, Versions> createSearch() {
		return new VersionSearchRequest();
	}
}
