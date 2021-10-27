/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.internal.eventbus;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.netty.IEventBusNettyHandler;
import com.b2international.snowowl.internal.eventbus.HandlerChangedEvent.Type;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;
import com.google.common.primitives.Ints;

/**
 * @since 3.1
 */
public class EventBus implements IEventBus {

	private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);
	
	private static final String GLOBAL_BUS = "eventbus";
	
	private final String description;
	private final int maxThreads;
	private final ExecutorServiceFactory executorServiceFactory;

	private final ConcurrentMap<String, ChoosableList<Handler>> remoteHandlerMap;
	private final ConcurrentMap<String, ChoosableList<Handler>> localHandlerMap;
	private final ConcurrentMap<String, AtomicLong> inQueueMessages;
	private final ConcurrentMap<String, AtomicLong> currentlyProcessingMessages;
	private final ConcurrentMap<String, AtomicLong> succeededMessages;
	private final ConcurrentMap<String, AtomicLong> completedMessages;
	private final ConcurrentMap<String, AtomicLong> failedMessages;
	
	private final Multiset<String> addressBook = ConcurrentHashMultiset.create();
	
	private ExecutorService executorService;

	public EventBus() {
		this(GLOBAL_BUS, Runtime.getRuntime().availableProcessors());
	}
	
	public EventBus(String description, int maxThreads) {
		checkArgument(description != null, "Description should be specified");
		checkArgument(maxThreads >= 0, "Number of workers must be greater than zero");
		this.description = description;
		this.maxThreads = maxThreads;
		this.executorServiceFactory = (maxThreads == 0) ? ExecutorServiceFactory.DIRECT : new WorkerExecutorServiceFactory();
		
		// init stat maps with 1-4 concurrencyLevel
		final int concurrencyLevel = Ints.constrainToRange(maxThreads, 1, 4);
		final MapMaker mapMaker = new MapMaker().concurrencyLevel(concurrencyLevel);
		
		this.remoteHandlerMap = mapMaker.makeMap();
		this.localHandlerMap = mapMaker.makeMap();
		this.inQueueMessages = mapMaker.makeMap();
		this.currentlyProcessingMessages = mapMaker.makeMap();
		this.succeededMessages = mapMaker.makeMap();
		this.completedMessages = mapMaker.makeMap();
		this.failedMessages = mapMaker.makeMap();
	}

	public void activate() {
		executorService = executorServiceFactory.createExecutorService(description, maxThreads);
	}
	
	public void deactivate() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	public boolean isActive() {
		return (executorService != null) && !executorService.isShutdown();
	}
	
	public void checkActive() {
		checkState(isActive(), "Event bus is not active.");
	}

	@Override
	public IEventBus send(String address, Object message, Map<String, String> headers) {
		return send(address, message, IMessage.DEFAULT_TAG, headers);
	}
	
	@Override
	public IEventBus send(String address, Object message, String tag, Map<String, String> headers) {
		return send(address, message, tag, headers, null);
	}
	
	@Override
	public IEventBus send(String address, Object message, Map<String, String> headers, IHandler<IMessage> handler) {
		return send(address, message, IMessage.DEFAULT_TAG, headers, handler);
	}

	@Override
	public IEventBus send(String address, Object message, String tag, Map<String, String> headers, IHandler<IMessage> replyHandler) {
		return sendMessageInternal(null, MessageFactory.createMessage(address, message, tag, headers), true, replyHandler);
	}
	
	@Override
	public IEventBus publish(String address, Object message, Map<String, String> headers) {
		return publish(address, message, IMessage.DEFAULT_TAG, headers);
	}
	
	@Override
	public IEventBus publish(String address, Object message, String tag, Map<String, String> headers) {
		return sendMessageInternal(null, MessageFactory.createMessage(address, message, tag, headers), false, null);
	}
	
	/*package*/ IEventBus sendReply(IEventBusNettyHandler via, BaseMessage message, IHandler<IMessage> replyHandler) {
		return sendMessageInternal(via, message, true, replyHandler);
	}
	
	private IEventBus sendMessageInternal(IEventBusNettyHandler via, BaseMessage message, boolean send, IHandler<IMessage> replyHandler) {
		checkActive();
		message.send = send;
		
		// Register reply handler to a random address for non-broadcast messages only, if they have one 
		if (replyHandler != null && send) {
			final String replyAddress = UUID.randomUUID().toString();
			message.replyAddress = replyAddress;
			registerHandler(replyAddress, replyHandler, true);
		}
		
		if (via != null) {
			// The message is sent back through the same protocol handler it was received from 
			via.handle(message);
		} else {
			final ChoosableList<Handler> handlers = remoteHandlerMap.get(message.address());
			if (handlers != null) {
				// Remote handlers are optional, no "dead letter" response needs to be sent if they are not present
				receiveMessage(handlers, message);
			}
			receiveMessage(message);
		}
		
		return this;
	}
	
	@Override
	public IEventBus receive(IMessage message) {
		checkArgument(message instanceof BaseMessage, "Accepts only BaseMessage instances");
		receiveMessage((BaseMessage) message);
		return this;
	}
	
	private void receiveMessage(BaseMessage message) {
		message.bus = this;
		final ChoosableList<Handler> handlers = localHandlerMap.get(message.address());
		receiveMessage(handlers, message);
	}
	
	private void receiveMessage(ChoosableList<Handler> handlers, BaseMessage message) {
		LOG.trace("Received message: {}", message);
		if (handlers != null) {
			if (message.isSend()) {
				final Handler handler = handlers.choose();
				if (handler != null) {
					doReceive(message, handler);
				}
			} else {
				for (final Handler holder : handlers) {
					doReceive(message, holder);
				}
			}
		} else {
			// TODO send reply to indicate that there is no handler
			LOG.trace("No event handler registered to handle message: {}", message);
		}
	}
	
	private void doReceive(final IMessage message, final Handler holder) {
		queue(message);
		holder.context.submit(new Runnable() {
			@Override
			public void run() {
				try {
					process(message);
					holder.handler.handle(message);
				} catch (Exception e) {
					LOG.error("Exception happened while delivering message", e);
					message.fail(e);
				} finally {
					complete(message);
					if (holder.isReplyHandler) {
						unregisterHandler(holder.address, holder.handler, holder.isReplyHandler);
					}
				}
			}
		});
	}
	
	private void queue(IMessage message) {
		final String tag = message.tag();
		increment(tag, inQueueMessages);
	}

	private void process(IMessage message) {
		final String tag = message.tag();
		decrement(tag, inQueueMessages);
		increment(tag, currentlyProcessingMessages);
	}

	private void complete(IMessage message) {
		final String tag = message.tag();
		decrement(tag, currentlyProcessingMessages);
	
		if (message.isSucceeded()) {
			increment(tag, succeededMessages);
		} else {
			increment(tag, failedMessages);
		}
	
		increment(tag, completedMessages);
	}

	private void increment(String tag, ConcurrentMap<String, AtomicLong> toIncrement) {
		final AtomicLong counter = getOrCreateCounter(tag, toIncrement);
		counter.incrementAndGet();
	}

	private void decrement(String tag, ConcurrentMap<String, AtomicLong> toDecrement) {
		final AtomicLong counter = getOrCreateCounter(tag, toDecrement);
		counter.decrementAndGet();
	}

	private AtomicLong getOrCreateCounter(final String tag, final ConcurrentMap<String, AtomicLong> counterMap) {
		return counterMap.computeIfAbsent(tag, key -> new AtomicLong());
	}

	@Override
	public IEventBus registerHandler(String address, IHandler<IMessage> handler) {
		if (isActive()) {
			registerHandler(address, handler, false);
		}
		return this;
	}

	private void registerHandler(String address, IHandler<IMessage> handler, boolean replyHandler) {
		checkActive();
		MessageFactory.checkAddress(address);
		if (handler == null) {
			return;
		}
		
		final ChoosableList<Handler> handlers;
		final boolean localHandler;
		
		if (handler instanceof IEventBusNettyHandler) {
			handlers = remoteHandlerMap.computeIfAbsent(address, key -> new ChoosableList<>());
			localHandler = false;
		} else {
			handlers = localHandlerMap.computeIfAbsent(address, key -> new ChoosableList<>());
			localHandler = true;
		}
		
		final Handler h = new Handler(address, handler, executorService, replyHandler);
		final boolean handlerAdded = handlers.add(h);
		if (handlerAdded) {
			LOG.trace("Registered handler {} to address {}", handler, address);
			
			if (localHandler && !replyHandler && !HANDLERS.equals(address)) {
				final int oldCount = addressBook.add(address, 1);
				if (oldCount == 0) {
					publish(HANDLERS, new HandlerChangedEvent(Type.ADDED, Set.of(address)), Map.of());
				}
			}
		}
	}

	@Override
	public IEventBus unregisterHandler(String address, IHandler<IMessage> handler) {
		if (isActive()) {
			unregisterHandler(address, handler, false);
		}
		return this;
	}
	
	private void unregisterHandler(String address, IHandler<IMessage> handler, boolean replyHandler) {
		checkActive();
		MessageFactory.checkAddress(address);
		if (handler == null) {
			return;
		}

		final ConcurrentMap<String, ChoosableList<Handler>> handlerMap;
		final boolean localHandler;
		
		if (handler instanceof IEventBusNettyHandler) {
			handlerMap = remoteHandlerMap;
			localHandler = false;
		} else {
			handlerMap = localHandlerMap;
			localHandler = true;
		}
				
		final ChoosableList<Handler> handlers = handlerMap.get(address);
		if (handlers == null) {
			return;
		}
			
		final boolean handlerRemoved = handlers.removeIf(holder -> holder.handler == handler);
		if (handlerRemoved) {
			LOG.trace("Unregistered handler {} from address {}", handler, address);
			
			// Remove list atomically if empty 
			handlerMap.computeIfPresent(address, (key, currentHandlers) -> {
				if (currentHandlers.isEmpty()) {
					return null;
				} else {
					return currentHandlers;
				}
			});
			
			if (localHandler && !replyHandler && !HANDLERS.equals(address)) {
				// if this was the last non protocol based handler, send unregistration event
				final int oldCount = addressBook.remove(address, 1);
				if (oldCount == 1) {
					publish(HANDLERS, new HandlerChangedEvent(Type.REMOVED, Set.of(address)), Map.of());
				}
			}
		}
	}
	
	@Override
	public Set<String> getAddressBook() {
		return new HashSet<String>(addressBook);
	}
	
	@Override
	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	@Override
	public long getInQueueMessages(String tag) {
		return getOrCreateCounter(tag, inQueueMessages).get();
	}

	@Override
	public long getProcessingMessages(String tag) {
		return getOrCreateCounter(tag, currentlyProcessingMessages).get();
	}
	
	@Override
	public long getFailedMessages(String tag) {
		return getOrCreateCounter(tag, failedMessages).get();
	}
	
	@Override
	public long getCompletedMessages(String tag) {
		return getOrCreateCounter(tag, completedMessages).get();
	}
	
	@Override
	public long getSucceededMessages(String tag) {
		return getOrCreateCounter(tag, succeededMessages).get();
	}
	
	private static class Handler {
		
		final String address;
		final IHandler<IMessage> handler;
		final boolean isReplyHandler;
		final ExecutorService context;

		public Handler(String address, IHandler<IMessage> handler, ExecutorService context, boolean isReplyHandler) {
			this.address = address;
			this.handler = handler;
			this.context = context;
			this.isReplyHandler = isReplyHandler;
		}

		@Override
		public int hashCode() {
			return Objects.hash(address, handler);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (obj == null) { return false; }
			if (getClass() != obj.getClass()) {	return false; }
			
			Handler other = (Handler) obj;
			return Objects.equals(address, other.address) &&
					Objects.equals(handler, other.handler);
		}
	}
}
