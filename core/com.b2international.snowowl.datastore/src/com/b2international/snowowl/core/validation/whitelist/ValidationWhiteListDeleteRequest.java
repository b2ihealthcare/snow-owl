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

import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepositoryContext;

/**
 * @since 6.1
 */
final class ValidationWhiteListDeleteRequest implements Request<ValidationRepositoryContext, Boolean> {

	private final String id;

	ValidationWhiteListDeleteRequest(final String id) {
		this.id = id;
	}
	
	@Override
	public Boolean execute(ValidationRepositoryContext context) {
		context.delete(ValidationWhiteList.class, id);
		return Boolean.TRUE;
	}

}
