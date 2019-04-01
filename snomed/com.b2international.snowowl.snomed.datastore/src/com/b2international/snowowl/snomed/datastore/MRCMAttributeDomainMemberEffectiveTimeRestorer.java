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

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 6.14
 */
public class MRCMAttributeDomainMemberEffectiveTimeRestorer extends MemberEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreMemberEffectiveTime(SnomedRefSetMember memberToRestore, SnomedReferenceSetMember previousMember) {
		final SnomedMRCMAttributeDomainRefSetMember domainMemberToRestore = (SnomedMRCMAttributeDomainRefSetMember) memberToRestore;

		final String previousDomainId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID);
		final Boolean previousdGrouped = ClassUtils.checkAndCast(previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_GROUPED), Boolean.class);		
		final String previousAttributeCardinality = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY);
		final String previousAtributeInGroupCardinality = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY);
		final String previousRuleStrengthId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
		final String previousContentTypeId = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);

		if (previousDomainId != null && !previousDomainId.equals(domainMemberToRestore.getDomainId())) {
			return false;
		}

		if (previousdGrouped != null && previousdGrouped.booleanValue() ^ domainMemberToRestore.isGrouped()) {
			return false;
		}

		if (previousAttributeCardinality != null && !previousAttributeCardinality.equals(domainMemberToRestore.getAttributeCardinality())) {
			return false;
		}

		if (previousAtributeInGroupCardinality != null && !previousAtributeInGroupCardinality.equals(domainMemberToRestore.getAttributeInGroupCardinality())) {
			return false;
		}

		if (previousRuleStrengthId != null && !previousRuleStrengthId.equals(domainMemberToRestore.getRuleStrengthId())) {
			return false;
		}

		if (previousContentTypeId != null && !previousContentTypeId.equals(domainMemberToRestore.getContentTypeId())) {
			return false;
		}

		return true;
	}

}
