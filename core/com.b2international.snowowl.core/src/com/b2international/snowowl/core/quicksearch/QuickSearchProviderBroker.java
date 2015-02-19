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
package com.b2international.snowowl.core.quicksearch;

import static com.b2international.commons.ClassUtils.checkAndCast;
import static com.b2international.commons.CompareUtils.isEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.collect.ImmutableSet;

/**
 * Manages quick search provider contributions. 
 *
 */
public enum QuickSearchProviderBroker {
	INSTANCE;
	
	private static final String EXTENSION_POINT_ID = "com.b2international.snowowl.core.quickSearchProvider";
	
	private static final String ATTRIBUTE_TERMINOLOGY_COMPONENT_ID = "terminologyComponentId";
	private static final String ATTRIBUTE_ID = "id";

	private static final String ATTRIBUTE_CLASS = "class";
	
	/**
	 * Returns {@link IQuickSearchProvider quick search providers} for the specified terminology components, with the ability to set terminology
	 * component specific configuration options.
	 * 
	 * @param configurationMap the map of configuration options, where the key is the terminology component ID, 
	 * 			and the value is the corresponding configuration map
	 * @return quick search providers
	 */
	public List<IQuickSearchProvider> getProviders(final Map<String, Map<String, Object>> configurationMap) {
		checkNotNull(configurationMap, "Configuration map may not be null.");
		
		final List<IQuickSearchProvider> providers = newArrayList();
		
		for (final Entry<String, Map<String, Object>> entry : configurationMap.entrySet()) {
			final String quickSearchProviderId = entry.getKey();
			final Map<String, Object> configuration = entry.getValue();
			final IQuickSearchProvider provider = getFirstProviderById(quickSearchProviderId);
			provider.setConfiguration(configuration);
			providers.add(provider);
		}
		
		return providers;
	}
	
	/**
	 * @param terminologyComponentId
	 * @return an {@link IQuickSearchProvider} with the highest priority registered for the terminology component
	 */
	public IQuickSearchProvider getFirstProvider(final String terminologyComponentId) {
		checkNotNull(terminologyComponentId, "Terminology component identifier cannot be null.");
		
		final IConfigurationElement[] configurationElements = getConfigurationElements();
		IQuickSearchProvider provider = null;
		
		for (final IConfigurationElement element : configurationElements) {
			final String elementTerminologyComponentId = getTerminologyComponentId(element);
			
			if (terminologyComponentId.equals(elementTerminologyComponentId)) {
				final IQuickSearchProvider candidate = createProvider(element);
				if (provider == null) {
					provider = candidate;
				} else if (QuickSearchProviderOrdering.INSTANCE.compare(provider, candidate) > 0) {
					provider = candidate;
				}
			}
		}
	
		if (null == provider) {
			throw new IllegalArgumentException(MessageFormat
					.format("Quick search provider implementation has not been registered for terminology component identifier ''{0}''.", terminologyComponentId));
		}
		
		return provider;
	}
	
	/**
	 * Returns with the quick search provider with highest priority registered for the given terminology components.
	 * The configuration will be applied on the provider.
	 * @param terminologyComponentId
	 * @param configuration the configuration for the quick search content provider.
	 * @return an {@link IQuickSearchProvider} with the highest priority registered for the terminology component
	 */
	public IQuickSearchProvider getFirstProvider(final String terminologyComponentId, @Nullable final Map<String, Object> configuration) {
		checkNotNull(terminologyComponentId, "Terminology component identifier cannot be null.");
		final IQuickSearchProvider provider = getFirstProvider(terminologyComponentId);
		if (!isEmpty(configuration)) {
			provider.setConfiguration(configuration);
		}
		return provider;
	}
	
	/**
	 * @param quickSearchProviderId the quick search provider's unique identifier
	 * @return an {@link IQuickSearchProvider} with the specified unique identifier
	 */
	public IQuickSearchProvider getFirstProviderById(final String quickSearchProviderId) {
		checkNotNull(quickSearchProviderId, "Terminology component identifier cannot be null.");
		
		final IConfigurationElement[] configurationElements = getConfigurationElements();
		for (final IConfigurationElement element : configurationElements) {
			final String id = getQuickSearchProviderId(element);
			
			if (quickSearchProviderId.equals(id)) {
				final IQuickSearchProvider candidate = createProvider(element);
				return candidate;
			}
		}
		
		throw new IllegalArgumentException(MessageFormat
				.format("Quick search provider implementation has not been registered with the identifier ''{0}''.", quickSearchProviderId));
	}

	/**
	 * Returns {@link IQuickSearchProvider quick search providers} for the specified terminology components.
	 * 
	 * @return quick search providers
	 */
	public List<IQuickSearchProvider> getProviders(final String... terminologyComponentIds) {
		checkArgument(terminologyComponentIds.length > 0, "At least one terminology component identifier should be specified.");
		
		final IConfigurationElement[] config = getConfigurationElements();
		final List<IQuickSearchProvider> providerList = newArrayList();
		final Set<String> terminologyComponentIdSet = ImmutableSet.copyOf(terminologyComponentIds);
		
		for (final IConfigurationElement element : config) {
			final String elementTerminologyComponentId = getTerminologyComponentId(element);
			if (terminologyComponentIdSet.contains(elementTerminologyComponentId)) {
				providerList.add(createProvider(element));
			}
		}
		
		Collections.sort(providerList, QuickSearchProviderOrdering.INSTANCE);
		return providerList;
	}

	/**
	 * Returns all registered {@link IQuickSearchProvider quick search providers}.
	 * 
	 * @return all quick search providers
	 */
	public List<IQuickSearchProvider> getAllProviders() {
		final IConfigurationElement[] config = getConfigurationElements();
		final List<IQuickSearchProvider> providerList = newArrayList();
		
		for (final IConfigurationElement element : config) {
			providerList.add(createProvider(element));
		}
		
		Collections.sort(providerList, QuickSearchProviderOrdering.INSTANCE);
		return providerList;
	}

	private IConfigurationElement[] getConfigurationElements() {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
	}

	private String getTerminologyComponentId(final IConfigurationElement element) {
		return element.getAttribute(ATTRIBUTE_TERMINOLOGY_COMPONENT_ID);
	}

	private String getQuickSearchProviderId(IConfigurationElement element) {
		return element.getAttribute(ATTRIBUTE_ID);
	}

	private IQuickSearchProvider createProvider(final IConfigurationElement element) {
		final Object instance;
		
		try {
			instance = element.createExecutableExtension(ATTRIBUTE_CLASS);
		} catch (final CoreException e) {
			throw new SnowowlRuntimeException("Caught exception while creating contributed quick search provider instance.", e);
		}
		
		return checkAndCast(instance, IQuickSearchProvider.class);
	}
}