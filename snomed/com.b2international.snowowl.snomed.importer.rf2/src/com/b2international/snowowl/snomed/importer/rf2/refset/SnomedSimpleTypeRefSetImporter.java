/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.importer.rf2.csv.RefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SnomedSimpleTypeRefSetImporter extends AbstractSnomedRefSetImporter<RefSetRow, SnomedRefSetMember> {

	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(RefSetRow.PROP_UUID, new ParseUuid())
			.put(RefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(RefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(RefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(RefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(RefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.build();
	
	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();

	private static final SnomedImportConfiguration<RefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<RefSetRow>(
			ComponentImportType.SIMPLE_TYPE_REFSET, 
			CELL_PROCESSOR_MAPPING, 
			RefSetRow.class, 
			SnomedRf2Headers.SIMPLE_TYPE_HEADER,
			INDEXES);

	public SnomedSimpleTypeRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.SIMPLE;
	}

	@Override
	protected void applyRow(SnomedRefSetMember member, RefSetRow row, Collection<SnomedRefSetMember> componentsToAttach) {
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
	}
	
	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_SIMPLE_TYPE;
	}

	@Override
	protected SnomedRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedRefSetMember();
	}
}