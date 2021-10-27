/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.internal.eventbus.netty;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.netty.IEventBusNettyHandler;
import com.b2international.snowowl.internal.eventbus.HandlerChangedEvent;
import com.b2international.snowowl.internal.eventbus.HandlerChangedEvent.Type;
import com.b2international.snowowl.internal.eventbus.MessageFactory;
import com.google.common.collect.Sets;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * A message handler bridge implementation for a Netty channel and a local event
 * bus. It exhibits the following behavior:
 * <ul>
 * <li>Messages read from the Netty channel are submitted to the local event bus
 * via {@link IEventBus#receive(IMessage)};
 * 
 * <li>Messages received from the local event bus are written to the Netty
 * channel in a serialization-friendly format;
 * 
 * <li>Appearance and disappearance of non-bridge, non-reply listeners on the
 * remote side for any address results in the registration/unregistration of
 * this bridge to the same address on the local event bus, so that future
 * messages can be forwarded over the Netty channel;
 * 
 * <li>An optional address book synchronization step lets the bridge know what
 * non-bridge, non-reply listeners are registered on each end at the time of
 * connection.
 * </ul>
 * 
 * @since 8.0
 */
public class EventBusNettyHandler extends SimpleChannelInboundHandler<IMessage> implements IEventBusNettyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(EventBusNettyHandler.class);
	
	private final boolean sendInitialSync;
	private final IEventBus eventBus;
	private final CountDownLatch addressBookSynced = new CountDownLatch(1); 
	private final Set<String> remoteAddresses = Sets.newConcurrentHashSet();
	
	private volatile ChannelHandlerContext ctx;

	public EventBusNettyHandler(final boolean sendInitialSync, final IEventBus eventBus) {
		this.sendInitialSync = sendInitialSync;
		this.eventBus = eventBus;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		eventBus.registerHandler(IEventBus.HANDLERS, this);

		if (sendInitialSync) {
			final HandlerChangedEvent e = new HandlerChangedEvent(Type.SYNC, getAddressBook());
			sendMessage(ctx, MessageFactory.createMessage(IEventBus.HANDLERS, e, IMessage.DEFAULT_TAG, Map.of()));
		}
		
		super.channelActive(ctx);
	}
	
	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		this.ctx = null;
		eventBus.unregisterHandler(IEventBus.HANDLERS, this);
		remoteAddresses.forEach(address -> eventBus.unregisterHandler(address, this));
		
		super.channelInactive(ctx);
	}
	
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final IMessage message) throws Exception {
		/*
		 * XXX: If the message is related to handler registrations, we handle it
		 * out-of-band, without passing it on to the local event bus. The event bus
		 * itself will publish ADDED and REMOVED events, which we send out in
		 * handle(IMessage) unmodified to bridges and local listeners.
		 */
		if (IEventBus.HANDLERS.equals(message.address())) {
			final HandlerChangedEvent event = message.body(HandlerChangedEvent.class);
			final HandlerChangedEvent.Type type = event.getType();
			final Set<String> addresses = event.getAddresses();
			
			switch (type) {
				case SYNC:
					registerAddresses(addresses);
					
					if (IMessage.DEFAULT_TAG.equals(message.tag())) {
						// We send an address book reply
						final HandlerChangedEvent e = new HandlerChangedEvent(Type.SYNC, getAddressBook());
						sendMessage(ctx, MessageFactory.createMessage(IEventBus.HANDLERS, e, IMessage.REPLY_TAG, Map.of()));
					}

					// Sending of messages can now commence
					addressBookSynced.countDown();
					break;
				case ADDED:
					waitForAddressBook();
					registerAddresses(addresses);
					break;
				case REMOVED:
					waitForAddressBook();
					unregisterAddresses(addresses);
					break;
				default:
					LOG.warn("Unexpected handler change request type '{}', ignoring.");
					break;
			}
		} else {
			// All other messages are going directly to the event bus through receive(IMessage).
			waitForAddressBook();
			eventBus.receive(MessageFactory.readMessage(message, this));
		}
	}
	
	private void waitForAddressBook() {
		boolean success;
		
		try {
			success = addressBookSynced.await(3L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			success = false;
		}
		
		if (!success) {
			// We don't want to wait further if the time ran out or waiting was interrupted
			addressBookSynced.countDown();
		}
	}

	@Override
	public void handle(final IMessage message) {
		final ChannelHandlerContext localCtx = ctx;
		if (localCtx != null) {
			waitForAddressBook();
			sendMessage(localCtx, message);
		}
	}
	
	private ChannelFuture sendMessage(final ChannelHandlerContext ctx, final IMessage message) {
		/* 
		 * Forward the message to the client through the channel, serializing the message 
		 * body to a byte array first.
		 */
		try {
			return ctx.writeAndFlush(MessageFactory.writeMessage(message));
		} catch (final IOException e) {
			LOG.error("Exception happened while sending async request", e);
			return null;
		}
	}
	
	private void registerAddresses(final Set<String> addresses) {
		checkNotNull(addresses, "addresses");
		for (final String address : addresses) {
			if (remoteAddresses.add(address)) {
				eventBus.registerHandler(address, this);
			}
		}
	}
	
	private void unregisterAddresses(final Set<String> addresses) {
		checkNotNull(addresses, "addresses");
		for (final String address : addresses) {
			if (remoteAddresses.remove(address)) {
				eventBus.unregisterHandler(address, this);
			}
		}
	}
	
	private Set<String> getAddressBook() {
		return eventBus.getAddressBook();
	}
}
