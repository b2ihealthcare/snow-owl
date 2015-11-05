/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.UUIDIdGenerationStrategy;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;

/**
 * @since 4.5
 */
public final class SnomedLanguageReferenceSetMemberBuilder
		extends SnomedComponentBuilder<SnomedLanguageReferenceSetMemberBuilder, SnomedLanguageRefSetMember> {

	private String referenceSetId;
	private String referencedComponent;
	private Acceptability acceptability = Acceptability.ACCEPTABLE;

	protected SnomedLanguageReferenceSetMemberBuilder() {
		super(ComponentCategory.SET_MEMBER);
		withId(new UUIDIdGenerationStrategy());
	}

	/**
	 * Specifies the referenced component ID of the new language reference set member.
	 * 
	 * @param referencedComponent
	 *            - the referenced component to refer to
	 * @return
	 */
	public SnomedLanguageReferenceSetMemberBuilder withReferencedComponent(String referencedComponent) {
		this.referencedComponent = referencedComponent;
		return getSelf();
	}

	/**
	 * Specifies the {@link Acceptability} value of the new language reference set member.
	 * 
	 * @param acceptability
	 *            - the acceptability to use
	 * @return
	 */
	public SnomedLanguageReferenceSetMemberBuilder withAcceptability(Acceptability acceptability) {
		this.acceptability = acceptability;
		return getSelf();
	}

	/**
	 * Specifies the reference set this language reference set member belongs to.
	 * 
	 * @param referenceSetId
	 *            - the identifier concept ID of the Language reference set
	 * @return
	 */
	public SnomedLanguageReferenceSetMemberBuilder withRefSet(String referenceSetId) {
		this.referenceSetId = referenceSetId;
		return getSelf();
	}

	/**
	 * Builds and adds a new SNOMED CT Language reference set member to the given description using the given {@link TransactionContext}.
	 * 
	 * @param context
	 * @param description
	 */
	public void addTo(TransactionContext context, Description description) {
		withReferencedComponent(description.getId());
		withModule(description.getModule().getId());
		description.getLanguageRefSetMembers().add(build(context));
	}

	@Override
	protected void init(SnomedLanguageRefSetMember component, TransactionContext context) {
		super.init(component, context);
		component.setReferencedComponentId(referencedComponent);
		component.setAcceptabilityId(acceptability.getConceptId());
		component.setRefSet(context.lookup(referenceSetId, SnomedStructuralRefSet.class));
	}

	@Override
	protected SnomedLanguageRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
	}

}
