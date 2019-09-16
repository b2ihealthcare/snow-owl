/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.authorization;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.google.common.collect.ImmutableMap;

/**
 * @since 7.2
 */
public class AuthorizedEventBus implements IEventBus {

	private final IEventBus bus;
	private final Map<String, String> headers;

	public AuthorizedEventBus(IEventBus bus, Map<String, String> headers) {
		this.bus = checkNotNull(bus);
		this.headers = headers;
	}
	
	private Map<String, String> merged(Map<String, String> headers) {
		return ImmutableMap.<String, String>builder().putAll(this.headers).putAll(headers).build();
	}
	
	@Override
	public IEventBus send(String address, Object message, Map<String, String> headers) {
		return bus.send(address, message, merged(headers));
	}

	@Override
	public IEventBus send(String address, Object message, Map<String, String> headers, IHandler<IMessage> replyHandler) {
		return bus.send(address, message, merged(headers), replyHandler);
	}

	@Override
	public IEventBus send(String address, Object message, String tag, Map<String, String> headers) {
		return bus.send(address, message, tag, merged(headers));
	}

	@Override
	public IEventBus send(String address, Object message, String tag, Map<String, String> headers, IHandler<IMessage> replyHandler) {
		return bus.send(address, message, tag, merged(headers), replyHandler);
	}

	@Override
	public IEventBus publish(String address, Object message, Map<String, String> headers) {
		return bus.publish(address, message, merged(headers));
	}

	@Override
	public IEventBus publish(String address, Object message, String tag, Map<String, String> headers) {
		return bus.publish(address, message, tag, merged(headers));
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

	@Override
	public ExecutorService getExecutorService() {
		return bus.getExecutorService();
	}

	@Override
	public long getInQueueMessages(String tag) {
		return bus.getInQueueMessages(tag);
	}

	@Override
	public long getProcessingMessages(String tag) {
		return bus.getProcessingMessages(tag);
	}

	@Override
	public long getFailedMessages(String tag) {
		return bus.getFailedMessages(tag);
	}

	@Override
	public long getCompletedMessages(String tag) {
		return bus.getCompletedMessages(tag);
	}

	@Override
	public long getSucceededMessages(String tag) {
		return 0;
	}

}
