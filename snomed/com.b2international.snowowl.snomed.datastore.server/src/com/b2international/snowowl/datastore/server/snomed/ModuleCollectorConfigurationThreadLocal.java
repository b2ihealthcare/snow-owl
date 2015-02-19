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
package com.b2international.snowowl.datastore.server.snomed;

import javax.annotation.Nullable;

/**
 * Thread local used by the {@link SnomedModuleDependencyCollectorService}.
 *
 */
/*default*/ class ModuleCollectorConfigurationThreadLocal {
	
	private static final ThreadLocal<ModuleCollectorConfiguration> THREAD_LOCAL = new ThreadLocal<>();
	
	/**Returns with the configuration for the current thread. May be {@code null} if not set from the current thread.*/
	/*default*/ static ModuleCollectorConfiguration getConfiguration() {
		return THREAD_LOCAL.get();
	}
	
	/**Sets the configuration for the current thread.*/
	/*default*/ static void setConfiguration(@Nullable final ModuleCollectorConfiguration configuration) {
		THREAD_LOCAL.set(configuration);
	}
	
	/**Discards the configuration.*/
	/*default*/ static void reset() {
		setConfiguration(null);
	}
	
}