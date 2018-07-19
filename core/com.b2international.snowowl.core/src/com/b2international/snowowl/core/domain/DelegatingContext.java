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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.MapMaker;
import com.google.common.reflect.Reflection;
import com.google.inject.Provider;

/**
 * A service registry that delegates calls to another {@link ServiceProvider} if the underlying registry does not have the required service.
 * 
 * @since 5.0
 */
public class DelegatingContext implements ServiceProvider, IDisposableService {

	private final Map<Class<?>, Object> registry = new MapMaker().makeMap();
	private final ServiceProvider delegate;
	private final AtomicBoolean disposed = new AtomicBoolean(false);

	protected DelegatingContext(ServiceProvider delegate) {
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
	 * Subclasses may override this method to do additional work before disposing this {@link DelegatingContext}. 
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
	public final <T> void bind(Class<T> type, T object) {
		registry.put(type, object);
	}
	
	/**
	 * Method to bind all bindings available in the given {@link Map}. Mainly used by the builder, but subclasses are allowed to change the underlying
	 * registry.
	 * 
	 * @param type
	 * @param object
	 * @return
	 */
	public final void bindAll(Map<Class<?>, Object> bindings) {
		registry.putAll(bindings);
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

	/**
	 * A builder to construct {@link DelegatingContext} instances or subclasses by specifying the instance to configure.
	 * 
	 * @since 5.0
	 */
	public static final class Builder<C extends ServiceProvider> {

		private final DelegatingContext provider;
		private final Class<C> nextContext;

		public Builder(C delegate, Class<C> nextContext) {
			this.nextContext = nextContext;
			this.provider = new DelegatingContext(delegate);
		}

		public final <T> Builder<C> bind(Class<T> type, T object) {
			provider.bind(type, object);
			return this;
		}
		
		public final Builder<C> bindAll(DelegatingContext other) {
			provider.bindAll(other.registry);
			return this;
		}
		
		public C build() {
			return Reflection.newProxy(nextContext, new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					try {
						if (ServiceProvider.class == method.getDeclaringClass()) {
							return method.invoke(provider, args);
						} else {
							return method.invoke(provider.getDelegate(), args);
						}
					} catch (InvocationTargetException e) {
						throw e.getCause();
					}
				}
			});
		}

	}

}
