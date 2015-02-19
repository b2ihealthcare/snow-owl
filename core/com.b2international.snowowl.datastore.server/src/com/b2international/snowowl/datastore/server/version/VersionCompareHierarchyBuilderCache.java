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
package com.b2international.snowowl.datastore.server.version;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableMap;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * Singleton for caching various {@link VersionCompareHierarchyBuilder} instances grouped by 
 * the unique repository UUIDs.
 * <p>This class requires a running {@link Platform}.
 */
public enum VersionCompareHierarchyBuilderCache {

	/**Shared singleton cache.*/
	INSTANCE;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionCompareHierarchyBuilderCache.class);
	
	/**Extension point for the {@code versionCompareHierarchyBuilder.exsd} extension point.*/
	private static final String HIERARCHY_BUILDER_EXT_POINT_ID = 
			"com.b2international.snowowl.datastore.server.versionCompareHierarchyBuilder";
	
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String REPOSITORY_UUID = "repositoryUuid"; 
	
	/**Returns with the concrete builder instance for the given terminology. This method never returns with {@code null}.*/
	/*package*/ VersionCompareHierarchyBuilder getBuilder(final String repositoryUuid) {
		return checkNotNull(createNewBuilder(checkNotNull(repositoryUuid, "repositoryUuid")), "Cannot find hierarchy builder for " + repositoryUuid);
	}
	
	private Supplier<Map<String, IConfigurationElement>> supplier = Suppliers.memoize(new Supplier<Map<String, IConfigurationElement>>() {
		@Override public Map<String, IConfigurationElement> get() {

			final Map<String, IConfigurationElement> cache = newHashMap();
			for (final IConfigurationElement builderElement : Platform.getExtensionRegistry().getConfigurationElementsFor(HIERARCHY_BUILDER_EXT_POINT_ID)) {
				final String uuid = builderElement.getAttribute(REPOSITORY_UUID);
				checkNotNull(uuid, "Repository UUID cannot be resolved for hierarchy builder. " + builderElement);
				cache.put(uuid, builderElement);
			}
			
			return unmodifiableMap(cache);
		}
	});
	
	private VersionCompareHierarchyBuilder createNewBuilder(final String repositoryUuid) {
		final IConfigurationElement element = supplier.get().get(checkNotNull(repositoryUuid, "repositoryUuid"));
		try {
			return ClassUtils.checkAndCast(element.createExecutableExtension(CLASS_ATTRIBUTE), VersionCompareHierarchyBuilder.class);			
		} catch (final CoreException e) {
			LOGGER.error("Cannot instantiate hierarchy builder for " + repositoryUuid, e);
			throw new SnowowlRuntimeException("Cannot instantiate hierarchy builder for " + repositoryUuid, e);
		}
	}
	
	
}