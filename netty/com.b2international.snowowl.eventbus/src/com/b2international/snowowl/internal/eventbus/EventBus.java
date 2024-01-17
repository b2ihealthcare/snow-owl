/*
 * Copyright 2011-2022 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.snowowl.internal.eventbus.MessageFactory.checkAddress;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
		
		if (maxThreads == 0) {
			this.executorServiceFactory = ExecutorServiceFactory.DIRECT;
		} else {
			this.executorServiceFactory = new WorkerExecutorServiceFactory();
		}
		
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
		return send(address, message, IMessage.TAG_EVENT, headers);
	}
	
	@Override
	public IEventBus send(String address, Object message, String tag, Map<String, String> headers) {
		return send(address, message, tag, headers, null);
	}
	
	@Override
	public IEventBus send(String address, Object message, Map<String, String> headers, IHandler<IMessage> replyHandler) {
		return send(address, message, IMessage.TAG_EVENT, headers, replyHandler);
	}

	@Override
	public IEventBus send(String address, Object message, String tag, Map<String, String> headers, IHandler<IMessage> replyHandler) {
		return sendMessage(true, MessageFactory.createMessage(address, message, tag, headers), replyHandler);
	}
	
	@Override
	public IEventBus publish(String address, Object message, Map<String, String> headers) {
		return publish(address, message, IMessage.TAG_EVENT, headers);
	}
	
	@Override
	public IEventBus publish(String address, Object message, String tag, Map<String, String> headers) {
		return sendMessage(false, MessageFactory.createMessage(address, message, tag, headers), null);
	}
	
	/*package*/ IEventBus sendMessage(boolean send, BaseMessage message, IHandler<IMessage> replyHandler) {
		checkActive();
		message.bus = this;
		message.send = send;
		
		// Register reply handler to a random address for non-broadcast messages only, if they have one 
		if (replyHandler != null && send) {
			final String replyAddress = UUID.randomUUID().toString();
			message.replyAddress = replyAddress;
			doRegisterHandler(replyAddress, replyHandler, true);
		}
		
		if (RECORD_SEND_STACK) {
			final Throwable t = new Exception("Message was submitted from this call stack").fillInStackTrace();
			
			try (StringWriter sw = new StringWriter()) {
				t.printStackTrace(new PrintWriter(sw));
				
				final String sendStack = sw.toString();
				Map<String, String> headersWithStack = newHashMap(message.headers());
				headersWithStack.put("sendStack", sendStack);
				message.headers = Map.copyOf(headersWithStack);
				
			} catch (IOException unexpected) {
				// String writer should not encounter any I/O exceptions
			}
		}
		
		// Allow both local and remote handlers to process the message
		handleMessage(message, remoteHandlerMap);
		handleMessage(message, localHandlerMap);
		return this;
	}
	
	@Override
	public IEventBus receive(IMessage message, IHandler<IMessage> replyHandler) {
		checkArgument(message instanceof BaseMessage, "Accepts only BaseMessage instances");
		final BaseMessage baseMessage = (BaseMessage) message;
		
		// Register reply handler to a random address for non-broadcast messages only, if they have one 
		if (replyHandler != null && message.isSend()) {
			final String replyAddress = UUID.randomUUID().toString();
			baseMessage.replyAddress = replyAddress;
			doRegisterHandler(replyAddress, replyHandler, true);
		}
		
		// Received messages are delivered to local listeners only (see javadoc)
		baseMessage.bus = this;
		handleMessage(baseMessage, localHandlerMap);
		return this;
	}
	
	private void handleMessage(BaseMessage message, ConcurrentMap<String, ChoosableList<Handler>> handlerMap) {
		LOG.trace("Received message: {}", message);
		final ChoosableList<Handler> handlers = handlerMap.get(message.address());
		
		if (handlers == null) {
			// TODO send reply to indicate that there is no handler
			LOG.trace("No event handler registered to handle message: {}", message);
			return;
		}
		
		if (message.isSend()) {
			// Pick a single item out of all available handlers
			final Handler handler = handlers.choose();
			if (handler != null) {
				handleMessage(message, handler);
			}
		} else {
			// Pass the message on to all available handlers
			for (final Handler handler : handlers) {
				handleMessage(message, handler);
			}
		}
	}
	
	private void handleMessage(final IMessage message, final Handler handler) {
		queue(message);
		
		executorService.submit(() -> {
			try {
				process(message);
				handler.handleMessage(message);
			} catch (Exception e) {
				LOG.error("Exception happened while delivering message", e);
				message.fail(e);
			} finally {
				complete(message);
				handler.onComplete(this);
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
			doRegisterHandler(address, handler, false);
		}
		return this;
	}

	private void doRegisterHandler(String address, IHandler<IMessage> handler, boolean replyHandler) {
		checkActive();
		checkAddress(address);
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
		
		final Handler h = new Handler(address, handler, replyHandler);
		final boolean handlerAdded = handlers.addIfAbsent(h);
		if (!handlerAdded) {
			return;
		}
		
		LOG.trace("Registered handler {} to address {}", handler, address);
			
		if (!replyHandler && !HANDLERS.equals(address)) {
			// if this is the first handler, broadcast registration event
			final int oldCount = addressBook.add(address, 1);
			if (oldCount == 0) {
				publish(HANDLERS, new HandlerChangedEvent(Type.ADDED, Set.of(address)), Map.of(LOCAL_HANDLER, Boolean.toString(localHandler)));
			}
		}
	}

	@Override
	public IEventBus unregisterHandler(String address, IHandler<IMessage> handler) {
		if (isActive()) {
			doUnregisterHandler(address, handler);
		}
		return this;
	}
	
	private void doUnregisterHandler(String address, IHandler<IMessage> handler) {
		checkActive();
		checkAddress(address);
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
				
		handlerMap.computeIfPresent(address, (key, currentHandlers) -> {
			// The "isReplyHandler" flag is not compared in equals checks
			final Handler h = new Handler(address, handler, false);
			final boolean handlerRemoved = currentHandlers.remove(h);

			if (handlerRemoved) {
				LOG.trace("Unregistered handler {} from address {}", handler, address);
				
				if (!HANDLERS.equals(address)) {
					/*
					 * If this was the last handler, broadcast unregistration event. Reply handlers
					 * were not in the address book to begin with, so no check is needed.
					 */
					final int oldCount = addressBook.remove(address, 1);
					if (oldCount == 1) {
						publish(HANDLERS, new HandlerChangedEvent(Type.REMOVED, Set.of(address)), Map.of(LOCAL_HANDLER, Boolean.toString(localHandler)));
					}
				}
			}

			// Completely remove handler list if it is empty
			if (currentHandlers.isEmpty()) {
				return null;
			} else {
				return currentHandlers;
			}
		});
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
		
		private final String address;
		private final IHandler<IMessage> handler;
		private final boolean isReplyHandler;

		public Handler(String address, IHandler<IMessage> handler, boolean isReplyHandler) {
			this.address = address;
			this.handler = handler;
			this.isReplyHandler = isReplyHandler;
		}

		public void handleMessage(IMessage message) {
			handler.handle(message);
		}

		public void onComplete(IEventBus eventBus) {
			if (isReplyHandler) {
				eventBus.unregisterHandler(address, handler);
			}			
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
