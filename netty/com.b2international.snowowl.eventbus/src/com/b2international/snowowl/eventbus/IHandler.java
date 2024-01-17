/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
 * Handler to handle event when submitted to the {@link IEventBus}.
 * 
 * @param <T>
 *            - the message to handle
 * @since 3.1
 */
public interface IHandler<T> {

	/**
	 * @param message
	 * @return
	 */
	void handle(T message);

	/**
	 * The No Operation implementation of the interface {@link IHandler} with the type {@link IMessage}.
	 * 
	 * @since 3.1
	 */
	public static final IHandler<IMessage> NOOP = new IHandler<IMessage>() {
		@Override
		public void handle(IMessage message) {
		}
	};

}