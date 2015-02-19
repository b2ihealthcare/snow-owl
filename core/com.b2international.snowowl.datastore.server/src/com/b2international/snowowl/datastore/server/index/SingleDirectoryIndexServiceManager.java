/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.index;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.ISingleDirectoryIndexService;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;

/**
 */
public class SingleDirectoryIndexServiceManager implements ISingleDirectoryIndexServiceManager {

	@Override
	public List<String> getServiceIds() {
		
		final Collection<ISingleDirectoryIndexService> indexes = getApplicationContext().getServices(ISingleDirectoryIndexService.class);
		final Collection<String> serviceIds = Collections2.transform(indexes, new Function<ISingleDirectoryIndexService, String>() {
			@Override
			public String apply(final ISingleDirectoryIndexService input) {
				return input.getIndexRootPath().getPath();
			}
		});
		
		return Ordering.natural().immutableSortedCopy(serviceIds);
	}

	@Override
	public ISingleDirectoryIndexService getService(final String serviceId) {
		
		Preconditions.checkNotNull(serviceId, "Service identifier may not be null.");
			
		final Collection<ISingleDirectoryIndexService> indexServices = getApplicationContext().getServices(ISingleDirectoryIndexService.class);
		for (final ISingleDirectoryIndexService service : indexServices) {
			if (serviceId.equals(service.getIndexRootPath().getPath())) {
				return service;
			}
		}
		
		return null;
	}
	
	private ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
}