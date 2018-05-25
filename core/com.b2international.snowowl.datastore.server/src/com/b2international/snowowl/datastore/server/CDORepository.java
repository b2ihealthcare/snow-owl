/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.commons.StringUtils.isEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.ContainerQueryHandlerProvider;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ReflectionUtils;
import com.b2international.snowowl.core.api.NsUri;
import com.b2international.snowowl.datastore.cdo.CDOContainer;
import com.b2international.snowowl.datastore.cdo.CDOManagedItem;
import com.b2international.snowowl.datastore.cdo.FilteringErrorLoggingStrategy;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.google.common.base.Preconditions;

/**
 * Provides access to the underlying CDO repository and manages its lifecycle.
 *
 *
 */
public class CDORepository extends CDOManagedItem<ICDORepository> implements ICDORepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(CDORepository.class);

	private final CDOServerChangeManager changeManager;
	private final RepositoryConfiguration configuration;
	private InternalRepository repository;

	public CDORepository(final String repositoryUuid, @Nullable final String repositoryName, final byte namespaceId, @Nullable final String toolingId, 
			final RepositoryConfiguration configuration, @Nullable final String dependsOnRepositoryUuid, final boolean meta) {
		
		super(repositoryUuid, repositoryName, namespaceId, toolingId, dependsOnRepositoryUuid, meta);
		this.configuration = checkNotNull(configuration, "configuration");
		this.changeManager = new CDOServerChangeManager(repositoryUuid, isEmpty(repositoryName) ? repositoryUuid : repositoryName);
	}

	@Override
	public IDBStore getDbStore() {
		return (IDBStore) repository.getStore();
	}

	@Override
	public IRepository getRepository(){
		return repository;
	}

	@Override
	public Connection getConnection() {
		return getDbStore().getConnection();
	}
	
	@Override
	public CDOPackageRegistry getPackageRegistry() {
		return repository.getPackageRegistry();
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} [{1}]", getRepositoryName(), getUuid());
	}

	/**
	 * (non-API)
	 *
	 * Returns with all {@link EPackage}s (including all dependent {@link EPackage}s as well) associated
	 * with the current {@link ICDORepository repository} instance.
	 * @return an array of the associated {@link EPackage}s.
	 *
	 */
	public EPackage[] getEPackages() {

		final Iterable<NsUri> nsUris = ((CDOContainer<ICDORepository>) getContainer()).getNsUris(this);
		final Set<EPackage> ePackageSet = newHashSet();

		for (final NsUri nsUri : nsUris) {
			final EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsUri.getNsUri());
			if (ePackageSet.add(ePackage)) {
				EMFUtil2.collectDependencies(ePackage, ePackageSet);
			}
		}

		return ePackageSet.toArray(new EPackage[ePackageSet.size()]);
	}

	@SuppressWarnings("restriction")
	@Override
	protected void doActivate() throws Exception {
		final IDBConnectionProvider connectionProvider = createConnectionProvider(configuration.getDatasourceProperties(getUuid()));

		final IDBStore dbStore = createDBStore(connectionProvider, configuration);
		final IIDHandler idHandler =
				new org.eclipse.emf.cdo.server.internal.db.LongIDHandler((org.eclipse.emf.cdo.server.internal.db.DBStore) dbStore);

		idHandler.setLastObjectID(CDOIDUtil.createLong(((long) getNamespaceId()) << 56L));
		((org.eclipse.emf.cdo.server.internal.db.DBStore) dbStore).setIdHandler(idHandler);

		repository = createRepository(IPluginContainer.INSTANCE, dbStore);
		setReaderPoolCapacity(configuration.getReaderPoolCapacity());
		setWriterPoolCapacity(configuration.getWriterPoolCapacity());
		
		// disable revision cache by using a NOOP instance
		if (configuration.isRevisionCacheEnabled()) {
			EvictingRevisionCache cache = new EvictingRevisionCache();
			repository.setRevisionManager((InternalCDORevisionManager) CDORevisionUtil.createRevisionManager(cache));
		} else {
			repository.setRevisionManager((InternalCDORevisionManager) CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
		}
		
		//set packages to create tables in DB
		repository.setInitialPackages(getEPackages());

		CDOServerUtil.addRepository(IPluginContainer.INSTANCE, repository); // Start the CDO repository

		Preconditions.checkState(getUuid().equals(repository.getName()),
				"Repository name mismatch. Expected: " + getUuid() + " was: " + repository.getName());

		LOGGER.info(this + " successfully started.");

		repository.addHandler(changeManager);

	}

	@Override
	protected void doDeactivate() throws Exception {
		LifecycleUtil.deactivate(repository);
	}

	public static IDBConnectionProvider createConnectionProvider(final Map<Object, Object> properties) {
		final DataSource dataSource = DBUtil.createDataSource(properties, "");
		return DBUtil.createConnectionProvider(dataSource);
	}

	public static IDBStore createDBStore(final IDBConnectionProvider connectionProvider, RepositoryConfiguration configuration) {

		// with ranges, audit and branching
		final IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true, true);

		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(IMappingStrategy.PROP_QUALIFIED_NAMES, "true");
		mappingStrategy.setProperties(properties);

		final String databaseType = configuration.getDatabaseConfiguration().getType();
		final IDBAdapter dbAdapter = DBUtil.getDBAdapter(databaseType);
		CheckUtil.checkState(dbAdapter, "DB adapter not found for id: " + databaseType);
	    return (DBStore) CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);
	}
	
	@Override
	public void setReaderPoolCapacity(int capacity) {
		StoreAccessorPool readerPool = ReflectionUtils.getField(DBStore.class, (DBStore) getDbStore(), "readerPool");
		readerPool.setCapacity(capacity);
		LOGGER.info("Setting {}.readerPoolCapacity to {}", getUuid(), capacity);
	}
	
	@Override
	public void setWriterPoolCapacity(int capacity) {
		StoreAccessorPool writerPool = ReflectionUtils.getField(DBStore.class, (DBStore) getDbStore(), "writerPool");
		writerPool.setCapacity(capacity);
		LOGGER.info("Setting {}.writerPoolCapacity to {}", getUuid(), capacity);
	}

	private InternalRepository createRepository(final IManagedContainer container, final IDBStore dbStore) {

		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(IRepository.Props.OVERRIDE_UUID, getUuid());
		properties.put(IRepository.Props.SUPPORTING_AUDITS, "true");
		properties.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
		properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");

		final InternalRepository repository = (InternalRepository) CDOServerUtils.createRepository(
				getUuid(),
				dbStore,
				properties,
				FilteringErrorLoggingStrategy.INSTANCE);

		final SnowowlSessionManager sessionManager = new SnowowlSessionManager();

		final UserManager userManager = new UserManager();
		userManager.activate();
		((InternalSessionManager)sessionManager).setUserManager(userManager);
		repository.setSessionManager(sessionManager);

		//this is needed in order to execute queries on the repository
		repository.setQueryHandlerProvider(new ContainerQueryHandlerProvider(container));

		LOGGER.info(MessageFormat.format("Starting repository ''{0}'' with JDBC URL ''{1}''.",
				getRepositoryName(), configuration.getDatabaseUrl().build(getUuid())));

		return repository;
	}
	
}