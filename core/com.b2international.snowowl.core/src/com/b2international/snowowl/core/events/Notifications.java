/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

import rx.Observable;

/**
 * @since 5.7
 */
public final class Notifications extends Observable<SystemNotification> {

	public Notifications(IEventBus bus, ClassLoader classLoader) {
		super(subscriber -> {
			final IHandler<IMessage> handler = new IHandler<IMessage>() {
				@Override
				public void handle(IMessage message) {
					try {
						if (!subscriber.isUnsubscribed()) {
							final SystemNotification notification = message.body(SystemNotification.class, classLoader);
							subscriber.onNext(notification);
						} else {
							bus.unregisterHandler(SystemNotification.ADDRESS, this);
						}
					} catch (Throwable e) {
						subscriber.onError(e);
					}
				}
			};
			bus.registerHandler(SystemNotification.ADDRESS, handler);
		});
	}

}
