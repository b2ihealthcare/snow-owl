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

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequest.OptionKey;

/**
 * <i>Builder</i> class to build requests responsible for searching SNOMED CT reference set members.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * Filter methods restrict the results set returned from the search requests; 
 * what passes the filters will be returned as part of the pageable resultset.
 * 
 * @since 4.5
 */
public final class SnomedRefSetMemberSearchRequestBuilder 
		extends SnomedSearchRequestBuilder<SnomedRefSetMemberSearchRequestBuilder, SnomedReferenceSetMembers>
		implements RevisionIndexRequestBuilder<SnomedReferenceSetMembers> {

	SnomedRefSetMemberSearchRequestBuilder() {
		super();
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, SnomedReferenceSetMembers> createSearch() {
		return new SnomedRefSetMemberSearchRequest();
	}
	
	public SnomedRefSetMemberSearchRequestBuilder filterByRefSet(String referenceSetId) {
		return addOption(OptionKey.REFSET, referenceSetId);
	}
	
	public SnomedRefSetMemberSearchRequestBuilder filterByRefSet(Iterable<String> referenceSetIds) {
		return addOption(OptionKey.REFSET, Collections3.toImmutableSet(referenceSetIds));
	}
	
	public SnomedRefSetMemberSearchRequestBuilder filterByReferencedComponent(String referencedComponentId) {
		return addOption(OptionKey.REFERENCED_COMPONENT, referencedComponentId);
	}
	
	public SnomedRefSetMemberSearchRequestBuilder filterByReferencedComponent(Iterable<String> referencedComponentIds) {
		return addOption(OptionKey.REFERENCED_COMPONENT, Collections3.toImmutableSet(referencedComponentIds));
	}
	
	public SnomedRefSetMemberSearchRequestBuilder filterByRefSetType(final Iterable<SnomedRefSetType> refSetTypes) {
		return addOption(OptionKey.REFSET_TYPE, Collections3.toImmutableSet(refSetTypes));
	}
	
	public SnomedRefSetMemberSearchRequestBuilder filterByProps(Options memberProps) {
		return addOption(OptionKey.PROPS, memberProps);
	}

	public SnomedRefSetMemberSearchRequestBuilder filterByReferencedComponentType(String referencedComponentType) {
		return filterByReferencedComponentType(TerminologyRegistry.INSTANCE.getTerminologyComponentById(referencedComponentType).shortId());
	}
	
	public SnomedRefSetMemberSearchRequestBuilder filterByReferencedComponentType(short referencedComponentType) {
		return addOption(OptionKey.REFERENCED_COMPONENT_TYPE, referencedComponentType);
	}
	
	public SnomedRefSetMemberSearchRequestBuilder filterByReferencedComponentTypes(Iterable<Short> referencedComponentTypes) {
		return addOption(OptionKey.REFERENCED_COMPONENT_TYPE, referencedComponentTypes);
	}

}
