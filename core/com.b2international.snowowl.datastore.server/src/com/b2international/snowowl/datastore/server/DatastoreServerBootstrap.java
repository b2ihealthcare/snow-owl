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

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexServerServiceManager;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.ModuleConfig;
import com.b2international.snowowl.core.setup.PreRunCapableBootstrapFragment;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.cdo.CDOConnectionFactoryProvider;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.net4j.Net4jUtils;
import com.b2international.snowowl.datastore.server.branch.BranchManager;
import com.b2international.snowowl.datastore.server.index.IndexServerServiceManager;
import com.b2international.snowowl.datastore.server.internal.RepositoryWrapper;
import com.b2international.snowowl.datastore.server.internal.branch.BranchEventHandler;
import com.b2international.snowowl.datastore.server.internal.branch.BranchSerializer;
import com.b2international.snowowl.datastore.server.internal.branch.CDOBranchManagerImpl;
import com.b2international.snowowl.datastore.server.internal.branch.InternalBranch;
import com.b2international.snowowl.datastore.server.internal.review.ConceptChangesImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewEventHandler;
import com.b2international.snowowl.datastore.server.internal.review.ReviewImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewManagerImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewSerializer;
import com.b2international.snowowl.datastore.server.review.ReviewManager;
import com.b2international.snowowl.datastore.server.session.ApplicationSessionManager;
import com.b2international.snowowl.datastore.server.session.LogListener;
import com.b2international.snowowl.datastore.server.session.VersionProcessor;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJobManager;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.datastore.store.IndexStore;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.net4j.EventBusNet4jUtil;
import com.b2international.snowowl.rpc.RpcConfiguration;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;
import com.google.common.base.Stopwatch;

/**
 * @since 3.3
 */
@ModuleConfig(fieldName = "reviewManager", type = ReviewConfiguration.class)
public class DatastoreServerBootstrap implements PreRunCapableBootstrapFragment {

