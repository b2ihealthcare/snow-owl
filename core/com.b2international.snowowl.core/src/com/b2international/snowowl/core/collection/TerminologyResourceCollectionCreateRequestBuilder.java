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
package com.b2international.snowowl.core.collection;

import com.b2international.snowowl.core.request.resource.BaseTerminologyResourceCreateRequestBuilder;

/**
 * @since 9.0.0
 */
public final class TerminologyResourceCollectionCreateRequestBuilder
		extends BaseTerminologyResourceCreateRequestBuilder<TerminologyResourceCollectionCreateRequestBuilder, TerminologyResourceCollectionCreateRequest> {

	private String toolingId;
	
	public TerminologyResourceCollectionCreateRequestBuilder setToolingId(String toolingId) {
		this.toolingId = toolingId;
		return getSelf();
	}

	@Override
	public TerminologyResourceCollectionCreateRequest createResourceRequest() {
		return new TerminologyResourceCollectionCreateRequest();
	}
	
	@Override
	protected void init(TerminologyResourceCollectionCreateRequest req) {
		super.init(req);
		req.setToolingId(toolingId);
	}

}
