/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.IRepository.Handler;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage.Priority;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.spi.net4j.ServerProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.collections.Procedure;
import com.b2international.commons.emf.NsUriProvider;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.cdo.CDOContainer;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.net4j.Net4jUtils;
import com.b2international.snowowl.datastore.net4j.TcpGZIPStreamWrapperInjector;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.common.net.HostAndPort;

/**
 * Provides access to the underlying CDO repositories and manages their life cycle.
 * 
 */
/*default*/ class CDORepositoryManager extends CDOContainer<ICDORepository> implements ICDORepositoryManager {

	private static final String ADMINISTRATOR_MESSAGE = "Administrator message";

	private static final Logger LOGGER = LoggerFactory.getLogger(CDORepositoryManager.class);
	
	private static final Procedure<ICDORepository> CLEAR_REVISION_CACHE = new Procedure<ICDORepository>() {
		@Override public void doApply(final ICDORepository repository) {
			((InternalCDORevisionManager) repository.getRepository().getRevisionManager()).getCache().clear();
		}
	};
	
	private static final Procedure<Collection<CDORemoteSession>> DISCONNECT_SESSIONS = new Procedure<Collection<CDORemoteSession>>() {
		@Override protected void doApply(final Collection<CDORemoteSession> sessions) {
			
			final AtomicBoolean messageSent = new AtomicBoolean(false);
			
			for (final CDORemoteSession session : sessions) {
			
				try {
		
					final CDORemoteSessionManager remoteSessionManager = session.getManager();
					final CDORepositoryInfo repositoryInfo = remoteSessionManager.getLocalSession().getRepositoryInfo();
					final String uuid = repositoryInfo.getUUID();
					
					final ICDORepository repository = ApplicationContext.getInstance().getService(ICDORepositoryManager.class).getByUuid(uuid);
	
					final InternalSessionManager localSessionManager = (InternalSessionManager) repository.getRepository().getSessionManager();
					
					final InternalSession localSession = (InternalSession) localSessionManager.getSession(session.getSessionID());
					
					//send message only once although we have multiple connections
					if (messageSent.compareAndSet(false, true)) {
						session.sendMessage(createMessage("Remote disconnect."));
					}
					
					localSessionManager.sessionClosed(localSession);
					localSession.close();
					
				} catch (final Throwable t) {
					
					LOGGER.error("Error while remotely disconnecting '" + session.getUserID() + "'.");
					
				}			
			}
		}
	};

	private static CDORepositoryManager instance;
	
	/**Mappings between the repository UUIDs and the {@link NsUriProvider} instances.*/
	private Map<String, NsUriProvider> uuidToNsUriProviders;

	@Override
	public void clearRevisionCache() {
		Collections3.forEach(this, CLEAR_REVISION_CACHE);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDORepositoryManager#disconnect(java.lang.Iterable, com.b2international.snowowl.datastore.server.ICDORepositoryManager.ISessionOperationCallback[])
	 */
	@Override
	public void disconnect(final Iterable<String> userIds, final ISessionOperationCallback... callbacks) {
		Preconditions.checkNotNull(userIds, "User ID iterable argument cannot be null.");
		applyDisconnect(Predicates.in(Sets.newHashSet(userIds)), callbacks);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDORepositoryManager#disconnectAll(com.b2international.snowowl.datastore.server.ICDORepositoryManager.ISessionOperationCallback[])
	 */
	@Override
	public void disconnectAll(final ISessionOperationCallback... callbacks) {
		applyDisconnect(Predicates.<String>alwaysTrue(), callbacks);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDORepositoryManager#sendMessageTo(java.lang.String, java.lang.Iterable, com.b2international.snowowl.datastore.server.ICDORepositoryManager.ISessionOperationCallback[])
	 */
	@Override
	public void sendMessageTo(final String message, final Iterable<String> userIds, final ISessionOperationCallback... callbacks) {
		Preconditions.checkNotNull(userIds, "User ID iterable argument cannot be null.");
		sendMessage(Predicates.in(Sets.newHashSet(userIds)), message, callbacks);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDORepositoryManager#sendMessageToAll(java.lang.String, com.b2international.snowowl.datastore.server.ICDORepositoryManager.ISessionOperationCallback[])
	 */
	@Override
	public void sendMessageToAll(final String message, final ISessionOperationCallback... callbacks) {
		sendMessage(Predicates.<String>alwaysTrue(), message, callbacks);
	}
	
	@Override
	public void addRepositoryHandler(final Handler handler) {
		
		Preconditions.checkNotNull(handler, "Handler argument cannot be null.");
		
		for (final ICDORepository repository : this) {
			
			repository.getRepository().addHandler(handler);
			
		}
		
	}
	
	@Override
	public NsUriProvider getNsUriProvider(final String uuid) {
		final NsUriProvider provider = uuidToNsUriProviders.get(checkNotNull(uuid, "uuid"));
		return null == provider ? NsUriProvider.NULL_IMPL : provider;
	}
	
	@Override
	protected String getUuid(final ICDORepository managedItem) {
		LifecycleUtil.checkActive(managedItem);
		return managedItem.getUuid();
	}

	protected ICDORepository createItem(final String repositoryUuid, @Nullable final String repositoryName, final byte repositoryNamespaceId, 
			@Nullable final String toolingId, @Nullable final String dependsOnRepositoryUuid, final boolean metaRepository) {
		return new CDORepository(repositoryUuid, repositoryName, repositoryNamespaceId, toolingId, getRepositoryConfiguration(), dependsOnRepositoryUuid, metaRepository);
	}

	@Override
	protected void doBeforeActivate() throws Exception {
		
		//read extension points first, then create managed items 
		super.doBeforeActivate();
		
		if (getSnowOwlConfiguration().isGzip()) {
			IPluginContainer.INSTANCE.addPostProcessor(new TcpGZIPStreamWrapperInjector(CDOProtocolConstants.PROTOCOL_NAME));
		}
		
		Net4jUtil.prepareContainer(IPluginContainer.INSTANCE);
		JVMUtil.prepareContainer(IPluginContainer.INSTANCE);
		TCPUtil.prepareContainer(IPluginContainer.INSTANCE);
		CDONet4jUtil.prepareContainer(IPluginContainer.INSTANCE);
		CDONet4jServerUtil.prepareContainer(IPluginContainer.INSTANCE);
		
		registerCustomProtocols();
		
		LifecycleUtil.activate(IPluginContainer.INSTANCE);
		
		final HostAndPort hostAndPort = getRepositoryConfiguration().getHostAndPort();
		// open port in server environments
		if (SnowOwlApplication.INSTANCE.getEnviroment().isServer()) {
			TCPUtil.getAcceptor(IPluginContainer.INSTANCE, hostAndPort.toString()); // Start the TCP transport
			LOGGER.info("Listening on {} for connections", hostAndPort);
		}
		
		JVMUtil.getAcceptor(IPluginContainer.INSTANCE,	Net4jUtils.NET_4_J_CONNECTOR_NAME); // Start the JVM transport
	}
	
	private RepositoryConfiguration getRepositoryConfiguration() {
		return getSnowOwlConfiguration().getModuleConfig(RepositoryConfiguration.class);
	}

	private SnowOwlConfiguration getSnowOwlConfiguration() {
		return ApplicationContext.getInstance().getServiceChecked(SnowOwlConfiguration.class);
	}
	
	@Override
	protected void doAfterActivate() throws Exception {
		super.doAfterActivate();
		uuidToNsUriProviders = newHashMap();
		for (final String uuid : uuidKeySet()) {
			uuidToNsUriProviders.put(uuid, new NsUriProviderImpl() {
				
				@Override
				protected String getRepositoryUuid() {
					return uuid;
				}
			});
		}
	}
	
	private void registerCustomProtocols() {
		final List<ServerProtocolFactory> serverProtocolFactories = ServerProtocolFactoryRegistry.getInstance().getRegisteredServerProtocolFactories();
		for (final ServerProtocolFactory serverProtocolFactory : serverProtocolFactories) {
			IPluginContainer.INSTANCE.registerFactory(serverProtocolFactory);
		}
	}
	
	/*creates a remote CDO message with the given message body*/
	private static CDORemoteSessionMessage createMessage(final String body) {
		
		final CDORemoteSessionMessage message = new CDORemoteSessionMessage(ADMINISTRATOR_MESSAGE, Priority.HIGH);
		final ExtendedDataOutputStream outputStream = message.getOutputStream();
		
		try {
			
			try {
				
				outputStream.writeString(Strings.nullToEmpty(body));
				outputStream.flush();

			} finally {
				
				Closeables.close(outputStream, true);
				
			}
			
		} catch (final IOException e) {
			
			throw new SnowowlRuntimeException("Error while creating remote message.", e);
			
		}
		
		return message;
	}
	
	/*applies the disconnect function to a subset of sessions*/
	private void applyDisconnect(final Predicate<String> p, final ISessionOperationCallback... callbacks) {
		apply(p, DISCONNECT_SESSIONS, callbacks);
	}
	
	private void sendMessage(final Predicate<String>p, final String message, final ISessionOperationCallback... callbacks) {
		
		final CDORemoteSessionMessage sessionMessage = createMessage(message);
		
		final Procedure<Collection<CDORemoteSession>> f = new Procedure<Collection<CDORemoteSession>>() {
			@Override protected void doApply(Collection<CDORemoteSession> session) {
				
				if (CompareUtils.isEmpty(session)) {
					return; //nothing to do
				}
				
				try {
					
					Iterables.get(session, 0).sendMessage(sessionMessage);
					
				} catch (final Exception e) {
					
					LOGGER.error("Error while sending remote message to '" + Iterables.get(session, 0).getUserID() + "'.");
					
				}
			}
		};
		
		apply(p, f, callbacks);
		
	}
	
	/*applies the function to the connected sessions based on the predicate*/
	private void apply(final Predicate<String> p, final Procedure<Collection<CDORemoteSession>> f, final ISessionOperationCallback... callbacks) {
		
		
		Collections3.forEach(getRemoteSessions().asMap().entrySet(), new Procedure<Entry<String, Collection<CDORemoteSession>>>() {

			@Override protected void doApply(final Entry<String, Collection<CDORemoteSession>> entry) {
				
				final String userId = entry.getKey();

				if (SpecialUserStore.SYSTEM_USER_NAME.equals(userId)) { //embedded client session always ignored
					return;
				}
				
				if (!p.apply(userId)) {
					return;
				} 
					
				//apply function on the session
				f.apply(entry.getValue());
					
				for (final ISessionOperationCallback callback : callbacks) {
					callback.done(Iterables.get(entry.getValue(), 0));
				}
				
			}
		});
		
		
	}
	
	/*returns with all remote sessions grouped by the unique user IDs*/
	private Multimap<String, CDORemoteSession> getRemoteSessions() {
			
		LifecycleUtil.checkActive(this);
		
		final HashMultimap<String, CDORemoteSession> $ = HashMultimap.create();
		
		for (final ICDORepository repository : this) {
		
			final String uuid = repository.getUuid();
			
			final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getByUuid(uuid);
			final CDORemoteSessionManager remoteSessionManager = connection.getSession().getRemoteSessionManager();
			
			for (final CDORemoteSession session : remoteSessionManager.getElements()) {
				
				$.put(session.getUserID(), session);
				
			}
			
		}
		
		return Multimaps.unmodifiableMultimap($);
		
	}
	
	/**Returns with the shared instance.*/
	public static CDORepositoryManager getInstance() {
		
		if (null == instance) {
			
			synchronized (CDORepositoryManager.class) {
				
				if (null == instance) {
					
					instance = new CDORepositoryManager();
					
				}
				
			}
			
		}
		
		return instance;
		
	}
	
	private CDORepositoryManager() { /*suppress instantiation*/ }

}