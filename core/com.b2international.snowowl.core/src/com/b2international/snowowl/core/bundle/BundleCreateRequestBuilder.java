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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.BaseResourceCreateRequestBuilder;

/**
 * @since 8.0
 */
public final class BundleCreateRequestBuilder extends BaseResourceCreateRequestBuilder<BundleCreateRequestBuilder> {

	@Override
	protected Request<TransactionContext, String> doBuild() {
		final BundleCreateRequest req = new BundleCreateRequest();
		req.setId(id);
		req.setUrl(url);
		req.setTitle(title);
		req.setLanguage(language);
		req.setDescription(description);
		req.setStatus(status);
		req.setCopyright(copyright);
		req.setOwner(owner);
		req.setContact(contact);
		req.setUsage(usage);
		req.setPurpose(purpose);
		req.setBundleId(bundleId);
		
		return req;
	}

}
