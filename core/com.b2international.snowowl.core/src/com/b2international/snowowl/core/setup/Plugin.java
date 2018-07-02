/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.setup;

import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;

/**
 * @since 7.0
 */
public abstract class Plugin {

	/**
	 * Plug-ins can provide additional configuration types to extend the main snowowl.yml configuration file capabilities with their own
	 * configurations.
	 * 
	 * @param registry
	 */
	public void addConfigurations(ConfigurationRegistry registry) {
	}
	
	/**
	 * Initializes the plug-ins base services.
	 * 
	 * @param configuration
	 *            - Snow Owl Application configuration
	 * @param env
	 *            - the environment within this plug-in will be initialized
	 * @throws Exception
	 */
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
	}

	/**
	 * Initializes application modules before running it completely. The method can use any required dependency which registered in
	 * {@link #init(SnowOwlConfiguration, Environment)}.
	 * 
	 * @param configuration
	 * @param env
	 * @throws Exception
	 */
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
	}

	/**
	 * Invoked by {@link SnowOwlApplication} at the end of the bootstrap process to let plug-ins initialize themselves finally before indicating that
	 * Snow Owl is ready to receive requests, data, etc.
	 * 
	 * @param configuration
	 *            - Snow Owl Application configuration
	 * @param env
	 *            - the environment
	 * @throws Exception
	 */
	public void run(SnowOwlConfiguration configuration, Environment env) throws Exception {
	}

	/**
	 * Executed after application {@link #init(SnowOwlConfiguration, Environment)} and
	 * {@link #run(SnowOwlConfiguration, Environment, org.eclipse.core.runtime.IProgressMonitor)} methods.
	 * 
	 * @param configuration
	 * @param env
	 * @throws Exception
	 */
	public void postRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
	}

}