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
package com.b2international.snowowl.core.request;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 6.1
 */
public abstract class UpdateRequestBuilder<B extends UpdateRequestBuilder<B>> extends BaseRequestBuilder<B, TransactionContext, Boolean> {

	private final String componentId;

	protected UpdateRequestBuilder(String componentId) {
		this.componentId = componentId;
	}
	
	@Override
	protected final Request<TransactionContext, Boolean> doBuild() {
		return doBuild(componentId);
	}

	protected abstract UpdateRequest doBuild(String componentId);
	
}
