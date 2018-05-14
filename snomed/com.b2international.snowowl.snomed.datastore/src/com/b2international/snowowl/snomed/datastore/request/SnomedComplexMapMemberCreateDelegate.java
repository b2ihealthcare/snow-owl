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

import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
 */
final class SnomedComplexMapMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedComplexMapMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(SnomedRefSet refSet, TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.COMPLEX_MAP);
		checkReferencedComponent(refSet);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_MAP_TARGET);
		checkHasProperty(refSet, SnomedRf2Headers.FIELD_MAP_GROUP);
		checkHasProperty(refSet, SnomedRf2Headers.FIELD_MAP_PRIORITY);
		checkHasProperty(refSet, SnomedRf2Headers.FIELD_MAP_RULE);
		checkHasProperty(refSet, SnomedRf2Headers.FIELD_MAP_ADVICE);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_CORRELATION_ID);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());
		// FIXME: check map target if it's also in SNOMED CT?
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_CORRELATION_ID);

		SnomedComplexMapRefSetMember member = SnomedComponents.newComplexMapMember()
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
				.addTo(context);

		return member.getUuid();
	}

	@Override
	protected Set<String> getRequiredComponentIds() {
		return ImmutableSet.of(getComponentId(SnomedRf2Headers.FIELD_CORRELATION_ID));
	}
}
