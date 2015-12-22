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
package com.b2international.snowowl.snomed.reasoner.server;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * Controls the server-side reasoner bundle's lifecycle.
 */
public class SnomedReasonerServerActivator extends Plugin {

	public static final String PLUGIN_ID = SnomedReasonerServerActivator.class.getPackage().getName();

	/**
	 * Set to {@code true} on configurations where incremental reasoning can not be performed (maximum memory reported by the runtime is less than
	 * 3GB), {@code false} on all other systems
	 */
	public static final boolean CONSTRAINED_HEAP = true;

	private static SnomedReasonerServerActivator instance;

	/**
	 * @return the shared activator instance
	 */
	public static SnomedReasonerServerActivator getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override public void start(final BundleContext context) throws Exception {
		super.start(context);
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override public void stop(final BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}

	/**
	 * Sends the specified message with {@link IStatus#INFO} priority and the current plug-in identifier to the Eclipse log.
	 * @param message the log message
	 */
	public static void logInfo(final String message) {
		getInstance().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
	}
}