/*
 * Copyright 2017-2021 B2i Healthcare, https://b2ihealthcare.com
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

import java.time.LocalDate;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 5.0
 */
public final class SnomedModuleDependencyReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedModuleDependencyReferenceSetMemberBuilder> {

	private LocalDate sourceEffectiveTime;
	private LocalDate targetEffectiveTime;

	public SnomedModuleDependencyReferenceSetMemberBuilder withSourceEffectiveTime(LocalDate sourceEffectiveTime) {
		this.sourceEffectiveTime = sourceEffectiveTime;
		return getSelf();
	}

	public SnomedModuleDependencyReferenceSetMemberBuilder withTargetEffectiveTime(LocalDate targetEffectiveTime) {
		this.targetEffectiveTime = targetEffectiveTime;
		return getSelf();
	}

	@Override
	public void init(SnomedRefSetMemberIndexEntry.Builder component, TransactionContext context) {
		super.init(component, context);
		component
			.field(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(sourceEffectiveTime))
			.field(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(targetEffectiveTime));
	}

}
