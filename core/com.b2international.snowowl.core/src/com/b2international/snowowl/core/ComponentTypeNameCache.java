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
package com.b2international.snowowl.core;

import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.b2international.snowowl.core.CoreTerminologyBroker.ICoreTerminologyComponentInformation;
import com.b2international.snowowl.core.api.component.TerminologyComponentIdProvider;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Shared singleton cache for mapping terminology component IDs to their human readable 
 * component names.
 *
 */
public enum ComponentTypeNameCache {

	/**Shared singleton.*/
	INSTANCE;
	
	private static final Logger LOGGER = getLogger(ComponentTypeNameCache.class);
	
	/**
	 * Returns with the human readable name of the component given with the
	 * application specific terminology component ID provider argument.
	 * @param provider the terminology component ID provider.
	 * @return the human readable name of the component.
	 */
	public String getComponentName(final TerminologyComponentIdProvider provider) {
		return getComponentName(checkNotNull(provider, "provider").getTerminologyComponentId());
	}
	
	/**
	 * Returns with the human readable name of the component given with the
	 * application specific terminology component ID argument.
	 * @param terminologyComponentId the terminology component ID.
	 * @return the human readable name of the component.
	 */
	public String getComponentName(final short terminologyComponentId) {
		try {
			return COMPONENT_TYPE_ID_LABEL_CACHE.get(terminologyComponentId);
		} catch (ExecutionException e) {
			LOGGER.error("Error while getting component type name for " + terminologyComponentId, e);
			return EMPTY_STRING;
		}
	}
	
	
	private static final LoadingCache<Short, String> COMPONENT_TYPE_ID_LABEL_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Short, String>() {
		@Override public String load(final Short key) throws Exception {
			if (null == key) {
				return EMPTY_STRING;
			}
			
			final ICoreTerminologyComponentInformation information = // 
					CoreTerminologyBroker.getInstance().getComponentInformation(key.shortValue());
			
			return null == information ? EMPTY_STRING : information.getName();
		}
	});
	
}