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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Controls the server-side datastore bundle's lifecycle.
 *
 */
public class DatastoreServerActivator implements BundleActivator {

	private static BundleContext context;
	
	public static final String PLUGIN_ID = "com.b2international.snowowl.datastore.server";

	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		DatastoreServerActivator.context = bundleContext;
	}

	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		DatastoreServerActivator.context = null;
	}
	
}