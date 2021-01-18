/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.server.product;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.SnowOwl;

/**
 * Server side Snow Owl product bootstraps and runs the entire headless server application.
 * 
 */
public class SnowOwlServerActivator implements BundleActivator {

	private static BundleContext bundleContext;
	private static SnowOwl snowowl;

	public static BundleContext getBundleContext() {
		return bundleContext;
	}
	
	public static SnowOwl getSnowOwl() {
		return snowowl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		try {
			SnowOwlServerActivator.bundleContext = context;
			snowowl = SnowOwl.create().bootstrap().run();
			
			final Bundle systemBundle = context.getBundle(0);
			final Framework framework = systemBundle.adapt(Framework.class);
			final Thread hook = new Thread(() -> {
                try {
                    framework.stop();
                    framework.waitForStop(60000);
                } catch (Exception e) {
                    System.err.println("Failed to cleanly shutdown OSGi Framework: " + e.getMessage());
                    e.printStackTrace();
                }
	        });
	        
	        Runtime.getRuntime().addShutdownHook(hook);
		} catch (Throwable e) {
			LoggerFactory.getLogger("snowowl").error(e.getMessage(), e);
			System.exit(-1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		snowowl.shutdown();
		SnowOwlServerActivator.bundleContext = null;
	}
}
