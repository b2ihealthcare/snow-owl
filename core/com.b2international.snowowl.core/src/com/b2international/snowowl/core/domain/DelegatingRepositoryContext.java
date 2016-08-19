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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public abstract class DelegatingRepositoryContext implements RepositoryContext {

	private final RepositoryContext context;

	public DelegatingRepositoryContext(RepositoryContext context) {
		this.context = checkNotNull(context, "context");
	}

	@Override
	public <T> T service(Class<T> type) {
		return context.service(type);
	}

	@Override
	public <T> Provider<T> provider(Class<T> type) {
		return context.provider(type);
	}

	@Override
	public final SnowOwlConfiguration config() {
		return context.config();
	}
	
	@Override
	public final String id() {
		return context.id();
	}

	/**
	 * Returns the context delegate
	 * 
	 * @return
	 */
	protected RepositoryContext getContext() {
		return context;
	}

}
