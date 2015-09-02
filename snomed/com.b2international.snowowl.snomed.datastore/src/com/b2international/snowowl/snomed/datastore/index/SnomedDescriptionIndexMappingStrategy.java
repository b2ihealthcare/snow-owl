/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index;

import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.asLong;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_EFFECTIVE_TIME;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.Long.parseLong;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericDocValuesField;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Mapping strategy for SNOMED CT descriptions.
 */
public class SnomedDescriptionIndexMappingStrategy extends AbstractIndexMappingStrategy {
	
	private final Description description;

	public SnomedDescriptionIndexMappingStrategy(final Description description) {
		this.description = description;
	}

	@Override
	public Document createDocument() {
		final long caseSignificanceId = parseLong(description.getCaseSignificance().getId());
		final long typeId = parseLong(description.getType().getId());
		final long conceptId = parseLong(description.getConcept().getId());
		final long effectiveTime = EffectiveTimes.getEffectiveTime(description.getEffectiveTime());
		
		final Document doc = SnomedMappings.doc()
				.id(description.getId())
				.type(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
				.storageKey(getStorageKey())
				.labelWithSearchKey(nullToEmpty(description.getTerm()))
				.active(description.isActive())
				.module(description.getModule().getId())
				.descriptionConcept(conceptId)
				.descriptionType(typeId)
				.storedOnly(DESCRIPTION_CASE_SIGNIFICANCE_ID, caseSignificanceId)
				.storedOnly(COMPONENT_RELEASED, description.isReleased() ? 1 : 0)
				.docValuesField(DESCRIPTION_EFFECTIVE_TIME, effectiveTime)
				.build();
		// TODO design stored + docvalues fields
		doc.add(new NumericDocValuesField(DESCRIPTION_CASE_SIGNIFICANCE_ID, caseSignificanceId));
		return doc;
	}
	
	@Override
	protected long getStorageKey() {
		return asLong(description.cdoID());
	}
}
