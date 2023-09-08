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
package com.b2international.snowowl.fhir.rest;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.InstantType;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequestBuilder;

import ca.uhn.fhir.rest.api.server.IBundleProvider;

/**
 * @since 9.0
 */
public class SearchAfterBundleProvider implements IBundleProvider {

	private static final String FIRST_PAGE_ID = "first";

	private final FhirResourceSearchRequestBuilder<?> requestBuilder;
	
	private String searchId;
	private Bundle results;
	
	public SearchAfterBundleProvider(FhirResourceSearchRequestBuilder<?> requestBuilder) {
		this.requestBuilder = requestBuilder;
	}
	
	public synchronized SearchAfterBundleProvider fetchPage(String searchAfter, IEventBus bus) {
		this.requestBuilder.setSearchAfter(searchAfter);
		this.results = requestBuilder.buildAsync()
			.execute(bus)
			.getSync();
		
		return this;
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
		final String currentPageId = results.getUserString("currentPageId");
		return StringUtils.isEmpty(currentPageId) ? FIRST_PAGE_ID : currentPageId; 
	}
	
	@Override
	public String getNextPageId() {
		if (searchId != null) {
			return results.getUserString("nextPageId");
		} else {
			// This bundle provider was not cached in a paging provider or we have reached the end
			return null;
		}
	}

	@Override
	public InstantType getPublished() {
		return results.getMeta().getLastUpdatedElement();
	}

	@Override
	public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
		return results.getEntry()
			.stream()
			.<IBaseResource>map(BundleEntryComponent::getResource)
			.toList();
	}

	@Override
	public Integer size() {
		return results.getTotal();
	}
}
