/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableSet;

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
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_VALUE);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_TYPE_ID);
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);

		DataType dataType = ((SnomedConcreteDataTypeRefSet) refSet).getDataType();
		String value = getProperty(SnomedRf2Headers.FIELD_VALUE);

		try {
			SnomedRefSetUtil.deserializeValue(dataType, value);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Couldn't deserialize value '%s' for data type '%s'.", value, dataType);
		}

		SnomedConcreteDataTypeRefSetMember member = SnomedComponents.newConcreteDomainReferenceSetMember()
				.withId(getId())
				.withActive(isActive())
				.withCharacteristicType(CharacteristicType.getByConceptId(getComponentId(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID)))
				.withGroup(getProperty(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, Integer.class))
				.withModule(getModuleId())
				.withReferencedComponent(getReferencedComponentId())
				.withRefSet(getReferenceSetId())
				.withSerializedValue(getProperty(SnomedRf2Headers.FIELD_VALUE))
				.withTypeId(getComponentId(SnomedRf2Headers.FIELD_TYPE_ID))
				.addTo(context);

		return member.getUuid();
	}

	@Override
	protected Set<String> getRequiredComponentIds() {
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_TYPE_ID);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);
		return ImmutableSet.of(
				getComponentId(SnomedRf2Headers.FIELD_TYPE_ID), 
				getComponentId(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID));
	}
}
