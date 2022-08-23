/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 8.4
 */
final class SnomedSimpleMapToMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedSimpleMapToMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(SnomedReferenceSet refSet, TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.SIMPLE_MAP_TO);
		checkReferencedComponent(refSet);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());

		// XXX: "Map from SNOMED CT to SNOMED CT" is symmetric; not sure if anyone uses the "map to" type for this use case
		if (SnomedIdentifiers.isValid(getProperty(SnomedRf2Headers.FIELD_MAP_SOURCE))) {
			checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MAP_SOURCE);
		}
		
		SnomedRefSetMemberIndexEntry member = SnomedComponents.newSimpleMapToMember()
				.withId(getId())
				.withActive(isActive())
				.withReferencedComponent(getReferencedComponentId())
				.withModuleId(getModuleId())
				.withRefSet(getReferenceSetId())
				.withMapSourceId(getComponentId(SnomedRf2Headers.FIELD_MAP_SOURCE))
				.addTo(context);

		return member.getId();
	}

	@Override
	protected Set<String> getRequiredComponentIds() {
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MAP_SOURCE);

		Builder<String> requiredComponentIds = ImmutableSet.<String>builder();
		
		if (SnomedIdentifiers.isValid(getProperty(SnomedRf2Headers.FIELD_MAP_SOURCE))) {
			requiredComponentIds.add(getComponentId(SnomedRf2Headers.FIELD_MAP_SOURCE));
		}
		
		return requiredComponentIds.build();
	}
	
}
