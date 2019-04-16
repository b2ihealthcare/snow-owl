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
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 5.0
 */
final class SnomedSimpleMapMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedSimpleMapMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(SnomedReferenceSet refSet, TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.SIMPLE_MAP);
		checkReferencedComponent(refSet);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());

		if (SnomedIdentifiers.isValid(getProperty(SnomedRf2Headers.FIELD_MAP_TARGET))) {
			checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MAP_TARGET);
		}
		
		SnomedRefSetMemberIndexEntry member = SnomedComponents.newSimpleMapMember()
				.withId(getId())
				.withActive(isActive())
				.withReferencedComponent(getReferencedComponentId())
				.withModule(getModuleId())
				.withRefSet(getReferenceSetId())
				.withMapTargetId(getComponentId(SnomedRf2Headers.FIELD_MAP_TARGET))
				.addTo(context);

		return member.getId();
	}

	@Override
	protected Set<String> getRequiredComponentIds() {
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MAP_TARGET);

		Builder<String> requiredComponentIds = ImmutableSet.<String>builder();
		
		if (SnomedIdentifiers.isValid(getProperty(SnomedRf2Headers.FIELD_MAP_TARGET))) {
			requiredComponentIds.add(getComponentId(SnomedRf2Headers.FIELD_MAP_TARGET));
		}
		
		return requiredComponentIds.build();
	}
	
}
