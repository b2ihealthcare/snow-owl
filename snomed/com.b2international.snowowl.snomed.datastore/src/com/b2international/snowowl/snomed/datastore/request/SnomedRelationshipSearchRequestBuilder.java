/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
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
		return addOption(OptionKey.SOURCE, sourceIds);
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
		return addOption(OptionKey.DESTINATION, destinationIds);
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
	
	public SnomedRelationshipSearchRequestBuilder filterByModifier(String modifier) {
		return addOption(OptionKey.MODIFIER, modifier);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByModifier(Iterable<String> modifier) {
		return addOption(OptionKey.MODIFIER, modifier);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByValueType(RelationshipValueType valueType) {
		return addOption(OptionKey.VALUE_TYPE, valueType);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByValueTypes(Iterable<RelationshipValueType> valueTypes) {
		return addOption(OptionKey.VALUE_TYPE, valueTypes);
	}

	public SnomedRelationshipSearchRequestBuilder filterByValue(RelationshipValue value) {
		return filterByValue(SearchResourceRequest.Operator.EQUALS, value);
	}
	
	public SnomedRelationshipSearchRequestBuilder filterByValues(Iterable<RelationshipValue> values) {
		return filterByValues(SearchResourceRequest.Operator.EQUALS, values);
	}

	public SnomedRelationshipSearchRequestBuilder filterByValue(SearchResourceRequest.Operator op, RelationshipValue value) {
		return addOption(OptionKey.OPERATOR, op).addOption(OptionKey.VALUE, value);
	}

	public SnomedRelationshipSearchRequestBuilder filterByValues(SearchResourceRequest.Operator op, Iterable<RelationshipValue> values) {
		return addOption(OptionKey.OPERATOR, op).addOption(OptionKey.VALUE, values);
	}
	
	public SnomedRelationshipSearchRequestBuilder hasDestinationId() {
		return addOption(OptionKey.HAS_DESTINATION_ID, true);
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, SnomedRelationships> createSearch() {
		return new SnomedRelationshipSearchRequest();
	}
}
