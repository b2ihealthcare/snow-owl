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
package com.b2international.snowowl.datastore;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.spi.net4j.ClientProtocolFactory;

import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Registry for custom Net4j client protocol factories.
 * 
 * @see org.eclipse.spi.net4j.ClientProtocolFactory
 */
public class ClientProtocolFactoryRegistry {

	private static final String EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.protocolFactory";
	private static final String CLASS_ATTRIBUTE = "class";
	private static ClientProtocolFactoryRegistry instance;

	public List<ClientProtocolFactory> getRegisteredClientProtocolFactories() {
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		return Lists.transform(Arrays.asList(configurationElements), new Function<IConfigurationElement, ClientProtocolFactory>() {
			@Override
			public ClientProtocolFactory apply(IConfigurationElement input) {
				try {
					return (ClientProtocolFactory) input.createExecutableExtension(CLASS_ATTRIBUTE);
				} catch (final CoreException e) {
					throw new RuntimeException("Error while creating executable extension from the passed in configuration element: " + input, e);
				}
			}
		});
	}

	private ClientProtocolFactoryRegistry() {}
	
	public static ClientProtocolFactoryRegistry getInstance() {
		if (instance == null) {
				synchronized (TerminologyRegistry.class) {
				if (instance == null)
					instance = new ClientProtocolFactoryRegistry();
			}
		}
		return instance;
	}
}