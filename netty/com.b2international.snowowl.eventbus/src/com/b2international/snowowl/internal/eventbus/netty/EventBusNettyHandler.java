/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.netty.IEventBusNettyHandler;
import com.b2international.snowowl.internal.eventbus.MessageFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * A message handler bridge implementation for a Netty channel and a local event
 * bus. It exhibits the following behavior:
 * <ul>
 * <li>Messages coming from a remote peer over a channel are submitted to the
 * local event bus via {@link IEventBus#receive(IMessage)};
 * 
 * <li>Messages received from the local event bus are written to the channel in
 * a serialization-friendly format.
 * </ul>
 * 
 * @since 8.1.0
 */
public class EventBusNettyHandler extends SimpleChannelInboundHandler<IMessage> implements IEventBusNettyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(EventBusNettyHandler.class);
	
	private final IEventBus eventBus;

	// Handler is stateful, ie. a separate instance is maintained for each connection
	private volatile ChannelHandlerContext ctx;

	public EventBusNettyHandler(final IEventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		super.channelActive(ctx);
	}
	
	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		this.ctx = null;
		super.channelInactive(ctx);
	}
	
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final IMessage message) throws Exception {
		final String remoteReplyAddress = message.replyAddress();
		final IHandler<IMessage> replyHandler;
		
		if (remoteReplyAddress == null) {
			replyHandler = null;
		} else {
			// Send reply back to the remote using a local reply handler
			replyHandler = reply -> handle(MessageFactory.createMessage(remoteReplyAddress, reply.body(), reply.tag(), reply.headers()));
		}
		
		eventBus.receive(message, replyHandler);
	}
	
	private void channelWrite(final ChannelHandlerContext ctx, final IMessage message) {
		try {
			ctx.writeAndFlush(MessageFactory.writeMessage(message))
				.addListener(f -> { if (!f.isSuccess()) {
					LOG.error("Exception happened when sending message", f.cause());
				}});
		} catch (final IOException e) {
			LOG.error("Exception happened trying to send message", e);
		}
	}
	
	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		LOG.error("Exception caught for channel:", cause);
	}

	@Override
	public void handle(final IMessage message) {
		final ChannelHandlerContext localCtx = ctx;
		if (localCtx != null) {
			channelWrite(localCtx, message);
		}
	}
}
