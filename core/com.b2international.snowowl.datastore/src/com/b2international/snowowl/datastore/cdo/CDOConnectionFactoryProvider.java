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
package com.b2international.snowowl.datastore.cdo;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.datastore.ICDOConnectionFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Singleton CDO connection factory provider. All {@link ICDOConnectionFactory CDO connection factory} instances
 * can be registered via 'connectionFacotry' extension points.
 * @see ICDOConnectionFactory
 */
public enum CDOConnectionFactoryProvider {

	INSTANCE;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CDOConnectionFactoryProvider.class);
	private static final String CONNECTION_FACTORY_ID = "com.b2international.snowowl.datastore.connectionFactory";
	private static final String CLASS_ATTRIBUTE_ID = "class";
	
	public ICDOConnectionFactory getConnectionFactory() {
		final List<IConfigurationElement> factories = Lists.newArrayList(Platform.getExtensionRegistry().getConfigurationElementsFor(CONNECTION_FACTORY_ID));
		
		if (CompareUtils.isEmpty(factories)) {
			throw new RuntimeException("There are no CDO connection factory registered to the application." +
					"\nConnection factory can be added via 'connectionFactory.exsd' extension point. See also: " + ICDOConnectionFactory.class.getName());
		}
		
		if (factories.size() > 1) {
			LOGGER.warn("More than one CDO connection factory are registered to the application. " +
					"The first applicable is configured to the application.");
		}
		
		final IConfigurationElement configurationElement = Iterables.getFirst(factories, null);
		

		Object extension = null;
		try {
			extension = configurationElement.createExecutableExtension(CLASS_ATTRIBUTE_ID);
		} catch (final CoreException e) {
			LOGGER.error("Error while creating executable extension for " + configurationElement);
			//intentionally fall through
		}
		
		if (extension instanceof ICDOConnectionFactory) {
			return (ICDOConnectionFactory) extension;
		}
		
		throw new RuntimeException("Error while creating executable extension for 'connection factory' extension point.");
	}
	
}