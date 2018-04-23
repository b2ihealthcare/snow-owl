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
import com.b2international.snowowl.snomed.importer.rf2.csv.AssociatingRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.SimpleMapRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.5
 */
public class SnomedSimpleMapWithDescriptionRefSetImporter extends AbstractSnomedMapTypeRefSetImporter<SimpleMapRefSetRow> {

	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(AssociatingRefSetRow.PROP_UUID, new ParseUuid())
			.put(AssociatingRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(AssociatingRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(AssociatingRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_ASSOCIATED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_ASSOCIATED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(SimpleMapRefSetRow.PROP_MAP_TARGET_DESCRIPTION, NullObjectPattern.INSTANCE)
			.build();
	
	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1004", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "MAPTARGETCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();
	
	private static final SnomedImportConfiguration<SimpleMapRefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<SimpleMapRefSetRow>(
			ComponentImportType.SIMPLE_MAP_TYPE_REFSET_WITH_DESCRIPTION, 
			CELL_PROCESSOR_MAPPING, 
			SimpleMapRefSetRow.class, 
			SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION, 
			INDEXES); 
	
	public SnomedSimpleMapWithDescriptionRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}
	
	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION;
	}

	@Override
	protected void applyRow(SnomedSimpleMapRefSetMember member, SimpleMapRefSetRow row, Collection<SnomedSimpleMapRefSetMember> componentsToAttach) {
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
		member.setMapTargetComponentId(row.getAssociatedComponentId());
		member.setMapTargetComponentDescription(((SimpleMapRefSetRow) row).getMapTargetDescription());
	}
	
	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_SIMPLE_MAP_TYPE;
	}

	@Override
	protected SnomedSimpleMapRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedSimpleMapRefSetMember();
	}
}
