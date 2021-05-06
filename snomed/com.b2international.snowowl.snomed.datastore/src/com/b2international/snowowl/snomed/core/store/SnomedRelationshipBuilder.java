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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * @since 4.5
 */
public final class SnomedRelationshipBuilder 
	extends SnomedComponentBuilder<SnomedRelationshipBuilder, SnomedRelationshipIndexEntry.Builder, SnomedRelationshipIndexEntry> {

	private String sourceId;
	private String typeId;
	private String destinationId;
	private boolean destinationNegated = false;
	private RelationshipValue value;
	private int group = 0;
	private int unionGroup = 0;
	private String characteristicTypeId = Concepts.STATED_RELATIONSHIP;
	private String modifierId = Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;

	/**
	 * Specifies the source of the SNOMED CT Relationship.
	 * 
	 * @param sourceId - the source concept ID
	 * @return
	 */
	public SnomedRelationshipBuilder withSourceId(final String sourceId) {
		this.sourceId = sourceId;
		return getSelf();
	}

	/**
	 * Specifies the type of the new SNOMED CT Relationship.
	 * 
	 * @param typeId - the type concept ID
	 * @return
	 */
	public SnomedRelationshipBuilder withTypeId(final String typeId) {
		this.typeId = typeId;
		return getSelf();
	}

	/**
	 * Specifies the destination of the new SNOMED CT Relationship.
	 * 
	 * @param destination - the destination concept Id
	 * @return
	 */
	public SnomedRelationshipBuilder withDestinationId(final String destinationId) {
		this.destinationId = destinationId;
		return getSelf();
	}

	/**
	 * Specifies the destination negated flag of the new SNOMED CT Relationship.
	 * 
	 * @param destinationNegated
	 * @return
	 */
	public SnomedRelationshipBuilder withDestinationNegated(final boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
		return getSelf();
	}

	/**
	 * Specifies the relationship value for the new SNOMED CT Relationship.
	 * 
	 * @param value
	 * @return
	 */
	public SnomedRelationshipBuilder withValue(final RelationshipValue value) {
		this.value = value;
		return getSelf();
	}

	/**
	 * Specifies the group of the new SNOMED CT Relationship.
	 * 
	 * @param group - the group number to use
	 * @return
	 */
	public SnomedRelationshipBuilder withGroup(final int group) {
		this.group = group;
		return getSelf();
	}

	/**
	 * Specifies the union group of the new SNOMED CT Relationship.
	 * 
	 * @param unionGroup - the union group to use
	 * @return
	 */
	public SnomedRelationshipBuilder withUnionGroup(final int unionGroup) {
		this.unionGroup = unionGroup;
		return getSelf();
	}

	/**
	 * Specifies the characteristic type of the new SNOMED CT Relationship.
	 * 
	 * @param characteristicTypeId - the characteristic type to use
	 * @return
	 */
	public SnomedRelationshipBuilder withCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
		return getSelf();
	}

	/**
	 * Specifies the modifier of the new SNOMED CT Relationship.
	 * 
	 * @param modifierId - the modifier to use
	 * @return
	 */
	public SnomedRelationshipBuilder withModifierId(final String modifierId) {
		this.modifierId = modifierId;
		return getSelf();
	}

	@Override
	protected SnomedRelationshipIndexEntry.Builder create() {
		return SnomedRelationshipIndexEntry.builder();
	}
	
	private String ensureConceptExists(final String conceptId, final TransactionContext context) {
		return context.lookup(conceptId, SnomedConceptDocument.class).getId();
	}

	@Override
	public void init(final SnomedRelationshipIndexEntry.Builder component, final TransactionContext context) {
		super.init(component, context);
		
		if (sourceId != null) { 
			component.sourceId(ensureConceptExists(sourceId, context)); 
		}
		
		if (destinationId != null) { 
			component.destinationId(ensureConceptExists(destinationId, context)); 
		}
		
		component.typeId(ensureConceptExists(typeId, context));
		component.destinationNegated(destinationNegated);
		component.value(value);
		component.group(group);
		component.unionGroup(unionGroup);
		component.characteristicTypeId(ensureConceptExists(characteristicTypeId, context));
		component.modifierId(ensureConceptExists(modifierId, context));
	}
}
