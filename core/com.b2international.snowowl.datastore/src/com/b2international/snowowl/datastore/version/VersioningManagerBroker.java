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
package com.b2international.snowowl.datastore.version;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Broker for all available terminology specific {@link IVersioningManager versioning manager} instances.
 * <br>Requires a running {@link Platform platform}.
 */
public enum VersioningManagerBroker {

	/**Singleton versioning manager broker.*/
	INSTANCE;

	private static final String VERSIONING_MANAGER_EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.versioningManager";
	private static final String TERMINOLOGY_ID_ATTRIBUTE = "terminologyId";
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String INTERFACE_ATTRIBUTE = "interface";
	
	/**Cache. Supports mapping between tooling feature IDs (terminology IDs) and {@link IVersioningManager} classes.*/
	private final Supplier<Map<String, Pair<Class<IVersioningManager>, Class<IVersioningManager>>>> cache = Suppliers.memoize(new Supplier<Map<String, Pair<Class<IVersioningManager>, Class<IVersioningManager>>>>() {

		@Override public Map<String, Pair<Class<IVersioningManager>, Class<IVersioningManager>>> get() {
			
			final Map<String, Pair<Class<IVersioningManager>, Class<IVersioningManager>>> $ = Maps.newHashMap();

			final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(VERSIONING_MANAGER_EXTENSION_POINT_ID);
			for (final IConfigurationElement element : elements) {
				
				try {
					
					final String toolingId = Preconditions.checkNotNull(element.getAttribute(TERMINOLOGY_ID_ATTRIBUTE));
					final IVersioningManager manager = //
							ClassUtils.checkAndCast(element.createExecutableExtension(CLASS_ATTRIBUTE), IVersioningManager.class);
					
					final String interfaceName = Preconditions.checkNotNull(element.getAttribute(INTERFACE_ATTRIBUTE));
					@SuppressWarnings("unchecked") final Class<IVersioningManager> managerInterface = // 
							(Class<IVersioningManager>) manager.getClass().getClassLoader().loadClass(interfaceName);
					
					@SuppressWarnings("unchecked") final Class<IVersioningManager> managerClass = // 
							(Class<IVersioningManager>) manager.getClass();
					
					
					$.put(toolingId, Pair.of(managerInterface, managerClass));
					
				} catch (final CoreException e) {
					throw new SnowowlRuntimeException("Error while instantiating versioning manager.", e);
				} catch (final ClassNotFoundException e) {
					throw new SnowowlRuntimeException("Error while instantiating versioning manager.", e);
				}
				
			}
			
			return Collections.unmodifiableMap($);
			
		}
		
	});
	
	/**Returns with the component versioning manager for the given tooling feature.*/
	public IVersioningManager createVersioningManager(final String toolingId) {
		try {
			return cache.get().get(toolingId).getB().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
	/**Returns with all registered component versioning manager interface-class mappings.*/
	public Iterable<Pair<Class<IVersioningManager>, Class<IVersioningManager>>> getVersioningManagerClasses() {
		final Collection<Pair<Class<IVersioningManager>, Class<IVersioningManager>>> $ = Sets.newHashSet();
		for (final Pair<Class<IVersioningManager>, Class<IVersioningManager>> pair : cache.get().values()) {
			$.add(pair);
		}
		return Iterables.unmodifiableIterable($);
	}
	
}