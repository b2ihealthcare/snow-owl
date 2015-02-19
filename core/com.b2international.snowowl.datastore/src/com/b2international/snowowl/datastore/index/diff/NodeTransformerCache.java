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
package com.b2international.snowowl.datastore.index.diff;

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
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * Cache for supplying {@link NodeTransformer transformer}s for a given terminology.
 *
 */
public enum NodeTransformerCache {

	/**Shared singleton cache.*/
	INSTANCE;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NodeTransformerCache.class);
	
	/**Extension point for the {@code nodeTransformer.exsd} extension point.*/
	private static final String NODE_TRANSFORMER_EXT_POINT_ID = 
			"com.b2international.snowowl.datastore.nodeTransformer";
	
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String REPOSITORY_UUID = "repositoryUuid"; 
	
	/**Returns with the concrete transformer instance for the given terminology. This method never returns with {@code null}.*/
	public NodeTransformer getTransformer(final String repositoryUuid) {
		final NodeTransformer transformer = supplier.get().get(checkNotNull(repositoryUuid, "repositoryUuid"));
		return null == transformer ? NodeTransformer.NULL_IMPL : transformer;
	}
	
	private Supplier<Map<String, NodeTransformer>> supplier = Suppliers.memoize(new Supplier<Map<String, NodeTransformer>>() {
		@Override public Map<String, NodeTransformer> get() {

			final Map<String, NodeTransformer> cache = newHashMap();
			for (final IConfigurationElement builderElement : Platform.getExtensionRegistry().getConfigurationElementsFor(NODE_TRANSFORMER_EXT_POINT_ID)) {
				
				final String uuid = builderElement.getAttribute(REPOSITORY_UUID);
				checkNotNull(uuid, "Repository UUID cannot be resolved for node transformer. " + builderElement);

				try {

					final NodeTransformer builder = // 
							ClassUtils.checkAndCast(builderElement.createExecutableExtension(CLASS_ATTRIBUTE), NodeTransformer.class);
					cache.put(uuid, builder);
					
				} catch (final CoreException e) {
					LOGGER.error("Cannot instantiate node transformer for " + uuid, e);
					continue;
				}
				
			}
			
			return unmodifiableMap(cache);
		}
	});
	
	
}