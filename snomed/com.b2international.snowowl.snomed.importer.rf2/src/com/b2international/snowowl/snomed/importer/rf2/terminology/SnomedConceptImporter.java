/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.importer.rf2.csv.ConceptRow;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SnomedConceptImporter extends AbstractSnomedTerminologyImporter<ConceptRow, Concept> {

	private static final Map<String, CellProcessor> CELLPROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(ConceptRow.PROP_ID, NullObjectPattern.INSTANCE)
			.put(ConceptRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(ConceptRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(ConceptRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(ConceptRow.PROP_DEFINITION_STATUS_ID, NullObjectPattern.INSTANCE)
			.build();

	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMED_CONCEPT_IDX1000", 
					"SNOMED_CONCEPT", 
					"ID", "CDO_BRANCH", "CDO_REVISED", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_CONCEPT_IDX1001", 
					"SNOMED_CONCEPT", 
					"ID", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_CONCEPT_IDX1002", 
					"SNOMED_CONCEPT", 
					"CDO_CREATED"))
			// Index for list mapping table of concrete domain members that reference concepts 
			.add(new IndexConfiguration("SNOMED_CONCEPT_CONCRETEDOMAINREFSETMEMBERS_LIST_IDX1000",
					"SNOMED_CONCEPT_CONCRETEDOMAINREFSETMEMBERS_LIST", 
					"CDO_SOURCE", "CDO_BRANCH", "CDO_VERSION_ADDED", "CDO_IDX"))
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
	protected Class<? extends SnomedDocument> getType() {
		return SnomedConceptDocument.class;
	}
	
	@Override
	protected Concept createCoreComponent() {
		return SnomedFactory.eINSTANCE.createConcept();
	}

	@Override
	protected void attach(Collection<Concept> componentsToAttach) {
		getImportContext().getEditingContext().addAll(componentsToAttach);
	}
	
	@Override
	protected void applyRow(Concept component, ConceptRow row, Collection<Concept> componentsToAttach) {
		if (row.getEffectiveTime() != null) {
			component.setEffectiveTime(row.getEffectiveTime());
			component.setReleased(true);
		} else {
			component.unsetEffectiveTime();
		}
		
		component.setActive(row.isActive());
		component.setModule(getOrCreate(row.getModuleId(), componentsToAttach));
		component.setDefinitionStatus(getOrCreate(row.getDefinitionStatusId(), componentsToAttach));
		component.setExhaustive(false);
		
		getImportContext().conceptVisited(row.getId());		
	}

	@Override
	protected int getImportWorkUnits(final int recordCount) {
		return (2 * recordCount) + (recordCount / COMMIT_EVERY_NUM_ELEMENTS + 1) * COMMIT_WORK_UNITS;
	}
	
	@Override
	protected boolean needsCommitting(final int unitsAdded) {
		return false;
	}
	
}