/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.internal;

import java.util.Collection;

import com.b2international.commons.CompositeClassLoader;
import com.b2international.commons.extension.Extensions;
import com.b2international.snowowl.datastore.RepositoryClassLoaderProvider;
import com.b2international.snowowl.datastore.RepositoryClassLoaderProviderRegistry;

/**
 * @since 4.5
 */
public class ExtensionBasedRepositoryClassLoaderProviderRegistry implements RepositoryClassLoaderProviderRegistry {

	private final Collection<RepositoryClassLoaderProvider> extensions;

	public ExtensionBasedRepositoryClassLoaderProviderRegistry() {
		this.extensions = Extensions.getExtensions("com.b2international.snowowl.datastore.server.classLoaderProvider", RepositoryClassLoaderProvider.class);
	}
	
	@Override
	public ClassLoader getClassLoader() {
		final CompositeClassLoader classLoader = new CompositeClassLoader();
		extensions.stream().map(RepositoryClassLoaderProvider::getClassLoader).forEach(classLoader::add);
		return classLoader;
	}

}
