/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 5.0
 */
final class SnomedConcreteDomainMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedConcreteDomainMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(SnomedRefSet refSet, TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.CONCRETE_DATA_TYPE);
		checkReferencedComponent(refSet);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_ATTRIBUTE_NAME);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_VALUE);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_OPERATOR_ID);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_UNIT_ID);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_OPERATOR_ID);
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_UNIT_ID);

		SnomedConcreteDataTypeRefSetMember member = SnomedComponents.newConcreteDomainReferenceSetMember()
				.withActive(isActive())
				.withAttributeLabel(getComponentId(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME))
				.withCharacteristicType(CharacteristicType.getByConceptId(getComponentId(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID)))
				.withModule(getModuleId())
				.withOperatorId(getComponentId(SnomedRf2Headers.FIELD_OPERATOR_ID))
				.withReferencedComponent(getReferencedComponentId())
				.withRefSet(getReferenceSetId())
				.withSerializedValue(getProperty(SnomedRf2Headers.FIELD_VALUE))
				.withUom(getComponentId(SnomedRf2Headers.FIELD_UNIT_ID))
				.addTo(context);

		return member.getUuid();
	}

}
