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

import java.util.Date;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 5.0
 */
public final class SnomedModuleDependencyReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedModuleDependencyReferenceSetMemberBuilder, SnomedModuleDependencyRefSetMember> {

	private Date sourceEffectiveTime = new Date(EffectiveTimes.UNSET_EFFECTIVE_TIME);
	private Date targetEffectiveTime = new Date(EffectiveTimes.UNSET_EFFECTIVE_TIME);

	public SnomedModuleDependencyReferenceSetMemberBuilder withSourceEffectiveTime(Date sourceEffectiveTime) {
		this.sourceEffectiveTime = sourceEffectiveTime;
		return getSelf();
	}

	public SnomedModuleDependencyReferenceSetMemberBuilder withTargetEffectiveTime(Date targetEffectiveTime) {
		this.targetEffectiveTime = targetEffectiveTime;
		return getSelf();
	}

	@Override
	protected SnomedModuleDependencyRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedModuleDependencyRefSetMember();
	}

	@Override
	protected void init(SnomedModuleDependencyRefSetMember component, TransactionContext context) {
		super.init(component, context);
		component.setSourceEffectiveTime(sourceEffectiveTime);
		component.setTargetEffectiveTime(targetEffectiveTime);
	}

}
