/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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

package com.b2international.snowowl.snomed.datastore.request;

import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.TransactionalRequestBuilder;

/**
 * @since 8.10.1
 */
public class SnomedMemberOfFieldFixRequestBuilder 
	extends BaseRequestBuilder<SnomedMemberOfFieldFixRequestBuilder, TransactionContext, Set<String>>
	implements TransactionalRequestBuilder<Set<String>> {
	
	@Override
	protected Request<TransactionContext, Set<String>> doBuild() {
		return new SnomedMemberOfFieldFixRequest();
	}

}
