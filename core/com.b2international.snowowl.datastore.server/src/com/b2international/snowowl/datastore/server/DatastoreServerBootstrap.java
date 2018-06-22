/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.spi.net4j.ServerProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.Notifications;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.ApiRequestHandler;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.ModuleConfig;
import com.b2international.snowowl.core.setup.PreRunCapableBootstrapFragment;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.net4j.Net4jUtils;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexManager;
import com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexManagerImpl;
import com.b2international.snowowl.datastore.server.internal.DefaultRepositoryContextProvider;
import com.b2international.snowowl.datastore.server.internal.DefaultRepositoryManager;
import com.b2international.snowowl.datastore.server.internal.ExtensionBasedEditingContextFactoryProvider;
import com.b2international.snowowl.datastore.server.internal.ExtensionBasedRepositoryClassLoaderProviderRegistry;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.b2international.snowowl.datastore.server.session.ApplicationSessionManager;
import com.b2international.snowowl.datastore.server.session.LogListener;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJobManager;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.net4j.EventBusNet4jUtil;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.rpc.RpcConfiguration;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.net.HostAndPort;

/**
 * @since 3.3
 */
@ModuleConfig(fieldName = "reviewManager", type = ReviewConfiguration.class)
public class DatastoreServerBootstrap implements PreRunCapableBootstrapFragment {

	private static final String REPOSITORY_EXT_ID = "com.b2international.snowowl.datastore.repository";
	private static final String UUID_ATTRIBUTE = "uuid";
	private static final String TOOLING_ID_ATTRIBUTE = "toolingId";
	
