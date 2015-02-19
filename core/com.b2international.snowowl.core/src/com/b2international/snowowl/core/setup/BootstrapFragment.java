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
package com.b2international.snowowl.core.setup;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.config.SnowOwlConfiguration;

/**
 * Initialization fragment to provide additional logic to ensure that the
 * application will run successfully with this bundle.
 * 
 * @since 3.3
 */
public interface BootstrapFragment {

	/**
	 * Initializes the given bootstrap with additional information, services.
	 * 
	 * @param configuration
	 *            - Snow Owl Application configuration
	 * @param env
	 *            - the environment within this fragment will be initialized
	 */
	void init(SnowOwlConfiguration configuration, Environment env) throws Exception;

	/**
	 * Runs this fragment, which actually means that this fragment will be able
	 * to provide services for the entire system.
	 * 
	 * @param configuration
	 *            - Snow Owl Application configuration
	 * @param env
	 *            - the environment within this fragment will run after this
	 *            method call
	 * @param monitor
	 */
	void run(SnowOwlConfiguration configuration, Environment env, IProgressMonitor monitor) throws Exception;

}