/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
 * @since 7.4
 */
final class SnomedComplexBlockMapMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedComplexBlockMapMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(SnomedReferenceSet refSet, TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.COMPLEX_BLOCK_MAP);
		checkReferencedComponent(refSet);
		checkHasProperty(SnomedRf2Headers.FIELD_MAP_GROUP);
		checkHasProperty(SnomedRf2Headers.FIELD_MAP_PRIORITY);
		checkHasProperty(SnomedRf2Headers.FIELD_MAP_RULE);
		checkHasProperty(SnomedRf2Headers.FIELD_MAP_ADVICE);
		checkHasProperty(SnomedRf2Headers.FIELD_MAP_BLOCK);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_CORRELATION_ID);
		
		if (SnomedIdentifiers.isValid(getProperty(SnomedRf2Headers.FIELD_MAP_TARGET))) {
			checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MAP_TARGET);
		}
		
		SnomedRefSetMemberIndexEntry member = SnomedComponents.newComplexBlockMapMember()
				.withId(getId())
				.withActive(isActive())
				.withReferencedComponent(getReferencedComponentId())
				.withModule(getModuleId())
				.withRefSet(getReferenceSetId())
				.withMapTargetId(getComponentId(SnomedRf2Headers.FIELD_MAP_TARGET))
				.withGroup(getProperty(SnomedRf2Headers.FIELD_MAP_GROUP, Integer.class))
				.withPriority(getProperty(SnomedRf2Headers.FIELD_MAP_PRIORITY, Integer.class))
				.withMapRule(getProperty(SnomedRf2Headers.FIELD_MAP_RULE))
				.withMapAdvice(getProperty(SnomedRf2Headers.FIELD_MAP_ADVICE))
				.withCorrelationId(getComponentId(SnomedRf2Headers.FIELD_CORRELATION_ID))
				.withBlock(getProperty(SnomedRf2Headers.FIELD_MAP_BLOCK, Integer.class))
				.addTo(context);

		return member.getId();
	}

	@Override
	protected Set<String> getRequiredComponentIds() {
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MAP_TARGET);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_CORRELATION_ID);
		
		Builder<String> requiredComponentIds = ImmutableSet.<String>builder()
			.add(getComponentId(SnomedRf2Headers.FIELD_CORRELATION_ID));
		
		if (SnomedIdentifiers.isValid(getProperty(SnomedRf2Headers.FIELD_MAP_TARGET))) {
			requiredComponentIds.add(getComponentId(SnomedRf2Headers.FIELD_MAP_TARGET));
		}
		
		return requiredComponentIds.build();
	}
}
