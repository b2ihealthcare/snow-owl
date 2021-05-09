/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.context;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.Bindable;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.MapMaker;
import com.google.inject.Provider;

/**
 * @since 8.0
 */
public class ServiceContext implements IDisposableService, ServiceProvider, Bindable {

	private final Map<Class<?>, Object> bindings = new MapMaker().makeMap();
	private final Map<Class<?>, Object> readOnlyBindings = Collections.unmodifiableMap(bindings);
	private final AtomicBoolean disposed = new AtomicBoolean(false);

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
	
	public final boolean hasBinding(Class<?> type) {
		return readOnlyBindings.containsKey(type);
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
	 * @param source
	 * @return
	 */
	@Override
	public final void bindAll(Map<Class<?>, Object> source) {
		bindings.putAll(source);
	}
	
	@Override
	public final void bindAll(Bindable bindable) {
		bindAll(bindable.getBindings());
	}
	
	/**
	 * Subclasses may override this method to do additional work before disposing this {@link ServiceContext}. 
	 */
	protected void doDispose() {
	}
	
	@Override
	public Map<Class<?>, Object> getBindings() {
		return readOnlyBindings;
	}

	@Override
	public <T> T service(Class<T> type) {
		Preconditions.checkArgument(hasBinding(type), "No binding present for %s", type.getName());
		return type.cast(bindings.get(type));
	}
	
	@Override
	public <T> Optional<T> optionalService(Class<T> type) {
		return Optional.ofNullable(type.cast(bindings.get(type)));
	}
	
	@Override
	public <T> Provider<T> provider(final Class<T> type) {
		return () -> service(type);
	}
	
}
