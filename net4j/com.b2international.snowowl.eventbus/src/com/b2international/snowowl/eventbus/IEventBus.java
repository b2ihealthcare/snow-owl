/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Event Bus to send messages.
 * 
 * @since 3.1
 */
public interface IEventBus {

	/**
	 * Sends the message over the event bus to the specified address.
	 * 
	 * @param address
	 * @param message
	 * @return this {@link IEventBus} for chaining
	 */
	IEventBus send(String address, Object message, Map<String, String> headers);

	/**
	 * Sends the message over the event bus to the specified address.
	 * 
	 * @param address
	 * @param message
	 * @param replyHandler
	 * @return this {@link IEventBus} for chaining
	 */
	IEventBus send(String address, Object message, Map<String, String> headers, IHandler<IMessage> replyHandler);
	
	/**
	 * Sends the message over the event bus to the specified address with a tag.
	 * 
	 * @param address
	 * @param message
	 * @param tag
	 * @return this {@link IEventBus} for chaining
	 */
	IEventBus send(String address, Object message, String tag, Map<String, String> headers);
	
	/**
	 * Sends the message over the event bus to the specified address with a tag.
	 * 
	 * @param address
	 * @param message
	 * @param replyHandler
	 * @param tag
	 * @return this {@link IEventBus} for chaining
	 */
	IEventBus send(String address, Object message, String tag, Map<String, String> headers, IHandler<IMessage> replyHandler);

	/**
	 * Sends the message over the event bus to all available handlers on the
	 * given address.
	 * 
	 * @param address
	 * @param message
	 * @return
	 */
	IEventBus publish(String address, Object message, Map<String, String> headers);
	
	/**
	 * Sends the message with a tag over the event bus to all available handlers on the
	 * given address.
	 * 
	 * @param address
	 * @param message
	 * @param tag
	 * @return
	 */
	IEventBus publish(String address, Object message, String tag, Map<String, String> headers);

	/**
	 * Receives the messages directly on this event bus bypassing all remote
	 * node. NOTE: DO NOT USE THIS METHOD, NECESSARY FOR THE PROTOCOL BUT NOT
	 * FOR CLIENTS.
	 * 
	 * @param message
	 * @return
	 */
	IEventBus receive(IMessage message);

	/**
	 * Register a handler against the given address.
	 * 
	 * @param address
	 * @param handler
	 * @return this {@link IEventBus} for chaining
	 */
	IEventBus registerHandler(String address, IHandler<IMessage> handler);

	/**
	 * Unregisters the given handler from the given address.
	 * 
	 * @param address
	 * @param handler
	 * @return this {@link IEventBus} for chaining
	 */
	IEventBus unregisterHandler(String address, IHandler<IMessage> handler);
	
	/**
	 * Returns all currently known locally registered addresses.
	 * @return
	 */
	Set<String> getAddressBook();
	
	/**
	 * @return the currently registered executor service
	 */
	ExecutorService getExecutorService();
	
	/**
	 * @param tag
	 * @return the amount of messages that are currently in queue by tag.
	 */
	long getInQueueMessages(String tag);
	
	/**
	 * @param tag
	 * @return the amount of currently processing messages by tag.
	 */
	long getProcessingMessages(String tag);
	
	/**
	 * @param tag
	 * @return the amount of messages that have failed by tag.
	 */
	long getFailedMessages(String tag);
	
	/**
	 * @param tag
	 * @return the amount of messages that are completed by tag.
	 */
	long getCompletedMessages(String tag);
	
	/**
	 * @param tag
	 * @return the amount of succeeded messages that are completed by tag.
	 */
	long getSucceededMessages(String tag);

}