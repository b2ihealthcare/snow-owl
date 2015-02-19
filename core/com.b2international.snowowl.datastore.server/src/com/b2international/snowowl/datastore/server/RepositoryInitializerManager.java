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
package com.b2international.snowowl.datastore.server;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Manager for {@link IRepositoryInitializer repository initializer} implementations.
 */
public enum RepositoryInitializerManager {

	/**Shared instance.*/
	INSTANCE;
	
	private Map<String, IRepositoryInitializer> cache;
	
	public IRepositoryInitializer getInitializer(final String uuid) {
		
		Preconditions.checkNotNull(uuid, "Repository UUID argument cannot be null.");
		
		if (null == cache) {
			
			synchronized (RepositoryInitializerManager.class) {
				
				if (null == cache) {
					
					cache = Collections.unmodifiableMap(initCache());
					
				}
				
			}
			
		}

		final IRepositoryInitializer initializer = cache.get(uuid);
		if (initializer instanceof RepositoryInitializer) {
			((RepositoryInitializer) initializer).setRepositoryUuid(uuid);
		}
		return null == initializer ? NULL_IMPL : initializer;
		
	}
	
	/*initialize the cache and returns with it.*/
	private Map<String, IRepositoryInitializer> initCache() {
		
		final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(INITIALIZER_EXTENSION_POINT_ID);

		final Map<String, IRepositoryInitializer> cache = Maps.newHashMap();
		
		for (final IConfigurationElement element : elements) {
			
			final String uuid = element.getAttribute(REPOSITORY_NAME_ATTRIBUTE);
			Preconditions.checkNotNull(uuid, "Repository UUID was null.");
			
			try {
				
				final IRepositoryInitializer initializer = (IRepositoryInitializer) element.createExecutableExtension(CLASS_ATTRIBUTE);
				cache.put(uuid, initializer);
				
			} catch (final ClassCastException e) {
				
				throw new SnowowlRuntimeException(e);
				
			} catch (CoreException e) {
				
				throw new SnowowlRuntimeException(e);
				
			}
			
		}
		
		return cache;
	}

	/**Null {@link IRepositoryInitializer repository initializer} implementation. Does nothing.*/
	private static final IRepositoryInitializer NULL_IMPL = new IRepositoryInitializer() {
		@Override public void initialize() { /*intentionally does nothing*/  }
	};
	
	private static final String INITIALIZER_EXTENSION_POINT_ID = 
			"com.b2international.snowowl.datastore.server.repositoryInitializer";
	private static final String REPOSITORY_NAME_ATTRIBUTE =  "repositoryUuid";
	private static final String CLASS_ATTRIBUTE = "class";
	
}