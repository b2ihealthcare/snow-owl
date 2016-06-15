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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.importer.rf2.csv.DescriptionRow;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SnomedDescriptionImporter extends AbstractSnomedTerminologyImporter<DescriptionRow, Description> {
	
	private static final Map<String, CellProcessor> CELLPROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(DescriptionRow.PROP_ID, NullObjectPattern.INSTANCE)
			.put(DescriptionRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(DescriptionRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(DescriptionRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(DescriptionRow.PROP_CONCEPT_ID, NullObjectPattern.INSTANCE)
			.put(DescriptionRow.PROP_LANGUAGE_CODE, NullObjectPattern.INSTANCE)
			.put(DescriptionRow.PROP_TYPE_ID, NullObjectPattern.INSTANCE)
			.put(DescriptionRow.PROP_TERM, NullObjectPattern.INSTANCE)
			.put(DescriptionRow.PROP_CASE_SIGNIFICANCE_ID, NullObjectPattern.INSTANCE)
			.build();

	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMED_DESCRIPTION_IDX1000", "SNOMED_DESCRIPTION", "ID", "CDO_BRANCH", "CDO_REVISED", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_DESCRIPTION_IDX1001", "SNOMED_DESCRIPTION", "ID", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_DESCRIPTION_IDX1002", "SNOMED_DESCRIPTION", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMED_DESCRIPTION_IDX1003", "SNOMED_DESCRIPTION", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.build();
	
	private static SnomedImportConfiguration<DescriptionRow> createImportConfiguration(final ComponentImportType type) {
		return new SnomedImportConfiguration<DescriptionRow>(
			type, 
			CELLPROCESSOR_MAPPING, 
			DescriptionRow.class, 
			SnomedRf2Headers.DESCRIPTION_HEADER,
			INDEXES);
	}

	public SnomedDescriptionImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier, final ComponentImportType type) {
		super(createImportConfiguration(type), importContext, releaseFileStream, releaseFileIdentifier);
	}
	
	@Override
	protected Class<? extends SnomedDocument> getType() {
		return SnomedDescriptionIndexEntry.class;
	}

	@Override
	protected void importRow(final DescriptionRow currentRow) {

		final Description editedDescription = getOrCreateComponent(currentRow.getConceptId(), currentRow.getId());
		
		if (skipCurrentRow(currentRow, editedDescription)) {
			getLogger().warn("Not importing concept '{}' with effective time '{}'; it should have been filtered from the input file.",
					currentRow.getId(), 
					EffectiveTimes.format(currentRow.getEffectiveTime(), DateFormats.SHORT));

			return;
		}

		if (currentRow.getEffectiveTime() != null) {
			editedDescription.setEffectiveTime(currentRow.getEffectiveTime());
			editedDescription.setReleased(true);
		} else {
			editedDescription.unsetEffectiveTime();
		}
		
		editedDescription.setActive(currentRow.isActive());
		editedDescription.setCaseSignificance(getConceptSafe(currentRow.getCaseSignificanceId(), SnomedRf2Headers.FIELD_CASE_SIGNIFICANCE_ID, currentRow.getId()));
		editedDescription.setLanguageCode(currentRow.getLanguageCode());
		editedDescription.setModule(getConceptSafe(currentRow.getModuleId(), SnomedRf2Headers.FIELD_MODULE_ID, currentRow.getId()));
		editedDescription.setTerm(currentRow.getTerm());
		editedDescription.setType(getConceptSafe(currentRow.getTypeId(), SnomedRf2Headers.FIELD_TYPE_ID, currentRow.getId()));
		
		getImportContext().conceptVisited(currentRow.getConceptId());
	}
	
	@Override
	protected Description createComponent(final String containerId, final String componentId) {
		final Description description = SnomedFactory.eINSTANCE.createDescription();
		description.setId(componentId);
		description.setConcept(getConceptSafe(containerId, SnomedRf2Headers.FIELD_CONCEPT_ID, componentId));
		
		return description;
	}
	
	@Override
	protected Description getComponent(final String componentId) {
		return getDescription(componentId);
	}
	
}