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
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 5.0
 */
final class SnomedComplexMapMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedComplexMapMemberUpdateDelegate(SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(SnomedRefSetMember member, TransactionContext context) {
		SnomedComplexMapRefSetMember concreteDomainMember = (SnomedComplexMapRefSetMember) member;
		String newMapTargetId = getProperty(SnomedRf2Headers.FIELD_MAP_TARGET);
		Byte newGroup = getProperty(SnomedRf2Headers.FIELD_MAP_GROUP, Byte.class);
		Byte newPriority = getProperty(SnomedRf2Headers.FIELD_MAP_PRIORITY, Byte.class);
		String newMapRule = getProperty(SnomedRf2Headers.FIELD_MAP_RULE);
		String newMapAdvice = getProperty(SnomedRf2Headers.FIELD_MAP_ADVICE);
		String newCorrelationId = getProperty(SnomedRf2Headers.FIELD_CORRELATION_ID);

		boolean changed = false;

		if (newMapTargetId != null && !newMapTargetId.equals(concreteDomainMember.getMapTargetComponentId())) {
			concreteDomainMember.setMapTargetComponentId(newMapTargetId);
			changed |= true;
		}

		if (newGroup != null && !newGroup.equals(concreteDomainMember.getMapGroup())) {
			concreteDomainMember.setMapGroup(newGroup);
			changed |= true;
		}

		if (newPriority != null && !newPriority.equals(concreteDomainMember.getMapPriority())) {
			concreteDomainMember.setMapPriority(newPriority);
			changed |= true;
		}

		if (newMapRule != null && !newMapRule.equals(concreteDomainMember.getMapRule())) {
			concreteDomainMember.setMapRule(newMapRule);
			changed |= true;
		}

		if (newMapAdvice != null && !newMapAdvice.equals(concreteDomainMember.getMapAdvice())) {
			concreteDomainMember.setMapAdvice(newMapAdvice);
			changed |= true;
		}

		if (newCorrelationId != null && !newCorrelationId.equals(concreteDomainMember.getCorrelationId())) {
			concreteDomainMember.setCorrelationId(newCorrelationId);
			changed |= true;
		}

		return changed;
	}

}
