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

import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.importer.rf2.csv.OWLExpressionRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.5
 */
public class SnomedOWLExpressionRefSetImporter extends AbstractSnomedRefSetImporter<OWLExpressionRefSetRow, SnomedOWLExpressionRefSetMember> {

	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(OWLExpressionRefSetRow.PROP_UUID, new ParseUuid())
			.put(OWLExpressionRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(OWLExpressionRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(OWLExpressionRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(OWLExpressionRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(OWLExpressionRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, NullObjectPattern.INSTANCE)
			.build();

	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDOWLEXPRESSIONREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDOWLEXPRESSIONREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDOWLEXPRESSIONREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDOWLEXPRESSIONREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDOWLEXPRESSIONREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDOWLEXPRESSIONREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDOWLEXPRESSIONREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDOWLEXPRESSIONREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();

	private static final SnomedImportConfiguration<OWLExpressionRefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<OWLExpressionRefSetRow>(
			ComponentImportType.OWL_EXPRESSION_REFSET,
			CELL_PROCESSOR_MAPPING,
			OWLExpressionRefSetRow.class,
			SnomedRf2Headers.OWL_EXPRESSION_HEADER,
			INDEXES);

	public SnomedOWLExpressionRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_OWL_EXPRESSION_TYPE;
	}

	@Override
	protected SnomedOWLExpressionRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedOWLExpressionRefSetMember();
	}

	@Override
	protected void applyRow(final SnomedOWLExpressionRefSetMember member, final OWLExpressionRefSetRow row, final Collection<SnomedOWLExpressionRefSetMember> componentsToAttach) {

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
		member.setOwlExpression(row.getOwlExpression());

	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		// FIXME this should be fixed properly by reverting to only one refset type from OWL_AXIOM and OWL_ONTOLOGY 
		return null;
	}
	
	@Override
	protected Expression getAvailableComponentQuery() {
		return SnomedRefSetMemberIndexEntry.Expressions.refSetTypes(ImmutableSet.<SnomedRefSetType>of(SnomedRefSetType.OWL_AXIOM, SnomedRefSetType.OWL_ONTOLOGY));
	}
	
	@Override
	protected SnomedRefSet createRefSet(String identifierConceptId, String referencedComponentId) {
		createIdentifierConceptIfNotExists(identifierConceptId);
		final SnomedRefSet refSet = createUninitializedRefSet(identifierConceptId);
		refSet.setIdentifierId(identifierConceptId);
		refSet.setReferencedComponentType(SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(referencedComponentId));
		refSet.setType(getRefsetType(identifierConceptId));
		initRefSet(refSet, referencedComponentId);
		return refSet;
	}

	private SnomedRefSetType getRefsetType(String identifierConceptId) {
		if (Concepts.REFSET_OWL_AXIOM.equals(identifierConceptId)) {
			return SnomedRefSetType.OWL_AXIOM;
		} else if (Concepts.REFSET_OWL_ONTOLOGY.equals(identifierConceptId)) {
			return SnomedRefSetType.OWL_ONTOLOGY;
		}
		throw new SnowowlRuntimeException("Unknown OWL Expression reference set identifier ID: " + identifierConceptId);
	}

}
