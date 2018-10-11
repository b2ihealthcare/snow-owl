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
package com.b2international.snowowl.core.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.b2international.commons.extension.Component;
import com.b2international.index.Index;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.Notifications;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.ApiRequestHandler;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.datastore.ServerProtocolFactoryRegistry;
import com.b2international.snowowl.datastore.config.IndexConfiguration;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.connection.RepositoryConnectionConfiguration;
import com.b2international.snowowl.datastore.internal.RpcServerServiceLookup;
import com.b2international.snowowl.datastore.internal.session.ApplicationSessionManager;
import com.b2international.snowowl.datastore.internal.session.InternalApplicationSessionManager;
import com.b2international.snowowl.datastore.internal.session.LogListener;
import com.b2international.snowowl.datastore.net4j.Net4jUtils;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.b2international.snowowl.datastore.review.ReviewConfiguration;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.net4j.EventBusNet4jUtil;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.rpc.RpcConfiguration;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;

/**
 * @since 3.3
 */
@Component
public final class RepositoryPlugin extends Plugin {

	private static final Logger LOG = LoggerFactory.getLogger("core");
	
	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("reviewManager", ReviewConfiguration.class);
		registry.add("repository", RepositoryConfiguration.class);
		registry.add("rpc", RpcConfiguration.class);
		registry.add("connection", RepositoryConnectionConfiguration.class);
	}
	
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
		// initialize Notification support
		env.services().registerService(Notifications.class, new Notifications(env.service(IEventBus.class), env.plugins().getCompositeClassLoader()));
		// initialize Index Settings
		final IndexSettings indexSettings = new IndexSettings();
		indexSettings.putAll(initIndexSettings(env));
		env.services().registerService(IndexSettings.class, indexSettings);
		env.services().registerService(TimestampProvider.class, new TimestampProvider.Default());
	}
	
	private Map<String, Object> initIndexSettings(Environment env) {
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
		builder.put(IndexClientFactory.DATA_DIRECTORY, env.getDataPath().resolve("indexes").toString());
		builder.put(IndexClientFactory.CONFIG_DIRECTORY, env.getConfigPath().toString());
		
		final RepositoryConfiguration repositoryConfig = env.service(SnowOwlConfiguration.class)
				.getModuleConfig(RepositoryConfiguration.class);
		builder.put(IndexClientFactory.INDEX_PREFIX, repositoryConfig.getDeploymentId());
		
		final IndexConfiguration indexConfig = repositoryConfig.getIndexConfiguration();
		if (indexConfig.getClusterUrl() != null) {
			builder.put(IndexClientFactory.CLUSTER_URL, indexConfig.getClusterUrl());
			if (indexConfig.getClusterUsername() != null) {
				builder.put(IndexClientFactory.CLUSTER_USERNAME, indexConfig.getClusterUsername());
			}
			if (indexConfig.getClusterPassword() != null) {
				builder.put(IndexClientFactory.CLUSTER_PASSWORD, indexConfig.getClusterPassword());
			}
		}
		
		builder.put(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, indexConfig.getCommitInterval());
		builder.put(IndexClientFactory.COMMIT_CONCURRENCY_LEVEL, indexConfig.getCommitConcurrencyLevel());
		builder.put(IndexClientFactory.CONNECT_TIMEOUT, indexConfig.getConnectTimeout());
		
		if (indexConfig.getClusterHealthTimeout() <= indexConfig.getSocketTimeout()) {
			throw new IllegalStateException(String.format("Cluster health timeout (%s ms) must be greater than the socket timeout (%s ms).", 
					indexConfig.getClusterHealthTimeout(),
					indexConfig.getSocketTimeout()));
		}
		
		builder.put(IndexClientFactory.SOCKET_TIMEOUT, indexConfig.getSocketTimeout());
		builder.put(IndexClientFactory.CLUSTER_HEALTH_TIMEOUT, indexConfig.getClusterHealthTimeout());
		
		final SlowLogConfig slowLog = createSlowLogConfig(indexConfig);
		builder.put(IndexClientFactory.SLOW_LOG_KEY, slowLog);
		
		return builder.build();
	}

	private SlowLogConfig createSlowLogConfig(final IndexConfiguration config) {
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder();
		builder.put(SlowLogConfig.FETCH_DEBUG_THRESHOLD, config.getFetchDebugThreshold());
		builder.put(SlowLogConfig.FETCH_INFO_THRESHOLD, config.getFetchInfoThreshold());
		builder.put(SlowLogConfig.FETCH_TRACE_THRESHOLD, config.getFetchTraceThreshold());
		builder.put(SlowLogConfig.FETCH_WARN_THRESHOLD, config.getFetchWarnThreshold());
		builder.put(SlowLogConfig.QUERY_DEBUG_THRESHOLD, config.getQueryDebugThreshold());
		builder.put(SlowLogConfig.QUERY_INFO_THRESHOLD, config.getQueryInfoThreshold());
		builder.put(SlowLogConfig.QUERY_TRACE_THRESHOLD, config.getQueryTraceThreshold());
		builder.put(SlowLogConfig.QUERY_WARN_THRESHOLD, config.getQueryWarnThreshold());
		
		return new SlowLogConfig(builder.build());
	}

	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) {
		if (env.isServer() || env.isEmbedded()) {
			LOG.debug("Initializing repository plugin.");
			
			final MeterRegistry registry = env.service(MeterRegistry.class);
			final ExecutorService requestExecutorService = env.service(IEventBus.class).getExecutorService();
			new ExecutorServiceMetrics(requestExecutorService, "request", Tags.empty()).bindTo(registry);
			
			final IManagedContainer container = env.container();
			
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
			
				final RepositoryManager repositoryManager = new DefaultRepositoryManager();
			env.services().registerService(RepositoryManager.class, repositoryManager);
			env.services().registerService(RepositoryContextProvider.class, repositoryManager);
			
			int numberOfWorkers = configuration.getModuleConfig(RepositoryConfiguration.class).getNumberOfWorkers();
			initializeRequestSupport(env, numberOfWorkers);
			
			LOG.debug("Initialized repository plugin.");
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
	public void run(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isEmbedded() || env.isServer()) {
			initializeJobSupport(env, configuration);
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
		final ClassLoader classLoader = env.plugins().getCompositeClassLoader();
		for (int i = 0; i < numberOfWorkers; i++) {
			events.registerHandler(Request.ADDRESS, new ApiRequestHandler(env, classLoader));
		}
	}

	private void connectSystemUser(IManagedContainer container) throws SnowowlServiceException {
		// Normally this is done for us by CDOConnectionFactory
		final IJVMConnector connector = JVMUtil.getConnector(container, Net4jUtils.NET_4_J_CONNECTOR_NAME);
		final RpcProtocol clientProtocol = RpcUtil.getRpcClientProtocol(container);
		clientProtocol.open(connector);

		RpcUtil.getRpcClientProxy(InternalApplicationSessionManager.class).connectSystemUser();
	}
}
