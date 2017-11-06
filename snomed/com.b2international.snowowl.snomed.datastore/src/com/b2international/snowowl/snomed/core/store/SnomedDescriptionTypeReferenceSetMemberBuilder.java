/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 5.0
 */
public final class SnomedDescriptionTypeReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedDescriptionTypeReferenceSetMemberBuilder, SnomedDescriptionTypeRefSetMember> {

	private String descriptionFormatId;
	private int descriptionLength;
	
	public SnomedDescriptionTypeReferenceSetMemberBuilder withDescriptionFormatId(String descriptionFormatId) {
		this.descriptionFormatId = descriptionFormatId;
		return getSelf();
	}
	
	public SnomedDescriptionTypeReferenceSetMemberBuilder withDescriptionLength(int descriptionLength) {
		this.descriptionLength = descriptionLength;
		return getSelf();
	}
	
	@Override
	protected SnomedDescriptionTypeRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedDescriptionTypeRefSetMember();
	}

	@Override
	public void init(SnomedDescriptionTypeRefSetMember component, TransactionContext context) {
		super.init(component, context);
		component.setDescriptionFormat(descriptionFormatId);
		component.setDescriptionLength(descriptionLength);
	}
	
}
