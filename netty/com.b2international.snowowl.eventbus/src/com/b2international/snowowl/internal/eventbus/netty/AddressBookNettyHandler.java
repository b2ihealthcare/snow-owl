/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.events.ClientConnectionNotification;
import com.b2international.snowowl.eventbus.netty.IEventBusNettyHandler;
import com.b2international.snowowl.internal.eventbus.HandlerChangedEvent;
import com.b2international.snowowl.internal.eventbus.MessageFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * A message handler bridge implementation for a Netty channel and a local event
 * bus. It exhibits the following behavior:
 * <ul>
 * <li>Appearance and disappearance of non-bridge, non-reply listeners on the
 * remote side for any address results in the registration/unregistration of
 * this bridge to the same address on the local event bus, so that future
 * messages can be forwarded over the channel;
 * 
 * <li>An optional address book synchronization step lets the bridge know what
 * non-bridge, non-reply listeners are registered on each end at the time of
 * connection.
 * </ul>
 * 
 * @since 8.1.0
 */
public class AddressBookNettyHandler extends SimpleChannelInboundHandler<IMessage> implements IEventBusNettyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(AddressBookNettyHandler.class);
	
	private static final LoadingCache<Channel, ChannelPromise> SYNC_FUTURES = CacheBuilder.newBuilder()
		.build(CacheLoader.from(ch -> ch.newPromise()));
	
	private final boolean sendInitialSync;
	private final IEventBus eventBus;
	private final IHandler<IMessage> messageHandler;

	// Handler is stateful, ie. a separate instance is maintained for each connection
	private final Set<String> remoteAddresses = Sets.newConcurrentHashSet();
	
	private volatile ChannelHandlerContext ctx; 

	public AddressBookNettyHandler(final boolean sendInitialSync, final IEventBus eventBus, final IHandler<IMessage> messageHandler) {
		this.sendInitialSync = sendInitialSync;
		this.eventBus = eventBus;
		this.messageHandler = messageHandler;
	}
	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		eventBus.registerHandler(IEventBus.HANDLERS, this);

		if (sendInitialSync) {
			// Send own address book immediately after connection is established if requested
			final HandlerChangedEvent syncEvent = new HandlerChangedEvent(HandlerChangedEvent.Type.SYNC, getAddressBook());
			channelWrite(ctx, MessageFactory.createMessage(IEventBus.HANDLERS, syncEvent, IMessage.TAG_EVENT, Map.of()));
		}
		
		super.channelActive(ctx);
	}
	
	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		this.ctx = null;
		SYNC_FUTURES.invalidate(ctx);
		
		eventBus.unregisterHandler(IEventBus.HANDLERS, this);
		
		// Unregister handler that does the actual exchange of messages from all subscribed addresses
		remoteAddresses.forEach(address -> eventBus.unregisterHandler(address, messageHandler));
		remoteAddresses.clear();
		
		if (sendInitialSync) {
			new ClientConnectionNotification(false, getChannelId(ctx)).publish(eventBus);
		}
		
		super.channelInactive(ctx);
	}
	
	@Override
	public boolean acceptInboundMessage(final Object msg) throws Exception {
		if (!super.acceptInboundMessage(msg)) {
			return false;
		}
		
		/*
		 * XXX: If the message is related to handler registration on the other side, we
		 * handle it out-of-band, without passing it on to our event bus.
		 */
		final IMessage message = (IMessage) msg;
		return IEventBus.HANDLERS.equals(message.address());
	}
	
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final IMessage message) throws Exception {
		final HandlerChangedEvent event = message.body(HandlerChangedEvent.class);
		final HandlerChangedEvent.Type type = event.getType();
		final Set<String> addresses = event.getAddresses();
			
		switch (type) {
			case SYNC:
				registerAddresses(addresses);
				
				if (IMessage.TAG_EVENT.equals(message.tag())) {
					// We send an address book reply (same address, but different tag)
					final HandlerChangedEvent syncReply = new HandlerChangedEvent(HandlerChangedEvent.Type.SYNC, getAddressBook());
					channelWrite(ctx, MessageFactory.createMessage(IEventBus.HANDLERS, syncReply, IMessage.TAG_REPLY, Map.of()));
				}

				// Signal that the channel can now exchange regular messages
				getFuture(ctx.channel()).setSuccess();
				
				if (sendInitialSync) {
					new ClientConnectionNotification(true, getChannelId(ctx)).publish(eventBus);
				}
				break;
			case ADDED:
				registerAddresses(addresses);
				break;
			case REMOVED:
				unregisterAddresses(addresses);
				break;
			default:
				LOG.warn("Unexpected handler change request type '{}', ignoring.", type);
				break;
		}
	}
	
	private String getChannelId(final ChannelHandlerContext ctx) {
		return ctx.channel().id().asShortText();
	}

	public boolean awaitAddressBookSynchronized(final Channel ch, final long time, final TimeUnit timeUnit) throws InterruptedException {
		return getFuture(ch).await(time, timeUnit);
	}

	private ChannelPromise getFuture(final Channel ch) {
		return SYNC_FUTURES.getUnchecked(ch);
	}
	
	private void channelWrite(final ChannelHandlerContext ctx, final IMessage message) {
		try {
			ctx.writeAndFlush(MessageFactory.writeMessage(message))
				.addListener(f -> { if (!f.isSuccess()) {
					LOG.error("Exception happened when sending message", f.cause());
				}});
		} catch (final Throwable e) {
			LOG.error("Exception happened trying to send message", e);
		}
	}

	@Override
	public void handle(final IMessage message) {
		final ChannelHandlerContext localCtx = ctx;
		if (localCtx != null) {
			// Notify our peer about local handlers only
			final Map<String, String> headers = message.headers();
			if ("true".equals(headers.get(IEventBus.LOCAL_HANDLER))) {
				channelWrite(localCtx, message);
			}
		}
	}

	private void registerAddresses(final Set<String> addresses) {
		checkNotNull(addresses, "addresses");
		for (final String address : addresses) {
			if (remoteAddresses.add(address)) {
				eventBus.registerHandler(address, messageHandler);
			}
		}
	}
	
	private void unregisterAddresses(final Set<String> addresses) {
		checkNotNull(addresses, "addresses");
		for (final String address : addresses) {
			if (remoteAddresses.remove(address)) {
				eventBus.unregisterHandler(address, messageHandler);
			}
		}
	}
	
	private Set<String> getAddressBook() {
		return eventBus.getAddressBook();
	}
}
