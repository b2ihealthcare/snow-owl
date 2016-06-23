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
package com.b2international.snowowl.snomed.importer.rf2.command;

import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.syncing.RepositorySynchronizer;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.snomed.importer.rf2.indexsynchronizer.SnowOwlLuceneIndexRepository;

/**
 *
 */
public class ReindexCommand extends AbstractRf2ImporterCommand {


	public ReindexCommand() {
		super("i", "", "recreates the index from the CDO store.", new String[] {});
	}

	@SuppressWarnings("restriction")
	@Override
	public void execute(final CommandInterpreter interpreter) {
		String repositoryName = "snomedStore";
		
		ICDORepositoryManager repositoryManager = ApplicationContext.getServiceForClass(ICDORepositoryManager.class);
		ICDORepository cdoRepository = repositoryManager.getByUuid(repositoryName);
		Repository repository = (Repository) cdoRepository.getRepository();
		
		InternalSession session = repository.getSessionManager().openSession(null);
		StoreThreadLocal.setSession(session);
		
		//for partial replication get the last branch id and commit time from the index
		repository.replicate(new IndexMigrationReplicationContext(-1, 0, session));
		
		StoreThreadLocal.release();
		session.close();
	}
		
	@SuppressWarnings("restriction")
	public void execute2(final CommandInterpreter interpreter) {
		
		
		String repositoryName = "snomedStore";
		ICDOConnectionManager connectionManager = ApplicationContext.getServiceForClass(ICDOConnectionManager.class);

		RepositorySynchronizer synchronizer = new RepositorySynchronizer();
		ICDOConnection cdoConnection = connectionManager.getByUuid(repositoryName);
		final CDONet4jSessionConfiguration sessionConfiguration = cdoConnection.getSessionConfiguration();
		
		// replicate commits as opposed to raw lines
		synchronizer.setRawReplication(false);
		synchronizer.setRemoteSessionConfigurationFactory(new CDOSessionConfigurationFactory() {

			@Override
			public CDOSessionConfiguration createSessionConfiguration() {
				
				CDONet4jSessionConfiguration newNet4jSessionConfiguration = CDONet4jUtil.createNet4jSessionConfiguration();
				
				//copy the configuration over from the available configuration
				newNet4jSessionConfiguration.setSignalTimeout(10000);
				newNet4jSessionConfiguration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
				
				newNet4jSessionConfiguration.getAuthenticator().setCredentialsProvider(sessionConfiguration.getAuthenticator().getCredentialsProvider());
				newNet4jSessionConfiguration.setRepositoryName(sessionConfiguration.getRepositoryName());
				
				//IJVMConnector connector = JVMUtil.getConnector(PluginContainer.INSTANCE, Net4jUtils.NET_4_J_CONNECTOR_NAME);
				newNet4jSessionConfiguration.setConnector(sessionConfiguration.getConnector());
				newNet4jSessionConfiguration.setStreamWrapper(sessionConfiguration.getStreamWrapper());
				return newNet4jSessionConfiguration;
			}
		});
		
		SnowOwlLuceneIndexRepository localRepository = new SnowOwlLuceneIndexRepository();
		synchronizer.setLocalRepository(localRepository);
		System.out.println("Activating the synchronizer");
		synchronizer.activate();

		// do the work, wait until it finishes
//		do {
//			Thread.sleep(10000);
//		} while (localRepository.getState() != State.ONLINE);
//
//		synchronizer.deactivate();
	}
}