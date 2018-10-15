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
package com.b2international.snowowl.eventbus;

/**
 * @since 4.5
 */
public class Pipe implements IHandler<IMessage> {

	private final String address;
	private IEventBus target;

	public Pipe(IEventBus target, String address) {
		this.target = target;
		this.address = address;
	}
	
	@Override
	public void handle(final IMessage origin) {
		try {
			final Object body = origin.body();
			if (origin.isSend()) {
				target.send(address, body, IMessage.NOTIFICATION_TAG, new IHandler<IMessage>() {
					@Override
					public void handle(IMessage inner) {
						if (inner.isSucceeded()) {
							origin.reply(inner.body());
						} else {
							origin.fail(inner.body());
						}
					}
				});
			} else {
				throw new UnsupportedOperationException("Cannot pipe broadcast messages to address " + address + ", body: " + body);
			}
		} catch (Throwable e) {
			origin.fail(e);
		}
	}
	
}
