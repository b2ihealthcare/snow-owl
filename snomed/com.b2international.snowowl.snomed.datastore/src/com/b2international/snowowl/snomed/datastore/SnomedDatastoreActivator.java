/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.PreferencesService;

import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJobManager;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJobManager.IServiceConfigJobChangeListener;

public class SnomedDatastoreActivator implements BundleActivator, IServiceConfigJobChangeListener {

	/**
	 * Unique identifier of the bundle. ID: {@value}
	 */
	public static final String PLUGIN_ID = "com.b2international.snowowl.snomed.datastore"; //$NON-NLS-1$
	
	/** The repository identifier for this tooling */
	public static final String REPOSITORY_UUID = "snomedStore";
	
	// Resource names
	public static final String ROOT_RESOURCE_NAME = "SNOMED";
	public static final String REFSET_ROOT_RESOURCE_NAME = "SNOMED_REFSET";
	public static final String MRCM_ROOT_RESOURCE_NAME = "MRCM";
	public static final String META_ROOT_RESOURCE_NAME = "META_SNOMED";
	public static final String GENERATOR_RESOURCE_NAME = "ogfRoot";
	
	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext bundleContext) throws Exception {
		SnomedDatastoreActivator.context = bundleContext;
		ServiceConfigJobManager.INSTANCE.addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext bundleContext) throws Exception {
		SnomedDatastoreActivator.context = null;
		ServiceConfigJobManager.INSTANCE.removeListener(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJobManager.IServiceConfigJobChangeListener#done(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void done(final IProgressMonitor monitor) {
		//register additional SNOMED CT related configurations
		final Environment env = SnowOwlApplication.INSTANCE.getEnviroment();
		final PreferencesService preferences = env.services().getService(PreferencesService.class);
		env.services().registerService(SnomedConfiguration.class, new SnomedConfiguration(preferences, env.getDefaultsDirectory()));
	}
}