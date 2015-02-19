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
package com.b2international.snowowl.eventbus.net4j;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since REMF 1.0
 */
public interface IEventBusAware extends IHandler<IMessage> {

	/**
	 * Returns the currently used {@link IEventBus} instance.
	 * 
	 * @return
	 */
	IEventBus getEventBus();

	/**
	 * Sets the {@link IEventBus} to use it when delivering messages.
	 * 
	 * @param bus
	 */
	void setEventBus(IEventBus bus);

}