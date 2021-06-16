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

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.request.ResourceRequests;

/**
 * @since 8.0
 */
public final class Bundle extends Resource {

	private static final long serialVersionUID = 1L;

	public static final String BUNDLE_RESOURCE_TYPE = "bundle";
	
	/**
	 * @since 8.0
	 */
	public static final class Expand {
		public static final String RESOURCES = "resources";
	}
	
	// Expandable content
	private Resources resources;
	
	public Resources getResources() {
		return resources;
	}
	
	public void setResources(Resources resources) {
		this.resources = resources;
	}
	
	@Override
	public String getResourceType() {
		return BUNDLE_RESOURCE_TYPE;
	}

	public BundleCreateRequestBuilder toCreateRequest() {
		return ResourceRequests.bundles().prepareCreate()
				.setId(getId())
				.setUrl(getUrl())
				.setTitle(getTitle())
				.setLanguage(getLanguage())
				.setDescription(getDescription())
				.setStatus(getStatus())
				.setCopyright(getCopyright())
				.setOwner(getOwner())
				.setContact(getContact())
				.setUsage(getUsage())
				.setPurpose(getPurpose())
				.setBundleId(getBundleId());
	}
}