	private static final Logger LOG = LoggerFactory.getLogger(DatastoreServerBootstrap.class);
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment environment) throws Exception {
		final IManagedContainer container = environment.container();
		final RpcConfiguration rpcConfig = configuration.getModuleConfig(RpcConfiguration.class);
		LOG.info("Preparing RPC communication with config {}", rpcConfig);
		RpcUtil.prepareContainer(container, rpcConfig);
		LOG.info("Preparing EventBus communication");
		EventBusNet4jUtil.prepareContainer(container);
		environment.services().registerService(IEventBus.class, EventBusNet4jUtil.getBus(container));
	}

	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment environment) {
		if (environment.isServer() || environment.isEmbedded()) {
			LOG.info(">>> Starting server-side datastore bundle.");
			final IManagedContainer container = environment.container();
			final Stopwatch serverStopwatch = Stopwatch.createStarted();
			
			RpcUtil.getInitialServerSession(container).registerServiceLookup(new RpcServerServiceLookup());
			final ApplicationSessionManager manager = new ApplicationSessionManager(configuration);
			manager.addListener(new LogListener());
			manager.addListener(new VersionProcessor());
			
			environment.services().registerService(IApplicationSessionManager.class, manager);
			environment.services().registerService(InternalApplicationSessionManager.class, manager);
			
			final ClassLoader managerClassLoader = manager.getClass().getClassLoader();
			RpcUtil.getInitialServerSession(container).registerClassLoader(IApplicationSessionManager.class, managerClassLoader);
			RpcUtil.getInitialServerSession(container).registerClassLoader(InternalApplicationSessionManager.class, managerClassLoader);
			
			final ICDORepositoryManager cdoRepositoryManager = CDORepositoryManager.getInstance();
			cdoRepositoryManager.activate();
			environment.services().registerService(ICDORepositoryManager.class, cdoRepositoryManager);
			
			environment.services().registerService(IIndexServerServiceManager.class, IndexServerServiceManager.INSTANCE);
			
			LOG.info("<<< Server-side datastore bundle started. [{}]", serverStopwatch);
		} else {
			LOG.info("Snow Owl application is running in remote mode.");
			LOG.info("Connecting to Snow Owl Terminology Server at {}", environment.service(ClientPreferences.class).getCDOUrl());
		}
		if (configuration.isSystemUserNeeded() || environment.isServer()) {
			try {
				connectSystemUser(environment.container());
			} catch (SnowowlServiceException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
	}
	
	@Override
	public void run(SnowOwlConfiguration configuration, Environment environment, IProgressMonitor monitor) throws Exception {
		ServiceConfigJobManager.INSTANCE.registerServices(monitor);
		
		if (environment.isServer() || environment.isEmbedded()) {
			ReviewConfiguration reviewConfiguration = configuration.getModuleConfig(ReviewConfiguration.class);
			initializeBranchingSupport(environment, reviewConfiguration);
		}
	}

	private void initializeBranchingSupport(Environment environment, ReviewConfiguration reviewConfiguration) {
		final Stopwatch branchStopwatch = Stopwatch.createStarted();
		LOG.info(">>> Initializing branch and review services.");
		
		final ICDOConnectionManager cdoConnectionManager = environment.service(ICDOConnectionManager.class);
		final ICDORepositoryManager cdoRepositoryManager = environment.service(ICDORepositoryManager.class);
		final IIndexServerServiceManager indexServerServiceManager = environment.service(IIndexServerServiceManager.class); 
		
		final BranchSerializer branchSerializer = new BranchSerializer();
		final ReviewSerializer reviewSerializer = new ReviewSerializer();
		
		for (String repositoryId : cdoRepositoryManager.uuidKeySet()) {
			final RepositoryWrapper wrapper = new RepositoryWrapper(repositoryId, cdoConnectionManager, cdoRepositoryManager, indexServerServiceManager);
			initializeBranchingSupport(environment, wrapper, branchSerializer, reviewSerializer, reviewConfiguration);
		}
		
		LOG.info("<<< Branch and review services registered. [{}]", branchStopwatch);
	}

	private void initializeBranchingSupport(Environment environment, RepositoryWrapper wrapper, BranchSerializer branchSerializer, 
			ReviewSerializer reviewSerializer, ReviewConfiguration reviewConfiguration) {
		
		final String repositoryId = wrapper.getCdoRepositoryId();
		final File branchIndexDirectory = environment.getDataDirectory()
				.toPath()
				.resolve(Paths.get("indexes", "branches", repositoryId))
				.toFile();

		final IndexStore<InternalBranch> branchStore = new IndexStore<InternalBranch>(branchIndexDirectory, branchSerializer, InternalBranch.class);
		final BranchManager branchManager = new CDOBranchManagerImpl(wrapper, branchStore);
		
		final File reviewsIndexDirectory = environment.getDataDirectory()
				.toPath()
				.resolve(Paths.get("indexes", "reviews", repositoryId))
				.toFile();
		
		final File conceptChangesIndexDirectory = environment.getDataDirectory()
				.toPath()
				.resolve(Paths.get("indexes", "concept_changes", repositoryId))
				.toFile();
		
		final IndexStore<ReviewImpl> reviewStore = new IndexStore<ReviewImpl>(reviewsIndexDirectory, reviewSerializer, ReviewImpl.class);
		final IndexStore<ConceptChangesImpl> conceptChangesStore = new IndexStore<ConceptChangesImpl>(conceptChangesIndexDirectory, reviewSerializer, ConceptChangesImpl.class);
		
		final ReviewManager reviewManager = new ReviewManagerImpl(wrapper.getCdoRepository(), 
				reviewStore, conceptChangesStore,
				reviewConfiguration.getKeepCurrentMins(), reviewConfiguration.getKeepOtherMins());

		environment.service(IEventBus.class).registerHandler("/" + repositoryId + "/branches" , new BranchEventHandler(branchManager, reviewManager));
		environment.service(IEventBus.class).registerHandler("/" + repositoryId + "/reviews" , new ReviewEventHandler(branchManager, reviewManager));
	}
	
	private void connectSystemUser(IManagedContainer container) throws SnowowlServiceException {
		// Normally this is done for us by CDOConnectionFactory
		final IJVMConnector connector = JVMUtil.getConnector(container, Net4jUtils.NET_4_J_CONNECTOR_NAME);
		final RpcProtocol clientProtocol = RpcUtil.getRpcClientProtocol(container);
		clientProtocol.open(connector);

		RpcUtil.getRpcClientProxy(InternalApplicationSessionManager.class).connectSystemUser();
		CDOConnectionFactoryProvider.INSTANCE.getConnectionFactory().connect(SpecialUserStore.SYSTEM_USER);
	}
}
