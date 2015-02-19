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
package com.b2international.snowowl.datastore.inject;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @since 3.9
 * @param <T> - the service interface
 */
public abstract class InjectorAwareServerServiceConfigJob<T> extends AbstractServerServiceConfigJob<T> {

	@Inject
	private Injector injector;

	protected InjectorAwareServerServiceConfigJob(String name, Object family) {
		super(name, family);
	}
	
	@Override
	protected T createServiceImplementation() throws SnowowlServiceException {
		return injector.getInstance(getImplementationClass());
	}

	protected abstract Class<? extends T> getImplementationClass();

}