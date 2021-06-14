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
package com.b2international.snowowl.core.bundle;

import com.b2international.snowowl.core.context.ResourceRepositoryTransactionRequestBuilder;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.0
 */
public final class BundleDeleteRequestBuilder
		extends BaseRequestBuilder<BundleDeleteRequestBuilder, TransactionContext, Boolean> 
		implements ResourceRepositoryTransactionRequestBuilder<Boolean> {
	
	private final String resourceId;

	public BundleDeleteRequestBuilder(final String resourceId) {
		super();
		this.resourceId = resourceId;
	}
	
	@Override
	protected Request<TransactionContext, Boolean> doBuild() {
		return new BundleDeleteRequest(resourceId);
	}

}
