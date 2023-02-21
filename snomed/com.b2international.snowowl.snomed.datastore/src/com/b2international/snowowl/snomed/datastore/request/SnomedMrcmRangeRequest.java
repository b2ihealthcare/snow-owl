/*
 * Copyright 2022-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.revision.Revision.Fields.ID;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.ALL_PRECOORDINATED_CONTENT;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.MRCM_ATTRIBUTE_RANGE;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.MRCM_MODULE_SCOPE;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.MRCM_RULE_REFSET_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.MrcmAttributeType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;

/**
 * @since 8.8.0
 */
final class SnomedMrcmRangeRequest extends SearchResourceRequest<BranchContext, SnomedReferenceSetMembers> {
	
	private static final long serialVersionUID = 1L;
	
	private Set<String> selfIds;
	private Set<String> parentIds; 
	private Set<String> refSetIds;
	private List<String> moduleIds;
	private MrcmAttributeType attributeType;

	public void setSelfIds(Set<String> selfIds) {
		this.selfIds = selfIds;
	}

	public void setParentIds(Set<String> parentIds) {
		this.parentIds = parentIds;
	}

	public void setRefSetIds(Set<String> refSetIds) {
		this.refSetIds = refSetIds;
	}

	public void setModuleIds(List<String> moduleIds) {
		this.moduleIds = moduleIds;
	}

	public void setAttributeType(MrcmAttributeType attributeType) {
		this.attributeType = attributeType;
	}
		
	@Override
	public SnomedReferenceSetMembers doExecute(BranchContext context) {
		Set<String> inScopeRefSetIds = SnomedRequests.prepareSearchMember()
				.filterByActive(true)
				.filterByRefSetType(MRCM_MODULE_SCOPE)
				.filterByReferencedComponent(moduleIds)
				.setFields(ID, MRCM_RULE_REFSET_ID)
				.stream(context)
				.flatMap(members -> members.stream())
				.map(m -> (String) m.getProperties().get(FIELD_MRCM_RULE_REFSET_ID))
				.collect(Collectors.toSet());

		if (inScopeRefSetIds.isEmpty()) {
			return new SnomedReferenceSetMembers(0, 0);
		}
		
		Set<String> typeIds = SnomedRequests.prepareGetMrcmTypeRules()
				.setAttributeType(attributeType)
				.setModuleIds(moduleIds)
				.setParentIds(parentIds)
				.setRefSetIds(refSetIds)
				.setSelfIds(selfIds)
				.build()
				.execute(context)
				.stream()
				.map(SnomedReferenceSetMember::getReferencedComponentId)
				.collect(Collectors.toSet());
		
		if (typeIds.isEmpty()) {
			return new SnomedReferenceSetMembers(0, 0);
		}
		
		Map<String, SnomedReferenceSetMember> rangeConstraintMembers = new HashMap<>();
		SnomedRequests.prepareSearchMember()
			.filterByActive(true)
			.filterByRefSetType(MRCM_ATTRIBUTE_RANGE)
			.filterByReferencedComponent(typeIds)
			.filterByRefSet(inScopeRefSetIds)
			.setLimit(limit())
			.build()
			.execute(context)
			.forEach(m -> {
				String contentType = (String) m.getProperties().get(FIELD_MRCM_CONTENT_TYPE_ID);
				if (!rangeConstraintMembers.containsKey(m.getReferencedComponentId()) || ALL_PRECOORDINATED_CONTENT.equals(contentType)) {
					rangeConstraintMembers.put(m.getReferencedComponentId(), m);
				}
			});
		
		return new SnomedReferenceSetMembers(List.copyOf(rangeConstraintMembers.values()), null, rangeConstraintMembers.size(), rangeConstraintMembers.size());		
	}

	@Override
	protected SnomedReferenceSetMembers createEmptyResult(int limit) {
		return new SnomedReferenceSetMembers(limit, 0);
	}
	
}
