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
package com.b2international.snowowl.internal.eventbus.netty;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.internal.eventbus.EventBus;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @since 3.1
 */
public class EventBusProtocolTest {

	private static final String ADDRESS = "address";

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private EventBus serverBus;
	private EventBus clientBus;
	private Channel clientChannel;
	private Channel serverChannel;

	@Before
	public void setup() throws InterruptedException {
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();

		serverBus = (EventBus) EventBusUtil.getBus();

		ServerBootstrap b = new ServerBootstrap()
			.group(bossGroup, workerGroup)
			.handler(new LoggingHandler(LogLevel.INFO))
			.channel(LocalServerChannel.class)
			.childHandler(new ChannelInitializer<LocalChannel>() {
				@Override
				public void initChannel(LocalChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast(
						new ObjectEncoder(),
						new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
						new EventBusNettyHandler(false, serverBus));
				}
			});
		
		serverChannel = b.bind(new LocalAddress("eb"))
			.sync()
			.channel();

		clientBus = (EventBus) EventBusUtil.getBus("client", 4);

		Bootstrap cb = new Bootstrap()
			.group(workerGroup)
			.channel(LocalChannel.class)
			.handler(new ChannelInitializer<LocalChannel>() {
				@Override
				public void initChannel(LocalChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast(
						new ObjectEncoder(),
						new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
						new EventBusNettyHandler(true, clientBus));
				}
			});
		
		clientChannel = cb.connect(new LocalAddress("eb"))
			.sync()
			.channel();
	}
	
	@After
	public void teardown() throws InterruptedException {
		clientChannel.close().sync();
		clientBus.deactivate();
		
		serverChannel.close().sync();
		serverBus.deactivate();
		
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
	
	@Test
	public void testRemoteSend() throws InterruptedException {
		serverBus.registerHandler(ADDRESS, message -> System.out.println(message.body(String.class)));
		Thread.sleep(100L);
		serverBus.send(ADDRESS, "server", Map.of());
		clientBus.send(ADDRESS, "client", Map.of());
		Thread.sleep(100L);
	}
}
