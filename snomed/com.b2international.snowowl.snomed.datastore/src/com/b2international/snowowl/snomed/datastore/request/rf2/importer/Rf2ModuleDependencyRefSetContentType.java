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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.0.0
 */
final class Rf2ModuleDependencyRefSetContentType implements Rf2RefSetContentType {

	@Override
	public void resolve(SnomedReferenceSetMember component, String[] values) {
		component.setType(SnomedRefSetType.MODULE_DEPENDENCY);
		component.setReferenceSetId(values[4]);
		// XXX actual type is not relevant here
		component.setReferencedComponent(new SnomedConcept(values[5]));
		component.setProperties(ImmutableMap.<String, Object>of(
			SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, EffectiveTimes.parse(values[6], DateFormats.SHORT),
			SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, EffectiveTimes.parse(values[7], DateFormats.SHORT)
		));
	}

	@Override
	public String[] getHeaderColumns() {
		return SnomedRf2Headers.MODULE_DEPENDENCY_HEADER;
	}

	@Override
	public String getType() {
		return "module-member";
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
			Long.parseLong(values[3]),
			Long.parseLong(values[4]),
			Long.parseLong(values[5])
		);
	}
	
}
