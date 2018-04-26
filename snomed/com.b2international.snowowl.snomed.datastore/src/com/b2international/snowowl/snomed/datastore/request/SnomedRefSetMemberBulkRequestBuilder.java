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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.events.bulk.BulkResponse;

/**
 * @since 6.4
 */
public final class SnomedRefSetMemberBulkRequestBuilder implements RequestBuilder<TransactionContext, BulkResponse> {

	private final BulkRequestBuilder<TransactionContext> requests = BulkRequest.create();

	public SnomedRefSetMemberBulkRequestBuilder add(final Request<TransactionContext, ?> req) {
		this.requests.add(req);
		return this;
	}

	public SnomedRefSetMemberBulkRequestBuilder add(final RequestBuilder<TransactionContext, ?> req) {
		this.requests.add(req);
		return this;
	}

	@Override
	public Request<TransactionContext, BulkResponse> build() {
		return new SnomedRefSetMemberBulkRequest(requests.build());
	}
}
