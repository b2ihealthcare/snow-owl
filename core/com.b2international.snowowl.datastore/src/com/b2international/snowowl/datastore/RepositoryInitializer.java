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
package com.b2international.snowowl.datastore;

import static com.b2international.snowowl.datastore.server.ServerDbUtils.createCdoCreatedIndexOnTables;

import java.util.Collection;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemCreateRequestBuilder;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;

/**
 * Abstract superclass for repository initializer implementations.
 */
public abstract class RepositoryInitializer implements IRepositoryInitializer {

	protected static final Logger LOGGER = LoggerFactory.getLogger("repo-initializer");

	@Override
	public void initialize(ICDORepository repository) {
		LifecycleUtil.checkActive(repository);

		String repositoryUuid = repository.getUuid();
		ICDOConnectionManager connectionManager = ApplicationContext.getServiceForClass(ICDOConnectionManager.class);
		ICDOConnection connection = connectionManager.getByUuid(repositoryUuid);
		String userId = connection.getSession().getUserID();
		
		CDOTransaction transaction = null;

		try {
			transaction = connection.createTransaction(BranchPathUtils.createMainPath());
			checkRootResources(transaction, getResourceNames());
			checkIndexes(repository);
			checkCodeSystem(userId, repositoryUuid);
			checkContent(userId, repositoryUuid, transaction);
		} catch (CommitException e) {
			LOGGER.error("Failed to initialize repository {} at startup.", repositoryUuid, e);
		} finally {
			LifecycleUtil.deactivate(transaction);
		}
	}

	private void checkRootResources(CDOTransaction transaction, Collection<String> resourcePaths) throws CommitException {
		transaction.rollback();
		
		for (final String path : resourcePaths) {
			transaction.getOrCreateResource(path);
		}

		if (transaction.isDirty()) {
			transaction.setCommitComment("Create terminology and metadata content storage for repository");
			transaction.commit();
		}
	}

	private void checkIndexes(ICDORepository repository) {
		if (shouldCreateDbIndexes()) {
			createCdoCreatedIndexOnTables(repository);
		}
	}

	private void checkCodeSystem(String userId, String repositoryUuid) {
		IEventBus bus = ApplicationContext.getServiceForClass(IEventBus.class);

		CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
				.filterById(getPrimaryCodeSystemShortName())
				.setLimit(0)
				.build(repositoryUuid)
				.execute(bus)
				.getSync();

		if (codeSystems.getTotal() < 1) {
			prepareNewPrimaryCodeSystem()
				.setShortName(getPrimaryCodeSystemShortName())
				.setRepositoryUuid(repositoryUuid)
				.build(repositoryUuid, Branch.MAIN_PATH, userId, "Create primary code system for repository")
				.execute(bus)
				.getSync();
		}
	}
	
	protected void checkContent(String userId, String repositoryUuid, CDOTransaction transaction) throws CommitException {
		return;
	}

	protected abstract Collection<String> getResourceNames();

	protected abstract String getPrimaryCodeSystemShortName();

	protected abstract CodeSystemCreateRequestBuilder prepareNewPrimaryCodeSystem();

	/**
	 * Returns {@code true} if the DB table indexes should be created on the repository startup, otherwise {@code false}.
	 * <p>This method returns with {@code true} by default, subclasses may override this method.
	 * @return {@code true} if the DB indexes should be created by the current {@link RepositoryInitializer repository initializer}
	 * otherwise {@code false}.
	 */
	protected boolean shouldCreateDbIndexes() {
		return true;
	}

}
