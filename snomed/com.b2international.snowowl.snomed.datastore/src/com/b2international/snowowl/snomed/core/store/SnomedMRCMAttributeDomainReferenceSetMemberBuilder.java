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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 6.5
 */
public final class SnomedMRCMAttributeDomainReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedMRCMAttributeDomainReferenceSetMemberBuilder> {

	private String domainId;
	private boolean grouped;
	private String attributeCardinality;
	private String attributeInGroupCardinality;
	private String ruleStrengthId;
	private String contentTypeId;

	public SnomedMRCMAttributeDomainReferenceSetMemberBuilder withDomainId(final String domainId) {
		this.domainId = domainId;
		return getSelf();
	}

	public SnomedMRCMAttributeDomainReferenceSetMemberBuilder withGrouped(final boolean grouped ) {
		this.grouped = grouped;
		return getSelf();
	}

	public SnomedMRCMAttributeDomainReferenceSetMemberBuilder withAttributeCardinality(final String attributeCardinality) {
		this.attributeCardinality = attributeCardinality;
		return getSelf();
	}

	public SnomedMRCMAttributeDomainReferenceSetMemberBuilder withAttributeInGroupCardinality(final String attributeInGroupCardinality) {
		this.attributeInGroupCardinality = attributeInGroupCardinality;
		return getSelf();
	}

	public SnomedMRCMAttributeDomainReferenceSetMemberBuilder withRuleStrengthId(final String ruleStrengthId) {
		this.ruleStrengthId = ruleStrengthId;
		return getSelf();
	}

	public SnomedMRCMAttributeDomainReferenceSetMemberBuilder withContentTypeId(final String contentTypeId) {
		this.contentTypeId = contentTypeId;
		return getSelf();
	}

	@Override
	public void init(final SnomedRefSetMemberIndexEntry.Builder component, final TransactionContext context) {
		super.init(component, context);
		component
			.field(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, domainId)
			.field(SnomedRf2Headers.FIELD_MRCM_GROUPED, grouped)
			.field(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, attributeCardinality)
			.field(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, attributeInGroupCardinality)
			.field(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, ruleStrengthId)
			.field(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, contentTypeId);
	}

}
