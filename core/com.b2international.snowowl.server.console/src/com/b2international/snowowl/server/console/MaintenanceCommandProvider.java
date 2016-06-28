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

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ApplicationContext.ServiceRegistryEntry;
import com.b2international.snowowl.datastore.server.ServerDbUtils;
import com.b2international.snowowl.datastore.server.reindex.ReindexRequest;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Strings;

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
		buffer.append("\tsnowowl reindex <repositoryId> - reindexes the content for the given repository ID\n");
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
			
			if ("reindex".equals(cmd)) {
				reindex(interpreter);
				return;
			}
			
			interpreter.println(getHelp());
		} catch (Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}

	private void reindex(CommandInterpreter interpreter) {
		final String repositoryId = interpreter.nextArgument();
		
		if (Strings.isNullOrEmpty(repositoryId)) {
			interpreter.println("repositoryId parameter is required");
		}
		try {
			ReindexRequest.builder(repositoryId)
			.create()
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
		} catch (Throwable e) {
			interpreter.printStackTrace(e);
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