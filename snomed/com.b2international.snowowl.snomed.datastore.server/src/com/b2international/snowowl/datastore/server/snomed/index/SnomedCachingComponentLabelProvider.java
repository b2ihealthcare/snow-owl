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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.server.snomed.InitializationState.BUILDING;
import static com.b2international.snowowl.datastore.server.snomed.InitializationState.INITIALIZED;
import static com.b2international.snowowl.datastore.server.snomed.InitializationState.UNINITIALIZED;
import static com.b2international.snowowl.datastore.server.snomed.InitializationState.isInitialized;
import static com.b2international.snowowl.datastore.server.snomed.InitializationState.isUninitialized;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.builder;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.search.Query;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.INameProviderFactory;
import com.b2international.snowowl.core.api.component.TerminologyComponentIdProvider;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.snomed.InitializationState;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Caching label provider implementation for SNOMED&nbsp;CT components.  
 */
/*default*/ abstract class SnomedCachingComponentLabelProvider implements TerminologyComponentIdProvider {

	private final IBranchPath branchPath;
	private final AtomicReference<InitializationState> state;
	private final Map<String, String> labelCache;
	
	/*default*/ SnomedCachingComponentLabelProvider(final IBranchPath branchPah) {
		this.branchPath = checkNotNull(branchPah, "branchPath");
		state = new AtomicReference<InitializationState>(UNINITIALIZED);
		labelCache = newHashMap();
	}
	
	/*default*/ String getLabel(final String componentId) {
		checkNotNull(componentId, "componentId");
		if (isInitialized(state.get())) {
			return labelCache.get(componentId);
		} else {
			initializeCacheInBackgroud();
			return getNameProvider().getComponentLabel(getBranchPath(), componentId);
		}
	}

	private IndexServerService<?> getIndexService() {
		return (IndexServerService<?>) getServiceForClass(SnomedIndexService.class);
	}
	
	private IBranchPath getBranchPath() {
		return branchPath;
	}

	private Map<String, String> transformMap(final LongKeyMap mapToTransform) {
		final Builder<String, String> builder = builder();
		for (final LongKeyMapIterator itr = mapToTransform.entries(); itr.hasNext(); /**/) {
			itr.next();
			builder.put(Long.toString(itr.getKey()), valueOfOrEmptyString(itr.getValue()));
		}
		return builder.build();
	}
	
	private Map<String, String> getComponentLabelMapping() {
		final SnomedComponentLabelCollector collector = new SnomedComponentLabelCollector();
		final IndexServerService<?> indexService = getIndexService();
		final Query query = SnomedMappings.newQuery().type(getTerminologyComponentId()).matchAll();
		indexService.search(getBranchPath(), query, collector);
		return transformMap(collector.getIdLabelMapping());
	}
	
	private IComponentNameProvider getNameProvider() {
		final CoreTerminologyBroker terminologyBroker = CoreTerminologyBroker.getInstance();
		final String terminologyComponentId = terminologyBroker.getTerminologyComponentId(getTerminologyComponentId());
		final INameProviderFactory factory = terminologyBroker.getNameProviderFactory(terminologyComponentId);
		return factory.getNameProvider();
	}
	
	private synchronized void initializeCacheInBackgroud() {
		if (isUninitialized(state.get())) {
			state.set(BUILDING);
			new Thread(new Runnable() {
				@Override
				public void run() {
					labelCache.putAll(getComponentLabelMapping());
					state.set(INITIALIZED);
				}
			}).start();
		}
	}
	
}