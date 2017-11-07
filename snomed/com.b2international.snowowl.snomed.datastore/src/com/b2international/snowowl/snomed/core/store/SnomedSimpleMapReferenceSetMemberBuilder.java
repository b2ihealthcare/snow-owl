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
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;

/**
 * @since 5.0
 */
public final class SnomedSimpleMapReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedSimpleMapReferenceSetMemberBuilder, SnomedSimpleMapRefSetMember> {

	private String mapTargetId;
	private String mapTargetDescription;

	public SnomedSimpleMapReferenceSetMemberBuilder withMapTargetId(String mapTargetId) {
		this.mapTargetId = mapTargetId;
		return getSelf();
	}

	public SnomedSimpleMapReferenceSetMemberBuilder withMapTargetDescription(String mapTargetDescription) {
		this.mapTargetDescription = mapTargetDescription;
		return getSelf();
	}

	@Override
	protected SnomedSimpleMapRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedSimpleMapRefSetMember();
	}

	@Override
	public void init(SnomedSimpleMapRefSetMember component, TransactionContext context) {
		super.init(component, context);
		component.setMapTargetComponentId(mapTargetId);
		component.setMapTargetComponentDescription(mapTargetDescription);
	}

}
