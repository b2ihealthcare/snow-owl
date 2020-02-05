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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * @since 4.5
 */
public final class SnomedRelationshipBuilder extends SnomedComponentBuilder<SnomedRelationshipBuilder, SnomedRelationshipIndexEntry.Builder, SnomedRelationshipIndexEntry> {

	private CharacteristicType characteristicType = CharacteristicType.STATED_RELATIONSHIP;
	private RelationshipModifier modifier = RelationshipModifier.EXISTENTIAL;
	private String type;
	private String source;
	private String destination;
	private int group = 0;
	private int unionGroup = 0;
	private boolean destinationNegated = false;

	/**
	 * Specifies the characteristic type of the new SNOMED CT Relationship.
	 * 
	 * @param characteristicType
	 *            - the characteristic type to use
	 * @return
	 */
	public final SnomedRelationshipBuilder withCharacteristicType(CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
		return getSelf();
	}

	/**
	 * Specifies the modifier of the new SNOMED CT Relationship.
	 * 
	 * @param modifier
	 *            - the modifier to use
	 * @return
	 */
	public final SnomedRelationshipBuilder withModifier(RelationshipModifier modifier) {
		this.modifier = modifier;
		return getSelf();
	}

	/**
	 * Specifies that the new SNOMED CT Relationship will be an IS A relationship.
	 * 
	 * @return
	 */
	public final SnomedRelationshipBuilder isa() {
		return withType(Concepts.IS_A);
	}

	/**
	 * Specifies the type of the new SNOMED CT Relationship.
	 * 
	 * @param type
	 *            - the type concept's SNOMED CT identifier
	 * @return
	 */
	public final SnomedRelationshipBuilder withType(String type) {
		this.type = type;
		return getSelf();
	}

	/**
	 * Specifies the destination of the new SNOMED CT Relationship.
	 * 
	 * @param destination
	 *            - the destination to point this relationship to
	 * @return
	 */
	public final SnomedRelationshipBuilder withDestination(String destination) {
		this.destination = destination;
		return getSelf();
	}

	/**
	 * Specifies the group of the new SNOMED CT Relationship.
	 * 
	 * @param group
	 *            - the group number to use
	 * @return
	 */
	public final SnomedRelationshipBuilder withGroup(int group) {
		this.group = group;
		return getSelf();
	}

	/**
	 * Specifies the union group of the new SNOMED CT Relationship.
	 * 
	 * @param unionGroup
	 *            - the union group to use
	 * @return
	 */
	public final SnomedRelationshipBuilder withUnionGroup(int unionGroup) {
		this.unionGroup = unionGroup;
		return getSelf();
	}

	/**
	 * Specifies the destination negated flag of the new SNOMED CT Relationship.
	 * 
	 * @param destinationNegated
	 * @return
	 */
	public final SnomedRelationshipBuilder withDestinationNegated(boolean destinationNegated) {
		this.destinationNegated = destinationNegated;
		return getSelf();
	}

	/**
	 * Specifies the source of the SNOMED CT Relationship.
	 * 
	 * @param source
	 * @return
	 */
	public SnomedRelationshipBuilder withSource(String source) {
		this.source = source;
		return getSelf();
	}

	@Override
	protected SnomedRelationshipIndexEntry.Builder create() {
		return SnomedRelationshipIndexEntry.builder();
	}

	@Override
	public void init(SnomedRelationshipIndexEntry.Builder component, TransactionContext context) {
		super.init(component, context);
		component.characteristicTypeId(context.lookup(characteristicType.getConceptId(), SnomedConceptDocument.class).getId());
		component.modifierId(context.lookup(modifier.getConceptId(), SnomedConceptDocument.class).getId());
		component.typeId(context.lookup(type, SnomedConceptDocument.class).getId());
		if (source != null) {
			component.sourceId(context.lookup(source, SnomedConceptDocument.class).getId());
		}
		component.destinationId(context.lookup(destination, SnomedConceptDocument.class).getId());
		component.destinationNegated(destinationNegated);
		component.group(group);
		component.unionGroup(unionGroup);
	}

}
