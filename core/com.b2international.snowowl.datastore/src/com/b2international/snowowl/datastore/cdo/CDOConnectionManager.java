/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.cdo;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.spi.net4j.ClientProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.datastore.ClientProtocolFactoryRegistry;
import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.connection.RepositoryConnectionConfiguration;
import com.b2international.snowowl.datastore.net4j.Net4jUtils;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.eventbus.net4j.EventBusNet4jUtil;
import com.b2international.snowowl.eventbus.net4j.IEventBusProtocol;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;
import com.google.common.base.Preconditions;

/**
 * Abstract implementation of the {@link ICDOConnectionManager}.
 * 
 */
/*default*/ class CDOConnectionManager extends Lifecycle {

	private static final Logger LOGGER = LoggerFactory.getLogger(CDOConnectionManager.class);
	
	private final String username;
	private final transient String password;

	@Nullable private User user;
	@Nullable private AtomicBoolean embedded;
	@Nullable private Authenticator authenticator;
	@Nullable private IConnector connector;

	CDOConnectionManager(final String username, final String password) {
		this.username = Preconditions.checkNotNull(username, "Username argument cannot be null.");
		this.password = Preconditions.checkNotNull(password, "Password argument cannot be null.");
		this.user = User.SYSTEM.getUsername().equals(username) ? User.SYSTEM : null;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#getUser()
	 */
	public User getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#getUserId()
	 */
	public String getUserId() {
		return getUser().getUsername();
	}
	
	public String getPassword() {
		return password;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#isEmbedded()
	 */
	public boolean isEmbedded() {
		return embedded.get();
	}

	void openProtocol(final ISignalProtocol<?> protocol) {
		Preconditions.checkNotNull(protocol, "Signal protocol argument cannot be null.");
		// Open with the default signal timeout, then update it, as the opposite does not seem to work for JVMConnectors. 
		protocol.open(connector);
		protocol.setTimeout(getRepositoryConnectionConfiguration().getSignalTimeout());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#getAuthenticator()
	 */
	Authenticator getAuthenticator() {
		return authenticator;
	}

	public IConnector getConnector() {
		return connector;
	}

	@Override
	protected void doBeforeActivate() throws Exception {

		//read extensions first
		super.doBeforeActivate();

		try {			
			
			final ClientPreferences clientConfiguration = getClientConfiguration();
			final RepositoryConnectionConfiguration connectionConfiguration = getRepositoryConnectionConfiguration();
			final boolean embedded = clientConfiguration.isClientEmbedded() || User.isSystem(username);
			
			final PasswordCredentialsProvider credentials = new PasswordCredentialsProvider(new PasswordCredentials(username, password.toCharArray()));

			setEmbedded(embedded);

			if (isEmbedded()) {
				connector = JVMUtil.getConnector(IPluginContainer.INSTANCE, Net4jUtils.NET_4_J_CONNECTOR_NAME);
			} else {
				TCPUtil.prepareContainer(IPluginContainer.INSTANCE);
				Net4jUtil.prepareContainer(IPluginContainer.INSTANCE);
				connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, clientConfiguration.getCDOUrl());
				connector.waitForConnection(connectionConfiguration.getConnectionTimeout());
				
				final HeartBeatProtocol watchdog = new HeartBeatProtocol(connector);
				watchdog.start(connectionConfiguration.getWatchdogRate(), connectionConfiguration.getWatchdogTimeout());
			}

		} catch (final ConnectorException e) {

			LOGGER.error("Could not connect to server, please check your settings.", e);
			throw new SnowowlServiceException("Could not connect to server, please check your settings.", e);

		} catch (final IllegalArgumentException e) {

			LOGGER.error("Invalid repository URL: " + e.getMessage(), e);
			throw new SnowowlServiceException("Invalid repository URL: " + e.getMessage(), e);

		} catch (final LifecycleException e) {

			LOGGER.error("Could not connect to server: " + e.getMessage(), e);
			throw new SnowowlServiceException("Could not connect to server: " + e.getMessage(), e);

		} catch (final Throwable e) {

			LOGGER.error("Could not connect to server.", e);
			throw new SnowowlServiceException("Could not connect to server.", e);

		}

	}

	private RepositoryConnectionConfiguration getRepositoryConnectionConfiguration() {
		return getSnowOwlConfiguration().getModuleConfig(RepositoryConnectionConfiguration.class);
	}

	private SnowOwlConfiguration getSnowOwlConfiguration() {
		return ApplicationContext.getInstance().getServiceChecked(SnowOwlConfiguration.class);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOContainer#doActivate()
	 */
	@Override
	protected void doActivate() throws Exception {

		//first perform the authentication before activating the managed items, but it required protocol activation
		openCustomProtocols();

		if (null == user) {

			try {

				final IApplicationSessionManager sessionManagerProxy = RpcUtil.getRpcClientProxy(IApplicationSessionManager.class);
				authenticator = new Authenticator(sessionManagerProxy, username, password);
				user = authenticator.authenticate();

			} catch (final SnowowlServiceException e) {
				throw new RuntimeException("Error while authenticating user.", e);
			}
		}

		super.doActivate();
	}

	@Override
	protected void doDeactivate() throws Exception {

		super.doDeactivate();

		LifecycleUtil.deactivate(connector);

		authenticator = null;
		embedded = null;
		connector = null;
		user = null;

	}

	/*sets the embedded flag if necessary*/
	private void setEmbedded(final boolean embedded) {

		if (null == this.embedded) {

			synchronized (CDOConnectionManager.class) {

				if (null == this.embedded) {

					this.embedded = new AtomicBoolean(embedded);

				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	private void openCustomProtocols() {
		// other client protocols
		final List<ClientProtocolFactory> protocolFactories = ClientProtocolFactoryRegistry.getInstance().getRegisteredClientProtocolFactories();
		for (final ClientProtocolFactory clientProtocolFactory : protocolFactories) {
			final SignalProtocol<Object> protocol = (SignalProtocol<Object>) clientProtocolFactory.create("");
			openProtocol(protocol);
		}

		// also set up the RPC client...
		final RpcProtocol rpcProtocol = RpcUtil.getRpcClientProtocol(IPluginContainer.INSTANCE);
		openProtocol(rpcProtocol);

		// ...and the application session manager...
		rpcProtocol.getInfraStructure().registerClassLoader(IApplicationSessionManager.class, DatastoreActivator.class.getClassLoader());

		if (!isEmbedded()) {
			// ...the event bus, too.
			final IEventBusProtocol eventBusProtocol = EventBusNet4jUtil.getClientProtocol(IPluginContainer.INSTANCE);
			openProtocol(eventBusProtocol);
		}
	}

	private ClientPreferences getClientConfiguration() {
		return ApplicationContext.getInstance().getService(ClientPreferences.class);
	}
}