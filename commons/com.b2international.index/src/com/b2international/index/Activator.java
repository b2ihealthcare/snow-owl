/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

public class Activator implements BundleActivator {

	private static Activator instance;

	public static Activator getDefault() {
		return instance;
	}

	private IndexClientFactory factory;
	
	@Override
	public void start(BundleContext context) throws Exception {
		final Bundle bundle = context.getBundle();
		final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		final ClassLoader bundleClassLoader = bundleWiring.getClassLoader();
		
		final ServiceLoader<IndexClientFactory> loader = ServiceLoader.load(IndexClientFactory.class, bundleClassLoader);
		factory = loader.iterator().next();
		factory.start(bundleClassLoader);
		
		instance = this;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		
		if (factory != null) {
			factory.stop();
			factory = null;
		}
	}

	public IndexClientFactory getIndexClientFactory() {
		return factory;
	}
}
