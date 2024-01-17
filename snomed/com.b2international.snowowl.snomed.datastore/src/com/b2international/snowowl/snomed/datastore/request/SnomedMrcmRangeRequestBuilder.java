/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.MrcmAttributeType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;

/**
 * @since 8.8.0
 */
public class SnomedMrcmRangeRequestBuilder extends SnomedSearchRequestBuilder<SnomedMrcmRangeRequestBuilder, SnomedReferenceSetMembers> {
	
	private Set<String> selfIds;
	private Set<String> parentIds; 
	private Set<String> refSetIds;
	private List<String> moduleIds = List.of();
	private MrcmAttributeType attributeType = MrcmAttributeType.ALL;
	
	public SnomedMrcmRangeRequestBuilder setSelfIds(Set<String> selfIds) {
		this.selfIds = selfIds;
		return getSelf();
	}
	
	public SnomedMrcmRangeRequestBuilder setParentIds(Set<String> parentIds) {
		this.parentIds = parentIds;
		return getSelf();
	}
	
	public SnomedMrcmRangeRequestBuilder setRefSetIds(Set<String> refSetIds) {
		this.refSetIds = refSetIds;
		return getSelf();
	}
	
	public SnomedMrcmRangeRequestBuilder setModuleIds(List<String> moduleIds) {
		this.moduleIds = moduleIds;
		return getSelf();
	}
	
	public SnomedMrcmRangeRequestBuilder setAttributeType(MrcmAttributeType attributeType) {
		this.attributeType = attributeType;
		return getSelf();
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, SnomedReferenceSetMembers> createSearch() {	
		SnomedMrcmRangeRequest request = new SnomedMrcmRangeRequest();
		request.setAttributeType(attributeType);
		request.setModuleIds(moduleIds);
		request.setRefSetIds(refSetIds);
		request.setParentIds(parentIds);
		request.setSelfIds(selfIds);
		
		return request;
	}

}
