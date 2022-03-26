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
package com.b2international.snowowl.eventbus.netty;

import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.internal.eventbus.netty.AddressBookNettyHandler;
import com.b2international.snowowl.internal.eventbus.netty.EventBusNettyHandler;

import io.netty.channel.*;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @since 8.1.0
 */
public final class EventBusNettyUtil {

	public static final String HEADER_CLIENT_ID = "clientId";
	
	public static final int MAX_OBJECT_SIZE = 16_777_216; // 16 MiB

	private static final int READ_TIMEOUT_SECONDS = 30;

	private static final int WRITE_TIMEOUT_SECONDS = 15;

	public static boolean awaitAddressBookSynchronized(Channel channel) throws InterruptedException {
		final AddressBookNettyHandler addressBookHandler = channel.pipeline().get(AddressBookNettyHandler.class);
		if (addressBookHandler == null) {
			return false;
		}
		
		return addressBookHandler.awaitAddressBookSynchronized(channel, 3L, TimeUnit.SECONDS);
	}
	
	public static ChannelHandler createChannelHandler(SslContext sslCtx, boolean gzip, boolean sendInitialSync, IEventBus eventBus, ClassLoader classLoader) {
		return createChannelHandler(sslCtx, gzip, sendInitialSync, WRITE_TIMEOUT_SECONDS, READ_TIMEOUT_SECONDS, eventBus, classLoader);
	}
	
	/**
	 * @param sslCtx 
	 * @param gzip
	 * @param sendInitialSync
	 * @param watchdogTimeout 
	 * @param watchdogRate 
	 * @param eventBus
	 * @param classLoader
	 * @return
	 */
	public static ChannelHandler createChannelHandler(
		SslContext sslCtx, 
		boolean gzip, 
		boolean sendInitialSync, 
		int watchdogRate, 
		int watchdogTimeout, 
		IEventBus eventBus, 
		ClassLoader classLoader) {
		
		return new ChannelInitializer<Channel>() {
			@Override
			public void initChannel(final Channel channel) throws Exception {
				channel.config().setOption(ChannelOption.ALLOW_HALF_CLOSURE, false);
				
				final ChannelPipeline pipeline = channel.pipeline();
//				pipeline.addLast(new LoggingHandler(LogLevel.INFO));
				
				// SSL and compression are mutually exclusive
				if (sslCtx != null) {
					pipeline.addLast(sslCtx.newHandler(channel.alloc()));
				} else if (gzip) {
					pipeline.addLast(new JdkZlibEncoder(), new JdkZlibDecoder());
				}
				
				pipeline.addLast(new ObjectEncoder(), new ObjectDecoder(MAX_OBJECT_SIZE, ClassResolvers.cacheDisabled(classLoader)));

				// Sends user events to handlers added later in the pipeline when there is no read/write activity
				pipeline.addLast(new IdleStateHandler(watchdogTimeout, watchdogRate, 0));

				final IEventBusNettyHandler messageHandler = EventBusNettyUtil.createMessageHandler(eventBus);
				final IEventBusNettyHandler addressBookHandler = EventBusNettyUtil.createAddressBookHandler(sendInitialSync, eventBus, messageHandler);
				pipeline.addLast(addressBookHandler, messageHandler);
			}
		};
	}

	/**
	 * Returns a handler for address book synchronization. 
	 * 
	 * @param sendInitialSync
	 * @param eventBus
	 * @param messageHandler
	 * @return
	 */
	public static IEventBusNettyHandler createAddressBookHandler(boolean sendInitialSync, IEventBus eventBus, IHandler<IMessage> messageHandler) {
		return new AddressBookNettyHandler(sendInitialSync, eventBus, messageHandler);
	}
	
	/**
	 * Returns the general message handler for the event bus protocol. 
	 * 
	 * @param eventBus
	 * @return
	 */
	public static IEventBusNettyHandler createMessageHandler(IEventBus eventBus) {
		return new EventBusNettyHandler(eventBus);
	}
}
