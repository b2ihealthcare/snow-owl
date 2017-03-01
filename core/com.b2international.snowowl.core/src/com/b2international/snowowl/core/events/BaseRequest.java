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
package com.b2international.snowowl.core.events;

import java.util.concurrent.TimeUnit;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Joiner;

/**
 * Base request class for all Snow Owl synchronous and asynchronous requests issued on the system's
 * event bus ({@link IEventBus}.
 *
 * @param <C> service provider
 * @param <B> request type
 * @see IEventBus
 * @see Request
 * 
 * @since 4.5
 */
public abstract class BaseRequest<C extends ServiceProvider, B> extends BaseEvent implements Request<C, B> {

	private static final Joiner COMMA_WITH_QUOTE_JOINER = Joiner.on("','").skipNulls();
	
	@Override
	public final Promise<B> execute(IEventBus bus) {
		return send(bus, getReturnType());
	}

	@Override
	public final B executeSync(IEventBus bus) {
		return execute(bus).getSync();
	}

	@Override
	public final B executeSync(IEventBus bus, long timeout) {
		return execute(bus).getSync(timeout, TimeUnit.MILLISECONDS);
	}

	@Override
	protected String getAddress() {
		throw new UnsupportedOperationException("This request "+ getClass().getName() +" cannot be sent over the bus on its own, wrap it into a RepositoryRequest");
	}
	
	/**
	 * Returns the class of the actual return type.
	 * 
	 * @return
	 */
	protected abstract Class<B> getReturnType();
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	/**
	 * To serialize an {@link Iterable} as a JSON array, return empty array string if the {@link Iterable} either <code>null</code> or empty.
	 * 
	 * @param parts
	 * @return
	 */
	protected final String formatStringList(Iterable<? extends Object> parts) {
		if (CompareUtils.isEmpty(parts)) {
			return "[]";
		} else {
			return String.format("['%s']", COMMA_WITH_QUOTE_JOINER.join(parts)); 
		}
	}

}
