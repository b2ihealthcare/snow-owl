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
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 5.0
 */
final class SnomedConcreteDomainMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedConcreteDomainMemberUpdateDelegate(SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(SnomedRefSetMemberIndexEntry original, SnomedRefSetMemberIndexEntry.Builder member, TransactionContext context) {
		String newValue = getProperty(SnomedRf2Headers.FIELD_VALUE);
		int newGroup = getProperty(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, Integer.class);
		String newTypeId = getComponentId(SnomedRf2Headers.FIELD_TYPE_ID);
		String newCharacteristicTypeId = getComponentId(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);

		boolean changed = false;

		if (newValue != null && !newValue.equals(SnomedRefSetUtil.serializeValue(original.getDataType(), original.getValue()))) {
			member.field(SnomedRf2Headers.FIELD_VALUE, newValue);
			changed |= true;
		}

		if (newGroup != original.getGroup()) {
			member.field(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, newGroup);
			changed |= true;
		}

		if (newTypeId != null && !newTypeId.equals(original.getTypeId())) {
			member.field(SnomedRf2Headers.FIELD_TYPE_ID, newTypeId);
			changed |= true;
		}

		if (newCharacteristicTypeId != null && !newCharacteristicTypeId.equals(original.getCharacteristicTypeId())) {
			member.field(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, newCharacteristicTypeId);
			changed |= true;
		}

		return changed;
	}
}
