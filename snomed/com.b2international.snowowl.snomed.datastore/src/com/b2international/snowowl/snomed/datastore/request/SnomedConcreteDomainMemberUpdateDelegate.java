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
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 5.0
 */
final class SnomedConcreteDomainMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedConcreteDomainMemberUpdateDelegate(SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(SnomedRefSetMember member, TransactionContext context) {
		SnomedConcreteDataTypeRefSetMember concreteDomainMember = (SnomedConcreteDataTypeRefSetMember) member;
		String newAttributeName = getComponentId(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME);
		String newCharacteristicTypeId = getComponentId(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);
		String newValue = getProperty(SnomedRf2Headers.FIELD_VALUE);
		String newOperatorId = getComponentId(SnomedRf2Headers.FIELD_OPERATOR_ID);
		String newUnitId = getComponentId(SnomedRf2Headers.FIELD_UNIT_ID);

		boolean changed = false;

		if (newAttributeName != null && !newAttributeName.equals(concreteDomainMember.getLabel())) {
			concreteDomainMember.setLabel(newAttributeName);
			changed |= true;
		}

		if (newCharacteristicTypeId != null && !newCharacteristicTypeId.equals(concreteDomainMember.getCharacteristicTypeId())) {
			concreteDomainMember.setCharacteristicTypeId(newCharacteristicTypeId);
			changed |= true;
		}

		if (newValue != null && !newValue.equals(concreteDomainMember.getSerializedValue())) {
			concreteDomainMember.setSerializedValue(newValue);
			changed |= true;
		}

		if (newOperatorId != null && !newOperatorId.equals(concreteDomainMember.getOperatorComponentId())) {
			concreteDomainMember.setOperatorComponentId(newOperatorId);
			changed |= true;
		}

		if (newUnitId != null && !newUnitId.equals(concreteDomainMember.getUomComponentId())) {
			concreteDomainMember.setUomComponentId(newUnitId);
			changed |= true;
		}

		return changed;
	}

}
