/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 7.4
 */
public final class SnomedComplexBlockMapReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedComplexBlockMapReferenceSetMemberBuilder> {

	private String mapTargetId;
	private String mapTargetDescription;
	private String mapRule;
	private String mapAdvice;
	private int group = 1;
	private int priority = 1;
	private String correlationId;
	private String mapCategoryId;
	private int block = 1;

	public SnomedComplexBlockMapReferenceSetMemberBuilder withMapTargetId(String mapTargetId) {
		this.mapTargetId = mapTargetId;
		return getSelf();
	}

	public SnomedComplexBlockMapReferenceSetMemberBuilder withMapTargetDescription(String mapTargetDescription) {
		this.mapTargetDescription = mapTargetDescription;
		return getSelf();
	}

	public SnomedComplexBlockMapReferenceSetMemberBuilder withMapRule(String mapRule) {
		this.mapRule = mapRule;
		return getSelf();
	}

	public SnomedComplexBlockMapReferenceSetMemberBuilder withMapAdvice(String mapAdvice) {
		this.mapAdvice = mapAdvice;
		return getSelf();
	}

	public SnomedComplexBlockMapReferenceSetMemberBuilder withGroup(int group) {
		this.group = group;
		return getSelf();
	}

	public SnomedComplexBlockMapReferenceSetMemberBuilder withPriority(int priority) {
		this.priority = priority;
		return getSelf();
	}

	public SnomedComplexBlockMapReferenceSetMemberBuilder withCorrelationId(String correlationId) {
		this.correlationId = correlationId;
		return getSelf();
	}

	public SnomedComplexBlockMapReferenceSetMemberBuilder withMapCategoryId(String mapCategoryId) {
		this.mapCategoryId = mapCategoryId;
		return getSelf();
	}

	public SnomedComplexBlockMapReferenceSetMemberBuilder withBlock(int block) {
		this.block = block;
		return getSelf();
	}

	@Override
	public void init(SnomedRefSetMemberIndexEntry.Builder component, TransactionContext context) {
		super.init(component, context);
		component
			.field(SnomedRf2Headers.FIELD_MAP_TARGET, mapTargetId)
			.field(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, mapTargetDescription)
			.field(SnomedRf2Headers.FIELD_MAP_RULE, mapRule)
			.field(SnomedRf2Headers.FIELD_MAP_ADVICE, mapAdvice)
			.field(SnomedRf2Headers.FIELD_MAP_GROUP, group)
			.field(SnomedRf2Headers.FIELD_MAP_PRIORITY, priority)
			.field(SnomedRf2Headers.FIELD_CORRELATION_ID, correlationId)
			.field(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, mapCategoryId)
			.field(SnomedRf2Headers.FIELD_MAP_BLOCK, block);
	}
}
