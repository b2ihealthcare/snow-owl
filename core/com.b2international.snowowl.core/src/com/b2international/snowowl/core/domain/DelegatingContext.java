/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Optional;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.context.ServiceContext;
import com.google.common.collect.Maps;

/**
 * A service registry that delegates calls to another {@link ServiceProvider} if the underlying registry does not have the required service.
 * 
 * @since 5.0
 */
public class DelegatingContext extends ServiceContext {

	private final ServiceProvider delegate;

	protected DelegatingContext(ServiceProvider delegate) {
		this.delegate = checkNotNull(delegate, "delegate");
	}
	
	@Override
	public final Map<Class<?>, Object> getBindings() {
		Map<Class<?>, Object> aggregatedBindings = Maps.newHashMap();
		if (delegate instanceof Bindable) {
			aggregatedBindings.putAll(((Bindable) delegate).getBindings());
		}
		aggregatedBindings.putAll(super.getBindings());
		return Map.copyOf(aggregatedBindings);
	}

	@Override
	public <T> T service(Class<T> type) {
		if (hasBinding(type)) {
			return super.service(type);
		} else {
			return delegate.service(type);
		}
	}
	
	@Override
	public <T> Optional<T> optionalService(Class<T> type) {
		if (hasBinding(type)) {
			return super.optionalService(type);
		} else {
			return delegate.optionalService(type);
		} 
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
