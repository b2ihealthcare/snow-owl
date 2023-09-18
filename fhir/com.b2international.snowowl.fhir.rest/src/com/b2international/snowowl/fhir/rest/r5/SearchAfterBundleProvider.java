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
package com.b2international.snowowl.fhir.rest.r5;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.InstantType;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.model.r5.FhirBundleConverter;
import com.b2international.snowowl.fhir.core.model.r5.ResourceConstants;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequestBuilder;

import ca.uhn.fhir.rest.api.server.IBundleProvider;

/**
 * @since 9.0
 */
public class SearchAfterBundleProvider implements IBundleProvider {

	private static final String FIRST_PAGE_ID = "first";

	private final FhirResourceSearchRequestBuilder<?> requestBuilder;
	
	private String searchId;
	private Promise<Bundle> results;
	
	public SearchAfterBundleProvider(FhirResourceSearchRequestBuilder<?> requestBuilder) {
		this.requestBuilder = requestBuilder;
	}
	
	public synchronized SearchAfterBundleProvider fetchPage(String searchAfter, IEventBus bus) {
		// Fire off a search request asynchronously
		this.requestBuilder.setSearchAfter(searchAfter);
		this.results = requestBuilder.buildAsync()
			.execute(bus)
			.then(FhirBundleConverter::toFhirBundle);
		
		return this;
	}
	
	private Bundle getResults() {
		return results.getSync();
	}

	@Override
	public String getUuid() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	@Override
	public Integer preferredPageSize() {
		return null;
	}

	@Override
	public String getCurrentPageId() {
		/*
		 * XXX: The special page ID "first" will not appear in responses, but we still
		 * need it so that instead of offsets named page IDs are generated in the
		 * response.
		 */
		final String currentPageId = getResults().getUserString(ResourceConstants.CURRENT_PAGE_ID);
		return StringUtils.isEmpty(currentPageId) ? FIRST_PAGE_ID : currentPageId; 
	}
	
	@Override
	public String getNextPageId() {
		if (searchId != null) {
			return getResults().getUserString(ResourceConstants.NEXT_PAGE_ID);
		} else {
			// This bundle provider was not cached in a paging provider or we have reached the end
			return null;
		}
	}

	@Override
	public InstantType getPublished() {
		return getResults().getMeta()
			.getLastUpdatedElement();
	}

	@Override
	public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
		return getResults().getEntry()
			.stream()
			.<IBaseResource>map(BundleEntryComponent::getResource)
			.toList();
	}

	@Override
	public Integer size() {
		return getResults().getTotal();
	}
}
