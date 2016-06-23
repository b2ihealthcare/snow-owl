/*
 * Copyright 2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.importer.rf2.indexsynchronizer.IndexMigrationReplicationContext;

/**
 * OSGi command-line command to recreate the Lucene index store 
 * from the transactional CDO store.  The idea relies on a non-committing replication
 * that triggers the change processors to write records into the lucene store.
 */
@SuppressWarnings("restriction")
public class ReindexCommand extends AbstractRf2ImporterCommand {


	public ReindexCommand() {
		super("recreateindex", "", "recreates the index from the CDO store.", new String[] {});
	}

	@Override
	public void execute(final CommandInterpreter interpreter) {
		
		ICDORepositoryManager repositoryManager = ApplicationContext.getServiceForClass(ICDORepositoryManager.class);
		ICDORepository cdoRepository = repositoryManager.getByUuid(SnomedDatastoreActivator.REPOSITORY_UUID);
		Repository repository = (Repository) cdoRepository.getRepository();
		
		InternalSession session = repository.getSessionManager().openSession(null);
		
		//set the session on the StoreThreadlocal for later access
		StoreThreadLocal.setSession(session);
		
		//for partial replication get the last branch id and commit time from the index
		//right now index is fully recreated
		repository.replicate(new IndexMigrationReplicationContext(-1, 0, session));
		
		StoreThreadLocal.release();
		session.close();
	}
		
}