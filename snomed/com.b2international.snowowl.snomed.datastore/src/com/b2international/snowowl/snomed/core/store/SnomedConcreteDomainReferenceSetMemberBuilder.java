/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 4.6
 */
public class SnomedConcreteDomainReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedConcreteDomainReferenceSetMemberBuilder, SnomedConcreteDataTypeRefSetMember> {

	private String uomId;
	private String operatorId = Concepts.CD_EQUAL;
	private String attributeLabel;
	private String serializedValue;
	private CharacteristicType characteristicType = CharacteristicType.STATED_RELATIONSHIP;
	
	SnomedConcreteDomainReferenceSetMemberBuilder() {
		super(ComponentCategory.SET_MEMBER);
	}

	@Override
	protected SnomedConcreteDataTypeRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSetMember();
	}

	public final SnomedConcreteDomainReferenceSetMemberBuilder withUom(final String uomId) {
		this.uomId = uomId;
		return getSelf();
	}
	
	public final SnomedConcreteDomainReferenceSetMemberBuilder withOperatorId(final String operatorId) {
		this.operatorId = operatorId;
		return getSelf();
	}
	
	public final SnomedConcreteDomainReferenceSetMemberBuilder withAttributeLabel(final String label) {
		this.attributeLabel = label;
		return getSelf();
	}
	
	public final SnomedConcreteDomainReferenceSetMemberBuilder withSerializedValue(final String serializedValue) {
		this.serializedValue = serializedValue;
		return getSelf();
	}
	
	public final SnomedConcreteDomainReferenceSetMemberBuilder withCharacteristicType(final CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
		return getSelf();
	}
	
	@Override
	public SnomedConcreteDataTypeRefSetMember addTo(final TransactionContext context) {
		final SnomedConcreteDataTypeRefSetMember member = build(context);
		final Annotatable annotatable = getAnnotatable(context, member.getReferencedComponentId());
		annotatable.getConcreteDomainRefSetMembers().add(member);
		return member;
	}

	private Annotatable getAnnotatable(final TransactionContext context, final String referencedComponentId) {
		try {
			return context.lookup(referencedComponentId, Concept.class);
		} catch (final NotFoundException e) {
			return context.lookup(referencedComponentId, Relationship.class);
		}
	}
	
	@Override
	protected void init(final SnomedConcreteDataTypeRefSetMember component, final TransactionContext context) {
		super.init(component, context);
		component.setUomComponentId(uomId);
		component.setOperatorComponentId(operatorId);
		component.setLabel(attributeLabel);
		component.setSerializedValue(serializedValue);
		component.setCharacteristicTypeId(characteristicType.getConceptId());
	}
	
}