	private static final Logger LOG = LoggerFactory.getLogger("core");
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final IManagedContainer container = env.container();
		final boolean gzip = configuration.isGzip();
		final RpcConfiguration rpcConfig = configuration.getModuleConfig(RpcConfiguration.class);
		LOG.debug("Preparing RPC communication (config={},gzip={})", rpcConfig, gzip);
		RpcUtil.prepareContainer(container, rpcConfig, gzip);
		LOG.debug("Preparing EventBus communication (gzip={})", gzip);
		int numberOfWorkers = configuration.getModuleConfig(RepositoryConfiguration.class).getNumberOfWorkers();
		EventBusNet4jUtil.prepareContainer(container, gzip, numberOfWorkers);
		env.services().registerService(IEventBus.class, EventBusNet4jUtil.getBus(container, numberOfWorkers));
		LOG.debug("Preparing JSON support");
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		mapper.registerModule(new PrimitiveCollectionModule());
		env.services().registerService(ObjectMapper.class, mapper);
		// initialize class loader registry
		env.services().registerService(RepositoryClassLoaderProviderRegistry.class, new ExtensionBasedRepositoryClassLoaderProviderRegistry());
		final ClassLoader classLoader = env.service(RepositoryClassLoaderProviderRegistry.class).getClassLoader();
		// initialize Notification support
		env.services().registerService(Notifications.class, new Notifications(env.service(IEventBus.class), classLoader));
	}

	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) {
		if (env.isServer() || env.isEmbedded()) {
			LOG.debug(">>> Starting server-side datastore bundle.");
			final IManagedContainer container = env.container();
			final Stopwatch serverStopwatch = Stopwatch.createStarted();
			
			RpcUtil.getInitialServerSession(container).registerServiceLookup(new RpcServerServiceLookup());
			final ApplicationSessionManager manager = new ApplicationSessionManager(env.service(IdentityProvider.class));
			manager.addListener(new LogListener());
			
			env.services().registerService(IApplicationSessionManager.class, manager);
			env.services().registerService(InternalApplicationSessionManager.class, manager);
			
			final ClassLoader managerClassLoader = manager.getClass().getClassLoader();
			RpcUtil.getInitialServerSession(container).registerClassLoader(IApplicationSessionManager.class, managerClassLoader);
			RpcUtil.getInitialServerSession(container).registerClassLoader(InternalApplicationSessionManager.class, managerClassLoader);
			
			Net4jUtil.prepareContainer(container);
			JVMUtil.prepareContainer(container);
			TCPUtil.prepareContainer(container);
			
			registerCustomProtocols(container);
			
			LifecycleUtil.activate(container);
			
			final HostAndPort hostAndPort = configuration.getModuleConfig(RepositoryConfiguration.class).getHostAndPort();
			// open port in server environments
			TCPUtil.getAcceptor(container, hostAndPort.toString()); // Starts the TCP transport
			LOG.info("Listening on {} for connections", hostAndPort);
			JVMUtil.getAcceptor(container,	Net4jUtils.NET_4_J_CONNECTOR_NAME); // Starts the JVM transport
			
			// TODO remove single directory manager
			env.services().registerService(SingleDirectoryIndexManager.class, new SingleDirectoryIndexManagerImpl());

			env.services().registerService(RepositoryManager.class, new DefaultRepositoryManager());
			env.services().registerService(EditingContextFactoryProvider.class, new ExtensionBasedEditingContextFactoryProvider());
			
			int numberOfWorkers = configuration.getModuleConfig(RepositoryConfiguration.class).getNumberOfWorkers();
			initializeRequestSupport(env, numberOfWorkers);
			
			LOG.debug("<<< Server-side datastore bundle started. [{}]", serverStopwatch);
		} else {
			LOG.debug("Snow Owl application is running in remote mode.");
			LOG.info("Connecting to Snow Owl Terminology Server at {}", env.service(ClientPreferences.class).getCDOUrl());
		}
		
		if (configuration.isSystemUserNeeded() || env.isServer()) {
			try {
				connectSystemUser(env.container());
			} catch (SnowowlServiceException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
		
	}
	
	private void registerCustomProtocols(IManagedContainer container) {
		final List<ServerProtocolFactory> serverProtocolFactories = ServerProtocolFactoryRegistry.getInstance().getRegisteredServerProtocolFactories();
		for (final ServerProtocolFactory serverProtocolFactory : serverProtocolFactories) {
			container.registerFactory(serverProtocolFactory);
		}
	}
	
	@Override
	public void run(SnowOwlConfiguration configuration, Environment env, IProgressMonitor monitor) throws Exception {
		ServiceConfigJobManager.INSTANCE.registerServices(monitor);
		
		if (env.isEmbedded() || env.isServer()) {
			initializeJobSupport(env, configuration);
			initializeRepositories(configuration, env);
		}
	}
	
	private void initializeJobSupport(Environment env, SnowOwlConfiguration configuration) {
		final Index index = Indexes.createIndex("jobs", env.service(ObjectMapper.class), new Mappings(RemoteJobEntry.class), env.service(IndexSettings.class));
		// TODO make this configurable
		final long defaultJobCleanUpInterval = TimeUnit.MINUTES.toMillis(1);
		env.services()
			.registerService(RemoteJobTracker.class, 
				new RemoteJobTracker(
					index, 
					env.service(IEventBus.class), 
					env.service(ObjectMapper.class), 
					defaultJobCleanUpInterval)
			);
	}

	private void initializeRequestSupport(Environment env, int numberOfWorkers) {
		final IEventBus events = env.service(IEventBus.class);
		final ClassLoader classLoader = env.service(RepositoryClassLoaderProviderRegistry.class).getClassLoader();
		for (int i = 0; i < numberOfWorkers; i++) {
			events.registerHandler(Request.ADDRESS, new ApiRequestHandler(env, classLoader));
		}
		
		env.services().registerService(RepositoryContextProvider.class, new DefaultRepositoryContextProvider(env.service(RepositoryManager.class)));
	}

	private void initializeRepositories(SnowOwlConfiguration configuration, Environment env) {
		
		final Stopwatch branchStopwatch = Stopwatch.createStarted();
		LOG.debug(">>> Initializing branch and review services.");
		
		final DefaultRepositoryManager repositories = (DefaultRepositoryManager) env.service(RepositoryManager.class);
		
		RepositoryConfiguration repositoryConfig = configuration.getModuleConfig(RepositoryConfiguration.class);
		
		//load all CDO repository extensions, instantiate repositories and apply inverse mapping to the namespace URI
		for (final IConfigurationElement repositoryElement : Platform.getExtensionRegistry().getConfigurationElementsFor(REPOSITORY_EXT_ID)) {
			
			final String repositoryId = repositoryElement.getAttribute(UUID_ATTRIBUTE);
			final String toolingId = repositoryElement.getAttribute(TOOLING_ID_ATTRIBUTE);
			
			Repository repo = repositories
					.prepareCreate(repositoryId, toolingId)
					.setMergeMaxResults(repositoryConfig.getMergeMaxResults())
					.build(env);
			if (repo.health() == Health.GREEN) {
				LOG.info("Started repository '{}' with status '{}'", repo.id(), repo.health());
			} else {
				LOG.warn("Started repository '{}' with status '{}'. Diagnosis: {}.", repo.id(), repo.health(), repo.diagnosis());
			}
		}
		
		LOG.debug("<<< Branch and review services registered. [{}]", branchStopwatch);
	}
	
	private void connectSystemUser(IManagedContainer container) throws SnowowlServiceException {
		// Normally this is done for us by CDOConnectionFactory
		final IJVMConnector connector = JVMUtil.getConnector(container, Net4jUtils.NET_4_J_CONNECTOR_NAME);
		final RpcProtocol clientProtocol = RpcUtil.getRpcClientProtocol(container);
		clientProtocol.open(connector);

		RpcUtil.getRpcClientProxy(InternalApplicationSessionManager.class).connectSystemUser();
	}
}
