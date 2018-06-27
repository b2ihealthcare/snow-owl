/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newConcurrentMap;

import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.cdo.ICDORepository;

/**
 * Collects {@link IRepositoryInitializer repository initializer} implementations.
 */
public enum RepositoryInitializerRegistry {

	/** The singleton instance of the registry. */
	INSTANCE;

	/**
	 * A no-op implementation of {@link IRepositoryInitializer}.
	 */
	private static final IRepositoryInitializer NULL_IMPL = new IRepositoryInitializer() {
		@Override 
		public void initialize(ICDORepository repository) { 
			return;
		}
	};

	private static final String INITIALIZER_EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.server.repositoryInitializer";
	private static final String REPOSITORY_UUID_ATTRIBUTE =  "repositoryUuid";

	private final ConcurrentMap<String, IRepositoryInitializer> initializers = newConcurrentMap();

	private RepositoryInitializerRegistry() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(INITIALIZER_EXTENSION_POINT_ID);
		for (IConfigurationElement element : elements) {
			try {
				String uuid = checkNotNull(element.getAttribute(REPOSITORY_UUID_ATTRIBUTE), "Repository UUID was null.");
				IRepositoryInitializer initializer = Extensions.instantiate(element, IRepositoryInitializer.class);
				initializers.put(uuid, initializer);
			} catch (CoreException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
	}

	public IRepositoryInitializer getInitializer(String uuid) {
		checkNotNull(uuid, "Repository UUID argument cannot be null.");
		return initializers.getOrDefault(uuid, NULL_IMPL);
	}

}
