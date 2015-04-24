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
package com.b2international.snowowl.core.events.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.events.Event;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * Simple class to ease the development of event based async messaging between nodes.
 * 
 * @since 4.1
 */
public final class AsyncSupport<T> {

	private IEventBus bus;
	private Class<T> clazz;

	public AsyncSupport(IEventBus bus, Class<T> clazz) {
		this.bus = checkNotNull(bus, "bus");
		this.clazz = checkNotNull(clazz, "clazz");
	}

	public Promise<T> send(Event event) {
		final SimplePromise<T> promise = new SimplePromise<>();
		event.send(bus, new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				try {
					if (message.isSucceeded()) {
						promise.resolve(clazz.cast(message.body()));
					} else {
						promise.reject((Throwable) message.body());
					}
				} catch (Throwable e) {
					promise.reject(e);
				}
			}
		});
		return promise;
	}

}
