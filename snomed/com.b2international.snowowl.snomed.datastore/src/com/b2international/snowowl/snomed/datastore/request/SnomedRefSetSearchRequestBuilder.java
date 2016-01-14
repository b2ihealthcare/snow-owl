/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collections;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.datastore.request.SearchRequest;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public final class SnomedRefSetSearchRequestBuilder extends SnomedSearchRequestBuilder<SnomedRefSetSearchRequestBuilder, SnomedReferenceSets> {

	SnomedRefSetSearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}

	@Override
	protected SearchRequest<SnomedReferenceSets> create() {
		return new SnomedRefSetSearchRequest();
	}
	
	public SnomedRefSetSearchRequestBuilder filterByType(SnomedRefSetType type) {
		return filterByTypes(Collections.singleton(type));
	}
	
	public SnomedRefSetSearchRequestBuilder filterByTypes(Collection<SnomedRefSetType> refSetTypes) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.TYPE, refSetTypes);
	}

	public SnomedRefSetSearchRequestBuilder filterByReferencedComponentTypes(String referencedComponentType) {
		if (Strings.isNullOrEmpty(referencedComponentType)) {
			return getSelf();
		}
		if (CoreTerminologyBroker.UNSPECIFIED.equals(referencedComponentType)) {
			return getSelf();
		}
		final int referencedComponentTypeAsInt = (int) CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(referencedComponentType);
		return filterByReferencedComponentTypes(referencedComponentTypeAsInt);
	}
	
	public SnomedRefSetSearchRequestBuilder filterByReferencedComponentTypes(Integer referencedComponentType) {
		if (referencedComponentType == null) {
			return getSelf();
		}
		return filterByReferencedComponentTypes(Collections.singleton(referencedComponentType));
	}
	
	public SnomedRefSetSearchRequestBuilder filterByReferencedComponentTypes(Collection<Integer> referencedComponentTypes) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.REFERENCED_COMPONENT_TYPE, referencedComponentTypes);
	}

}
