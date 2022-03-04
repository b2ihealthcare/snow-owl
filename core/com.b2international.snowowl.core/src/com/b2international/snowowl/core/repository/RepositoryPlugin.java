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
package com.b2international.snowowl.core.repository;

import java.io.File;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.commons.StringUtils;
import com.b2international.index.Index;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.client.TransportConfiguration;
import com.b2international.snowowl.core.config.IndexConfiguration;
import com.b2international.snowowl.core.config.IndexSettings;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.Notifications;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.jobs.RemoteJobTracker;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.netty.EventBusNettyUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * @since 3.3
 */
@Component
public final class RepositoryPlugin extends Plugin {

	private static final Logger LOG = LoggerFactory.getLogger("core");
	
	private static final String JOBS_INDEX = "jobs";
	
	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("repository", RepositoryConfiguration.class);
		registry.add("transport", TransportConfiguration.class);
	}
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		RepositoryConfiguration repositoryConfiguration = configuration.getModuleConfig(RepositoryConfiguration.class);
		env.services().registerService(RepositoryConfiguration.class, repositoryConfiguration);
		int maxThreads = repositoryConfiguration.getMaxThreads();
		LOG.debug("Preparing EventBus communication (maxThreads={})", maxThreads);
		env.services().registerService(IEventBus.class, EventBusUtil.getBus("server", maxThreads));
		LOG.debug("Preparing JSON support");
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		mapper.registerModule(new PrimitiveCollectionModule());
		env.services().registerService(ObjectMapper.class, mapper);
		// initialize Notification support
		env.services().registerService(Notifications.class, new Notifications(env.service(IEventBus.class), env.plugins().getCompositeClassLoader()));
		env.services().registerService(RepositoryCommitNotificationSender.class, new RepositoryCommitNotificationSender());
		// initialize Index Settings
		final IndexSettings indexSettings = new IndexSettings();
		indexSettings.putAll(initIndexSettings(env));
		env.services().registerService(IndexSettings.class, indexSettings);
	}
	
	private Map<String, Object> initIndexSettings(Environment env) {
		final RepositoryConfiguration repositoryConfig = env.service(RepositoryConfiguration.class);
		final IndexConfiguration indexConfig = repositoryConfig.getIndexConfiguration();
		
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
		indexConfig.configure(builder);
		
		builder.put(IndexClientFactory.DATA_DIRECTORY, env.getDataPath().resolve("indexes").toString());
		builder.put(IndexClientFactory.CONFIG_DIRECTORY, env.getConfigPath().toString());
		builder.put(IndexClientFactory.INDEX_PREFIX, repositoryConfig.getDeploymentId());
		
		return builder.build();
	}

	private static final class ServerChannelService implements IDisposableService {

		private final Channel serverChannel;
		
		public ServerChannelService(final Channel serverChannel) {
			this.serverChannel = serverChannel;
		}

		@Override
		public void dispose() {
			serverChannel.close().awaitUninterruptibly();
		}

		@Override
		public boolean isDisposed() {
			return !serverChannel.isActive();
		}
	}
	
	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) {
		if (env.isServer()) {
			LOG.debug("Initializing repository plugin.");
			final MeterRegistry registry = env.service(MeterRegistry.class);
			final IEventBus eventBus = env.service(IEventBus.class);
			// Add event bus based request metrics
			registerRequestMetrics(registry, eventBus);
			
			final RepositoryConfiguration repositoryConfiguration = env.service(RepositoryConfiguration.class);
			final HostAndPort hostAndPort = repositoryConfiguration.getHostAndPort();
			// open port in server environments
			if (hostAndPort.getPort() > 0) {
				final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
				final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

				final boolean gzip = configuration.isGzip();
				final ClassLoader compositeClassLoader = env.plugins().getCompositeClassLoader();
				
				final SslContext sslCtx;
				try {
				
					final String certificateChainPath = repositoryConfiguration.getCertificateChainPath();
					final String privateKeyPath = repositoryConfiguration.getPrivateKeyPath();
					
					if (!StringUtils.isEmpty(certificateChainPath) && !StringUtils.isEmpty(privateKeyPath)) {
						
						sslCtx = SslContextBuilder
							.forServer(new File(certificateChainPath), new File(privateKeyPath))
							.build();
						
					} else {
						
						try {
							
							final SelfSignedCertificate ssc = new SelfSignedCertificate();
							sslCtx = SslContextBuilder
								.forServer(ssc.certificate(), ssc.privateKey())
							    .build();
							
						} catch (final CertificateException e) {
							throw new SnowowlRuntimeException("Failed to generate self-signed certificate.", e);
						}
					}
				
				} catch (final SSLException e) {
					throw new SnowowlRuntimeException("Failed to create server SSL context.", e);
				}
		        
				final Channel serverChannel = new ServerBootstrap()
					.group(bossGroup, workerGroup)
//					.handler(new LoggingHandler(LogLevel.INFO))
					.channel(NioServerSocketChannel.class)
					.childHandler(EventBusNettyUtil.createChannelHandler(sslCtx, gzip, true, eventBus, compositeClassLoader))
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.bind(hostAndPort.getHost(), hostAndPort.getPortOrDefault(2036))
					.syncUninterruptibly()
					.channel();

				// Register channel as a service so it will be shut down when services are disposed
				env.services().registerService(ServerChannelService.class, new ServerChannelService(serverChannel));
				
				LOG.info("Listening on {} for connections", hostAndPort);
			}
			
			final RepositoryManager repositoryManager = new DefaultRepositoryManager();
			env.services().registerService(RepositoryManager.class, repositoryManager);
			env.services().registerService(RepositoryContextProvider.class, repositoryManager);
			
			int numberOfWorkers = repositoryConfiguration.getMaxThreads();
			initializeRequestSupport(env, numberOfWorkers);
			
			LOG.debug("Initialized repository plugin.");
		} else {
			LOG.debug("Snow Owl application is running in remote mode.");
		}
	}
	
	private void registerRequestMetrics(MeterRegistry registry, IEventBus eventBus) {
		FunctionCounter.builder("requests.completed", eventBus, bus -> bus.getCompletedMessages(Request.TAG))
				.description("The total number of requests that have completed execution")
				.register(registry);

		Gauge.builder("requests.processing", eventBus, bus -> bus.getProcessingMessages(Request.TAG))
				.description("The approximate number of requests that are currently under execution")
				.register(registry);

		Gauge.builder("requests.queued", eventBus, bus -> bus.getInQueueMessages(Request.TAG))
				.description("The approximate number of requests that are queued for execution")
				.register(registry);
		
		FunctionCounter.builder("requests.succeeded", eventBus, bus -> bus.getSucceededMessages(Request.TAG))
				.description("The total number of requests that have successfully completed execution")
				.register(registry);
		
		FunctionCounter.builder("requests.failed", eventBus, bus -> bus.getFailedMessages(Request.TAG))
				.description("The total number of requests that have failed execution")
				.register(registry);
		
	}
	
	@Override
	public void run(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isServer()) {
			initializeJobSupport(env, configuration);
		}
	}
	
	private void initializeJobSupport(Environment env, SnowOwlConfiguration configuration) {
		final ObjectMapper objectMapper = env.service(ObjectMapper.class);
		final Index jobsIndex = Indexes.createIndex(
			JOBS_INDEX, 
			objectMapper, 
			new Mappings(RemoteJobEntry.class), 
			env.service(IndexSettings.class).forIndex(env.service(RepositoryConfiguration.class).getIndexConfiguration(), JOBS_INDEX)
		);
		// TODO make this configurable
		final long defaultJobCleanUpInterval = TimeUnit.MINUTES.toMillis(1);
		env.services()
			.registerService(RemoteJobTracker.class, 
				new RemoteJobTracker(
					jobsIndex, 
					env.service(IEventBus.class), 
					objectMapper, 
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
}
