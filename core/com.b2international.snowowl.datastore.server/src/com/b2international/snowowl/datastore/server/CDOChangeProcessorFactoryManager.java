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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Class for loading and managing CDO specific change processor factories via extension points.
 * @see CDOChangeProcessorFactory
 */
public enum CDOChangeProcessorFactoryManager {

	/**
	 * The manager instance.
	 */
	INSTANCE;

	/**
	 * Unique identifier of the change processor factory extension point. <br><br>ID: {@value}
	 */
	public static final String  CHANGE_PROCESSOR_FACTORY_EXTENSION_POINT_ID = 
			"com.b2international.snowowl.datastore.changeProcessorFactory";
	
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String ID_ATTRIBUTE = "id";
	private static final String REPOSITORY_NAME_ATTRIBUTE = "repositoryUuid";
	private static final Logger LOGGER = LoggerFactory.getLogger(CDOChangeProcessorFactoryManager.class);
	
	/**Mapping between the repository names (UUIDs) and the associated CDO change processor factories.*/
	private Multimap<String, CDOChangeProcessorFactory> factories;
	
	/**
	 * Returns with the number of all available and registered CDO change processors.
	 * @return the number of CDO change processors registered via Eclipse extension points.
	 */
	public int getCDOChangeProcessorCount() {
		return getFactories().size();
	}

	/**
	 * Returns with a maps of all available {@link CDOChangeProcessorFactory} instances grouped by the repository UUIDs.
	 * @return a collection of change processor factories.
	 */
	public Multimap<String, CDOChangeProcessorFactory> getFactories() {
		if (null == factories) {
			synchronized (CDOChangeProcessorFactoryManager.class) {
				if (null == factories) {
					factories = Multimaps.unmodifiableMultimap(loadFactories());
				}
			}
		}
		return factories;
	}
	
	/**
	 * Returns with a collection of CDO change processor factories working on given repository as a write access handler. 
	 * @param repositoryName the unique name (UUID) of the repository.
	 * @return a collection of CDO change processor factories.
	 */
	public Collection<CDOChangeProcessorFactory> getFactories(final String repositoryName) {
		final Collection<CDOChangeProcessorFactory> $ = getFactories().asMap().get(Preconditions.checkNotNull(repositoryName, "Repository name argument cannot be null."));
		return null == $ ? Collections.<CDOChangeProcessorFactory>emptySet() : Collections.unmodifiableCollection($);
	}

	/*loads the change processor factories via extension points*/
	private Multimap<String, CDOChangeProcessorFactory> loadFactories() {
		final Multimap<String, CDOChangeProcessorFactory> factories = HashMultimap.create();
		for (final IConfigurationElement element : getChangeProcessorFactoryExtensions()) {
			
			final String id = element.getAttribute(ID_ATTRIBUTE);
			final String repositoryName = element.getAttribute(REPOSITORY_NAME_ATTRIBUTE);
			Preconditions.checkNotNull(repositoryName, "Repository name should be specified for CDO change processor factory. ID: " + id);
			
			final CDOChangeProcessorFactory factory = createFactorySafe(element);
			if (null != factory) {
				factories.put(repositoryName, factory);
			}
		}
		
		return factories;
	}

	/*returns with the configuration elements to the change processor extension point*/
	private IConfigurationElement[] getChangeProcessorFactoryExtensions() {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(CHANGE_PROCESSOR_FACTORY_EXTENSION_POINT_ID);
	}
	
	/*creates the executable change processor factories from the specified configuration element. returns with null if error occurred.*/
	private CDOChangeProcessorFactory createFactorySafe(final IConfigurationElement element) {
		checkNotNull(element, "Configuration element argument should not be null.");
		try {
			final Object executableExtension = element.createExecutableExtension(CLASS_ATTRIBUTE);
			if (executableExtension instanceof CDOChangeProcessorFactory) {
				return (CDOChangeProcessorFactory) executableExtension;
			} else {
				throw new Exception("Executable extension should be a CDO change processor but was: " + executableExtension.getClass());
			}
		} catch (final Exception e) {
			final String id = element.getAttribute(ID_ATTRIBUTE);
			LOGGER.error("Error while creating executable CDO change processor factory with ID: '" + String.valueOf(id) + "'.", e);
			return null;
		}
	}
	
	
}