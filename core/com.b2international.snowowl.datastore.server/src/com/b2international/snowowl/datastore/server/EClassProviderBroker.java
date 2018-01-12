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

import javax.annotation.Nullable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.ClassUtils;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Broker for registered {@link IEClassProvider} instances.
 * @see IEClassProvider
 */
public enum EClassProviderBroker {

	INSTANCE;

	/**The singleton instance.*/
	private ArrayListMultimap<String, IEClassProvider> providers;
	

	/**
	 * Returns with the {@link EClass} of an object identified by its unique {@link CDOID}. May return with {@code null}.
	 * @param branchPath the branch path.
	 * @param cdoId the unique CDO ID.
	 * @return the {@link EClass} of an object.
	 */
	@Nullable private EClass getEClass(final IBranchPath branchPath, final long storageKey) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(storageKey);
		final String repositoryUuid = connection.getUuid();
		
		return ApplicationContext
				.getInstance()
				.getService(RepositoryManager.class)
				.get(repositoryUuid)
				.service(RevisionIndex.class)
				.read(branchPath.getPath(), searcher -> {
					for (final IEClassProvider provider : getProviders().get(repositoryUuid)) {
						final EClass eClass = provider.getEClass(searcher, storageKey);
						if (null != eClass) {
							return eClass;
						}
					}
					
					// look for the storageKey as generic CodeSystem or CodeSystemVersion doc
					CodeSystemEntry codeSystem = searcher.searcher().get(CodeSystemEntry.class, Long.toString(storageKey));
					if (codeSystem != null) {
						return TerminologymetadataPackage.Literals.CODE_SYSTEM;
					}
					
					CodeSystemVersionEntry version = searcher.searcher().get(CodeSystemVersionEntry.class, Long.toString(storageKey));
					if (version != null) {
						return TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION;
					}
					
					return null;
				});
	} 

	/**
	 * Returns with the {@link EClass} of an object identified by its unique {@link CDOID}. May return with {@code null}.
	 * @param branchPath the branch path.
	 * @param cdoId the unique CDO ID.
	 * @return the {@link EClass} of an object.
	 */
	@Nullable public EClass getEClass(final IBranchPath branchPath, final CDOID cdoId) {
		return getEClass(Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null."), CDOIDUtils.asLongSafe(cdoId));
	} 
	
	/*returns with a map of providers. keys repository UUIDs values are the providers*/
	private Multimap<String, IEClassProvider> getProviders() {
		
		if (null == providers) {
			
			synchronized (EClassProviderBroker.class) {
				
				if (null == providers) {
					
					providers = ArrayListMultimap.<String, IEClassProvider>create();
					
					for (final IConfigurationElement element : Platform.getExtensionRegistry().getConfigurationElementsFor(ECLASS_PROVIDER_EXTENSION_ID)) {
						
						try {

							final Object executableExtension = element.createExecutableExtension(CLASS_ATTRIBUTE);
							final IEClassProvider provider = ClassUtils.checkAndCast(executableExtension, IEClassProvider.class);
							final String repositoryUuid = provider.getRepositoryUuid();
							
							providers.put(repositoryUuid, provider);
						
						} catch (final CoreException e) {
							throw new SnowowlRuntimeException("Cannot instantiate EClass provider." + element.getAttribute(CLASS_ATTRIBUTE)); 
						}
						
						
					}
					
				}
				
			}
		}
		
		return providers;
	}

	private static final String ECLASS_PROVIDER_EXTENSION_ID = "com.b2international.snowowl.datastore.server.eclassProvider";
	private static final String CLASS_ATTRIBUTE = "class";
	
}