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

import java.util.concurrent.CountDownLatch;
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
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @since 8.1.0
 */
public final class EventBusNettyUtil {

	public static boolean awaitAddressBookSynchronized(final Channel channel) throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final SimpleUserEventChannelHandler<AttributeKey<?>> handler = new SimpleUserEventChannelHandler<AttributeKey<?>>() {
			@Override
			protected void eventReceived(final ChannelHandlerContext ctx, final AttributeKey<?> key) throws Exception {
				if (IEventBusNettyHandler.KEY_ADDRESS_BOOK_SYNCHRONIZED == key) {
					latch.countDown();
				}
			}
		};
		
		try {
			
			channel.pipeline().addLast(handler);
		
			final Attribute<Boolean> attribute = channel.attr(IEventBusNettyHandler.KEY_ADDRESS_BOOK_SYNCHRONIZED);
			if (attribute != null && Boolean.TRUE.equals(attribute.get())) {
				latch.countDown();
			}
			
			return latch.await(3L, TimeUnit.SECONDS);
			
		} finally {
			channel.pipeline().remove(handler);
		}
	}
	
	/**
	 * @param gzip
	 * @param sendInitialSync
	 * @param eventBus
	 * @param classLoader
	 * @return
	 */
	public static ChannelHandler createChannelHandler(boolean gzip, boolean sendInitialSync, IEventBus eventBus, ClassLoader classLoader) {
		return new ChannelInitializer<Channel>() {
			@Override
			public void initChannel(final Channel channel) throws Exception {
				final IEventBusNettyHandler messageHandler = EventBusNettyUtil.createMessageHandler(eventBus);
				final IEventBusNettyHandler addressBookHandler = EventBusNettyUtil.createAddressBookHandler(sendInitialSync, eventBus, messageHandler);

				final ChannelPipeline pipeline = channel.pipeline();
				if (gzip) {
					pipeline.addLast(new JdkZlibEncoder(), new JdkZlibDecoder());
				}
				
				pipeline.addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(classLoader)));
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
