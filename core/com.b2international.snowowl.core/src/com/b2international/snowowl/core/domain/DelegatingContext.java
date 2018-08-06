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
package com.b2international.snowowl.core.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.MapMaker;
import com.google.inject.Provider;

/**
 * A service registry that delegates calls to another {@link ServiceProvider} if the underlying registry does not have the required service.
 * 
 * @since 5.0
 */
public class DelegatingContext implements ServiceProvider, Bindable, IDisposableService {

	private final Map<Class<?>, Object> bindings = new MapMaker().makeMap();
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
			FluentIterable.from(bindings.values()).filter(IDisposableService.class).forEach(IDisposableService::dispose);
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
	@Override
	public final <T> void bind(Class<T> type, T object) {
		bindings.put(type, object);
	}
	
	/**
	 * Method to bind all bindings available in the given {@link Map}. Mainly used by the builder, but subclasses are allowed to change the underlying
	 * registry.
	 * 
	 * @param type
	 * @param object
	 * @return
	 */
	@Override
	public final void bindAll(Map<Class<?>, Object> source) {
		bindings.putAll(source);
	}
	
	@Override
	public final Map<Class<?>, Object> getBindings() {
		return bindings;
	}

	@Override
	public <T> T service(Class<T> type) {
		if (bindings.containsKey(type)) {
			return type.cast(bindings.get(type));
		} else {
			return delegate.service(type);
		}
	}

	@Override
	public <T> Provider<T> provider(final Class<T> type) {
		return () -> service(type);
	}

	protected ServiceProvider getDelegate() {
		return delegate;
	}

	/**
	 * Builds a delegating proxy for any sub-interface of {@link ServiceProvider} that can
	 * be extended with service registry entries.
	 * 
	 * @param <C> the {@link ServiceProvider} (sub-)type to impersonate
	 * @since 5.0
	 */
	public static final class Builder<C extends ServiceProvider> {

		
		private final Class<C> delegateType;
		private final Method injectMethod;
		private final DelegatingContext bindable;

		public Builder(Class<C> delegateType, C delegate) {
			this.delegateType = delegateType;
			
			try {
				this.injectMethod = delegateType.getMethod("inject"); // may or may not be a declared method
			} catch (NoSuchMethodException | SecurityException e) {
				throw new SnowowlRuntimeException("Couldn't retrieve reference to inject() method.", e);
			}

			this.bindable = new DelegatingContext(delegate);
		}

		public final <T> Builder<C> bind(Class<T> type, T object) {
			bindable.bind(type, object);
			return this;
		}
		
		public final Builder<C> bindAll(Bindable other) {
			bindable.bindAll(other.getBindings());
			return this;
		}
		
		public C build() {
			final Object proxyInstance = Proxy.newProxyInstance(
					delegateType.getClassLoader(), 
					new Class[] { delegateType, Bindable.class },
					(proxy, method, args) -> {
						try {
							
							final Class<?> declaringClass = method.getDeclaringClass();
							
							if (injectMethod.equals(method)) {
								return new Builder<C>(delegateType, (C) proxy);
							} else if (ServiceProvider.class == declaringClass) {
								return method.invoke(bindable, args);
							} else if (Bindable.class == declaringClass) {
								return method.invoke(bindable, args);
							} else {
								return method.invoke(bindable.getDelegate(), args);
							}
							
						} catch (InvocationTargetException e) {
							throw e.getCause();
						}
					});
			
			return delegateType.cast(proxyInstance);
		}
	}
}
