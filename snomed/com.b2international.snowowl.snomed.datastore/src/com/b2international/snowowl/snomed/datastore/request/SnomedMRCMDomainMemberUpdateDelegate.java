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
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Strings;

/**
 * @since 6.5
 */
public class SnomedMRCMDomainMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedMRCMDomainMemberUpdateDelegate(final SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(final SnomedRefSetMember member, final TransactionContext context) {

		final SnomedMRCMDomainRefSetMember domainMember = (SnomedMRCMDomainRefSetMember) member;
		final String domainConstraint = getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT);
		final String parentDomain = getProperty(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN);
		final String proximalPrimitiveConstraint = getProperty(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT);
		final String proximalPrimitiveRefinement = getProperty(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT);
		final String domainTemplateForPrecoordination = getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION);
		final String domainTemplateForPostcoordination = getProperty(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION);
		final String editorialGuideReference = getProperty(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE);

		boolean changed = false;

		if (!Strings.isNullOrEmpty(domainConstraint) && !domainConstraint.equals(domainMember.getDomainConstraint())) {
			domainMember.setDomainConstraint(domainConstraint);
			changed |= true;
		}

		if (parentDomain != null && !parentDomain.equals(domainMember.getParentDomain())) {
			domainMember.setParentDomain(parentDomain);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(proximalPrimitiveConstraint) && !proximalPrimitiveConstraint.equals(domainMember.getProximalPrimitiveConstraint())) {
			domainMember.setProximalPrimitiveConstraint(proximalPrimitiveConstraint);
			changed |= true;
		}

		if (proximalPrimitiveRefinement != null && !proximalPrimitiveRefinement.equals(domainMember.getProximalPrimitiveRefinement())) {
			domainMember.setProximalPrimitiveRefinement(proximalPrimitiveRefinement);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(domainTemplateForPrecoordination) && !domainTemplateForPrecoordination.equals(domainMember.getDomainTemplateForPrecoordination())) {
			domainMember.setDomainTemplateForPrecoordination(domainTemplateForPrecoordination);
			changed |= true;
		}

		if (!Strings.isNullOrEmpty(domainTemplateForPostcoordination) && !domainTemplateForPostcoordination.equals(domainMember.getDomainTemplateForPostcoordination())) {
			domainMember.setDomainTemplateForPostcoordination(domainTemplateForPostcoordination);
			changed |= true;
		}

		if (editorialGuideReference != null && !editorialGuideReference.equals(domainMember.getEditorialGuideReference())) {
			domainMember.setEditorialGuideReference(editorialGuideReference);
			changed |= true;
		}

		return changed;
	}

}
