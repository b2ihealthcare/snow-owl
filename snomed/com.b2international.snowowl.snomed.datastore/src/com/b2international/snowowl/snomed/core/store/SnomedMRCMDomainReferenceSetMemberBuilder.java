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
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 5.10.19
 */
public class SnomedMRCMDomainReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedMRCMDomainReferenceSetMemberBuilder, SnomedMRCMDomainRefSetMember> {

	private String domainConstraint;
	private String parentDomain;
	private String proximalPrimitiveConstraint;
	private String proximalPrimitiveRefinement;
	private String domainTemplateForPrecoordination;
	private String domainTemplateForPostcoordination;
	private String editorialGuideReference;

	public SnomedMRCMDomainReferenceSetMemberBuilder withDomainConstraint(final String domainConstraint) {
		this.domainConstraint = domainConstraint;
		return getSelf();
	}

	public SnomedMRCMDomainReferenceSetMemberBuilder withParentDomain(final String parentDomain) {
		this.parentDomain = parentDomain;
		return getSelf();
	}

	public SnomedMRCMDomainReferenceSetMemberBuilder withProximalPrimitiveConstraint(final String proximalPrimitiveConstraint) {
		this.proximalPrimitiveConstraint = proximalPrimitiveConstraint;
		return getSelf();
	}

	public SnomedMRCMDomainReferenceSetMemberBuilder withProximalPrimitiveRefinement(final String proximalPrimitiveRefinement) {
		this.proximalPrimitiveRefinement = proximalPrimitiveRefinement;
		return getSelf();
	}

	public SnomedMRCMDomainReferenceSetMemberBuilder withDomainTemplateForPrecoordination(final String domainTemplateForPrecoordination) {
		this.domainTemplateForPrecoordination = domainTemplateForPrecoordination;
		return getSelf();
	}

	public SnomedMRCMDomainReferenceSetMemberBuilder withDomainTemplateForPostcoordination(final String domainTemplateForPostcoordination) {
		this.domainTemplateForPostcoordination = domainTemplateForPostcoordination;
		return getSelf();
	}

	public SnomedMRCMDomainReferenceSetMemberBuilder withEditorialGuideReference(final String editorialGuideReference) {
		this.editorialGuideReference = editorialGuideReference;
		return getSelf();
	}

	@Override
	protected SnomedMRCMDomainRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedMRCMDomainRefSetMember();
	}

	@Override
	public void init(final SnomedMRCMDomainRefSetMember component, final TransactionContext context) {
		super.init(component, context);
		component.setDomainConstraint(domainConstraint);
		component.setParentDomain(parentDomain);
		component.setProximalPrimitiveConstraint(proximalPrimitiveConstraint);
		component.setProximalPrimitiveRefinement(proximalPrimitiveRefinement);
		component.setDomainTemplateForPrecoordination(domainTemplateForPrecoordination);
		component.setDomainTemplateForPostcoordination(domainTemplateForPostcoordination);
		component.setEditorialGuideReference(editorialGuideReference);
	}

}
