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
package com.b2international.snowowl.core.rest.bundle;

import com.b2international.snowowl.core.bundle.BundleCreateRequestBuilder;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.BaseResourceRestInput;

/**
 * @since 8.0
 */
public final class BundleRestInput extends BaseResourceRestInput {

	public BundleCreateRequestBuilder toCreateRequest() {
		return ResourceRequests.bundles().prepareCreate()
				.setId(getId())
				.setBundleId(getBundleId())
				.setUrl(getUrl())
				.setTitle(getTitle())
				.setLanguage(getLanguage())
				.setDescription(getDescription())
				.setStatus(getStatus())
				.setCopyright(getCopyright())
				.setOwner(getOwner())
				.setContact(getContact())
				.setUsage(getUsage())
				.setPurpose(getPurpose());
	}
}
