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

import org.apache.lucene.search.Query;
import org.eclipse.core.runtime.SubMonitor;
import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.importer.AbstractImportUnit;
import com.b2international.snowowl.importer.ImportAction;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.importer.rf2.csv.ConceptRow;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportUnit;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SnomedConceptImporter extends AbstractSnomedTerminologyImporter<ConceptRow, Concept> {

	
	private ImmutableMap.Builder<String, ConceptRow> conceptRowBuilder;
	
	private static final Map<String, CellProcessor> CELLPROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(ConceptRow.PROP_ID, NullObjectPattern.INSTANCE)
			.put(ConceptRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(ConceptRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(ConceptRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(ConceptRow.PROP_DEFINITION_STATUS_ID, NullObjectPattern.INSTANCE)
			.build();

	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMED_CONCEPT_IDX1000", "SNOMED_CONCEPT", "ID", "CDO_BRANCH", "CDO_REVISED", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_CONCEPT_IDX1001", "SNOMED_CONCEPT", "ID", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_CONCEPT_IDX1002", "SNOMED_CONCEPT", "CDO_CREATED"))
			.build();
	
	private static final SnomedImportConfiguration<ConceptRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<ConceptRow>(
			ComponentImportType.CONCEPT, 
			CELLPROCESSOR_MAPPING, 
			ConceptRow.class, 
			SnomedRf2Headers.CONCEPT_HEADER,
			INDEXES);

	public SnomedConceptImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected Query getAvailableComponentsQuery() {
		return SnomedMappings.newQuery().concept().matchAll();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden in {@link SnomedConceptImporter} to read all concept rows into
	 * a map first, then do the actual import.
	 */
	@Override
	public void doImport(final SubMonitor subMonitor, final AbstractImportUnit unit) {

		final Map<String, ConceptRow> conceptRowMap;
		final ComponentImportUnit concreteUnit = (ComponentImportUnit) unit;
		final String effectiveTimeKey = concreteUnit.getEffectiveTimeKey();
		
		try {
			conceptRowBuilder = ImmutableMap.builder();
			super.doImport(subMonitor, unit);
			conceptRowMap = conceptRowBuilder.build();
		} finally {
			conceptRowBuilder = null;
		}
		
		int unitsCommitted = 0;
		
		for (final ConceptRow rowMapValue : conceptRowMap.values()) {
			
			importRow(rowMapValue, conceptRowMap);
			unitsCommitted++;
			subMonitor.worked(1);

			if (!needsCommitting(unitsCommitted)) {
				continue;
			}
			
			if (ImportAction.BREAK.equals(cdoCommit(subMonitor, effectiveTimeKey))) {
				break;
			}
		}
		
		cdoCommit(subMonitor, effectiveTimeKey);
	}

	@Override
	protected int getImportWorkUnits(final int recordCount) {
		return (2 * recordCount) + (recordCount / COMMIT_EVERY_NUM_ELEMENTS + 1) * COMMIT_WORK_UNITS;
	}
	
	////////////////////////////////////////////////////////
	// Diversion of commit operations to separate methods
	////////////////////////////////////////////////////////
	
	@Override
	protected boolean needsCommitting(final int unitsAdded) {
		return false;
	}
	
	
	@Override
	protected ImportAction commit(final SubMonitor subMonitor, final String formattedEffectiveTime) {
		return ImportAction.CONTINUE;
	}

	private ImportAction cdoCommit(final SubMonitor subMonitor, final String formattedEffectiveTime) {
		return super.commit(subMonitor, formattedEffectiveTime);
	}

	@Override
	protected void importRow(final ConceptRow currentRow) {
		conceptRowBuilder.put(currentRow.getId(), currentRow);
	}
	
	protected void importRow(final ConceptRow currentRow, final Map<String, ConceptRow> conceptRowMap) {

		final Concept editedConcept = getOrCreateComponent(null, currentRow.getId());
		
		if (skipCurrentRow(currentRow, editedConcept)) {
			getLogger().warn("Not importing concept '{}' with effective time '{}'; it should have been filtered from the input file.",
					currentRow.getId(), 
					EffectiveTimes.format(currentRow.getEffectiveTime(), DateFormats.SHORT));
			
			return;
		}
		
		if (currentRow.getEffectiveTime() != null) {
			editedConcept.setEffectiveTime(currentRow.getEffectiveTime());
			editedConcept.setReleased(true);
		} else {
			editedConcept.unsetEffectiveTime();
		}
		
		editedConcept.setExhaustive(false);
		editedConcept.setActive(currentRow.isActive());
		editedConcept.setDefinitionStatus(getOrCreateComponent(null, currentRow.getDefinitionStatusId()));
		editedConcept.setModule(getOrCreateComponent(null, currentRow.getModuleId()));
		
		getImportContext().conceptVisited(currentRow.getId());
	}
	
	@Override
	protected Concept getComponent(final String componentId) {
		return getConcept(componentId);
	}
	
	@Override
	protected Concept createComponent(final String containerId, final String componentId) {
		final Concept concept = SnomedFactory.eINSTANCE.createConcept();
		concept.setId(componentId);
		//add to CDO resource after initializing it
		//this is to avoid dangling reference exception when committing
		//consider the following use case
		//after every 50k RF2 file row, a commit is performed if the underlying transaction is dirty
		//consider 50k brand new concept with a non-existing module concept which will be processed as the 50001 RF2 file row
		//the module concept will be created, but will not be added to its CDO resource but set as a module for the previous 50k concepts
		//cause dangling reference exception on commit
		getImportContext().getEditingContext().add(concept);
		
		return concept;
	}
}