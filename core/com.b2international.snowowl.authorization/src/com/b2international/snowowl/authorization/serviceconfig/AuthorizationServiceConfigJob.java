/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.authorization.serviceconfig;

import com.b2international.snowowl.authorization.AuthorizationActivator;
import com.b2international.snowowl.core.users.IAuthorizationService;
import com.b2international.snowowl.datastore.serviceconfig.AbstractClientServiceConfigJob;

/**
 * Client-side service config. job, registering {@link AuthorizationService} with the application context.  
 */
public class AuthorizationServiceConfigJob extends AbstractClientServiceConfigJob<IAuthorizationService>{

	
	public AuthorizationServiceConfigJob() {
		super("Authorization service configuration...", AuthorizationActivator.PLUGIN_ID);
	}

	@Override
	protected Class<IAuthorizationService> getServiceClass() {
		return IAuthorizationService.class;
	}

}