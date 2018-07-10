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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 5.0
 */
final class SnomedExtendedMapMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedExtendedMapMemberUpdateDelegate(SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(SnomedRefSetMemberIndexEntry original, SnomedRefSetMemberIndexEntry.Builder member, TransactionContext context) {
		String newMapTargetId = getComponentId(SnomedRf2Headers.FIELD_MAP_TARGET);
		Integer newGroup = getProperty(SnomedRf2Headers.FIELD_MAP_GROUP, Integer.class);
		Integer newPriority = getProperty(SnomedRf2Headers.FIELD_MAP_PRIORITY, Integer.class);
		String newMapRule = getProperty(SnomedRf2Headers.FIELD_MAP_RULE);
		String newMapAdvice = getProperty(SnomedRf2Headers.FIELD_MAP_ADVICE);
		String newCorrelationId = getComponentId(SnomedRf2Headers.FIELD_CORRELATION_ID);
		String newMapCategoryId = getComponentId(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID);

		boolean changed = false;

		if (newMapTargetId != null && !newMapTargetId.equals(original.getMapTarget())) {
			member.field(SnomedRf2Headers.FIELD_MAP_TARGET, newMapTargetId);
			changed |= true;
		}

		if (newGroup != null && newGroup.intValue() != original.getMapGroup()) {
			member.field(SnomedRf2Headers.FIELD_MAP_GROUP, newGroup);
			changed |= true;
		}

		if (newPriority != null && newPriority.intValue() != original.getMapPriority()) {
			member.field(SnomedRf2Headers.FIELD_MAP_PRIORITY, newPriority);
			changed |= true;
		}

		if (newMapRule != null && !newMapRule.equals(original.getMapRule())) {
			member.field(SnomedRf2Headers.FIELD_MAP_RULE, newMapRule);
			changed |= true;
		}

		if (newMapAdvice != null && !newMapAdvice.equals(original.getMapAdvice())) {
			member.field(SnomedRf2Headers.FIELD_MAP_ADVICE, newMapAdvice);
			changed |= true;
		}

		if (newCorrelationId != null && !newCorrelationId.equals(original.getCorrelationId())) {
			member.field(SnomedRf2Headers.FIELD_CORRELATION_ID, newCorrelationId);
			changed |= true;
		}

		if (newMapCategoryId != null && !newMapCategoryId.equals(original.getMapCategoryId())) {
			member.field(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, newMapCategoryId);
			changed |= true;
		}

		return changed;
	}

}
