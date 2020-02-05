/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
final class SnomedMRCMAttributeRangeMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedMRCMAttributeRangeMemberUpdateDelegate(final SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(final SnomedRefSetMemberIndexEntry original, final SnomedRefSetMemberIndexEntry.Builder member, final TransactionContext context) {
		final String rangeConstraint = getProperty(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT);
		final String attributeRule = getProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE);
		final String ruleStrengthId = getProperty(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
		final String contentTypeId = getProperty(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);

		boolean changed = false;

		if (!Strings.isNullOrEmpty(rangeConstraint) && !rangeConstraint.equals(original.getRangeConstraint())) {
			member.field(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, rangeConstraint);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(attributeRule) && !attributeRule.equals(original.getAttributeRule())) {
			member.field(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, attributeRule);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(ruleStrengthId) && !ruleStrengthId.equals(original.getRuleStrengthId())) {
			member.field(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, ruleStrengthId);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(contentTypeId) && !contentTypeId.equals(original.getContentTypeId())) {
			member.field(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, contentTypeId);
			changed |= true;
		}

		return changed;
	}

}
