/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 6.14
 */
public final class ComplexMapMemberEffectiveTimeRestorer extends MemberEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreMemberEffectiveTime(SnomedRefSetMember memberToRestore, SnomedReferenceSetMember previousMember) {
		final SnomedComplexMapRefSetMember complexMapMemberToRestore = (SnomedComplexMapRefSetMember) memberToRestore;
		
		final String previousMapTargetId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET);
		final Integer previousMapGroup = (Integer) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_GROUP);
		final Integer previousMapPriority = (Integer) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_PRIORITY);
		final String previousMapRule = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_RULE);
		final String previousMapAdvice = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_ADVICE);
		final String previousCorrelationId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_CORRELATION_ID);

		if (previousMapTargetId != null && !previousMapTargetId.equals(complexMapMemberToRestore.getMapTargetComponentId())) {
			return false;
		}

		if (previousMapGroup != null && previousMapGroup.intValue() != complexMapMemberToRestore.getMapGroup()) {
			return false;
		}

		if (previousMapPriority != null && previousMapPriority.intValue() != complexMapMemberToRestore.getMapPriority()) {
			return false;
		}

		if (previousMapRule != null && !previousMapRule.equals(complexMapMemberToRestore.getMapRule())) {
			return false;
		}

		if (previousMapAdvice != null && !previousMapAdvice.equals(complexMapMemberToRestore.getMapAdvice())) {
			return false;
		}

		if (previousCorrelationId != null && !previousCorrelationId.equals(complexMapMemberToRestore.getCorrelationId())) {
			return false;
		}
		
		
		return true;
	}

}
