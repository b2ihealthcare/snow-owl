/*
 * Copyright 2019-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.client;

import java.io.File;
import java.util.Locale;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.Mode;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.authorization.AuthorizedEventBus;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.identity.AuthorizationHeaderVerifier;
import com.b2international.snowowl.core.identity.Token;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.identity.request.UserRequests;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.netty.EventBusNettyUtil;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * @since 7.2
 */
public final class TransportClient implements IDisposableService {
	
	private static final String TCP_PREFIX = "tcp://";

	private static final Logger LOG = LoggerFactory.getLogger(TransportClient.class);
	
	public static final String NET_4_J_CONNECTOR_NAME = "SnowOwlConnector";
	
	private static final String COULD_NOT_ACTIVATE_PREFIX = "Could not activate TCPClientConnector";
	private static final String ALREADY_LOGGED_IN_PREFIX = "Already logged in";
	private static final String INCORRECT_USER_NAME_OR_PASSWORD = "Incorrect user name or password.";
	private static final String LOGIN_DISABLED = "Logging in for non-administrator users is temporarily disabled.";
	private static final String LDAP_CONNECTION_REFUSED = "Connection refused: connect";

	private final Environment env;
	private final IEventBus bus;
//	private final TransportConfiguration transportConfiguration;
	private final String address;
	
	private Channel channel;
	private String user;
	private String password;
	
	public TransportClient(Environment env, String address) {
		this.env = env;
		this.address = address;
		// override CLIENT/SERVER mode when connecting to a valid address
		env.services().registerService(Mode.class, Strings.isNullOrEmpty(address) ? Mode.SERVER : Mode.CLIENT);
		this.bus = env.service(IEventBus.class);
//		this.transportConfiguration = env.service(SnowOwlConfiguration.class).getModuleConfig(TransportConfiguration.class);
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPassword() {
		return password;
	}
	
	@Override
	public void dispose() {
		final Channel localChannel = channel;
		if (localChannel != null) {
			channel.close().awaitUninterruptibly();
		}
	}
	
	@Override
	public boolean isDisposed() {
		return channel == null || !channel.isActive();
	}

	private synchronized void initConnection() throws SnowowlServiceException {
		if (!isDisposed()) {
			return;
		}
		
		try {
			
			if (!Strings.isNullOrEmpty(address)) {
				final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

				final SnowOwlConfiguration configuration = env.service(SnowOwlConfiguration.class);
				final boolean gzip = configuration.isGzip();
				final ClassLoader compositeClassLoader = env.plugins().getCompositeClassLoader();
				
				final HostAndPort hostAndPort;
				if (address.toLowerCase(Locale.ENGLISH).startsWith(TCP_PREFIX)) {
					hostAndPort = HostAndPort.fromString(address.substring(TCP_PREFIX.length()));
				} else {
					hostAndPort = HostAndPort.fromString(address);
				}
				
				final SslContext sslCtx;
				try {
				
					final RepositoryConfiguration repositoryConfiguration = configuration.getModuleConfig(RepositoryConfiguration.class);
					final String certificateChainPath = repositoryConfiguration.getCertificateChainPath();
					
					if (!StringUtils.isEmpty(certificateChainPath)) {
						
						sslCtx = SslContextBuilder
							.forClient()
							.trustManager(new File(certificateChainPath))
							.build();
						
					} else {
						
						// FIXME: add warning that client will accept any certificate?
						sslCtx = SslContextBuilder
							.forClient()
							.trustManager(InsecureTrustManagerFactory.INSTANCE)
							.build();
					}
				
				} catch (final SSLException e) {
					throw new SnowowlRuntimeException("Failed to create client SSL context.", e);
				}
		        
				channel = new Bootstrap()
					.group(workerGroup)
					.channel(NioSocketChannel.class)
					.handler(EventBusNettyUtil.createChannelHandler(sslCtx, gzip, false, bus, compositeClassLoader))
					.connect(hostAndPort.getHost(), hostAndPort.getPortOrDefault(2036))
					.syncUninterruptibly()
					.channel();
				
				EventBusNettyUtil.awaitAddressBookSynchronized(channel);
			}
			
		} catch (final InterruptedException e) {

			LOG.error("Could not connect to server, please check your settings.", e);
			throw new SnowowlServiceException("Could not connect to server, please check your settings.", e);

		} catch (final IllegalArgumentException e) {

			LOG.error("Invalid repository URL: " + e.getMessage(), e);
			throw new SnowowlServiceException("Invalid repository URL: " + e.getMessage(), e);

		} catch (final Throwable e) {

			LOG.error("Could not connect to server.", e);
			throw new SnowowlServiceException("Could not connect to server.", e);

		}
	}
	
	public User connect(final String username, final String password) throws SnowowlServiceException {
		try {
			this.user = username;
			this.password = password;
			// initialize connectors first
			initConnection();
			
			// try to log in with the specified username and password using the non-authorized bus instance
			final Token token = UserRequests.prepareLogin()
				.setUsername(username)
				.setPassword(password)
				.buildAsync()
				.execute(bus)
				.getSync();
			
			// if successfully logged in replace the event bus with an authorized one
			env.services().registerService(IEventBus.class, new AuthorizedEventBus(bus, ImmutableMap.of("Authorization", token.getToken())));
			env.services().registerService(TransportClient.class, this);
			
			return env.service(AuthorizationHeaderVerifier.class).toUser(token.getToken());
		} catch (UnauthorizedException e) {
			throw new SnowowlServiceException(e.getMessage());
		} catch (final Throwable t) {
			
			final Throwable rootCause = Throwables.getRootCause(t);
			final String message = Strings.nullToEmpty(StringUtils.getLine(rootCause.getMessage(), "\n", 0))
					.replace("\r", "");
			LOG.error("Exception caught while connecting to the server.", t);
			
			// FIXME: "Sentiment analysis" for exception messages (might be outdated)
			if (message.startsWith(COULD_NOT_ACTIVATE_PREFIX)) {
				throw new SnowowlServiceException("The server could not be reached. Please verify the connection URL.");
			} else if (message.startsWith(ALREADY_LOGGED_IN_PREFIX)) {
				throw new SnowowlServiceException("Another client with the same user is already connected to the server.");
			} else if (message.startsWith(INCORRECT_USER_NAME_OR_PASSWORD)) {
				throw new SnowowlServiceException(message);
			} else if (message.startsWith(LOGIN_DISABLED)) {
				throw new SnowowlServiceException(message);
			} else if (message.startsWith(LDAP_CONNECTION_REFUSED)) {
				throw new SnowowlServiceException("The LDAP server could not be reached for authentication. Please contact the administrator.");
			} else {
				throw new SnowowlServiceException("An unexpected error occurred while connecting to the server. Please contact the administrator.");
			}
		}
	}
}
