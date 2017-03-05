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
package com.b2international.snowowl.datastore.server;

import java.util.Set;

import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since 5.7
 */
final class Handlers implements IEventBus {

	private final IEventBus bus;

	public Handlers(int numberOfWorkers) {
		this.bus = EventBusUtil.getWorkerBus("handlers", numberOfWorkers); 
	}
	
	@Override
	public IEventBus send(String address, Object message) {
		return bus.send(address, message);
	}

	@Override
	public IEventBus send(String address, Object message, IHandler<IMessage> replyHandler) {
		return bus.send(address, message, replyHandler);
	}

	@Override
	public IEventBus publish(String address, Object message) {
		return bus.publish(address, message);
	}

	@Override
	public IEventBus receive(IMessage message) {
		return bus.receive(message);
	}

	@Override
	public IEventBus registerHandler(String address, IHandler<IMessage> handler) {
		return bus.registerHandler(address, handler);
	}

	@Override
	public IEventBus unregisterHandler(String address, IHandler<IMessage> handler) {
		return bus.unregisterHandler(address, handler);
	}

	@Override
	public Set<String> getAddressBook() {
		return bus.getAddressBook();
	}

}
