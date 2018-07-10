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
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 5.0
 */
final class SnomedModuleDependencyMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedModuleDependencyMemberUpdateDelegate(SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(SnomedRefSetMemberIndexEntry original, SnomedRefSetMemberIndexEntry.Builder member, TransactionContext context) {

		boolean changed = false;

		if (hasProperty(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME)) {
			long newSourceEffectiveTime = EffectiveTimes.parse(getProperty(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME), DateFormats.SHORT).getTime();
			if (newSourceEffectiveTime != original.getSourceEffectiveTime()) {
				member.field(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, newSourceEffectiveTime);
				changed |= true;
			}
		}

		if (hasProperty(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
			long newTargetEffectiveTime = EffectiveTimes.parse(getProperty(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME), DateFormats.SHORT).getTime();
			if (newTargetEffectiveTime != original.getTargetEffectiveTime()) {
				member.field(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, newTargetEffectiveTime);
				changed |= true;
			}
		}

		return changed;
	}

}
