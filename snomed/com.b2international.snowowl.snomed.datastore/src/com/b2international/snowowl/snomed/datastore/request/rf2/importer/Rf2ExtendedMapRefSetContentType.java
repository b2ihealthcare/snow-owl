/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.EXTENDED_MAP_TYPE_HEADER;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_CORRELATION_ID;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MAP_ADVICE;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MAP_CATEGORY_ID;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MAP_GROUP;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MAP_PRIORITY;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MAP_RULE;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MAP_TARGET;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.0.0
 */
final class Rf2ExtendedMapRefSetContentType implements Rf2RefSetContentType {

	@Override
	public void resolve(SnomedReferenceSetMember component, String[] values) {
		component.setType(SnomedRefSetType.EXTENDED_MAP);
		component.setReferenceSetId(values[4]);
		// XXX actual type is not relevant here
		component.setReferencedComponent(new SnomedConcept(values[5]));
		component.setProperties(
			ImmutableMap.<String, Object>builder()
				.put(FIELD_MAP_GROUP, Integer.parseInt(values[6]))
				.put(FIELD_MAP_PRIORITY, Integer.parseInt(values[7])) 
				.put(FIELD_MAP_RULE, values[8])
				.put(FIELD_MAP_ADVICE, values[9]) 
				.put(FIELD_MAP_TARGET, values[10])
				.put(FIELD_CORRELATION_ID, values[11])
				.put(FIELD_MAP_CATEGORY_ID, values[12])
				.build()
		);
	}
	
	@Override
	public String[] getHeaderColumns() {
		return EXTENDED_MAP_TYPE_HEADER;
	}
	
	@Override
	public String getType() {
		return "extended-member";
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
			Long.parseLong(values[3]),
			Long.parseLong(values[4]),
			Long.parseLong(values[11]),
			Long.parseLong(values[12])
		);
	}

	@Override
	public void validateMembersByReferenceSetContentType(Rf2ValidationIssueReporter reporter, String[] values) {
		final String memberId = values[0];
		final String mapGroup = values[6];
		final String mapPriority = values[7];
		final String mapRule = values[8];
		final String mapAdvice = values[9];
		final String mapTarget = values[10];
		final String correlationId = values[11];
		final String mapCategoryId = values[12];
		
		if (Strings.isNullOrEmpty(mapGroup)) {
			reporter.error("Extended Map group field was empty for '%s'", memberId);
		}
		
		if (Strings.isNullOrEmpty(mapPriority)) {
			reporter.error("Extended Map priority field was empty for '%s'", memberId);
		}
		
		if (Strings.isNullOrEmpty(mapRule)) {
			reporter.error("Extended Map rule field was empty for '%s'", memberId);
		}
		
		if (Strings.isNullOrEmpty(mapAdvice)) {
			reporter.error("Extended Map advice field was empty for '%s'", memberId);
		}
		
		if (Strings.isNullOrEmpty(mapTarget)) {
			reporter.warning("Extended Map target field was empty for '%s'", memberId);
		}
				
		validateConceptIds(reporter, correlationId, mapCategoryId);
	}
	
	
}