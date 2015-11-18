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
package com.b2international.snowowl.snomed.datastore.index.mapping;

import com.b2international.snowowl.datastore.index.mapping.QueryBuilderBase;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.3
 */
public class SnomedQueryBuilder extends QueryBuilderBase<SnomedQueryBuilder> {

	protected SnomedQueryBuilder() {
		super();
	}

	@Override
	public final SnomedQueryBuilder id(String value) {
		return id(Long.parseLong(value));
	}

	@Override
	public final SnomedQueryBuilder parent(String value) {
		return parent(Long.parseLong(value));
	}

	@Override
	public final SnomedQueryBuilder ancestor(String value) {
		return ancestor(Long.parseLong(value));
	}
	
	public SnomedQueryBuilder id(Long value) {
		return addToQuery(SnomedMappings.id(), value);
	}
	
	public SnomedQueryBuilder parent(Long value) {
		return addToQuery(SnomedMappings.parent(), value);
	}
	
	public final SnomedQueryBuilder parent(String value, String characteristicTypeId) {
		return field(SnomedMappings.parent(characteristicTypeId).fieldName(), Long.valueOf(value));
	}
	
	public final SnomedQueryBuilder parent(Long value, String characteristicTypeId) {
		return field(SnomedMappings.parent(characteristicTypeId).fieldName(), Long.valueOf(value));
	}

	public SnomedQueryBuilder ancestor(Long value) {
		return addToQuery(SnomedMappings.ancestor(), value);
	}
	
	public final SnomedQueryBuilder ancestor(String value, String characteristicTypeId) {
		return field(SnomedMappings.ancestor(characteristicTypeId).fieldName(), Long.valueOf(value));
	}
	
	public final SnomedQueryBuilder ancestor(Long value, String characteristicTypeId) {
		return field(SnomedMappings.ancestor(characteristicTypeId).fieldName(), Long.valueOf(value));
	}

	public SnomedQueryBuilder memberRefSetType(SnomedRefSetType type) {
		return memberRefSetType(type.getValue());
	}
	
	public SnomedQueryBuilder memberRefSetType(int type) {
		return addToQuery(SnomedMappings.memberRefSetType(), type);
	}
	
	public SnomedQueryBuilder memberRefSetId(String value) {
		return memberRefSetId(Long.parseLong(value));
	}
	
	public SnomedQueryBuilder memberRefSetId(long value) {
		return addToQuery(SnomedMappings.memberRefSetId(), value);
	}
	
	public SnomedQueryBuilder memberReferencedComponentId(String value) {
		return memberReferencedComponentId(Long.valueOf(value));
	}
	
	public SnomedQueryBuilder memberReferencedComponentId(Long value) {
		return addToQuery(SnomedMappings.memberReferencedComponentId(), value);
	}
	
	public SnomedQueryBuilder memberReferencedComponentType(int value) {
		return addToQuery(SnomedMappings.memberReferencedComponentType(), value);
	}

	public SnomedQueryBuilder active() {
		return addToQuery(SnomedMappings.active(), 1);
	}
	
	public SnomedQueryBuilder inactive() {
		return addToQuery(SnomedMappings.active(), 0);
	}

	public SnomedQueryBuilder relationshipType(String value) {
		return relationshipType(Long.valueOf(value));
	}
	
	public SnomedQueryBuilder relationshipType(Long value) {
		return addToQuery(SnomedMappings.relationshipType(), value);
	}

	public SnomedQueryBuilder relationshipCharacteristicType(String value) {
		return relationshipCharacteristicType(Long.valueOf(value));
	}
	
	public SnomedQueryBuilder relationshipCharacteristicType(Long value) {
		return addToQuery(SnomedMappings.relationshipCharacteristicType(), value);
	}

	public SnomedQueryBuilder descriptionType(String value) {
		return descriptionType(Long.valueOf(value));
	}

	public SnomedQueryBuilder descriptionType(Long value) {
		return addToQuery(SnomedMappings.descriptionType(), value);
	}

	public SnomedQueryBuilder descriptionConcept(String value) {
		return descriptionConcept(Long.valueOf(value));
	}
	
	public SnomedQueryBuilder descriptionConcept(Long value) {
		return addToQuery(SnomedMappings.descriptionConcept(), value);
	}

	public SnomedQueryBuilder module(String value) {
		return module(Long.valueOf(value));
	}

	public SnomedQueryBuilder module(Long value) {
		return addToQuery(SnomedMappings.module(), value);
	}

	public SnomedQueryBuilder concept() {
		return type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
	}
	
	public SnomedQueryBuilder relationship() {
		return type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER);
	}
	
	public SnomedQueryBuilder description() {
		return type(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER);
	}
	
	public SnomedQueryBuilder refSet() {
		return type(SnomedTerminologyComponentConstants.REFSET_NUMBER);
	}

	public SnomedQueryBuilder predicate() {
		return type(SnomedTerminologyComponentConstants.PREDICATE_TYPE_ID);
	}

	public SnomedQueryBuilder refSetStorageKey(Long value) {
		return addToQuery(SnomedMappings.refSetStorageKey(), value);
	}

	public SnomedQueryBuilder effectiveTime(Long value) {
		return addToQuery(SnomedMappings.effectiveTime(), value);
	}
}
