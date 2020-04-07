/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.slf4j.Logger;

import com.b2international.index.es.client.EsIndexStatus;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public final class DefaultRepositoryContext implements RepositoryContext {

	private final ServiceProvider serviceProvider;
	private final RepositoryInfo info;

	DefaultRepositoryContext(ServiceProvider serviceProvider, RepositoryInfo info) {
		this.info = checkNotNull(info, "info");
		this.serviceProvider = checkNotNull(serviceProvider, "serviceProvider");
	}

	@Override
	public <T> T service(Class<T> type) {
		return serviceProvider.service(type);
	}

	@Override
	public <T> Provider<T> provider(Class<T> type) {
		return serviceProvider.provider(type);
	}

	@Override
	public SnowOwlConfiguration config() {
		return service(SnowOwlConfiguration.class);
	}
	
	@Override
	public Logger log() {
		return service(Logger.class);
	}
	
	@Override
	public String id() {
		return info.id();
	}
	
	@Override
	public Health health() {
		return info.health();
	}
	
	@Override
	public List<EsIndexStatus> indices() {
		return info.indices();
	}
	
}
