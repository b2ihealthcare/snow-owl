/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.whitelist;

import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepositoryContext;
import com.b2international.snowowl.core.internal.validation.ValidationRepositoryRequestBuilder;

/**
 * @since 6.1
 */
public final class ValidationWhiteListDeleteRequestBuilder 
	extends BaseRequestBuilder<ValidationWhiteListDeleteRequestBuilder, ValidationRepositoryContext, Boolean>
	implements ValidationRepositoryRequestBuilder<Boolean> {

	private final Set<String> ids;

	ValidationWhiteListDeleteRequestBuilder(final Iterable<String> ids) {
		this.ids = Collections3.toImmutableSet(ids);
	}
	
	@Override
	protected Request<ValidationRepositoryContext, Boolean> doBuild() {
		return new ValidationWhiteListDeleteRequest(ids);
	}
}
