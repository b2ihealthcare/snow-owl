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

import com.b2international.snowowl.datastore.index.mapping.FieldsToLoadBuilderBase;

/**
 * @since 4.3
 */
public class SnomedFieldsToLoadBuilder extends FieldsToLoadBuilderBase<SnomedFieldsToLoadBuilder> {

	protected SnomedFieldsToLoadBuilder() {
		super();
	}

	public SnomedFieldsToLoadBuilder parent(String characteristicTypeId) {
		return field(SnomedMappings.parent(characteristicTypeId));
	}
	
	public SnomedFieldsToLoadBuilder ancestor(String characteristicTypeId) {
		return field(SnomedMappings.ancestor(characteristicTypeId));
	}
	
	public SnomedFieldsToLoadBuilder memberReferencedComponentId() {
		return field(SnomedMappings.memberReferencedComponentId());
	}
	
	public SnomedFieldsToLoadBuilder memberReferenceSetId() {
		return field(SnomedMappings.memberRefSetId());
	}
	
	public SnomedFieldsToLoadBuilder memberReferenceSetType() {
		return field(SnomedMappings.memberRefSetType());
	}
	
	public SnomedFieldsToLoadBuilder module() {
		return field(SnomedMappings.module());
	}

	public SnomedFieldsToLoadBuilder active() {
		return field(SnomedMappings.active());
	}
	
	public SnomedFieldsToLoadBuilder released() {
		return field(SnomedMappings.released());
	}

	public SnomedFieldsToLoadBuilder descriptionType() {
		return field(SnomedMappings.descriptionType());
	}
	
	public SnomedFieldsToLoadBuilder descriptionConcept() {
		return field(SnomedMappings.descriptionConcept());
	}

	public SnomedFieldsToLoadBuilder relationshipType() {
		return field(SnomedMappings.relationshipType());
	}

	public SnomedFieldsToLoadBuilder relationshipCharacteristicType() {
		return field(SnomedMappings.relationshipCharacteristicType());
	}

	public SnomedFieldsToLoadBuilder refSetStorageKey() {
		return field(SnomedMappings.refSetStorageKey());
	}

	public SnomedFieldsToLoadBuilder effectiveTime() {
		return field(SnomedMappings.effectiveTime());
	}
}
