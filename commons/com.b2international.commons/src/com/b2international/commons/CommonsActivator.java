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
package com.b2international.commons;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator.
 */
public class CommonsActivator implements BundleActivator {

	/**Unique ID of the plug-in. ID: {@value}.*/
	public static final String PLUGIN_ID = "com.b2international.commons";

	// The shared instance
	private static BundleContext context;
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		CommonsActivator.context = context;
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		CommonsActivator.context = null;
	}
	
	/**
	 * Returns with the shared bundle context.
	 * @return the bundle context.
	 */
	public static BundleContext getContext() {
		return context;
	}
	
}