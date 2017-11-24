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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 5.10.19
 */
public class SnomedMRCMAttributeDomainReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedMRCMAttributeDomainReferenceSetMemberBuilder, SnomedMRCMAttributeDomainRefSetMember> {

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
	protected SnomedMRCMAttributeDomainRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedMRCMAttributeDomainRefSetMember();
	}

	@Override
	public void init(final SnomedMRCMAttributeDomainRefSetMember component, final TransactionContext context) {
		super.init(component, context);
		component.setDomainId(domainId);
		component.setGrouped(grouped);
		component.setAttributeCardinality(attributeCardinality);
		component.setAttributeInGroupCardinality(attributeInGroupCardinality);
		component.setRuleStrengthId(ruleStrengthId);
		component.setContentTypeId(contentTypeId);
	}

}
