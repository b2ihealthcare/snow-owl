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
package com.b2international.snowowl.server.console;

import java.util.Collection;

import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.internal.server.syncing.RepositorySynchronizer;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ApplicationContext.ServiceRegistryEntry;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.ServerDbUtils;
import com.b2international.snowowl.datastore.server.internal.InternalRepository;

/**
 * OSGI command contribution with Snow Owl commands.
 * 
 *
 */
public class MaintenanceCommandProvider implements CommandProvider {

	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl commands---\n");
//		buffer.append("\tsnowowl test - Execute Snow Owl server smoke test\n");
		buffer.append("\tsnowowl checkservices - Checks the core services presence\n");
		buffer.append("\tsnowowl dbcreateindex [nsUri] - creates the CDO_CREATED index on the proper DB tables for all classes contained by a package identified by its unique namspace URI\n");
		buffer.append("\tsnowowl recreateindex - recreates the index from the CDO store.");
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with "_".
	 * @param interpreter
	 */
	public void _snowowl(CommandInterpreter interpreter) {
		try {
			String cmd = interpreter.nextArgument();
			
			if ("checkservices".equals(cmd)) {
				checkServices(interpreter);
				return;
			}
			
//			if ("test".equals(cmd)) {
//				test(interpreter);
//				return;
//			}
			
			if ("dbcreateindex".equals(cmd)) {
				executeCreateDbIndex(interpreter);
				return;
			}
			
			if ("recreateindex".equals(cmd)) {
				executeRecreateIndex(interpreter);
				return;
			}
			
			interpreter.println(getHelp());
		} catch (Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}

	public synchronized void executeCreateDbIndex(CommandInterpreter interpreter) {
		
		String nsUri = interpreter.nextArgument();
		if (null != nsUri) {
			ServerDbUtils.createCdoCreatedIndexOnTables(nsUri);
		} else {
			interpreter.print("Namespace URI should be specified.");
		}
	}
	
	@SuppressWarnings("restriction")
	public synchronized void executeRecreateIndex(CommandInterpreter interpreter) throws InterruptedException {
		
		String repositoryName = "snomedStore";
		
		ICDORepositoryManager repositoryManager = ApplicationContext.getServiceForClass(ICDORepositoryManager.class);
		ICDOConnectionManager connectionManager = ApplicationContext.getServiceForClass(ICDOConnectionManager.class);
		
		
		RepositorySynchronizer synchronizer = new RepositorySynchronizer();
		ICDORepository cdoRepository = repositoryManager.getByUuid(repositoryName);
		IRepository repository = cdoRepository.getRepository();
		ICDOConnection cdoConnection = connectionManager.getByUuid(repositoryName);
		final CDONet4jSessionConfiguration sessionConfiguration = cdoConnection.getSessionConfiguration();
		synchronizer.setRemoteSessionConfigurationFactory(new CDOSessionConfigurationFactory() {
			
			@Override
			public CDOSessionConfiguration createSessionConfiguration() {
				return sessionConfiguration;
			}
		});

		//replicate commits as opposed to raw lines
		synchronizer.setRawReplication(false);
		SnowOwlDummyInternalRepository localRepository = new SnowOwlDummyInternalRepository();
		synchronizer.setLocalRepository(localRepository);
		synchronizer.activate();
		
		//do the work, wait until it finishes
		do {
			Thread.sleep(10000);
		} while (localRepository.getState() == State.ONLINE);
		
		synchronizer.deactivate();
	}
	
	public synchronized void checkServices(CommandInterpreter ci) {
		
		ci.println("Checking core services...");
		
		try {
			
			Collection<ServiceRegistryEntry<?>> services = ApplicationContext.getInstance().checkServices();
			for (ServiceRegistryEntry<?> entry : services) {
				ci.println("Interface: " + entry.getServiceInterface() + " : " + entry.getImplementation());
			}
			ci.println("Core services are registered properly and available for use.");
			
		} catch (final Throwable t) {
			
			ci.print("Error: " + t.getMessage());
			
		}
		
	}
	
//	/**
//	 * OSGi console contribution, the test touches cdo and index stores.
//	 * @param ci
//	 */
//	public synchronized void test(CommandInterpreter ci) {
//
//		ci.println("Smoke testing the Snow Owl server....");
//		SnomedConceptIndexEntry rootConceptMini = null;
//		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
//		Collection<SnomedConceptIndexEntry> rootConcepts = terminologyBrowser.getRootConcepts();
//		for (SnomedConceptIndexEntry rootConcept : rootConcepts) {
//			rootConceptMini = rootConcept;
//			ci.println(" Root concept from the semantic cache: " + rootConcept);
//		}
//
//		ci.println(" Semantic cache size: " + terminologyBrowser.getConceptCount());
//		SnomedClientIndexService indexSearcher = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
//
//		SnomedConceptFullQueryAdapter adapter = new SnomedConceptFullQueryAdapter(rootConceptMini.getId(), SnomedConceptFullQueryAdapter.SEARCH_BY_CONCEPT_ID);
//		List<SnomedConceptIndexEntry> search = indexSearcher.search(adapter);
//		ci.println(" Root concept from the index store: " + search.get(0).getLabel());
//
//		SnomedEditingContext editingContext = null;
//		try {
//			editingContext = new SnomedEditingContext();
//			Concept rootConcept = new SnomedConceptLookupService().getComponent(rootConceptMini.getId(), editingContext.getTransaction());
//			ci.println(" Root concept from the main repository: " + rootConcept.getFullySpecifiedName());
//		} finally {
//			editingContext.close();
//		}
//	}
}