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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.b2international.snowowl.eventbus.IEventBus;
import com.google.inject.Provider;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IPagingProvider;

/**
 * @since 9.0
 */
public class SearchAfterPagingProvider implements IPagingProvider {

	@Autowired
	@Qualifier("delegatePagingProvider")
	private IPagingProvider delegate;

	@Autowired
	private Provider<IEventBus> bus;

	@Override
	public int getDefaultPageSize() {
		return delegate.getDefaultPageSize();
	}

	@Override
	public int getMaximumPageSize() {
		return delegate.getMaximumPageSize();
	}

	@Override
	public IBundleProvider retrieveResultList(RequestDetails requestDetails, String searchId) {
		return retrieveResultList(requestDetails, searchId, null);
	}
	
	@Override
	public IBundleProvider retrieveResultList(RequestDetails requestDetails, String searchId, String pageId) {
		IBundleProvider bundleProvider = delegate.retrieveResultList(requestDetails, searchId);
		if (bundleProvider instanceof SearchAfterBundleProvider searchAfterAware) {
			searchAfterAware.fetchPage(pageId, bus.get());
		}
		
		return bundleProvider;
	}

	@Override
	public String storeResultList(RequestDetails requestDetails, IBundleProvider bundleProvider) {
		String searchId = delegate.storeResultList(requestDetails, bundleProvider);
		if (bundleProvider instanceof SearchAfterBundleProvider searchAfterAware) {
			searchAfterAware.setSearchId(searchId);
		}
		
		return searchId;
	}
}
