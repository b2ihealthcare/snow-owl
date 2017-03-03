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
package com.b2international.snowowl.core.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.MapMaker;
import com.google.inject.Provider;

/**
 * A service registry that delegates calls to another {@link ServiceProvider} if the underlying registry does not have the required service.
 * 
 * @since 5.0
 */
public class DelegatingServiceProvider implements ServiceProvider, IDisposableService {

	private final Map<Class<?>, Object> registry = new MapMaker().makeMap();
	private final ServiceProvider delegate;
	private final AtomicBoolean disposed = new AtomicBoolean(false);

	protected DelegatingServiceProvider(ServiceProvider delegate) {
		this.delegate = checkNotNull(delegate, "delegate");
	}
	
	@Override
	public final boolean isDisposed() {
		return disposed.get();
	}
	
	@Override
	public final void dispose() {
		if (disposed.compareAndSet(false, true)) {
			doDispose();
			FluentIterable.from(registry.values()).filter(IDisposableService.class).forEach(IDisposableService::dispose);
		}
	}

	/**
	 * Subclasses may override this method to do additional work before disposing this {@link DelegatingServiceProvider}. 
	 */
	protected void doDispose() {
	}

	/**
	 * Method to bind a service interface to an implementation. Mainly used by the builder, but subclasses are allowed to change the underlying
	 * registry.
	 * 
	 * @param type
	 * @param object
	 * @return
	 */
	protected final <T> void bind(Class<T> type, T object) {
		registry.put(type, object);
	}

	@Override
	public <T> T service(Class<T> type) {
		if (registry.containsKey(type)) {
			return type.cast(registry.get(type));
		} else {
			return delegate.service(type);
		}
	}

	@Override
	public <T> Provider<T> provider(final Class<T> type) {
		return new Provider<T>() {
			@Override
			public T get() {
				return service(type);
			}
		};
	}

	protected ServiceProvider getDelegate() {
		return delegate;
	}

	public static DelegatingServiceProvider.Builder<DelegatingServiceProvider> basedOn(ServiceProvider context) {
		return new DelegatingServiceProvider.Builder<>(new DelegatingServiceProvider(context));
	}

	/**
	 * A builder to construct {@link DelegatingServiceProvider} instances or subclasses by specifying the instance to configure.
	 * 
	 * @since 5.0
	 */
	public static class Builder<C extends DelegatingServiceProvider> {

		private final C provider;

		Builder(C delegate) {
			this.provider = delegate;
		}

		public final <T> Builder<C> bind(Class<T> type, T object) {
			provider.bind(type, object);
			return this;
		}

		public C build() {
			return provider;
		}

	}

}
