/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.events;

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import net.jodah.typetools.TypeResolver;

/**
 * A {@link Request} represents an executable form of user intent. They can be executed in a specific context usually within a {@link ServiceProvider}
 * . Executing a {@link Request} will result in either a success or failure. Success usually returns the requested object or success message while
 * failure usually delivers an error object or {@link Throwable} to the caller.
 * <p>
 * A reference to the context is required in order to execute a {@link Request} instance. This is basically the same as invoking a function. If you
 * don't have the required context, then you can build an {@link AsyncRequest} using a different build on the sub type of the {@link RequestBuilder}
 * interface.
 *
 * @since 4.5
 * @param <C>
 *            - the type of context where this {@link Request} can be executed
 * @param <R>
 *            - the type of response
 */
@FunctionalInterface
public interface Request<C extends ServiceProvider, R> extends Serializable {

	/**
	 * Address where implementations of the {@link Request} interface will be sent over the {@link IEventBus}.
	 */
	String ADDRESS = "/requests";

	/**
	 * Tag to use for {@link IEventBus} communication.
	 */
	String TAG = "request";
	
	/**
	 * Executes this action in the given context.
	 *
	 * @param context
	 *            - the context where this {@link Request} is going to be executed
	 * @return - the result of the {@link Request}, never <code>null</code>.
	 */
	R execute(C context);
	
	/**
	 * @return the type of the request for serialization in log messages
	 */
	@JsonProperty
	default String getType() {
		return getClass().getSimpleName();
	}
	
	/**
	 * Returns the {@link ClassLoader} associated with this {@link Request} implementation. By default the class loader equals to the return type's class loader.
	 * @return
	 */
	@JsonIgnore
	default ClassLoader getClassLoader() {
		return getReturnType().getClassLoader() == null ? getClass().getClassLoader() : getReturnType().getClassLoader();
	}
	
	/**
	 * Returns the class of the actual return type.
	 * 
	 * @return
	 */
	@JsonIgnore
	@SuppressWarnings("unchecked")
	default Class<R> getReturnType() {
		final Class<?>[] types = TypeResolver.resolveRawArguments(Request.class, getClass());
		checkState(TypeResolver.Unknown.class != types[1], "Couldn't resolve return type parameter for class %s", getClass().getSimpleName());
		return (Class<R>) types[1];
	}
	
	/**
	 * @return all nested {@link Request}s (including this {@link Request}) as a {@link Collection} 
	 * @since 7.2
	 */
	@JsonIgnore
	default Collection<Request<?, ?>> getNestedRequests() {
		return ImmutableList.of(this);
	}

	/**
	 * @param <T>
	 * @param type
	 * @return
	 */
	default <T> T getNestedRequest(Class<T> type) {
		return Request.getNestedRequest(this, type);
	}
	
	/**
	 * @param <T>
	 * @param req
	 * @param type
	 * @return
	 */
	static <T> T getNestedRequest(Request<?, ?> req, Class<T> type) {
		if (type.isInstance(req)) {
			return type.cast(req);
		} else if (req instanceof DelegatingRequest<?, ?, ?>) {
			return getNestedRequest(((DelegatingRequest) req).next(), type);
		}
		return null;
	}

}
