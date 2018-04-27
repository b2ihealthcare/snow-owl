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

import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_OWL_EXPRESSION;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.OWL_AXIOM_HEADER;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.5.0
 */
public class Rf2OwlAxiomRefSetContentType implements Rf2RefSetContentType {

	@Override
	public void resolve(SnomedReferenceSetMember component, String[] values) {
		component.setType(SnomedRefSetType.OWL_AXIOM);
		component.setReferenceSetId(values[4]);
		// XXX actual type is not relevant here
		component.setReferencedComponent(new SnomedConcept(values[5]));
		component.setProperties(ImmutableMap.of(FIELD_OWL_EXPRESSION, values[6]));
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
				Long.parseLong(values[3]),	
				Long.parseLong(values[4])
			);
	}

	@Override
	public String getType() {
		return "owl-axiom";
	}

	@Override
	public String[] getHeaderColumns() {
		return OWL_AXIOM_HEADER;
	}

}
