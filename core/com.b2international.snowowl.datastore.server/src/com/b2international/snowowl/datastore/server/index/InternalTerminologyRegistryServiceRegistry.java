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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.synchronizedMap;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.InternalTerminologyRegistryService;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.google.common.collect.Maps;

/**
 * Registry for storing {@link InternalTerminologyRegistryService} implementations
 * as strong references. It's the service responsibility to register itself to the registry.
 *
 */
public enum InternalTerminologyRegistryServiceRegistry {

	INSTANCE;
	
	private final Map<String, InternalTerminologyRegistryService> cache = // 
			synchronizedMap(Maps.<String, InternalTerminologyRegistryService>newHashMap());
	
	public void register(final String repositoryUuid, final InternalTerminologyRegistryService service) {
		checkNotNull(repositoryUuid, "repositoryUuid");
		checkNotNull(service, "service");
		cache.put(repositoryUuid, service);
	}
	
	@Nullable public InternalTerminologyRegistryService getService(final String repositoryUuid) {
		return cache.get(checkNotNull(repositoryUuid, "repositoryUuid"));
	}
	
	public Iterable<Pair<InternalTerminologyRegistryService, IBranchPath>> getServices(final IBranchPathMap branchPathMap) {
		checkNotNull(branchPathMap, "branchPathMap");
		final Collection<Pair<InternalTerminologyRegistryService, IBranchPath>> services = newArrayList();
		final Set<String> repositoryIds = getServiceForClass(ICDOConnectionManager.class).uuidKeySet();

		for (final Entry<String, IBranchPath> entry : branchPathMap.asMap(repositoryIds).entrySet()) {
			final InternalTerminologyRegistryService service = getService(entry.getKey());
			if (null != service) {
				services.add(Pair.of(service, entry.getValue()));
			}
		}
		
		return services;
	} 
	
}