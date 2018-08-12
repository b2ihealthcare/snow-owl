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
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.AbstractRf2RowValidator;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2SimpleRefSetRowValidator;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;

/**
 * @since 6.0.0
 */
final class Rf2SimpleRefSetContentType implements Rf2RefSetContentType {
	
	@Override
	public String[] getHeaderColumns() {
		return SnomedRf2Headers.SIMPLE_TYPE_HEADER;
	}
	
	@Override
	public void resolve(SnomedReferenceSetMember component, String[] values) {
		component.setType(SnomedRefSetType.SIMPLE);
		component.setReferenceSetId(values[4]);
		// XXX actual type is not relevant here
		component.setReferencedComponent(new SnomedConcept(values[5]));
	}
	
	@Override
	public String getType() {
		return "simple-member";
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
			Long.parseLong(values[3]),
			Long.parseLong(values[4])
		);
	}
	
	@Override
	public AbstractRf2RowValidator getValidator(Rf2ValidationIssueReporter reporter, String[] values) {
		return new Rf2SimpleRefSetRowValidator(reporter, values);
	}
	
}