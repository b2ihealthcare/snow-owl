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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 4.6
 */
public final class SnomedConcreteDomainReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedConcreteDomainReferenceSetMemberBuilder> {

	private String uomId;
	private String operatorId = Concepts.CD_EQUAL;
	private String attributeLabel;
	private String serializedValue;
	private CharacteristicType characteristicType = CharacteristicType.STATED_RELATIONSHIP;
	
	public SnomedConcreteDomainReferenceSetMemberBuilder withUom(String uomId) {
		this.uomId = uomId;
		return getSelf();
	}
	
	public SnomedConcreteDomainReferenceSetMemberBuilder withOperatorId(String operatorId) {
		this.operatorId = operatorId;
		return getSelf();
	}
	
	public SnomedConcreteDomainReferenceSetMemberBuilder withAttributeLabel(String label) {
		this.attributeLabel = label;
		return getSelf();
	}
	
	public SnomedConcreteDomainReferenceSetMemberBuilder withSerializedValue(String serializedValue) {
		this.serializedValue = serializedValue;
		return getSelf();
	}
	
	public SnomedConcreteDomainReferenceSetMemberBuilder withCharacteristicType(CharacteristicType characteristicType) {
		this.characteristicType = characteristicType;
		return getSelf();
	}
	
	@Override
	public void init(SnomedRefSetMemberIndexEntry.Builder component, TransactionContext context) {
		super.init(component, context);
		component
			.field(SnomedRf2Headers.FIELD_UNIT_ID, uomId)
			.field(SnomedRf2Headers.FIELD_OPERATOR_ID, operatorId)
			.field(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, attributeLabel)
			.field(SnomedRf2Headers.FIELD_VALUE, serializedValue)
			.field(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, characteristicType.getConceptId());
	}

}
