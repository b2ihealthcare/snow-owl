/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor.ImportDefectBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.0.0
 */
final class Rf2LanguageRefSetContentType implements Rf2RefSetContentType {
	
	@Override
	public String[] getHeaderColumns() {
		return SnomedRf2Headers.LANGUAGE_TYPE_HEADER;
	}
	
	@Override
	public void resolve(SnomedReferenceSetMember component, String[] values) {
		component.setType(SnomedRefSetType.LANGUAGE);
		component.setReferenceSetId(values[4]);
		component.setReferencedComponent(new SnomedDescription(values[5]));
		component.setProperties(ImmutableMap.of(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, values[6]));
	}
	
	@Override
	public String getType() {
		return "language-member";
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
			Long.parseLong(values[3]),
			Long.parseLong(values[4]),
			Long.parseLong(values[6])
		);
	}

	@Override
	public void validateMembersByReferenceSetContentType(ImportDefectBuilder defectBuilder, String[] values) {
		final String referencedComponentId = values[5];
		final String acceptabilityId = values[6];
		
		validateByComponentCategory(defectBuilder, referencedComponentId, ComponentCategory.DESCRIPTION);
		validateConceptIds(defectBuilder, acceptabilityId);
	}
}
