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
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 4.5
 */
public final class SnomedLanguageReferenceSetMemberBuilder
		extends SnomedMemberBuilder<SnomedLanguageReferenceSetMemberBuilder, SnomedLanguageRefSetMember> {

	private Acceptability acceptability = Acceptability.ACCEPTABLE;

	SnomedLanguageReferenceSetMemberBuilder() {
		super(ComponentCategory.SET_MEMBER);
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
	 * Builds and adds a new SNOMED CT Language reference set member to the given description using the given {@link TransactionContext}.
	 * 
	 * @param context - the context where the new member should be made available
	 * @param description - the corresponding description of the referenced component
	 */
	public SnomedLanguageRefSetMember addTo(TransactionContext context, Description description) {
		// FIXME default module handling (sometimes we would like to specify other modules for member than the description's)
		final SnomedLanguageRefSetMember member = this
				.withReferencedComponent(description.getId())
				.withModule(description.getModule().getId())
				.addTo(context);
		description.getLanguageRefSetMembers().add(member);
		return member;
	}

	@Override
	protected void init(SnomedLanguageRefSetMember component, TransactionContext context) {
		super.init(component, context);
		component.setAcceptabilityId(acceptability.getConceptId());
	}

	@Override
	protected SnomedLanguageRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
	}

}
