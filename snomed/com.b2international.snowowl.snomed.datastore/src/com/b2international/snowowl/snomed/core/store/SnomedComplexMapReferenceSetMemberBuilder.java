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
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;

/**
 * @since 5.0
 */
public final class SnomedComplexMapReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedComplexMapReferenceSetMemberBuilder, SnomedComplexMapRefSetMember> {

	private String mapTargetId;
	private String mapTargetDescription;
	private String mapRule;
	private String mapAdvice;
	private byte group = 1;
	private byte priority = 1;
	private String correlationId;
	private String mapCategoryId;

	public SnomedComplexMapReferenceSetMemberBuilder withMapTargetId(String mapTargetId) {
		this.mapTargetId = mapTargetId;
		return getSelf();
	}

	public SnomedComplexMapReferenceSetMemberBuilder withMapTargetDescription(String mapTargetDescription) {
		this.mapTargetDescription = mapTargetDescription;
		return getSelf();
	}

	public SnomedComplexMapReferenceSetMemberBuilder withMapRule(String mapRule) {
		this.mapRule = mapRule;
		return getSelf();
	}

	public SnomedComplexMapReferenceSetMemberBuilder withMapAdvice(String mapAdvice) {
		this.mapAdvice = mapAdvice;
		return getSelf();
	}

	public SnomedComplexMapReferenceSetMemberBuilder withGroup(byte group) {
		this.group = group;
		return getSelf();
	}

	public SnomedComplexMapReferenceSetMemberBuilder withPriority(byte priority) {
		this.priority = priority;
		return getSelf();
	}

	public SnomedComplexMapReferenceSetMemberBuilder withCorrelationId(String correlationId) {
		this.correlationId = correlationId;
		return getSelf();
	}

	public SnomedComplexMapReferenceSetMemberBuilder withMapCategoryId(String mapCategoryId) {
		this.mapCategoryId = mapCategoryId;
		return getSelf();
	}

	@Override
	protected SnomedComplexMapRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedComplexMapRefSetMember();
	}

	@Override
	protected void init(SnomedComplexMapRefSetMember component, TransactionContext context) {
		super.init(component, context);
		component.setMapTargetComponentId(mapTargetId);
		component.setMapTargetComponentDescription(mapTargetDescription);
		component.setMapRule(mapRule);
		component.setMapAdvice(mapAdvice);
		component.setMapGroup(group);
		component.setMapPriority(priority);
		component.setCorrelationId(correlationId);
		component.setMapCategoryId(mapCategoryId);
	}

}
