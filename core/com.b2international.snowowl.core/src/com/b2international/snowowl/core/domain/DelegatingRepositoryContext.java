/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.config.SnowOwlConfiguration;

/**
 * @since 4.5
 */
public class DelegatingRepositoryContext extends DelegatingServiceProvider implements RepositoryContext {

	protected DelegatingRepositoryContext(RepositoryContext context) {
		super(context);
	}

	@Override
	public final SnowOwlConfiguration config() {
		return getDelegate().config();
	}
	
	@Override
	public final String id() {
		return getDelegate().id();
	}
	
	@Override
	public Health health() {
		return getDelegate().health();
	}
	
	@Override
	public String diagnosis() {
		return getDelegate().diagnosis();
	}

	@Override
	protected RepositoryContext getDelegate() {
		return (RepositoryContext) super.getDelegate();
	}
	
	public static DelegatingRepositoryContext.Builder<DelegatingRepositoryContext> basedOn(RepositoryContext context) {
		return new DelegatingServiceProvider.Builder<>(new DelegatingRepositoryContext(context));
	}
	
}
