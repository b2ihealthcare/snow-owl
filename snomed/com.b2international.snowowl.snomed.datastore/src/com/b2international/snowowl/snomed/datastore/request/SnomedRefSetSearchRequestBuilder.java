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

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.google.common.base.Strings;

/**
 * <i>Builder</i> class to build requests responsible for searching SNOMED CT reference sets.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * Filter methods restrict the results set returned from the search requests; 
 * what passes the filters will be returned as part of the pageable resultset.
 * 
 * @since 4.5
 */
public final class SnomedRefSetSearchRequestBuilder extends SnomedSearchRequestBuilder<SnomedRefSetSearchRequestBuilder, SnomedReferenceSets> {

	SnomedRefSetSearchRequestBuilder() {}

	@Override
	protected SearchResourceRequest<BranchContext, SnomedReferenceSets> createSearch() {
		return new SnomedRefSetSearchRequest();
	}
	
	public SnomedRefSetSearchRequestBuilder filterByType(SnomedRefSetType refSetType) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.TYPE, refSetType);
	}
	
	public SnomedRefSetSearchRequestBuilder filterByTypes(Collection<SnomedRefSetType> refSetTypes) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.TYPE, Collections3.toImmutableSet(refSetTypes));
	}

	public SnomedRefSetSearchRequestBuilder filterByReferencedComponentType(String referencedComponentType) {
		if (Strings.isNullOrEmpty(referencedComponentType)) {
			return getSelf();
		}
		if (TerminologyRegistry.UNSPECIFIED.equals(referencedComponentType)) {
			return getSelf();
		}
		final int referencedComponentTypeAsInt = TerminologyRegistry.INSTANCE.getTerminologyComponentById(referencedComponentType).shortId();
		return filterByReferencedComponentType(referencedComponentTypeAsInt);
	}
	
	public SnomedRefSetSearchRequestBuilder filterByReferencedComponentType(Integer referencedComponentType) {
		if (referencedComponentType == null) {
			return getSelf();
		}
		return addOption(SnomedRefSetSearchRequest.OptionKey.REFERENCED_COMPONENT_TYPE, referencedComponentType);
	}
	
	public SnomedRefSetSearchRequestBuilder filterByReferencedComponentTypes(Collection<Integer> referencedComponentTypes) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.REFERENCED_COMPONENT_TYPE, Collections3.toImmutableSet(referencedComponentTypes));
	}

}
