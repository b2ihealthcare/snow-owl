/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintPredicateType;

/**
 * 
 * @since 4.7
 */
public final class SnomedConstraintSearchRequestBuilder 
		extends SearchResourceRequestBuilder<SnomedConstraintSearchRequestBuilder, BranchContext, SnomedConstraints>
		implements RevisionIndexRequestBuilder<SnomedConstraints> {

	SnomedConstraintSearchRequestBuilder() {
		super();
	}
	
	public SnomedConstraintSearchRequestBuilder filterBySelfId(String selfId) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.SELF, selfId);
	}
	
	public SnomedConstraintSearchRequestBuilder filterBySelfIds(Collection<String> selfIds) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.SELF, selfIds);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByChildId(String childId) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.CHILD, childId);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByChildIds(Collection<String> childIds) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.CHILD, childIds);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByDescendantId(String descendantId) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.DESCENDANT, descendantId);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByDescendantIds(Collection<String> descendantIds) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.DESCENDANT, descendantIds);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByRefSetId(String refSetId) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.REFSET, refSetId);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByRefSetIds(Collection<String> refSetIds) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.REFSET, refSetIds);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByRelationshipKeys(Collection<String> relationships) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.RELATIONSHIP, relationships);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByType(SnomedConstraintPredicateType type) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.TYPE, type);
	}
	
	public SnomedConstraintSearchRequestBuilder filterByTypes(Collection<SnomedConstraintPredicateType> type) {
		return addOption(SnomedConstraintSearchRequest.OptionKey.TYPE, type);
	}

	@Override
	protected SearchResourceRequest<BranchContext, SnomedConstraints> createSearch() {
		return new SnomedConstraintSearchRequest();
	}
}
