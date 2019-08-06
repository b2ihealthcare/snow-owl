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
package com.b2international.snowowl.core.domain;

import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.events.Request;

/**
 * Execution context for {@link Request requests} targeting a branch in a single repository.
 *
 * @since 4.5
 */
public interface RepositoryContext extends ServiceProvider, RepositoryInfo {

	/**
	 * Returns the current application configuration object.
	 * 
	 * @return
	 */
	SnowOwlConfiguration config();
	
	@Override
	default DelegatingContext.Builder<? extends RepositoryContext> inject() {
		return new DelegatingContext.Builder<>(RepositoryContext.class, this);
	}

}
