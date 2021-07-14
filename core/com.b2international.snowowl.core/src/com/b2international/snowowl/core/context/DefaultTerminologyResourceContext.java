/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.context;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.domain.DelegatingContext;

/**
 * @since 8.0
 */
public final class DefaultTerminologyResourceContext extends DelegatingContext implements TerminologyResourceContext {

	public DefaultTerminologyResourceContext(final ServiceProvider delegate, ResourceURI resourceUri, TerminologyResource resource) {
		super(delegate);
		bind(ResourceURI.class, resourceUri);
		bind(TerminologyResource.class, resource);
	}

}
