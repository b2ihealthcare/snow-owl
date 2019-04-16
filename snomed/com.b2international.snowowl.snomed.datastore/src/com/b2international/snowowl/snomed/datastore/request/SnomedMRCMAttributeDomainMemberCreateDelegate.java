/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.5
 */
final class SnomedMRCMAttributeDomainMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedMRCMAttributeDomainMemberCreateDelegate(final SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(final SnomedReferenceSet refSet, final TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN);
		checkReferencedComponent(refSet);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MRCM_GROUPED);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID));
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, getProperty(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID));
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, getProperty(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID));

		final SnomedRefSetMemberIndexEntry member = SnomedComponents.newMRCMAttributeDomainReferenceSetMember()
				.withId(getId())
				.withActive(isActive())
				.withModule(getModuleId())
				.withRefSet(getReferenceSetId())
				.withReferencedComponent(getReferencedComponentId())
				.withDomainId(getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID))
				.withGrouped(getProperty(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.class))
				.withAttributeCardinality(getProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY))
				.withAttributeInGroupCardinality(getProperty(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY))
				.withRuleStrengthId(getProperty(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID))
				.withContentTypeId(getProperty(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID))
				.addTo(context);

		return member.getId();
	}

	@Override
	protected Set<String> getRequiredComponentIds() {
		checkNonEmptyProperty( SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);
		return ImmutableSet.of(
				getComponentId(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID),
				getComponentId(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID),
				getComponentId(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID));
	}

}
