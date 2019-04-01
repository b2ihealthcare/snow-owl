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
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 6.14
 */
public class MRCMDomainMemberEffectiveTimeRestorer extends MemberEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreMemberEffectiveTime(SnomedRefSetMember memberToRestore, SnomedReferenceSetMember previousMember) {
		final SnomedMRCMDomainRefSetMember domainMemberToRestore = (SnomedMRCMDomainRefSetMember) memberToRestore;

		final String previousDomainConstraint = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT);
		final String previousParentDomain = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN);
		final String previousProximalPrimitiveConstraint = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT);
		final String previousProximalPrimitiveRefinement = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT);
		final String previousDomainTemplateForPrecoordination = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION);
		final String previousDomainTemplateForPostcoordination = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION);
		final String previousEditorialGuideReference = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE);

		if (previousDomainConstraint != null && !previousDomainConstraint.equals(domainMemberToRestore.getDomainConstraint())) {
			return false;
		}

		if (previousParentDomain != null && !previousParentDomain.equals(domainMemberToRestore.getParentDomain())) {
			return false;
		}

		if (previousProximalPrimitiveConstraint != null && !previousProximalPrimitiveConstraint.equals(domainMemberToRestore.getProximalPrimitiveConstraint())) {
			return false;
		}

		if (previousProximalPrimitiveRefinement != null && !previousProximalPrimitiveRefinement.equals(domainMemberToRestore.getProximalPrimitiveRefinement())) {
			return false;
		}

		if (previousDomainTemplateForPrecoordination != null && !previousDomainTemplateForPrecoordination.equals(domainMemberToRestore.getDomainTemplateForPrecoordination())) {
			return false;
		}

		if (previousDomainTemplateForPostcoordination != null && !previousDomainTemplateForPostcoordination.equals(domainMemberToRestore.getDomainTemplateForPostcoordination())) {
			return false;
		}

		if (previousEditorialGuideReference != null && !previousEditorialGuideReference.equals(domainMemberToRestore.getEditorialGuideReference())) {
			return false;
		}

		return true;
	}


}
