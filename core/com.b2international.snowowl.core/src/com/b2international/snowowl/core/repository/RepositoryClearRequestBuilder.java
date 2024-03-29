/*
 * Copyright 2017-2020 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.repository;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.TransactionalRequestBuilder;

/**
 * Builder for request that clear the repository content.
 * 
 * @since 5.12
 */
public final class RepositoryClearRequestBuilder 
	extends BaseRequestBuilder<RepositoryClearRequestBuilder, TransactionContext, Boolean> 
	implements TransactionalRequestBuilder<Boolean>{
	
	@Override
	protected Request<TransactionContext, Boolean> doBuild() {
		final RepositoryClearRequest req = new RepositoryClearRequest();
		return req;
	}
}
