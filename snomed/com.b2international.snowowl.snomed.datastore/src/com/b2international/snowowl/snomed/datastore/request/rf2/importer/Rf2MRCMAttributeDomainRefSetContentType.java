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

import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_GROUPED;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.MRCM_ATTRIBUTE_DOMAIN_HEADER;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.5
 */
public class Rf2MRCMAttributeDomainRefSetContentType implements Rf2RefSetContentType {

	@Override
	public void resolve(SnomedReferenceSetMember component, String[] values) {
		component.setType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN);
		component.setReferenceSetId(values[4]);
		// XXX actual type is not relevant here
		component.setReferencedComponent(new SnomedConcept(values[5]));
		
		component.setProperties(
			ImmutableMap.<String, Object>builder()
				.put(FIELD_MRCM_DOMAIN_ID, values[6])
				.put(FIELD_MRCM_GROUPED, BooleanUtils.valueOf(values[7])) 
				.put(FIELD_MRCM_ATTRIBUTE_CARDINALITY, values[8])
				.put(FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, values[9]) 
				.put(FIELD_MRCM_RULE_STRENGTH_ID, values[10])
				.put(FIELD_MRCM_CONTENT_TYPE_ID, values[11])
				.build()
		);
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
				Long.parseLong(values[3]),	
				Long.parseLong(values[4]),
				Long.parseLong(values[6]),
				Long.parseLong(values[10]),
				Long.parseLong(values[11])
			);
	}

	@Override
	public String getType() {
		return "mrcm-attribute-domain";
	}

	@Override
	public String[] getHeaderColumns() {
		return MRCM_ATTRIBUTE_DOMAIN_HEADER;
	}

}
