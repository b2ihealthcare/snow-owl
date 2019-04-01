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
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 6.14
 */
public class MRCMAttributeRangeMemberEffectiveTimeRestorer extends MemberEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreMemberEffectiveTime(SnomedRefSetMember memberToRestore, SnomedReferenceSetMember previousMember) {
		final SnomedMRCMAttributeRangeRefSetMember attributeRangeMemberToRestore = (SnomedMRCMAttributeRangeRefSetMember) memberToRestore;

		final String previousRangedConstraint = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT);
		final String previousAttributeRule = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE);
		final String previousRuleStrengthId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
		final String previousContentTypeId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);

		if (previousRangedConstraint != null && !previousRangedConstraint.equals(attributeRangeMemberToRestore.getRangeConstraint())) {
			return false;
		}

		if (previousAttributeRule != null && !previousAttributeRule.equals(attributeRangeMemberToRestore.getAttributeRule())) {
			return false;
		}

		if (previousRuleStrengthId != null && !previousRuleStrengthId.equals(attributeRangeMemberToRestore.getRuleStrengthId())) {
			return false;
		}

		if (previousContentTypeId != null && !previousContentTypeId.equals(attributeRangeMemberToRestore.getContentTypeId())) {
			return false;
		}

		return true;
	}

}
