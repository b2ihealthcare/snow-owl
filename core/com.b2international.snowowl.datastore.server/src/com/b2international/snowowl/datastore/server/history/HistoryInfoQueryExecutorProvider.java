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
package com.b2international.snowowl.datastore.server.history;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.platform.Extensions.getExtensions;
import static com.b2international.snowowl.datastore.server.history.HistoryInfoQueryExecutor.NOOP;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.collect.Iterables.find;

import java.util.Collection;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Singleton service for providing {@link HistoryInfoQueryExecutor} instances.
 *
 */
public enum HistoryInfoQueryExecutorProvider {

	/**Shared stateful service.*/
	INSTANCE;
	
	private static final String EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.server.historyInfo";
	private static final String CLASS_ATTRIBUTE_NAME = "executorClass";
	
	
	private final LoadingCache<String, HistoryInfoQueryExecutor> cache = // 
			newBuilder().build(new CacheLoader<String, HistoryInfoQueryExecutor>() {
				public HistoryInfoQueryExecutor load(final String terminologyComponentId) throws Exception {
					checkNotNull(terminologyComponentId, "terminologyComponentId");
					
					return find(getExecutors(), new Predicate<HistoryInfoQueryExecutor>() {
						public boolean apply(final HistoryInfoQueryExecutor executor) {
							return terminologyComponentId.equals(getTerminologyComponentId(executor));
						}
					}, NOOP);
					
				}
			});

	/**
	 * Returns with the {@link HistoryInfoQueryExecutor query executor} for a given 
	 * component type.<br>This method never returns with {@code null}.
	 * @param terminologyComponentId the application specific terminology component ID.
	 * @return the query executor instance for the component.
	 */
	public HistoryInfoQueryExecutor getExecutor(final String terminologyComponentId) {
		return isEmpty(terminologyComponentId) ? NOOP : cache.getUnchecked(terminologyComponentId);
	} 
	
	private String getTerminologyComponentId(final HistoryInfoQueryExecutor executor) {
		final CoreTerminologyBroker terminologyBroker = CoreTerminologyBroker.getInstance();
		return terminologyBroker.getTerminologyComponentId(executor.getTerminologyComponentId());
	}
	
	private Collection<HistoryInfoQueryExecutor> getExecutors() {
		return getExtensions(EXTENSION_POINT_ID, CLASS_ATTRIBUTE_NAME, HistoryInfoQueryExecutor.class);
	}
	
}