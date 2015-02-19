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
package com.b2international.snowowl.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class CoreActivator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.b2international.snowowl.core"; //$NON-NLS-1$

	private static BundleContext context;
	
	private static Logger logger;
	
	/**
	 * Returns the bundle context.
	 * @return
	 */
	public static BundleContext getContext() {
		return context;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		CoreActivator.context = context;
		logger = LoggerFactory.getLogger(PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		CoreActivator.context = null;
		logger = null;
	}
	
	/**
	 * Logs an error message and Throwable for a plugin.
	 * @param plugin in which the error happened
	 * @param message error message
	 * @param t throwable
	 */
	public static void logError(final Plugin plugin, final Object message, final Throwable t) {
		final String pluginString = plugin.getBundle().getSymbolicName() +"("+ plugin.getBundle().getVersion()+ ")";
		logger.error(message+" in plugin: " + pluginString, t);
	}
	
	/**
	 * Logs an error message and Throwable for a bundle.
	 * @param bundle in which the error happened
	 * @param message error message
	 * @param t throwable
	 */
	public static void logError(final Bundle bundle, final Object message, final Throwable t) {
		final String pluginString = bundle.getSymbolicName() +"("+ bundle.getVersion()+ ")";
		logger.error(message+" in bundle: " + pluginString, t);
	}
	
	/**
	 * Logs an error and a message.
	 * @param message
	 * @param t
	 */
	public static void logError(final String message, final Throwable t) {
		logger.error(message, t);
	}
	
	/**
	 * Logs an info.
	 * @param message
	 */
	public static void logInfo(final String message) {
		logger.info(message);
	}
	
	/**
	 * Logs a warning.
	 * @param message
	 */
	public static void logWarn(final String message) {
		logger.warn(message);
	}
	
	/**
	 * Returns the logger.
	 * @return
	 */
	public static Logger getLogger() {
		return logger;
	}
	
}