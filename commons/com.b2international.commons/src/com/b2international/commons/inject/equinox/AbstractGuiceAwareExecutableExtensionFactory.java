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
package com.b2international.commons.inject.equinox;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.inject.Injector;

/**
 * @since 2.8
 */
public abstract class AbstractGuiceAwareExecutableExtensionFactory implements IExecutableExtensionFactory, IExecutableExtension {

	private static final Logger log = LoggerFactory.getLogger(AbstractGuiceAwareExecutableExtensionFactory.class);
	
	protected String clazzName;
	
	protected String configurationData;
	
	private IConfigurationElement config;

	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		if (data instanceof String) {
			final Iterator<String> configurationStringIterator = Splitter.on(':').limit(2).split((String) data).iterator();
			clazzName = configurationStringIterator.next();
			configurationData = configurationStringIterator.hasNext() ? configurationStringIterator.next() : null;
		}
		if (clazzName == null) {
			throw new IllegalArgumentException("couldn't handle passed data : " + data);
		}
		this.config = config;
	}

	@Override
	public Object create() throws CoreException {
		try {
			final Class<?> clazz = getBundle().loadClass(clazzName);
			final Injector injector = getInjector();
			final Object result = injector.getInstance(clazz);
			if (result instanceof IExecutableExtension)
				((IExecutableExtension) result).setInitializationData(config, null, configurationData);
			return result;
		} catch (final Exception e) {
			log.error("Exception happened, when instantiating class through extension injection", e);
			throw new CoreException(new Status(IStatus.ERROR, getBundle().getSymbolicName(), e.getMessage() + " ExtensionFactory: " + getClass().getName(), e));
		}
	}

	protected abstract Bundle getBundle();

	protected abstract Injector getInjector();

}