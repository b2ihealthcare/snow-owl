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
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public class SnomedMRCMAttributeRangeMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedMRCMAttributeRangeMemberUpdateDelegate(final SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(final SnomedRefSetMember member, final TransactionContext context) {

		final SnomedMRCMAttributeRangeRefSetMember attributeRangeMember = (SnomedMRCMAttributeRangeRefSetMember) member;

		final String rangeConstraint = getProperty(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT);
		final String attributeRule = getProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE);
		final String ruleStrengthId = getProperty(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
		final String contentTypeId = getProperty(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);

		boolean changed = false;

		if (!Strings.isNullOrEmpty(rangeConstraint) && !rangeConstraint.equals(attributeRangeMember.getRangeConstraint())) {
			attributeRangeMember.setRangeConstraint(rangeConstraint);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(attributeRule) && !attributeRule.equals(attributeRangeMember.getAttributeRule())) {
			attributeRangeMember.setAttributeRule(attributeRule);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(ruleStrengthId) && !ruleStrengthId.equals(attributeRangeMember.getRuleStrengthId())) {
			attributeRangeMember.setRuleStrengthId(ruleStrengthId);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(contentTypeId) && !contentTypeId.equals(attributeRangeMember.getContentTypeId())) {
			attributeRangeMember.setContentTypeId(contentTypeId);
			changed |= true;
		}

		return changed;
	}

}
