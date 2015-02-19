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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.datastore.browser.TerminologyBrowserAdapter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RecursiveTerminologyBrowser<C extends IComponent<K>, K> extends TerminologyBrowserAdapter<C, K> {
	
	public static <C extends IComponent<K>, K> RecursiveTerminologyBrowser<C, K> create(final IClientTerminologyBrowser<C, K> delegate) {
		return new RecursiveTerminologyBrowser<C, K>(delegate);
	}

	private final LoadingCache<C, Collection<C>> superTypeCache;
	private final LoadingCache<C, Collection<C>> subTypeCache;
	
	public RecursiveTerminologyBrowser(final IClientTerminologyBrowser<C, K> delegate) {
		super(delegate);
		
		superTypeCache = CacheBuilder.newBuilder().concurrencyLevel(Runtime.getRuntime().availableProcessors()).build(new CacheLoader<C, Collection<C>>() {
			@Override
			public Collection<C> load(final C key) throws Exception {
				return delegate.getAllSuperTypes(key);
			}
			
		});
		
		subTypeCache = CacheBuilder.newBuilder().concurrencyLevel(Runtime.getRuntime().availableProcessors()).build(new CacheLoader<C, Collection<C>>() {
			@Override
			public Collection<C> load(final C key) throws Exception {
				return delegate.getAllSubTypes(key);
			}
			
		});
		
	}

	@Override
	public Collection<C> getSuperTypes(final C concept) {
		try {
			return superTypeCache.get(concept);
		} catch (final ExecutionException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public Collection<C> getSubTypes(final C concept) {
		try {
			return subTypeCache.get(concept);
		} catch (final ExecutionException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
}