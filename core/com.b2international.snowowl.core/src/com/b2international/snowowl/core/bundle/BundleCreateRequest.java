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
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.BaseResourceCreateRequest;

/**
 * @since 8.0
 */
final class BundleCreateRequest extends BaseResourceCreateRequest {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute(TransactionContext context) {
		context.add(createBundleDocument());
		return id;
	}

	private ResourceDocument createBundleDocument() {
		return ResourceDocument.builder()
				.id(id)
				.resourceType(Bundle.BUNDLE_RESOURCE_TYPE)
				.url(url)
				.title(title)
				.language(language)
				.description(description)
				.status(status)
				.copyright(copyright)
				.owner(owner)
				.contact(contact)
				.usage(usage)
				.purpose(purpose)
				.bundleId(bundleId)
				.build();
	}
}
