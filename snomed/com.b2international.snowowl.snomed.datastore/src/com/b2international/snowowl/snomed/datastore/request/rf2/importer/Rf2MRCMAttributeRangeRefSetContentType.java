/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.MRCM_ATTRIBUTE_RANGE_HEADER;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.5
 */
public class Rf2MRCMAttributeRangeRefSetContentType implements Rf2RefSetContentType {

	@Override
	public void resolve(SnomedReferenceSetMember component, String[] values) {
		component.setType(SnomedRefSetType.MRCM_ATTRIBUTE_RANGE);
		component.setReferenceSetId(values[4]);
		// XXX actual type is not relevant here
		component.setReferencedComponent(new SnomedConcept(values[5]));
		
		component.setProperties(
			ImmutableMap.<String, Object>builder()
				.put(FIELD_MRCM_RANGE_CONSTRAINT, values[6])
				.put(FIELD_MRCM_ATTRIBUTE_RULE, values[7]) 
				.put(FIELD_MRCM_RULE_STRENGTH_ID, values[8])
				.put(FIELD_MRCM_CONTENT_TYPE_ID, values[9])
				.build()
		);
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
				Long.parseLong(values[3]),	
				Long.parseLong(values[4]),
				Long.parseLong(values[8]),
				Long.parseLong(values[9])
			);
	}

	@Override
	public String getType() {
		return "mrcm-attribute-range";
	}

	@Override
	public String[] getHeaderColumns() {
		return MRCM_ATTRIBUTE_RANGE_HEADER;
	}

	@Override
	public void validateMembersByReferenceSetContentType(Rf2ValidationIssueReporter reporter, String[] values) {
		final String memberId = values[0];
		final String rangeConstraint = values[6];
		final String attributeRule = values[7];
		final String ruleStrenghtId = values[8];
		final String contentTypeId = values[9];
		
		if (Strings.isNullOrEmpty(rangeConstraint)) {
			reporter.error(String.format("Range constraint field was empty for '%s'", memberId));
		}
		
		if (Strings.isNullOrEmpty(attributeRule)) {
			reporter.warning(String.format("Attribute Rule field was empty for '%s'", memberId));
		}
		
		validateConceptIds(reporter, ruleStrenghtId, contentTypeId);
	}

}
