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
package com.b2international.snowowl.datastore.serviceconfig;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.SnowowlServiceException;

/**
 * Job for creating, initializing and registering a service for the application on the client side which requires a
 * tracking service counterpart.
 * 
 * 
 * @param <S> the branch aware service interface type
 * @param <T> the client tracking service interface type
 */
public abstract class ClientServiceConfigJob<S, T> extends AbstractClientServiceConfigJob<S> {

	/**
	 * Creates a new job for initializing and configuring the index service.
	 * @param name the name of the job.
	 * @param family family object where this job belongs to. 
	 */
	protected ClientServiceConfigJob(final String name, final Object family) {
		super(name, family);
	}

	protected abstract Class<T> getTrackingClass();
	
	protected abstract T createTrackingService(S branchAwareService);
	
	@Override
	protected final boolean initService() throws SnowowlServiceException {
		// XXX: this sets up a tracking client service both for client and server
		ApplicationContext.getInstance().addServiceListener(getServiceClass(), new IServiceChangeListener<S>() {
			@Override public void serviceChanged(final S oldService, final S newService) {
				//null can be the newService while un-registering an existing service.
				ApplicationContext.getInstance().registerService(getTrackingClass(), null == newService ? null : createTrackingService(newService));
			}
		});

		return super.initService();
	}
}