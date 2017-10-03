/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.migrate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.cdo.FilteringErrorLoggingStrategy;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.config.DatabaseConfiguration;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.connection.RepositoryConnectionConfiguration;
import com.b2international.snowowl.datastore.server.CDORepository;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.internal.InternalRepository;
import com.b2international.snowowl.datastore.server.internal.branch.InternalCDOBasedBranch;
import com.b2international.snowowl.datastore.server.reindex.ReindexRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.10
 */
@SuppressWarnings("restriction")
public final class MigrateRequest implements Request<RepositoryContext, MigrationResult> {

	@NotEmpty
	@JsonProperty
	private final String remoteLocation;
	
	@JsonProperty
	private String scriptLocation;
	
	@JsonProperty
	private long commitTimestamp = 1;

	MigrateRequest(String remoteLocation) {
		this.remoteLocation = remoteLocation;
	}
	
	void setScriptLocation(String scriptLocation) {
		this.scriptLocation = scriptLocation;
	}
	
	void setCommitTimestamp(final long commitTimestamp) {
		this.commitTimestamp = commitTimestamp;
	}
	
	@Override
	public MigrationResult execute(RepositoryContext context) {
		final InternalRepository repository = (InternalRepository) context.service(Repository.class);
		final FeatureToggles features = context.service(FeatureToggles.class);
		
		int maxCdoBranchId = -1;
		final BranchManager branchManager = context.service(BranchManager.class);
		final Collection<? extends Branch> branches = branchManager.getBranches();
		
		for (final Branch branch : branches) {
			final InternalCDOBasedBranch cdoBranch = (InternalCDOBasedBranch) branch;
			if (cdoBranch.cdoBranchId() > maxCdoBranchId) {
				maxCdoBranchId = cdoBranch.cdoBranchId();
			}
		}
		
		final org.eclipse.emf.cdo.internal.server.Repository localRepository = (org.eclipse.emf.cdo.internal.server.Repository) repository.getCdoRepository().getRepository();
		
		// initialize a new repository to the location
		RepositoryConfiguration repositoryConfiguration = context.config().getModuleConfig(RepositoryConfiguration.class);
		final org.eclipse.emf.cdo.internal.server.Repository remoteRepository = createRemoteRepository((CDORepository) repository.getCdoRepository(), createRemoteConfiguration(repositoryConfiguration, remoteLocation));
		
		// open JVM based connections
		final CDONet4jSessionConfiguration sessionConfiguration = CDONet4jUtil.createNet4jSessionConfiguration();
		sessionConfiguration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
		sessionConfiguration.setRepositoryName("replicated"+localRepository.getUUID());
		sessionConfiguration.setConnector(context.service(ICDOConnectionManager.class).getConnector());
		sessionConfiguration.getAuthenticator().setCredentialsProvider(new PasswordCredentialsProvider(SpecialUserStore.SYSTEM_USER.getUserName(), SpecialUserStore.SYSTEM_USER.getPassword()));
		
		final InternalSession session = localRepository.getSessionManager().openSession(null);
		final CDONet4jSessionImpl remoteSession = (CDONet4jSessionImpl) sessionConfiguration.openNet4jSession();
		
		try {
			repository.setHealth(Health.YELLOW, "Migration is in progress...");
			features.enable(ReindexRequest.featureFor(context.id()));
			MigrationReplicationContext delegate = new MigrationReplicationContext(context, maxCdoBranchId, commitTimestamp - 1, session, scriptLocation);
			final AsyncReplicationContext replicationContext = new AsyncReplicationContext(delegate);

			remoteSession.setSignalTimeout(context.config().getModuleConfig(RepositoryConnectionConfiguration.class).getSignalTimeout());
			
			StoreThreadLocal.setSession(session);
			remoteSession.getSessionProtocol().replicateRepository(replicationContext, new Monitor());
			
			replicationContext.await(2L, TimeUnit.HOURS);
			
			localRepository.getStore().setLastBranchID(delegate.getLastReplicatedBranchID());
			
			return new MigrationResult(delegate.getFailedCommitTimestamp(), delegate.getProcessedCommits(), delegate.getSkippedCommits(),
					delegate.getException());
		} finally {
			StoreThreadLocal.release();
			session.close();
			repository.checkHealth();
			// close the replicator source repository
			remoteSession.close();
			LifecycleUtil.deactivate(remoteRepository);
			features.disable(ReindexRequest.featureFor(context.id()));
		}
	}

	private static RepositoryConfiguration createRemoteConfiguration(RepositoryConfiguration base, String newLocation) {
		// copy everything from the base config except the location
		RepositoryConfiguration conf = new RepositoryConfiguration();
		conf.setHost(base.getHost());
		conf.setPort(base.getPort());
		conf.setRevisionCacheEnabled(false);
		
		DatabaseConfiguration dbConf = new DatabaseConfiguration();
		conf.setDatabaseConfiguration(dbConf);
		dbConf.setDatasourceClass(base.getDatabaseConfiguration().getDatasourceClass());
		dbConf.setDirectory(base.getDatabaseConfiguration().getDirectory());
		dbConf.setDriverClass(base.getDatabaseConfiguration().getDriverClass());
		dbConf.setPassword(base.getDatabaseConfiguration().getPassword());
		dbConf.setScheme(base.getDatabaseConfiguration().getScheme());
		dbConf.setSettings(base.getDatabaseConfiguration().getSettings());
		dbConf.setType(base.getDatabaseConfiguration().getType());
		dbConf.setUsername(base.getDatabaseConfiguration().getUsername());
		dbConf.setLocation(newLocation);
		return conf;
	}

	private org.eclipse.emf.cdo.internal.server.Repository createRemoteRepository(CDORepository localRepository, RepositoryConfiguration configuration) {
		final IDBConnectionProvider connectionProvider = CDORepository.createConnectionProvider(configuration.getDatasourceProperties(localRepository.getUuid()));

		final IDBStore dbStore = CDORepository.createDBStore(connectionProvider, configuration);
		final IIDHandler idHandler =
				new org.eclipse.emf.cdo.server.internal.db.LongIDHandler((org.eclipse.emf.cdo.server.internal.db.DBStore) dbStore);

		idHandler.setLastObjectID(CDOIDUtil.createLong(((long) localRepository.getNamespaceId()) << 56L));
		((org.eclipse.emf.cdo.server.internal.db.DBStore) dbStore).setIdHandler(idHandler);
		
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(IRepository.Props.OVERRIDE_UUID, localRepository.getUuid());
		properties.put(IRepository.Props.SUPPORTING_AUDITS, "true");
		properties.put(IRepository.Props.SUPPORTING_BRANCHES, "true");

		final org.eclipse.emf.cdo.internal.server.Repository repository = (org.eclipse.emf.cdo.internal.server.Repository) CDOServerUtils.createRepository(
				"replicated"+localRepository.getUuid(),
				dbStore,
				properties,
				FilteringErrorLoggingStrategy.INSTANCE);
		
		// disable revision cache by using a NOOP instance
		repository.setRevisionManager((InternalCDORevisionManager) CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
		repository.setInitialPackages(localRepository.getEPackages());

		// start the remote repository
		CDOServerUtil.addRepository(IPluginContainer.INSTANCE, repository);
		
		return repository; 
	}

	public static MigrateRequestBuilder builder(String remoteLocation) {
		return new MigrateRequestBuilder(remoteLocation);
	}

}
