/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.spi.net4j.ClientProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.authorization.AuthorizedEventBus;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.net4j.EventBusNet4jUtil;
import com.b2international.snowowl.eventbus.net4j.IEventBusProtocol;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.Token;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.request.UserRequests;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

/**
 * @since 
 */
public final class TransportClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(TransportClient.class);
	
	public static final String NET_4_J_CONNECTOR_NAME = "SnowOwlConnector";
	
	private static final String COULD_NOT_ACTIVATE_PREFIX = "Could not activate TCPClientConnector";
	private static final String ALREADY_LOGGED_IN_PREFIX = "Already logged in";
	private static final String INCORRECT_USER_NAME_OR_PASSWORD = "Incorrect user name or password.";
	private static final String LOGIN_DISABLED = "Logging in for non-administrator users is temporarily disabled.";
	private static final String LDAP_CONNECTION_REFUSED = "Connection refused: connect";

	private final Environment env;
	private final IEventBus bus;
	private final ClientPreferences preferences;
	private final TransportConfiguration transportConfiguration;
	
	private IConnector connector;
	private HostAndPort address;
	private AtomicBoolean embedded;
	
	public TransportClient(Environment env) {
		this.env = env;
		this.bus = env.service(IEventBus.class);
		this.preferences = env.service(ClientPreferences.class);
		this.transportConfiguration = env.service(SnowOwlConfiguration.class).getModuleConfig(TransportConfiguration.class);
	}
	
	public HostAndPort getAddress() {
		return address;
	}
	
	public boolean isEmbedded() {
		return embedded.get();
	}

	private synchronized void initConnection() throws SnowowlServiceException {
		if (connector != null) {
			return;
		}
		
		try {
			
			if (embedded.get()) {
				connector = JVMUtil.getConnector(IPluginContainer.INSTANCE, NET_4_J_CONNECTOR_NAME);
			} else {
				TCPUtil.prepareContainer(IPluginContainer.INSTANCE);
				Net4jUtil.prepareContainer(IPluginContainer.INSTANCE);
				connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, "tcp://" + getAddress());
				connector.waitForConnection(transportConfiguration.getConnectionTimeout());
				
				final HeartBeatProtocol watchdog = new HeartBeatProtocol(connector);
				watchdog.start(transportConfiguration.getWatchdogRate(), transportConfiguration.getWatchdogTimeout());
			}
			
			openCustomProtocols();

		} catch (final ConnectorException e) {

			LOG.error("Could not connect to server, please check your settings.", e);
			throw new SnowowlServiceException("Could not connect to server, please check your settings.", e);

		} catch (final IllegalArgumentException e) {

			LOG.error("Invalid repository URL: " + e.getMessage(), e);
			throw new SnowowlServiceException("Invalid repository URL: " + e.getMessage(), e);

		} catch (final LifecycleException e) {

			LOG.error("Could not connect to server: " + e.getMessage(), e);
			throw new SnowowlServiceException("Could not connect to server: " + e.getMessage(), e);

		} catch (final Throwable e) {

			LOG.error("Could not connect to server.", e);
			throw new SnowowlServiceException("Could not connect to server.", e);

		}
	}
	
	public void connect(final String username, final String password) throws SnowowlServiceException {
		try {
			// initialize connectors first
			embedded = new AtomicBoolean();
			embedded.set(preferences.isClientEmbedded() || User.isSystem(username));
			initConnection();
			
			// try to log in with the specified username and password using the non-authorized bus instance
			final Token token = UserRequests.prepareLogin()
				.setUsername(username)
				.setPassword(password)
				.buildAsync()
				.execute(bus)
				.getSync();
			
			// register user in app context
			final User user = IdentityProvider.authJWT(token.getToken());
			env.services().registerService(User.class, user);
			
			// if successfully logged in replace the event bus with an authorized one
			env.services().registerService(IEventBus.class, new AuthorizedEventBus(bus, ImmutableMap.of("Authorization", token.getToken())));
		} catch (UnauthorizedException e) {
			throw new SnowowlServiceException(e.getMessage());
		} catch (final Throwable t) {
			
			final Throwable rootCause = Throwables.getRootCause(t);
			final String message = Strings.nullToEmpty(StringUtils.getLine(rootCause.getMessage(), "\n", 0))
					.replace("\r", "");
			LOG.error("Exception caught while connecting to the server.", t);
			
			// FIXME: "Sentiment analysis" for exception messages
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
	
	@SuppressWarnings("unchecked")
	private void openCustomProtocols() {
		// other client protocols
		final List<ClientProtocolFactory> protocolFactories = getRegisteredClientProtocolFactories();
		for (final ClientProtocolFactory clientProtocolFactory : protocolFactories) {
			final SignalProtocol<Object> protocol = (SignalProtocol<Object>) clientProtocolFactory.create("");
			openProtocol(protocol);
		}

		// also set up the RPC client...
		final RpcProtocol rpcProtocol = RpcUtil.getRpcClientProtocol(IPluginContainer.INSTANCE);
		openProtocol(rpcProtocol);

		if (!isEmbedded()) {
			// ...the event bus, too.
			final IEventBusProtocol eventBusProtocol = EventBusNet4jUtil.getClientProtocol(IPluginContainer.INSTANCE);
			openProtocol(eventBusProtocol);
		}
	}

	private static final String EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.protocolFactory";
	private static final String CLASS_ATTRIBUTE = "class";
	
	private List<ClientProtocolFactory> getRegisteredClientProtocolFactories() {
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		return Lists.transform(Arrays.asList(configurationElements), (input) -> {
			try {
				return (ClientProtocolFactory) input.createExecutableExtension(CLASS_ATTRIBUTE);
			} catch (final CoreException e) {
				throw new RuntimeException("Error while creating executable extension from the passed in configuration element: " + input, e);
			}
		});
	}

	void openProtocol(final ISignalProtocol<?> protocol) {
		Preconditions.checkNotNull(protocol, "Signal protocol argument cannot be null.");
		// Open with the default signal timeout, then update it, as the opposite does not seem to work for JVMConnectors. 
		protocol.open(connector);
		protocol.setTimeout(transportConfiguration.getSignalTimeout());
	}

}
