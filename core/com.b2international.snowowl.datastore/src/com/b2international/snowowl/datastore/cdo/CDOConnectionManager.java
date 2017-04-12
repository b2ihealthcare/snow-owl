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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.RepositoryNotFoundException;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.GZIPStreamWrapper;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.spi.net4j.ClientProtocolFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.core.users.User;
import com.b2international.snowowl.datastore.Authenticator;
import com.b2international.snowowl.datastore.ClientProtocolFactoryRegistry;
import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.connection.RepositoryConnectionConfiguration;
import com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.net4j.Net4jUtils;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.eventbus.net4j.EventBusNet4jUtil;
import com.b2international.snowowl.eventbus.net4j.IEventBusProtocol;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Abstract implementation of the {@link ICDOConnectionManager}.
 * 
 */
/*default*/ class CDOConnectionManager extends CDOContainer<ICDOConnection> implements ICDOConnectionManager {

	private final String username;
	private final transient String password;

	@Nullable private User user;
	@Nullable private Map<String, CDONet4jSessionConfiguration> repositoryNameToConfiguration;
	@Nullable private AtomicBoolean embedded;
	@Nullable private Authenticator authenticator;
	@Nullable private IConnector connector;

	static CDOConnectionManager create(final User user) {
		return new CDOConnectionManager(user.getUserName(), user.getPassword(), user);
	}

	static CDOConnectionManager create(final String username, final String password) {
		return new CDOConnectionManager(username, password, /*intentionally null. authentication is required.*/ null);
	}
	
	CDOConnectionManager(final String username, final String password, @Nullable final User user) {
		this.username = Preconditions.checkNotNull(username, "Username argument cannot be null.");
		this.password = Preconditions.checkNotNull(password, "Password argument cannot be null.");
		this.user = user;
		repositoryNameToConfiguration = Maps.newHashMap();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#getUser()
	 */
	@Override
	public User getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#getUserId()
	 */
	@Override
	public String getUserId() {
		return getUser().getUserName();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#isEmbedded()
	 */
	@Override
	public boolean isEmbedded() {
		return embedded.get();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#get(org.eclipse.emf.cdo.common.branch.CDOBranch)
	 */
	@Override
	public ICDOConnection get(final CDOBranch branch) {

		Preconditions.checkNotNull(branch, "CDO branch argument cannot be null.");
		final BranchLoader loader = ((InternalCDOBranchManager) branch.getBranchManager()).getBranchLoader();

		String uuid = null;

		if (loader instanceof CDONet4jSession) {

			uuid = ((CDONet4jSession) loader).getRepositoryInfo().getUUID();

		} else if (loader instanceof IRepository) {

			uuid = ((IRepository) loader).getUUID();

		} else if (loader instanceof SignalProtocol<?>) {

			final Object infraStructure = ((SignalProtocol<?>) loader).getInfraStructure();

			if (infraStructure instanceof CDONet4jSession) {

				uuid = ((CDONet4jSession) infraStructure).getRepositoryInfo().getUUID();

			}

		}

		Preconditions.checkState(!StringUtils.isEmpty(uuid), "Unrecognized CDO branch loader. [Branch loader: " + loader + "]");

		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getByUuid(uuid);

	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#get(org.eclipse.emf.cdo.common.branch.CDOBranchPoint)
	 */
	@Override
	public ICDOConnection get(final CDOBranchPoint branchPoint) {

		Preconditions.checkNotNull(branchPoint, "CDO branch point argument cannot be null.");
		return get(branchPoint.getBranch());

	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#get(com.b2international.snowowl.core.api.IBranchPoint)
	 */
	@Override
	public ICDOConnection get(final IBranchPoint branchPoint) {

		Preconditions.checkNotNull(branchPoint, "Branch point argument cannot be null.");
		return getByUuid(Preconditions.checkNotNull(branchPoint.getUuid()));

	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#get(com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy)
	 */
	@Override
	public ICDOConnection get(final IBranchPointCalculationStrategy strategy) {

		Preconditions.checkNotNull(strategy, "Branch point calculation strategy argument cannot be null.");
		return get(strategy.getSourceBranchPoint());

	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#get(org.eclipse.emf.cdo.common.revision.CDORevision)
	 */
	@Override
	public ICDOConnection get(final CDORevision revision) {

		Preconditions.checkNotNull(revision, "CDO revision argument cannot be null.");
		return get(revision.getBranch());

	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#get(org.eclipse.emf.cdo.view.CDOView)
	 */
	@Override
	public ICDOConnection get(final CDOView view) {
		return get(CDOUtils.check(view).getBranch());
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#openProtocol(org.eclipse.net4j.signal.ISignalProtocol)
	 */
	@Override
	public void openProtocol(final ISignalProtocol<?> protocol) {
		Preconditions.checkNotNull(protocol, "Signal protocol argument cannot be null.");
		// Open with the default signal timeout, then update it, as the opposite does not seem to work for JVMConnectors. 
		protocol.open(connector);
		protocol.setTimeout(getRepositoryConnectionConfiguration().getSignalTimeout());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#getAuthenticator()
	 */
	@Override
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	/**
	 * (non-API)
	 * <br>
	 * Returns with the session configuration for the connection.
	 * @param connection the connection instance.
	 * @return the CDO session configuration.
	 */
	public synchronized CDONet4jSessionConfiguration getSessionConfiguration(final ICDOConnection connection) {

		Preconditions.checkNotNull(connection, "Connection argument cannot be null.");

		return repositoryNameToConfiguration.get(connection.getUuid());

	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#getConnector()
	 */
	@Override
	public IConnector getConnector() {
		return connector;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#subscribeForRemoteMessages(org.eclipse.net4j.util.event.IListener)
	 */
	@Override
	public void subscribeForRemoteMessages(final IListener listener) {

		Preconditions.checkNotNull(listener, "Listener argument cannot be null.");

		for (final Iterator<ICDOConnection> itr = iterator(); itr.hasNext(); /* */) {

			final ICDOConnection connection = itr.next();
			final CDORemoteSessionManager sessionManager = connection.getSession().getRemoteSessionManager();
			sessionManager.addListener(listener);
			sessionManager.setForceSubscription(true);

		}

	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOConnectionManager#unsbscribeFromRemoteMessages(org.eclipse.net4j.util.event.IListener)
	 */
	@Override
	public void unsbscribeFromRemoteMessages(final IListener listener) {

		Preconditions.checkNotNull(listener, "Listener argument cannot be null.");

		for (final Iterator<ICDOConnection> itr = iterator(); itr.hasNext(); /* */) {

			final ICDOConnection connection = itr.next();
			final CDORemoteSessionManager sessionManager = connection.getSession().getRemoteSessionManager();
			sessionManager.removeListener(listener);

		}

	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOContainer#doBeforeActivate()
	 */
	@Override
	protected void doBeforeActivate() throws Exception {

		//read extensions first
		super.doBeforeActivate();

		try {			
			
			final ClientPreferences clientConfiguration = getClientConfiguration();
			final RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
			final RepositoryConnectionConfiguration connectionConfiguration = getRepositoryConnectionConfiguration();
			final boolean embedded = clientConfiguration.isClientEmbedded() || SpecialUserStore.SYSTEM_USER.equals(user);
			
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

			for (final Iterator<ICDOConnection> itr = _iterator(); itr.hasNext(); /**/) {

				final ICDOConnection connection = itr.next();
				final String repositoryUuid = connection.getUuid();

				final CDONet4jSessionConfiguration sessionConfiguration = CDONet4jUtil.createNet4jSessionConfiguration();
				
				// Set initial signal timeout to the connection timeout, as we want to be notified in a reasonable time
				sessionConfiguration.setSignalTimeout(connectionConfiguration.getConnectionTimeout());
				sessionConfiguration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
				sessionConfiguration.getAuthenticator().setCredentialsProvider(credentials);
				sessionConfiguration.setRepositoryName(repositoryUuid);
				sessionConfiguration.setConnector(connector);

				if (getSnowOwlConfiguration().isGzip()) {
					sessionConfiguration.setStreamWrapper(new GZIPStreamWrapper());
				}

				repositoryNameToConfiguration.put(repositoryUuid, sessionConfiguration);

			}

		} catch (final ConnectorException e) {

			LOGGER.error("Could not connect to server, please check your settings.", e);
			throw new SnowowlServiceException("Could not connect to server, please check your settings.", e);

		} catch (final RepositoryNotFoundException e) {

			LOGGER.error("Repository doesn't exist: " + e.getRepositoryName(), e);
			throw new SnowowlServiceException("Repository doesn't exist: " + e.getRepositoryName(), e);

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

	private RepositoryConfiguration getRepositoryConfiguration() {
		return getSnowOwlConfiguration().getModuleConfig(RepositoryConfiguration.class);
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

		if (null != repositoryNameToConfiguration) {

			repositoryNameToConfiguration.clear();
			repositoryNameToConfiguration = null;

		}

		LifecycleUtil.deactivate(connector);

		authenticator = null;
		embedded = null;
		connector = null;
		user = null;

	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOManager#getUuid(org.eclipse.net4j.util.lifecycle.ILifecycle)
	 */
	@Override
	protected String getUuid(final ICDOConnection managedItem) {
		return managedItem.getUuid();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOContainer#createItem(java.lang.String, java.lang.String, byte, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	protected ICDOConnection createItem(final String repositoryUuid, @Nullable final String repositoryName, final byte namespaceId, 
			@Nullable final String toolingId, @Nullable final String dependsOnRepositoryUuid, final boolean meta) {
		return new CDOConnection(repositoryUuid, repositoryName, namespaceId, toolingId, dependsOnRepositoryUuid, meta);
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