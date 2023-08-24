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
package com.b2international.snowowl.core.collection;

import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;

/**
 * @since 9.0
 */
public final class TerminologyResourceCollection extends TerminologyResource {

	private static final long serialVersionUID = 1L;
	
	public static final String RESOURCE_TYPE = "collections";
	
	private String childResourceType;
	
	public String getChildResourceType() {
		return childResourceType;
	}
	
	public void setChildResourceType(String childResourceType) {
		this.childResourceType = childResourceType;
	}
	
	@Override
	public String getResourceType() {
		return RESOURCE_TYPE;
	}
	
	@Override
	public Builder toDocumentBuilder() {
		return super.toDocumentBuilder()
				.childResourceType(getChildResourceType());
	}

	public static ResourceURI uri(String collectionId) {
		return ResourceURI.of(RESOURCE_TYPE, collectionId);
	}
	
	public static ResourceURI uri(String collectionId, String versionId) {
		return ResourceURI.branch(RESOURCE_TYPE, collectionId, versionId);
	}
	
	public static TerminologyResourceCollection from(ResourceDocument doc) {
		final TerminologyResourceCollection result = new TerminologyResourceCollection();
		result.setId(doc.getId());
		result.setUrl(doc.getUrl());
		result.setTitle(doc.getTitle());
		result.setLanguage(doc.getLanguage());
		result.setDescription(doc.getDescription());
		result.setStatus(doc.getStatus());
		result.setCopyright(doc.getCopyright());
		result.setOwner(doc.getOwner());
		result.setContact(doc.getContact());
		result.setUsage(doc.getUsage());
		result.setPurpose(doc.getPurpose());
		result.setBundleAncestorIds(doc.getBundleAncestorIds());
		result.setBundleId(doc.getBundleId());
		result.setCreatedAt(doc.getCreatedAt());
		result.setUpdatedAt(doc.getUpdatedAt());
		result.setOid(doc.getOid());
		result.setBranchPath(doc.getBranchPath());
		result.setToolingId(doc.getToolingId());
		result.setSettings(doc.getSettings());
		if (doc.getDependencies() != null) {
			result.setDependencies(doc.getDependencies().stream().map(Dependency::from).toList());
		}
		result.setChildResourceType(doc.getChildResourceType());
		return result;
	}

}
