/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.ecore.EObject;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.google.inject.Provider;

/**
 * @since 4.6
 */
public class ImportOnlySnomedTransactionContext implements TransactionContext {

	private final String userId;
	private final SnomedEditingContext editingContext;
	private final RevisionSearcher searcher;
	private Branch branch;

	public ImportOnlySnomedTransactionContext(final String userId, final RevisionSearcher searcher, final SnomedEditingContext editingContext) {
		this.userId = userId;
		this.searcher = searcher;
		this.editingContext = editingContext;
	}

	@Override
	public String userId() {
		return userId;
	}
	
	@Override
	public Branch branch() {
		if (null == branch) {
			branch = RepositoryRequests
						.branching()
						.prepareGet(editingContext.getBranch())
						.build(SnomedDatastoreActivator.REPOSITORY_UUID)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync();
		}
		return branch;
	}
	
	@Override
	public String branchPath() {
		return this.editingContext.getBranch();
	}
	
	@Override
	public SnowOwlConfiguration config() {
		return SnowOwlApplication.INSTANCE.getConfiguration();
	}

	@Override
	public String id() {
		// FIXME hardcoded ID
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
	@Override
	public Health health() {
		return Health.GREEN;
	}

	@Override
	public <T> T service(final Class<T> type) {
		if (type.isAssignableFrom(RevisionSearcher.class)) {
			return type.cast(searcher);
		} else if (type.isAssignableFrom(SnomedEditingContext.class)) {
			return type.cast(editingContext);
		}
		return ApplicationContext.getInstance().getServiceChecked(type);
	}

	@Override
	public <T> Provider<T> provider(final Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws Exception {
		editingContext.close();
	}

	@Override
	public void add(final EObject o) {
		editingContext.add(o);
	}

	@Override
	public void delete(final EObject o) {
		editingContext.delete(o);
	}
	
	@Override
	public void delete(EObject o, boolean force) {
		editingContext.delete(o, force);
	}

	@Override
	public void preCommit() {
		editingContext.preCommit();
	}

	@Override
	public long commit() {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public long commit(final String userId, final String commitComment, final String parentContextDescription) {
		try {
			final CDOCommitInfo info = new CDOServerCommitBuilder(userId, commitComment, editingContext.getTransaction())
					.parentContextDescription(parentContextDescription)
					.commitOne();
			return info.getTimeStamp();
		} catch (final CommitException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	@Override
	public void rollback() {
		editingContext.rollback();
	}

	@Override
	public <T extends EObject> T lookup(final String componentId, final Class<T> type) {
		return editingContext.lookup(componentId, type);
	}
	
	@Override
	public <T extends EObject> T lookupIfExists(String componentId, Class<T> type) {
		return editingContext.lookupIfExists(componentId, type);
	}
	
	@Override
	public <T extends CDOObject> Map<String, T> lookup(Collection<String> componentIds, Class<T> type) {
		return editingContext.lookup(componentIds, type);
	}
	
	@Override
	public void clearContents() {
		editingContext.clearContents();
	}

	public SnomedEditingContext getEditingContext() {
		return editingContext;
	}
	
	public String getDefaultLanguageRefsetId() {
		return editingContext.getLanguageRefSetId();
	}

	public String getDefaultLanguageCode() {
		return editingContext.getDefaultLanguageCode();
	}
	
	@Override
	public boolean isNotificationEnabled() {
		return true;
	}
	
	@Override
	public void setNotificationEnabled(boolean notificationEnabled) {
		throw new UnsupportedOperationException();
	}
	
	public SnomedCoreConfiguration getSnomedCoreConfig() {
		return config().getModuleConfig(SnomedCoreConfiguration.class);
	}
}
