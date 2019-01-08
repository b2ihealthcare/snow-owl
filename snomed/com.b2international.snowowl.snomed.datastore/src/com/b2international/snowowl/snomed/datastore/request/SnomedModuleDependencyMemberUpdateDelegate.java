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

import java.util.Date;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * @since 5.0
 */
final class SnomedModuleDependencyMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedModuleDependencyMemberUpdateDelegate(SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(SnomedRefSetMember member, TransactionContext context) {
		SnomedModuleDependencyRefSetMember moduleDependencyMember = (SnomedModuleDependencyRefSetMember) member;

		boolean changed = false;

		if (hasProperty(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME)) {
			String sourceEffectiveTime = getProperty(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME);
			Date newSourceEffectiveTime = Strings.isNullOrEmpty(sourceEffectiveTime) ? null : EffectiveTimes.parse(sourceEffectiveTime, DateFormats.SHORT);
			if (!Objects.equal(newSourceEffectiveTime, moduleDependencyMember.getSourceEffectiveTime())) {
				moduleDependencyMember.setSourceEffectiveTime(newSourceEffectiveTime);
				changed |= true;
			}
		}

		if (hasProperty(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
			String targetEffectiveTime = getProperty(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME);
			Date newTargetEffectiveTime = Strings.isNullOrEmpty(targetEffectiveTime) ? null : EffectiveTimes.parse(targetEffectiveTime, DateFormats.SHORT);
			if (!Objects.equal(newTargetEffectiveTime, moduleDependencyMember.getTargetEffectiveTime())) {
				moduleDependencyMember.setTargetEffectiveTime(newTargetEffectiveTime);
				changed |= true;
			}
		}

		return changed;
	}

}
