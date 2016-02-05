/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.inject.Provider;

/**
 * @since 4.6
 */
public class FakeSnomedTransactionContext implements TransactionContext {

	private final SnomedEditingContext editingContext;
	private final SnomedIdentifiers snomedIdentifiers;

	public FakeSnomedTransactionContext(final SnomedEditingContext editingContext) {
		this.editingContext = editingContext;
		final ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class);
		snomedIdentifiers = new SnomedIdentifiers(identifierService);
	}
	
	@Override
	public Branch branch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SnowOwlConfiguration config() {
		return SnowOwlApplication.INSTANCE.getConfiguration();
	}

	@Override
	public String id() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T service(final Class<T> type) {
		if (type.isAssignableFrom(SnomedIdentifiers.class)) {
			return type.cast(snomedIdentifiers);
		}
		return ApplicationContext.getInstance().getServiceChecked(type);
	}

	@Override
	public <T> Provider<T> provider(final Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(final EObject o) {
		editingContext.add(o);
	}

	@Override
	public void delete(final EObject o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void preCommit() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long commit(final String userId, final String commitComment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rollback() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EObject> T lookup(final String componentId, final Class<T> type) {
		return editingContext.lookup(componentId, type);
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
	
	public SnomedCoreConfiguration getSnomedCoreConfig() {
		return config().getModuleConfig(SnomedCoreConfiguration.class);
	}
}
