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

import com.b2international.snowowl.core.config.SnowOwlConfiguration;

/**
 * Adds additional capabilities to {@link BootstrapFragment} in order to execute
 * logic between
 * {@link #init(org.osgi.service.prefs.PreferencesService, Environment)} and
 * {@link #run(com.b2international.snowowl.core.config.SnowOwlConfiguration, Environment, org.eclipse.core.runtime.IProgressMonitor)}
 * 
 * @since 3.3
 */
public interface PreRunCapableBootstrapFragment extends BootstrapFragment {

	/**
	 * Initializes application modules before running it completely. The method
	 * can use any required dependency which registered in
	 * {@link #init(org.osgi.service.prefs.PreferencesService, Environment)}.
	 * 
	 * @param configuration
	 * @param env
	 * @throws Exception 
	 */
	void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception;

}