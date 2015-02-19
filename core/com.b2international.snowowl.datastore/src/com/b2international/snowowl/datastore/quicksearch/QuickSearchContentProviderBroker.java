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
package com.b2international.snowowl.datastore.quicksearch;

import java.util.Map;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.google.common.collect.Maps;

/**
 * Singleton instance for looking up and providing {@link IQuickSearchContentProvider} implementations
 * for the clients.
 * @see IQuickSearchContentProvider
 */
public enum QuickSearchContentProviderBroker {

	INSTANCE;
	
	private static final String QUICK_SEARCH_CONTENT_PROVIDER_EXTENSION_ID = 
			"com.b2international.snowowl.datastore.server.quickSearchContentProvider";
	private static final String CLASS_ATTRIBUTE_ID = "class";
	private static final String TERMINOLOGY_COMPONENT_ID_ATTRIBUTE_ID = "terminologyComponentId";
	private static final String QUICK_SEARCH_PROVIDER_ID = "quickSearchProviderId";
	private static final Logger LOGGER = LoggerFactory.getLogger(QuickSearchContentProviderBroker.class);
	
	private boolean initialized = false;
	/**Map for looking up server side content providers by the associated quick search provider IDs.*/
	private Map<String, IQuickSearchContentProvider> providerIdContentProviderMap;
	/**Map for looking up server side content providers by the unique terminology component IDs.*/
	private Map<String, IQuickSearchContentProvider> terminologyComponentIdContentProviderMap;
	
	/**
	 * Returns with the proper {@link IQuickSearchContentProvider content provider} for the {@link IQuickSearchProvider} identified 
	 * by the specified unique ID.
	 * <p><b>NOTE:&nbsp;</b>can be {@code null}. Clients must check return value before referencing it.
	 * @param quickSearchProviderId the unique ID of the {@link IQuickSearchProvider}.
	 * @return the proper {@link IQuickSearchContentProvider} for the {@link IQuickSearchProvider} instance.
	 */
	@Nullable public IQuickSearchContentProvider getProvider(final String quickSearchProviderId) {
		checkAndInitCache();
		return getElementUnsafe(quickSearchProviderId);
	}
	
	/**
	 * Returns with the proper {@link IQuickSearchContentProvider content provider} for the associated terminology component ID.
	 * <p><b>NOTE:&nbsp;</b>can be {@code null}. Clients must check return value before referencing it.
	 * @param terminologyComponentId the unique ID of the terminology independent component.
	 * @return the proper {@link IQuickSearchContentProvider} for the {@link IQuickSearchProvider} instance.
	 */
	@Nullable public IQuickSearchContentProvider getProviderForComponent(final String terminologyComponentId) {
		checkAndInitCache();
		return getElementForComponentUnsafe(terminologyComponentId);
	}

	/*checks whether the cache is initialized or not. if the cache is not ready yet, creates it*/
	private void checkAndInitCache() {
		if (false == initialized) {
			synchronized (QuickSearchContentProviderBroker.class) {
				if (false == initialized) {
					initCache();
					initialized = true;
				}
			}
		}
	}

	/*unsafely returns with the quick search content provider instance from the cache.*/
	private IQuickSearchContentProvider getElementUnsafe(final String quickSearchProviderId) {
		return providerIdContentProviderMap.get(quickSearchProviderId);
	}

	/*unsafely returns with the quick search content provider instance from the cache associated for the specified terminology component ID.*/
	private IQuickSearchContentProvider getElementForComponentUnsafe(final String terminologyComponentId) {
		return terminologyComponentIdContentProviderMap.get(terminologyComponentId);
	}

	
	/*initialized the cache after looking up the proper implementations from extension points.
	 * also creates mapping from terminology component IDs to quick search content provider implementations*/
	private void initCache() {
		
		providerIdContentProviderMap = Maps.newHashMap();
		terminologyComponentIdContentProviderMap = Maps.newHashMap();
		
		for (final IConfigurationElement element : Platform.getExtensionRegistry().getConfigurationElementsFor(QUICK_SEARCH_CONTENT_PROVIDER_EXTENSION_ID)) {
			
			IQuickSearchContentProvider provider = null;
			try {
				provider = (IQuickSearchContentProvider) element.createExecutableExtension(CLASS_ATTRIBUTE_ID);
				providerIdContentProviderMap.put(element.getAttribute(QUICK_SEARCH_PROVIDER_ID), provider);
				terminologyComponentIdContentProviderMap.put(element.getAttribute(TERMINOLOGY_COMPONENT_ID_ATTRIBUTE_ID), provider);
			} catch (final CoreException e) {
				LOGGER.error("Error while instantiating quick search content provider instance with ID: " + element.getAttribute(QUICK_SEARCH_PROVIDER_ID), e);
			}
		}
	}
}