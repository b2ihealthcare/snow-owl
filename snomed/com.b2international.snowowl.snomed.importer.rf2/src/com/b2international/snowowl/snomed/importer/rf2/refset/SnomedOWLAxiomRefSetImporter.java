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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.rf2.csv.OWLAxiomRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.5
 */
public class SnomedOWLAxiomRefSetImporter extends AbstractSnomedRefSetImporter<OWLAxiomRefSetRow, SnomedAnnotationRefSetMember> {

	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(OWLAxiomRefSetRow.PROP_UUID, new ParseUuid())
			.put(OWLAxiomRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(OWLAxiomRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(OWLAxiomRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(OWLAxiomRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(OWLAxiomRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, NullObjectPattern.INSTANCE)
			.build();

	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDANNOTATIONREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDANNOTATIONREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDANNOTATIONREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDANNOTATIONREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDANNOTATIONREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDANNOTATIONREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDANNOTATIONREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDANNOTATIONREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();

	private static final SnomedImportConfiguration<OWLAxiomRefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<OWLAxiomRefSetRow>(
			ComponentImportType.OWL_AXIOM_REFSET,
			CELL_PROCESSOR_MAPPING,
			OWLAxiomRefSetRow.class,
			SnomedRf2Headers.OWL_AXIOM_HEADER,
			INDEXES);

	public SnomedOWLAxiomRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_ANNOTATION_TYPE;
	}

	@Override
	protected SnomedAnnotationRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedAnnotationRefSetMember();
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.OWL_AXIOM;
	}

	@Override
	protected void applyRow(final SnomedAnnotationRefSetMember member, final OWLAxiomRefSetRow row, final Collection<SnomedAnnotationRefSetMember> componentsToAttach) {

		if (row.getEffectiveTime() != null) {
			member.setEffectiveTime(row.getEffectiveTime());
			member.setReleased(true);
		} else {
			member.unsetEffectiveTime();
		}

		member.setRefSet(getOrCreateRefSet(row.getRefSetId(), row.getReferencedComponentId()));
		member.setActive(row.isActive());
		member.setModuleId(row.getModuleId());
		member.setReferencedComponentId(row.getReferencedComponentId());
		member.setAnnotation(row.getOwlExpression());

	}

}
