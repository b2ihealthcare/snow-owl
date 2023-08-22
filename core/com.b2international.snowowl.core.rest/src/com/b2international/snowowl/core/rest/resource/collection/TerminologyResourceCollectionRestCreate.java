/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.resource.collection;

import com.b2international.snowowl.core.request.collection.TerminologyResourceCollectionCreateRequestBuilder;
import com.b2international.snowowl.core.request.collection.TerminologyResourceCollectionRequests;
import com.b2international.snowowl.core.rest.BaseTerminologyResourceRestCreate;

/**
 * @since 9.0
 */
public class TerminologyResourceCollectionRestCreate extends BaseTerminologyResourceRestCreate {
	
	private String toolingId;
	private String childResourceType;
	
	public String getChildResourceType() {
		return childResourceType;
	}
	
	public String getToolingId() {
		return toolingId;
	}
	
	public void setToolingId(String toolingId) {
		this.toolingId = toolingId;
	}
	
	public void setChildResourceType(String childResourceType) {
		this.childResourceType = childResourceType;
	}

	public TerminologyResourceCollectionCreateRequestBuilder toCreateRequest() {
		return TerminologyResourceCollectionRequests.prepareCreate()
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
				.setPurpose(getPurpose())
				.setOid(getOid())
				.setBranchPath(getBranchPath())
				.setSettings(getSettings())
				.setDependencies(getDependencies())
				.setToolingId(getToolingId())
				.setChildResourceType(getChildResourceType());
	}
	
}
