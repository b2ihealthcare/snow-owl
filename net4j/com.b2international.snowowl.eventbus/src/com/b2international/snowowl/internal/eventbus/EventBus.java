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
package com.b2international.snowowl.internal.eventbus;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.net4j.EventBusConstants;
import com.b2international.snowowl.eventbus.net4j.IEventBusProtocol;

/**
 * @since 3.1
 */
public class EventBus extends Lifecycle implements IEventBus {

	private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);
	
	private Set<String> addressBook = new CopyOnWriteArraySet<>();
	private ConcurrentMap<String, ChoosableList<Handler>> protocolMap = new ConcurrentHashMap<>();
	private ConcurrentMap<String, ChoosableList<Handler>> handlerMap = new ConcurrentHashMap<>();
	private ConcurrentMap<String, AtomicLong> inQueueMessages = new ConcurrentHashMap<>();
	private ConcurrentMap<String, AtomicLong> currentlyProcessingMessages = new ConcurrentHashMap<>();
	private ConcurrentMap<String, AtomicLong> succeededMessages = new ConcurrentHashMap<>();
	private ConcurrentMap<String, AtomicLong> completedMessages = new ConcurrentHashMap<>();
	private ConcurrentMap<String, AtomicLong> failedMessages = new ConcurrentHashMap<>();
	private ExecutorService executorService;
	private final String description;
	private final int numberOfWorkers;
	private final WorkerExecutorServiceFactory executorServiceFactory;

	public EventBus() {
		this(EventBusConstants.GLOBAL_BUS, Runtime.getRuntime().availableProcessors());
	}
	
	public EventBus(String description, int numberOfWorkers) {
		CheckUtil.checkArg(description, "Description should be specified");
		CheckUtil.checkArg(numberOfWorkers > 0, "Number of workers must be greater than zero");
		this.description = description;
		this.numberOfWorkers = numberOfWorkers;
		this.executorServiceFactory = new WorkerExecutorServiceFactory();
	}

	@Override
	protected void doActivate() throws Exception {
		super.doActivate();
		executorService = executorServiceFactory.createExecutorService(description, numberOfWorkers);
	}

	@Override
	public IEventBus send(String address, Object message) {
		return send(address, message, IMessage.DEFAULT_TAG);
	}
	
	@Override
	public IEventBus send(String address, Object message, String tag) {
		return send(address, message, tag, null);
	}
	
	@Override
	public IEventBus send(String address, Object message, IHandler<IMessage> handler) {
		return send(address, message, IMessage.DEFAULT_TAG, handler);
	}

	@Override
	public IEventBus send(String address, Object message, String tag, IHandler<IMessage> replyHandler) {
		return sendMessageInternal(null, MessageFactory.createMessage(address, message, tag), true, replyHandler);
	}
	
	@Override
	public IEventBus publish(String address, Object message) {
		return publish(address, message, IMessage.DEFAULT_TAG);
	}
	
	@Override
	public IEventBus publish(String address, Object message, String tag) {
		return sendMessageInternal(null, MessageFactory.createMessage(address, message, tag), false, null);
	}
	
	@Override
	public IEventBus receive(IMessage message) {
		CheckUtil.checkArg(message instanceof BaseMessage, "Accepts only BaseMessage instances");
		receiveMessage((BaseMessage) message);
		queue(message);
		return this;
	}
	
	private void receiveMessage(BaseMessage message) {
		final String address = message.address();
		message.bus = this;
		final ChoosableList<Handler> handlers = handlerMap.get(address);
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
				for (final Handler holder : handlers.list) {
					doReceive(message, holder);
				}
			}
		} else {
			// TODO send reply to indicate that there is no handler
			LOG.trace("No event handler registered to handle message: {}", message);
		}
	}
	
	private void doReceive(final IMessage message, final Handler holder) {
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
					if (holder.isReplyHandler || !LifecycleUtil.isActive(holder.handler)) {
						unregisterHandler(holder.address, holder.handler);
					}
				}
			}
		});
	}
	
	@Override
	public IEventBus registerHandler(String address, IHandler<IMessage> handler) {
		if (isActive()) {
			registerHandler(address, handler, false, false);
		}
		return this;
	}

	@Override
	public IEventBus unregisterHandler(String address, IHandler<IMessage> handler) {
		if (isActive()) {
			MessageFactory.checkAddress(address);
			if (handler != null) {
				final ConcurrentMap<String, ChoosableList<Handler>> map = handler instanceof IEventBusProtocol ? protocolMap : handlerMap; 
				final ChoosableList<Handler> handlers = map.get(address);
				if (handlers != null) {
					synchronized (handlers) {
						final int size = handlers.list.size();
						// Requires a list traversal. This is tricky to optimise since we can't use a set since
						// we need fast ordered traversal for the round robin
						for (int i = 0; i < size; i++) {
							final Handler entry = handlers.list.get(i);
							if (entry.handler == handler) {
								handlers.list.remove(i);
								if (handlers.list.isEmpty()) {
									map.remove(address);
									// if this was the last non protocol based handler, send unregistration event
									if (!entry.isReplyHandler && !(handler instanceof IEventBusProtocol)) {
										addressBook.remove(address);
										fireEvent(new HandlerChangedEvent(this, address, false));
									}
								}
								LOG.trace("Unregistered handler {} from address {}", entry.handler, address);
								return this;
							}
						}
					}
				}
			}
		}
		return this;
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

	private void increment(String tag, Map<String, AtomicLong> toIncrement) {
		final AtomicLong counter = getOrCreateCounter(tag, toIncrement);
		counter.set(counter.incrementAndGet());
		toIncrement.put(tag, counter);
	}

	private void decrement(String tag, Map<String, AtomicLong> toDecrement) {
		final AtomicLong counter = getOrCreateCounter(tag, toDecrement);
		counter.set(counter.decrementAndGet());
		toDecrement.put(tag, counter);
	}
	
	private AtomicLong getOrCreateCounter(final String tag, final Map<String, AtomicLong> counterMap) {
		if (!counterMap.containsKey(tag)) {
			synchronized (counterMap) {
				if (!counterMap.containsKey(tag)) {
					counterMap.put(tag, new AtomicLong(0L));
				}
			}
		}
		
		return counterMap.get(tag);
	}
	
	private void registerHandler(String address, IHandler<IMessage> handler, boolean replyHandler, boolean localOnly) {
		checkActive();
		MessageFactory.checkAddress(address);
		if (handler != null) {
			ChoosableList<Handler> handlers = null;
			if (handler instanceof IEventBusProtocol) {
				handlers = protocolMap.get(address);
				if (handlers == null) {
					handlers = new ChoosableList<Handler>();
					final ChoosableList<Handler> previousHandlers = protocolMap.putIfAbsent(address, handlers);
					if (previousHandlers != null) {
						handlers = previousHandlers;
					}
				}
			} else {
				handlers = handlerMap.get(address);
				if (handlers == null) {
					handlers = new ChoosableList<Handler>();
					final ChoosableList<Handler> previousHandlers = handlerMap.putIfAbsent(address, handlers);
					if (previousHandlers != null) {
						handlers = previousHandlers;
					}
				}
			}
			final Handler h = new Handler(address, handler, executorService, replyHandler);
			if (!handlers.list.contains(h)) {
				handlers.list.add(h);
				LOG.trace("Registered handler {} to address {}", handler, address);
				if (!replyHandler && handlers.list.size() == 1 && !(handler instanceof IEventBusProtocol)) {
					addressBook.add(address);
					fireEvent(new HandlerChangedEvent(this, address, true));
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
	
	private IEventBus sendMessageInternal(IEventBusProtocol protocol, BaseMessage message, boolean send, IHandler<IMessage> replyHandler) {
		checkActive();
		message.send = send;
		if (replyHandler != null && send) {
			// register a random UUID address to handle result for this send message
			final String replyAddress = UUID.randomUUID().toString();
			message.replyAddress = replyAddress;
			// register reply handler to allow result 
			registerHandler(replyAddress, replyHandler, true, true);
			// TODO set timer with a given timeout to remove this handler
		}
		if (protocol != null) {
			protocol.handle(message);
		} else {
			final ChoosableList<Handler> handlers = protocolMap.get(message.address());
			if (handlers != null) {
				receiveMessage(handlers, message);
			}
			receiveMessage(message);
		}
		return this;
	}
	
	public static class Factory extends org.eclipse.net4j.util.factory.Factory {

		public Factory() {
			super(EventBusConstants.EVENT_BUS_PRODUCT_GROUP, EventBusConstants.PROTOCOL_NAME);
		}

		@Override
		public Object create(String description) throws ProductCreationException {
			final String[] values = description.split(":");
			return new EventBus(values[0], Integer.parseInt(values[1]));
		}

	}
	
	private static class Handler {
		
		final String address;
		final IHandler<IMessage> handler;
		final boolean isReplyHandler;
		ExecutorService context;

		public Handler(String address, IHandler<IMessage> handler, ExecutorService context, boolean isReplyHandler) {
			this.address = address;
			this.handler = handler;
			this.context = context;
			this.isReplyHandler = isReplyHandler;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((address == null) ? 0 : address.hashCode());
			result = prime * result + ((handler == null) ? 0 : handler.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Handler other = (Handler) obj;
			if (address == null) {
				if (other.address != null)
					return false;
			} else if (!address.equals(other.address))
				return false;
			if (handler == null) {
				if (other.handler != null)
					return false;
			} else if (!handler.equals(other.handler))
				return false;
			return true;
		}
		
	}

	private static class ChoosableList<T> {

		final List<T> list = new CopyOnWriteArrayList<>();
		final AtomicInteger pos = new AtomicInteger(0);

		T choose() {
			while (true) {
				int size = list.size();
				if (size == 0) {
					return null;
				}
				int p = pos.getAndIncrement();
				if (p >= size - 1) {
					pos.set(0);
				}
				try {
					return list.get(p);
				} catch (IndexOutOfBoundsException e) {
					// Can happen
					pos.set(0);
				}
			}
		}
	}

	/*package*/ IEventBus sendReply(IEventBusProtocol replyProtocol,
			BaseMessage message, IHandler<IMessage> replyHandler) {
		return sendMessageInternal(replyProtocol, message, true, replyHandler);
	}

}