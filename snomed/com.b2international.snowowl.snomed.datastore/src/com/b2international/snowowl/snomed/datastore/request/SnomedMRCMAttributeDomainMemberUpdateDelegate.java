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
final class SnomedMRCMAttributeDomainMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedMRCMAttributeDomainMemberUpdateDelegate(final SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(final SnomedRefSetMemberIndexEntry original, final SnomedRefSetMemberIndexEntry.Builder member, final TransactionContext context) {

		final String domainId = getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID);
		final Boolean grouped = getProperty(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.class);
		final String attributeCardinality = getProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY);
		final String attributeInGroupCardinality = getProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY);
		final String ruleStrengthId = getProperty(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
		final String contentTypeId = getProperty(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);

		boolean changed = false;

		if (!Strings.isNullOrEmpty(domainId) && !domainId.equals(original.getDomainId())) {
			member.field(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, domainId);
			changed |= true;
		}

		if (grouped != null && grouped.booleanValue() ^ original.isGrouped()) {
			member.field(SnomedRf2Headers.FIELD_MRCM_GROUPED, grouped);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(attributeCardinality) && !attributeCardinality.equals(original.getAttributeCardinality())) {
			member.field(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, attributeCardinality);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(attributeInGroupCardinality) && !attributeInGroupCardinality.equals(original.getAttributeInGroupCardinality())) {
			member.field(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, attributeInGroupCardinality);
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
