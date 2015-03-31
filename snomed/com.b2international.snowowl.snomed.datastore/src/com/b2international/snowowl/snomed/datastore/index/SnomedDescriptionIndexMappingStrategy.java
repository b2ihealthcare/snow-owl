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
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CONCEPT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.Long.parseLong;
import static org.apache.lucene.document.Field.Store.YES;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.SortKeyMode;
import com.b2international.snowowl.snomed.Description;

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
		
		final String descriptionId = description.getId();
		final String term = nullToEmpty(description.getTerm());
		final boolean active = description.isActive();
		final long storageKey = asLong(description.cdoID());
		final long caseSignificanceId = parseLong(description.getCaseSignificance().getId());
		final long typeId = parseLong(description.getType().getId());
		final long conceptId = parseLong(description.getConcept().getId());
		final long moduleId = parseLong(description.getModule().getId());
		final long effectiveTime = EffectiveTimes.getEffectiveTime(description.getEffectiveTime());
		
		final Document doc = new Document();
		doc.add(new LongField(COMPONENT_ID, parseLong(descriptionId), YES));
		doc.add(new IntField(COMPONENT_TYPE, DESCRIPTION_NUMBER, YES));
		doc.add(new TextField(COMPONENT_LABEL, term, YES));
		SortKeyMode.SEARCH_ONLY.add(doc, term);
		doc.add(new BinaryDocValuesField(COMPONENT_LABEL, new BytesRef(term)));
		doc.add(new IntField(COMPONENT_ACTIVE, active ? 1 : 0, YES));
		doc.add(new LongField(COMPONENT_STORAGE_KEY, storageKey, YES));
		doc.add(new StoredField(DESCRIPTION_CASE_SIGNIFICANCE_ID, caseSignificanceId));
		doc.add(new StoredField(COMPONENT_RELEASED, description.isReleased() ? 1 : 0));
		doc.add(new LongField(DESCRIPTION_TYPE_ID, typeId, YES));
		doc.add(new LongField(DESCRIPTION_CONCEPT_ID, conceptId, YES));
		doc.add(new LongField(DESCRIPTION_MODULE_ID, moduleId, YES));
		doc.add(new LongField(DESCRIPTION_EFFECTIVE_TIME, effectiveTime, YES));

		doc.add(new NumericDocValuesField(COMPONENT_ID, parseLong(descriptionId)));
		doc.add(new NumericDocValuesField(COMPONENT_STORAGE_KEY, storageKey));
		doc.add(new NumericDocValuesField(DESCRIPTION_CASE_SIGNIFICANCE_ID, caseSignificanceId));
		doc.add(new NumericDocValuesField(DESCRIPTION_TYPE_ID, typeId));
		doc.add(new NumericDocValuesField(DESCRIPTION_CONCEPT_ID, conceptId));
		doc.add(new NumericDocValuesField(DESCRIPTION_MODULE_ID, moduleId));
		doc.add(new NumericDocValuesField(DESCRIPTION_EFFECTIVE_TIME, effectiveTime));
		
		return doc;
	}
	
	@Override
	protected long getStorageKey() {
		return asLong(description.cdoID());
	}
}
