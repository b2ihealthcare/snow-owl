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

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationDefects;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationIssueReporter;

/**
 * @since 6.0.0
 */
final class Rf2DescriptionContentType implements Rf2ContentType<SnomedDescription> {

	@Override
	public SnomedDescription create() {
		return new SnomedDescription();
	}

	@Override
	public void resolve(SnomedDescription component, String[] values) {
		component.setConceptId(values[4]);
		component.setLanguageCode(values[5]);
		component.setTypeId(values[6]);
		component.setTerm(values[7]);
		component.setCaseSignificance(CaseSignificance.getByConceptId(values[8]));
	}

	@Override
	public String getContainerId(String[] values) {
		final String conceptId = values[4];
		return conceptId;
	}

	@Override
	public String[] getHeaderColumns() {
		return SnomedRf2Headers.DESCRIPTION_HEADER;
	}

	@Override
	public String getType() {
		return "description";
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
			Long.parseLong(values[3]), 
			Long.parseLong(values[4]),
			Long.parseLong(values[6]),
			Long.parseLong(values[8])
		);
	}

	@Override
	public void validateByContentType(Rf2ValidationIssueReporter reporter, String[] values) {
		final String descriptionId = values[0];
		final String conceptId = values[4];
		final String typeId = values[6];
		final String caseSignificanceId = values[8];
		
		try {
			SnomedIdentifiers.validate(descriptionId);
			validateByComponentCategory(descriptionId, reporter, ComponentCategory.DESCRIPTION);
		} catch (IllegalArgumentException e) {
			reporter.error(String.format("%s %s", descriptionId, Rf2ValidationDefects.INVALID_ID.getLabel()));
		}
		
		validateConceptIds(reporter, conceptId, typeId, caseSignificanceId);
		
	}
	
	
	
}