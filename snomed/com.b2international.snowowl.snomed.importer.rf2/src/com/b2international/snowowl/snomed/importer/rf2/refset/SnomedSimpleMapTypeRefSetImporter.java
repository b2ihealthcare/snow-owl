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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
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
import com.google.common.collect.ImmutableMap.Builder;

public class SnomedSimpleMapTypeRefSetImporter extends AbstractSnomedMapTypeRefSetImporter<SimpleMapRefSetRow> {

	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER_IDX1004", "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER", "MAPTARGETCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();
	private boolean extended;
	
	public SnomedSimpleMapTypeRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, boolean extended, final String releaseFileIdentifier) {
		super(createImportConfiguration(extended), importContext, releaseFileStream, releaseFileIdentifier);
		this.extended = extended;
	}

	private static SnomedImportConfiguration<SimpleMapRefSetRow> createImportConfiguration(boolean extended) {
		
		final Builder<String, CellProcessor> builder = ImmutableMap.<String, CellProcessor>builder()
				.put(AssociatingRefSetRow.PROP_UUID, new ParseUuid())
				.put(AssociatingRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
				.put(AssociatingRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
				.put(AssociatingRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
				.put(AssociatingRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
				.put(AssociatingRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
				.put(AssociatingRefSetRow.PROP_ASSOCIATED_COMPONENT_ID, NullObjectPattern.INSTANCE);
		
		if (extended) {
			builder.put(SimpleMapRefSetRow.PROP_MAP_TARGET_DESCRIPTION, NullObjectPattern.INSTANCE);
		}
		
		final Map<String, CellProcessor> cellProcessorMapping = builder.build();
		
		final ComponentImportType type = (extended) 
				? ComponentImportType.SIMPLE_MAP_TYPE_REFSET_WITH_DESCRIPTION
				: ComponentImportType.SIMPLE_MAP_TYPE_REFSET;
		
		final String[] expectedHeader = (extended)
				? SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION
				: SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER;
		
		return new SnomedImportConfiguration<SimpleMapRefSetRow>(
				type, 
				cellProcessorMapping, 
				SimpleMapRefSetRow.class, 
				expectedHeader, 
				INDEXES);
	}
	
	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.SIMPLE_MAP;
	}
	
	@Override
	protected SnomedSimpleMapRefSetMember doImportRow(final SimpleMapRefSetRow currentRow) {

		final SnomedSimpleMapRefSetMember editedMember = (SnomedSimpleMapRefSetMember) getOrCreateMember(currentRow.getUuid());
		
		if (skipCurrentRow(currentRow, editedMember)) {
			getLogger().warn("Not importing simple map reference set member '{}' with effective time '{}'; it should have been filtered from the input file.",
					currentRow.getUuid(), 
					EffectiveTimes.format(currentRow.getEffectiveTime(), DateFormats.SHORT));

			return null;
		}

		if (currentRow.getEffectiveTime() != null) {
			editedMember.setEffectiveTime(currentRow.getEffectiveTime());
			editedMember.setReleased(true);
		} else {
			editedMember.unsetEffectiveTime();
		}

		editedMember.setRefSet(getOrCreateRefSet(currentRow.getRefSetId(), currentRow.getReferencedComponentId()));
		editedMember.setActive(currentRow.isActive());
		editedMember.setModuleId(currentRow.getModuleId());
		editedMember.setReferencedComponentId(currentRow.getReferencedComponentId());
		editedMember.setMapTargetComponentId(currentRow.getAssociatedComponentId());
		
		if (extended) {
			editedMember.setMapTargetComponentDescription(((SimpleMapRefSetRow) currentRow).getMapTargetDescription());
		}
		
		return editedMember;
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_SIMPLE_MAP_TYPE;
	}

	@Override
	protected SnomedSimpleMapRefSetMember createRefSetMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedSimpleMapRefSetMember();
	}
}