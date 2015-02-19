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

import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.DatastoreActivator;

/**
 * Job for registering the {@link CodeSystemService} onto the client-side.
 *
 */
public class CodeSystemServiceClientConfigJob extends AbstractClientServiceConfigJob<CodeSystemService> {

	private static final String JOB_NAME = "Code system client service configuration...";
	
	public CodeSystemServiceClientConfigJob() {
		super(JOB_NAME, DatastoreActivator.PLUGIN_ID);
	}

	@Override
	protected Class<CodeSystemService> getServiceClass() {
		return CodeSystemService.class;
	}
	
}