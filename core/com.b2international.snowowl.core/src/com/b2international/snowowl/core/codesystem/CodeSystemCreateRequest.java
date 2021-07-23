/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.BaseTerminologyResourceCreateRequest;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;

/**
 * @since 4.7
 */
final class CodeSystemCreateRequest extends BaseTerminologyResourceCreateRequest {

	private static final long serialVersionUID = 3L;

	@Override
	protected String getResourceType() {
		return CodeSystem.RESOURCE_TYPE;
	}

	@Override
	protected ResourceURLSchemaSupport getResourceURLSchemaSupport(ServiceProvider context) {
		return validateAndGetToolingRepository(context).service(ResourceURLSchemaSupport.class);
	}
}
