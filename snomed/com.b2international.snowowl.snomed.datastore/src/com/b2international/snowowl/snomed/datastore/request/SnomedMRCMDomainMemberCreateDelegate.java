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
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 6.5
 */
final class SnomedMRCMDomainMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedMRCMDomainMemberCreateDelegate(final SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(final SnomedRefSet refSet, final TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.MRCM_DOMAIN);
		checkReferencedComponent(refSet);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());

		final SnomedMRCMDomainRefSetMember member = SnomedComponents.newMRCMDomainReferenceSetMember()
				.withId(getId())
				.withActive(isActive())
				.withModule(getModuleId())
				.withRefSet(getReferenceSetId())
				.withReferencedComponent(getReferencedComponentId())
				.withDomainConstraint(getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT))
				.withParentDomain(getProperty(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN))
				.withProximalPrimitiveConstraint(getProperty(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT))
				.withProximalPrimitiveRefinement(getProperty(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT))
				.withDomainTemplateForPrecoordination(getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION))
				.withDomainTemplateForPostcoordination(getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION))
				.withEditorialGuideReference(getProperty(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE))
				.addTo(context);

		return member.getUuid();
	}

}
