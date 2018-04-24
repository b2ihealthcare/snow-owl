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
package com.b2international.snowowl.datastore.server;

import static com.b2international.commons.collections.Collections3.forEach;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.core.api.NsUri.TO_STRING_FUNCTION;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.commons.collections.Procedure;
import com.b2international.commons.emf.NsUriProvider;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOContainer;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.google.common.base.Supplier;

/**
 * Application specific repository dependent namespace URI provider implementation.
 *
 */
public abstract class NsUriProviderImpl implements NsUriProvider {

	private Supplier<Set<String>> nsUriSupplier;
	private Supplier<Set<String>> resourceURISupplier;

	public NsUriProviderImpl() {
		nsUriSupplier = memoize(new Supplier<Set<String>>() {
			public Set<String> get() {
				return initNsURIs();
			}
		});
		resourceURISupplier = memoize(new Supplier<Set<String>>() {
			public Set<String> get() {
				return initResourceURIs();
			}
		});
	}

	@Override
	public Set<String> getNsURIs() {
		return nsUriSupplier.get();
	}

	@Override
	public Set<String> getResourceURIs() {
		return resourceURISupplier.get();
	}

	/**Returns with the UUID of the associated repository.*/
	protected abstract String getRepositoryUuid();

	private Set<String> initNsURIs() {
		return unmodifiableSet(newHashSet(transform(getContainer().getNsUris(getRepository()), TO_STRING_FUNCTION)));
	}

	private Set<String> initResourceURIs() {
		final CDOTransaction transaction = getConnection().createTransaction(BranchPathUtils.createMainPath());
		try {
			final Set<String> resourceUris = newHashSet();
			forEach(transaction.getRootResource().getContents(), new Procedure<EObject>() {
				
				@Override
				protected void doApply(final EObject object) {
					if (object instanceof CDOResource) {
						resourceUris.add(((CDOResource) object).getURI().toString());
					}
				}
			});
			return resourceUris;
		} finally {
			LifecycleUtil.deactivate(transaction);
		}
	}

	private ICDOConnection getConnection() {
		return getServiceForClass(ICDOConnectionManager.class).getByUuid(getRepositoryUuid());
	}
	
	private ICDORepository getRepository() {
		return getContainer().getByUuid(getRepositoryUuid());
	}

	@SuppressWarnings("unchecked")
	private CDOContainer<ICDORepository> getContainer() {
		return (CDOContainer<ICDORepository>) getServiceForClass(ICDORepositoryManager.class);
	}

}