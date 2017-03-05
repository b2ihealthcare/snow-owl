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
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.rf2.csv.ComplexMapTypeRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * SNOMED CT complex map type reference set importer.
 * @see SnomedSimpleMapTypeRefSetImporter
 */
public class SnomedComplexMapTypeRefSetImporter extends AbstractSnomedMapTypeRefSetImporter<ComplexMapTypeRefSetRow> {
	
	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER_IDX1004", "SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER", "MAPTARGETCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();
	private boolean extended;

	public SnomedComplexMapTypeRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, boolean extended, final String releaseFileIdentifier) {
		super(createImportConfiguration(extended), importContext, releaseFileStream, releaseFileIdentifier);
		this.extended = extended;
	}

	private static SnomedImportConfiguration<ComplexMapTypeRefSetRow> createImportConfiguration(boolean extended) {
		
		final Builder<String, CellProcessor> builder = ImmutableMap.<String, CellProcessor>builder()
				.put(ComplexMapTypeRefSetRow.PROP_UUID, new ParseUuid())
				.put(ComplexMapTypeRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
				.put(ComplexMapTypeRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
				.put(ComplexMapTypeRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
				.put(ComplexMapTypeRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
				.put(ComplexMapTypeRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
				.put(ComplexMapTypeRefSetRow.PROP_MAP_GROUP, new ParseInt())
				.put(ComplexMapTypeRefSetRow.PROP_MAP_PRIORITY, new ParseInt())
				.put(ComplexMapTypeRefSetRow.PROP_MAP_RULE, NullObjectPattern.INSTANCE)
				.put(ComplexMapTypeRefSetRow.PROP_MAP_ADVICE, NullObjectPattern.INSTANCE)
				.put(ComplexMapTypeRefSetRow.PROP_ASSOCIATED_COMPONENT_ID, NullObjectPattern.INSTANCE)
				.put(ComplexMapTypeRefSetRow.PROP_CORRELATION_ID, NullObjectPattern.INSTANCE);
		
		if (extended) {
			builder.put(ComplexMapTypeRefSetRow.PROP_MAP_CATEGORY_ID, NullObjectPattern.INSTANCE);
		}
		
		final Map<String, CellProcessor> cellProcessorMapping = builder.build();
		
		final ComponentImportType type = (extended) 
				? ComponentImportType.EXTENDED_MAP_TYPE_REFSET 
				: ComponentImportType.COMPLEX_MAP_TYPE_REFSET;
		
		final String[] expectedHeader = (extended)
				? SnomedRf2Headers.EXTENDED_MAP_TYPE_HEADER
				: SnomedRf2Headers.COMPLEX_MAP_TYPE_HEADER;
		
		return new SnomedImportConfiguration<ComplexMapTypeRefSetRow>(
				type, 
				cellProcessorMapping, 
				ComplexMapTypeRefSetRow.class, 
				expectedHeader, 
				INDEXES);
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return extended ? SnomedRefSetType.EXTENDED_MAP : SnomedRefSetType.COMPLEX_MAP;
	}
	
	@Override
	protected void applyRow(SnomedSimpleMapRefSetMember member, ComplexMapTypeRefSetRow row, Collection<SnomedSimpleMapRefSetMember> componentsToAttach) {
		SnomedComplexMapRefSetMember complexMember = (SnomedComplexMapRefSetMember) member;
		if (row.getEffectiveTime() != null) {
			complexMember.setEffectiveTime(row.getEffectiveTime());
			complexMember.setReleased(true);
		} else {
			complexMember.unsetEffectiveTime();
		}

		complexMember.setRefSet(getOrCreateRefSet(row.getRefSetId(), row.getReferencedComponentId()));
		complexMember.setActive(row.isActive());
		complexMember.setModuleId(row.getModuleId());
		complexMember.setReferencedComponentId(row.getReferencedComponentId());
		complexMember.setMapTargetComponentId(row.getAssociatedComponentId());
		complexMember.setCorrelationId(row.getCorrelationId());
		complexMember.setMapAdvice(row.getMapAdvice());
		complexMember.setMapRule(row.getMapRule());
		complexMember.setMapGroup(row.getMapGroup());
		complexMember.setMapPriority(row.getMapPriority());
		
		if (extended) {
			complexMember.setMapCategoryId(row.getMapCategoryId());
		}
	}
	
	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_COMPLEX_MAP_TYPE;
	}

	@Override
	protected SnomedComplexMapRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedComplexMapRefSetMember();
	}
}