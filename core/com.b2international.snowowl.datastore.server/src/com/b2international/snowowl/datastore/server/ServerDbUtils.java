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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.collections.Procedure;
import com.b2international.commons.db.JdbcUrl;
import com.b2international.commons.db.JdbcUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Server side utility class for the object-relational backend.
 */
public class ServerDbUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CDOServerUtils.class);
	/**
	 * The unique name of the index for the CDO_CREATED column.
	 * <br>Name: {@value}.
	 */
	private static final String CDO_CREATED_IDX = "CDO_CREATED_IDX";
	
	public static long getClassMetaID(final EClass eClass) {
		
		final ICDORepositoryManager repositoryManager = ApplicationContext.getInstance().getService(ICDORepositoryManager.class);
		final ICDORepository cdoRepository = repositoryManager.get(eClass);
		final IDBStore dbStore = cdoRepository.getDbStore();		
		final IRepository repository = cdoRepository.getRepository();
		
		final ISession session = repository.getSessionManager().getSessions()[0];
		StoreThreadLocal.setSession((InternalSession) session);

		final long lastCommitTime = dbStore.getLastCommitTime();
		return CDOIDUtil.getLong(dbStore.getMetaDataManager().getMetaID(eClass, lastCommitTime));
	}
	
	/**
	 * Creates an index for the CDO_CREATED DB column for all tables mapped to the specified package.
	 * <br>This method, first, tried to drop the existing CDO_CREATED index, if any.
	 * @param nsUri the unique namespace URI for the package containing all classifiers that are mapped to an ORM backend.
	 * @param otherNsUris additional packages.
	 */
	public static void createCdoCreatedIndexOnTables(final ICDORepository repository) {

		Preconditions.checkNotNull(repository, "Repository argument cannot be null.");

		final InternalRepository internalRepository = (InternalRepository) repository.getRepository();
		final InternalStore store = internalRepository.getStore();

		if (store instanceof IDBStore) {

			try {

				final IDBStore dbStore = (IDBStore) store;
				final IDBStoreAccessor accessor = dbStore.getWriter(null);

				//set accessor on thread local
				StoreThreadLocal.setAccessor(accessor);

				final InternalCDOPackageRegistry packageRegistry = internalRepository.getPackageRegistry();

				final Set<String> nsUris = Sets.newHashSet(Iterables.transform(Arrays.asList(((CDORepository) repository).getEPackages()), new Function<EPackage, String>() {
					@Override public String apply(final EPackage ePackage) {
						return Preconditions.checkNotNull(ePackage).getNsURI();
					}
				}));

				//process namespace URIs
				for (final String _nsUri : nsUris) {
					final EPackage _package = packageRegistry.getEPackage(_nsUri);

					if (null == _package) {
						LOGGER.warn("Cannot found package in registry for '" + _nsUri + "'.");
						continue;
					} else {

						for (final EClassifier classifier : _package.getEClassifiers()) {

							if (classifier instanceof EClass) {

								final EClass eclass = (EClass) classifier;

								if (eclass.isAbstract()) {
									continue; //abstract classes are not mapped
								}

								if (eclass.isInterface()) {
									continue; //interfaces are not mapped
								}

								if (CDOModelUtil.isCorePackage(_package)) {
									continue; //do not create indexes and class mapping for ecore packages
								}
								
								final IClassMapping classMapping = dbStore.getMappingStrategy().getClassMapping(eclass);
								for (final IDBTable table : classMapping.getDBTables()) {

									@SuppressWarnings("restriction")
									final IDBField createdField = table.getField(org.eclipse.emf.cdo.server.internal.db.CDODBSchema.ATTRIBUTES_CREATED);

									if (null != createdField) {

										Connection connection = null;
										PreparedStatement createStatement = null;
										PreparedStatement dropStatement = null;
										
										final String tableName = table.getName().toUpperCase();
										final String indexName = tableName + "_" + CDO_CREATED_IDX;
										final String createdFieldName = createdField.getName().toUpperCase();
										
										try {

											connection = repository.getConnection();

											Integer indexCount = JdbcUtils.executeIntQuery(connection, "SELECT COUNT(1) "
													+ "FROM information_schema.statistics "
													+ "WHERE TRUE"
													+ "AND index_schema = ? "
													+ "AND index_name = ?", connection.getSchema(), indexName); 
															
											if (indexCount < 1) {
												LOGGER.info("Creating index '" + indexName + "' on table '" + tableName + "'.");
												final String createIndexSql = "CREATE INDEX " + indexName + " ON " + tableName + "(" + createdFieldName + ")";
												JdbcUtils.executeUpdate(connection, createIndexSql);
											}
											
										} catch (final SQLException e) {

											LOGGER.error("Cannot create index for " + tableName);
											throw new SnowowlRuntimeException(e);

										} finally {
											DBUtil.close(connection);
											DBUtil.close(dropStatement);
											DBUtil.close(createStatement);
										}
									}
								}
							}
						}
					}
				}

			} finally {
				StoreThreadLocal.release();
			}

		} else {
			LOGGER.warn("CDO store is not backed by a object/relational mapper.");
		}

		return;

	}
	
	/**
	 * Creates an index for the CDO_CREATED DB column for all tables mapped to the specified package.
	 * <br>This method, first, tried to drop the existing CDO_CREATED index, if any.
	 * @param nsUri the unique namespace URI for the package containing all classifiers that are mapped to an ORM backend.
	 * @param otherNsUris additional packages.
	 */
	public static void createCdoCreatedIndexOnTables(final String nsUri, final String... otherNsUris) {
		Preconditions.checkNotNull(nsUri, "Namespace URI argument cannot be null.");
		
		final Set<ICDORepository> repositories = Sets.newHashSet(Iterables.transform(Lists.asList(nsUri, otherNsUris), new Function<String, ICDORepository>() {
			@Override public ICDORepository apply(final String _nsUri) {
				return getRepository(_nsUri);
			}
		}));

		Collections3.forEach(repositories, new Procedure<ICDORepository>() {
			@Override protected void doApply(final ICDORepository repository) {
				createCdoCreatedIndexOnTables(repository);
			}
		});
	}
	
	public static Connection createConnection(final EPackage ePackage, final RepositoryConfiguration configuration) {
		
		Preconditions.checkNotNull(ePackage, "EClass argument cannot be null.");
		
		final String driverName = configuration.getDatabaseConfiguration().getDriverClass();
		final JdbcUrl jdbcUrl = configuration.getDatabaseUrl();
		final String username = configuration.getDatabaseConfiguration().getUsername();
		final String password = configuration.getDatabaseConfiguration().getPassword();
		
		final ICDORepositoryManager repositoryManager = ApplicationContext.getInstance().getService(ICDORepositoryManager.class);
		final String repositoryUuid = repositoryManager.get(ePackage).getUuid();
		
		final String localDbUrl = jdbcUrl.build(repositoryUuid);
		
		return JdbcUtils.createConnection(driverName, localDbUrl, username, password);
	}
	
	/*returns with the repository instance*/
	private static ICDORepository getRepository(final String nsUri) {
		Preconditions.checkNotNull(nsUri, "Namespace URI argument cannot be null.");
		final ICDORepositoryManager repositoryManager = ApplicationContext.getInstance().getService(ICDORepositoryManager.class);
		return repositoryManager.get(nsUri);
		
	}		
	
}
