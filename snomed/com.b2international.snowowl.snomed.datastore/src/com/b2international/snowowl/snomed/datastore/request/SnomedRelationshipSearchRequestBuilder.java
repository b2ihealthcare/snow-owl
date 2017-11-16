/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequest.OptionKey;

/**
 * <i>Builder</i> class to build requests responsible for searching SNOMED CT relationships.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * Filter methods restrict the results set returned from the search requests; 
 * what passes the filters will be returned as part of the pageable resultset.
 * 
 * @since 4.5
 */
public final class SnomedRelationshipSearchRequestBuilder extends SnomedComponentSearchRequestBuilder<SnomedRelationshipSearchRequestBuilder, SnomedRelationships> {

	SnomedRelationshipSearchRequestBuilder() {
		super();
	}

	public SnomedRelationshipSearchRequestBuilder filterBySource(String sourceId) {
		return addOption(OptionKey.SOURCE, sourceId);
	}

	public SnomedRelationshipSearchRequestBuilder filterBySource(Iterable<String> sourceIds) {
		return addOption(OptionKey.SOURCE, Collections3.toImmutableSet(sourceIds));
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByType(String typeId) {
		return addOption(OptionKey.TYPE, typeId);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByType(Iterable<String> typeIds) {
		return addOption(OptionKey.TYPE, typeIds);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByDestination(String destinationId) {
		return addOption(OptionKey.DESTINATION, destinationId);
	}

	public SnomedRelationshipSearchRequestBuilder filterByDestination(Iterable<String> destinationIds) {
		return addOption(OptionKey.DESTINATION, Collections3.toImmutableSet(destinationIds));
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByCharacteristicType(String characteristicType) {
		return addOption(OptionKey.CHARACTERISTIC_TYPE, characteristicType);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByCharacteristicTypes(Iterable<String> characteristicType) {
		return addOption(OptionKey.CHARACTERISTIC_TYPE, characteristicType);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByGroup(Integer group) {
		return filterByGroup(group, group);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByUnionGroup(Integer unionGroup) {
		return addOption(OptionKey.UNION_GROUP, unionGroup);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByGroup(Integer groupMin, Integer groupMax) {
		return addOption(OptionKey.GROUP_MIN, groupMin).addOption(OptionKey.GROUP_MAX, groupMax);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByModifier(RelationshipModifier modifier) {
		return addOption(OptionKey.MODIFIER, modifier);
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, SnomedRelationships> createSearch() {
		return new SnomedRelationshipSearchRequest();
	}

}
