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
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 5.10.19
 */
public class SnomedMRCMAttributeRangeReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedMRCMAttributeRangeReferenceSetMemberBuilder, SnomedMRCMAttributeRangeRefSetMember> {

	private String rangeConstraint;
	private String attributeRule;
	private String ruleStrengthId;
	private String contentTypeId;

	public SnomedMRCMAttributeRangeReferenceSetMemberBuilder withRangeConstraint(final String rangeConstraint) {
		this.rangeConstraint = rangeConstraint;
		return getSelf();
	}

	public SnomedMRCMAttributeRangeReferenceSetMemberBuilder withAttributeRule(final String attributeRule) {
		this.attributeRule = attributeRule;
		return getSelf();
	}

	public SnomedMRCMAttributeRangeReferenceSetMemberBuilder withRuleStrengthId(final String ruleStrengthId) {
		this.ruleStrengthId = ruleStrengthId;
		return getSelf();
	}

	public SnomedMRCMAttributeRangeReferenceSetMemberBuilder withContentTypeId(final String contentTypeId) {
		this.contentTypeId = contentTypeId;
		return getSelf();
	}

	@Override
	protected SnomedMRCMAttributeRangeRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedMRCMAttributeRangeRefSetMember();
	}

	@Override
	public void init(final SnomedMRCMAttributeRangeRefSetMember component, final TransactionContext context) {
		super.init(component, context);
		component.setRangeConstraint(rangeConstraint);
		component.setAttributeRule(attributeRule);
		component.setRuleStrengthId(ruleStrengthId);
		component.setContentTypeId(contentTypeId);
	}

}
