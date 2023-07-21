/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.codesystem;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemUpdateRequestBuilder;
import com.b2international.snowowl.core.rest.BaseTerminologyResourceUpdateRestInput;

/**
 * @since 8.0
 */
public final class CodeSystemUpdateRestInput extends BaseTerminologyResourceUpdateRestInput {
	
	private ResourceURI extensionOf;
	
	/**
	 * @param extensionOf
	 * @deprecated - replaced by {@link #setDependencies(java.util.List)}, will be removed in 9.0
	 */
	public void setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	/**
	 * @return
	 * @deprecated - replaced by {@link #getDependencies()}, will be removed in 9.0
	 */
	public ResourceURI getExtensionOf() {
		return extensionOf;
	}

	public CodeSystemUpdateRequestBuilder toCodeSystemUpdateRequest(String codeSystemId) {
		return CodeSystemRequests.prepareUpdateCodeSystem(codeSystemId)
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
			.setBundleId(getBundleId())
			.setOid(getOid())
			.setBranchPath(getBranchPath())
			.setSettings(getSettings())
			.setDependencies(getDependencies())
			// XXX maintain old model field options until 9.0
			.setExtensionOf(getExtensionOf())
			;
	}
}
