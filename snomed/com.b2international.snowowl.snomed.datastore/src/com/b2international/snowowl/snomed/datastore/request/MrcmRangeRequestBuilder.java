/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.MrcmTypeRequest.ATTRIBUTE_TYPE;

/**
 * @since 8.7.0
 */
public class MrcmRangeRequestBuilder extends BaseRequestBuilder<MrcmRangeRequestBuilder, BranchContext, SnomedReferenceSetMembers>
	implements SnomedContentRequestBuilder<SnomedReferenceSetMembers> {
	
	private Set<String> selfIds;
	private Set<String> parentIds; 
	private Set<String> refSetIds;
	private List<String> moduleIds = List.of();
	private ATTRIBUTE_TYPE attributeType = ATTRIBUTE_TYPE.ALL;
	
	public MrcmRangeRequestBuilder setSelfIds(Set<String> selfIds) {
		this.selfIds = selfIds;
		return getSelf();
	}
	
	public MrcmRangeRequestBuilder setParentIds(Set<String> parentIds) {
		this.parentIds = parentIds;
		return getSelf();
	}
	
	public MrcmRangeRequestBuilder setRefSetIds(Set<String> refSetIds) {
		this.refSetIds = refSetIds;
		return getSelf();
	}
	
	public MrcmRangeRequestBuilder setModuleIds(List<String> moduleIds) {
		this.moduleIds = moduleIds;
		return getSelf();
	}
	
	public MrcmRangeRequestBuilder setAttributeType(ATTRIBUTE_TYPE attributeType) {
		this.attributeType = attributeType;
		return getSelf();
	}
	
	@Override
	protected Request<BranchContext, SnomedReferenceSetMembers> doBuild() {	
		MrcmRangeRequest request = new MrcmRangeRequest();
		request.setAttributeType(attributeType);
		request.setModuleIds(moduleIds);
		request.setRefSetIds(refSetIds);
		request.setParentIds(parentIds);
		request.setSelfIds(selfIds);
		
		return request;
	}

}
