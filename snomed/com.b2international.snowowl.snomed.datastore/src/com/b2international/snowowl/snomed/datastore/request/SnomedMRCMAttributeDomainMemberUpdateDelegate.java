/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Strings;

/**
 * @since 6.1.0
 */
public class SnomedMRCMAttributeDomainMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedMRCMAttributeDomainMemberUpdateDelegate(final SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(final SnomedRefSetMember member, final TransactionContext context) {

		final SnomedMRCMAttributeDomainRefSetMember domainMember = (SnomedMRCMAttributeDomainRefSetMember) member;

		final String domainId = getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID);
		final Boolean grouped = getProperty(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.class);
		final String attributeCardinality = getProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY);
		final String attributeInGroupCardinality = getProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY);
		final String ruleStrengthId = getProperty(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
		final String contentTypeId = getProperty(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);

		boolean changed = false;

		if (!Strings.isNullOrEmpty(domainId) && !domainId.equals(domainMember.getDomainId())) {
			domainMember.setDomainId(domainId);
			changed |= true;
		}

		if (grouped != null && grouped.booleanValue() ^ domainMember.isGrouped()) {
			domainMember.setGrouped(grouped);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(attributeCardinality) && !attributeCardinality.equals(domainMember.getAttributeCardinality())) {
			domainMember.setAttributeCardinality(attributeCardinality);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(attributeInGroupCardinality) && !attributeInGroupCardinality.equals(domainMember.getAttributeInGroupCardinality())) {
			domainMember.setAttributeInGroupCardinality(attributeInGroupCardinality);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(ruleStrengthId) && !ruleStrengthId.equals(domainMember.getRuleStrengthId())) {
			domainMember.setRuleStrengthId(ruleStrengthId);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(contentTypeId) && !contentTypeId.equals(domainMember.getContentTypeId())) {
			domainMember.setContentTypeId(contentTypeId);
			changed |= true;
		}

		return changed;
	}

}
