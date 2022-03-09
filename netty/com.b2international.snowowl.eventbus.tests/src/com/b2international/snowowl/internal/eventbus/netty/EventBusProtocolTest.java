/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.events.SystemNotification;
import com.b2international.snowowl.eventbus.netty.EventBusNettyUtil;
import com.b2international.snowowl.internal.eventbus.EventBus;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * @since 3.1
 */
public class EventBusProtocolTest {

	private static final String ADDRESS = "address";

	private EventBus serverBus;
	private EventBus clientBus;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel clientChannel;
	private Channel serverChannel;

	@Before
	public void setup() throws InterruptedException, SSLException, CertificateException {
		serverBus = (EventBus) EventBusUtil.getBus("server", 4);
		clientBus = (EventBus) EventBusUtil.getBus("client", 4);
		
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup(1);
		
		final ClassLoader classLoader = getClass().getClassLoader();

		final SelfSignedCertificate ssc = new SelfSignedCertificate();
		final SslContext sslServerCtx = SslContextBuilder
			.forServer(ssc.certificate(), ssc.privateKey())
			.build();
		
		final ServerBootstrap serverBootstrap = new ServerBootstrap()
			.group(bossGroup, workerGroup)
//			.handler(new LoggingHandler(LogLevel.INFO))
			.channel(NioServerSocketChannel.class)
			.childHandler(EventBusNettyUtil.createChannelHandler(sslServerCtx, false, true, serverBus, classLoader))
			.childOption(ChannelOption.SO_KEEPALIVE, true);
		
		serverChannel = serverBootstrap.bind(new InetSocketAddress("127.0.0.1", 10001))
			.sync()
			.channel();

		final SslContext sslClientCtx = SslContextBuilder.forClient()
			.trustManager(ssc.certificate())
			.build();

		final Bootstrap clientBootstrap = new Bootstrap()
			.group(workerGroup)
			.channel(NioSocketChannel.class)
			.handler(EventBusNettyUtil.createChannelHandler(sslClientCtx, false, false, clientBus, classLoader));
		
		clientChannel = clientBootstrap.connect(new InetSocketAddress("127.0.0.1", 10001))
			.sync()
			.channel();
	}
	
	@After
	public void teardown() throws InterruptedException {
		clientChannel.close().sync();
		serverChannel.close().sync();
		
		workerGroup.shutdownGracefully().sync();
		bossGroup.shutdownGracefully().sync();
		
		clientBus.deactivate();
		serverBus.deactivate();
	}
	
	@Test
	public void testRemoteSend() throws InterruptedException {
		serverBus.registerHandler(SystemNotification.ADDRESS, message -> {
			System.out.println("Server got: " + message.body());
		});
		
		serverBus.registerHandler(ADDRESS, message -> {
			final String ping = message.body(String.class);
			System.out.println("Server got: " + ping);
			message.reply(ping + " world");
		});

		assertTrue("Address book could not be synchronized.", EventBusNettyUtil.awaitAddressBookSynchronized(clientChannel));

		final CountDownLatch latch = new CountDownLatch(1);

		clientBus.send(ADDRESS, "hello", Map.of(), message -> { 
			System.out.println("Client got: " + message.body(String.class));
			latch.countDown();
		});
		
		assertTrue("Response not received from server bus within 1 second.", latch.await(3L, TimeUnit.SECONDS));
	}
}
