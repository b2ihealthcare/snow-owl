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

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.core.store.SnomedModuleDependencyReferenceSetMemberBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 5.0
 */
final class SnomedModuleDependencyMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedModuleDependencyMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(SnomedRefSet refSet, TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.MODULE_DEPENDENCY);
		checkReferencedComponent(refSet);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());

		SnomedModuleDependencyReferenceSetMemberBuilder builder = SnomedComponents.newModuleDependencyMember()
				.withId(getId())
				.withActive(isActive())
				.withReferencedComponent(getReferencedComponentId())
				.withModule(getModuleId())
				.withRefSet(getReferenceSetId());
		
		try {
			
			if (hasProperty(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME)) {
				builder.withSourceEffectiveTime(EffectiveTimes.parse(getProperty(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME), DateFormats.SHORT));
			}
			
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("Error while parsing date")) {
				throw new BadRequestException(e.getMessage());
			}
		}
		
		
		try {
			
			if (hasProperty(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
				builder.withTargetEffectiveTime(EffectiveTimes.parse(getProperty(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME), DateFormats.SHORT));
			}
			
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("Error while parsing date")) {
				throw new BadRequestException(e.getMessage());
			}
		}

			
		return builder.addTo(context).getUuid();
	}

}
