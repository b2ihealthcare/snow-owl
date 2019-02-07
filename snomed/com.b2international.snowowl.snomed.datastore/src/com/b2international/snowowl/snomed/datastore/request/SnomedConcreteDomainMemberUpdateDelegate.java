/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
		String newValue = getProperty(SnomedRf2Headers.FIELD_VALUE);
		Integer newGroup = getProperty(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, Integer.class);
		String newTypeId = getComponentId(SnomedRf2Headers.FIELD_TYPE_ID);
		String newCharacteristicTypeId = getComponentId(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);

		boolean changed = false;

		if (newValue != null && !newValue.equals(concreteDomainMember.getSerializedValue())) {
			concreteDomainMember.setSerializedValue(newValue);
			changed |= true;
		}

		if (newGroup != null && newGroup.intValue() != concreteDomainMember.getGroup()) {
			concreteDomainMember.setGroup(newGroup);
			changed |= true;
		}

		if (newTypeId != null && !newTypeId.equals(concreteDomainMember.getTypeId())) {
			concreteDomainMember.setTypeId(newTypeId);
			changed |= true;
		}

		if (newCharacteristicTypeId != null && !newCharacteristicTypeId.equals(concreteDomainMember.getCharacteristicTypeId())) {
			concreteDomainMember.setCharacteristicTypeId(newCharacteristicTypeId);
			changed |= true;
		}

		return changed;
	}
}
