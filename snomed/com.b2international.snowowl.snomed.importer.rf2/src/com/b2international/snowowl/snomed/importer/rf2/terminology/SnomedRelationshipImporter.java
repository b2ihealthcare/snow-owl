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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.importer.rf2.csv.RelationshipRow;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class SnomedRelationshipImporter extends AbstractSnomedTerminologyImporter<RelationshipRow, Relationship> {

	private static final Map<String, CellProcessor> CELLPROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
				.put(RelationshipRow.PROP_ID, NullObjectPattern.INSTANCE)
				.put(RelationshipRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
				.put(RelationshipRow.PROP_ACTIVE, new ParseBool("1", "0"))
				.put(RelationshipRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
				.put(RelationshipRow.PROP_SOURCE_ID, NullObjectPattern.INSTANCE)
				.put(RelationshipRow.PROP_DESTINATION_ID, NullObjectPattern.INSTANCE)
				.put(RelationshipRow.PROP_RELATIONSHIP_GROUP, new ParseInt())
				.put(RelationshipRow.PROP_TYPE_ID, NullObjectPattern.INSTANCE)
				.put(RelationshipRow.PROP_CHARACTERISTIC_TYPE_ID, NullObjectPattern.INSTANCE)
				.put(RelationshipRow.PROP_MODIFIER_ID, NullObjectPattern.INSTANCE)
				.build();
		
	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMED_RELATIONSHIP_IDX1000", "SNOMED_RELATIONSHIP", "ID", "CDO_BRANCH", "CDO_REVISED", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_RELATIONSHIP_IDX1001", "SNOMED_RELATIONSHIP", "ID", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_RELATIONSHIP_IDX1002", "SNOMED_RELATIONSHIP", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMED_RELATIONSHIP_IDX1003", "SNOMED_RELATIONSHIP", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMED_RELATIONSHIP_IDX1004", "SNOMED_RELATIONSHIP", "DESTINATION", "ACTIVE", "CDO_VERSION"))
			.build();

	private static SnomedImportConfiguration<RelationshipRow> createImportConfiguration(final ComponentImportType type) {
		return new SnomedImportConfiguration<RelationshipRow>(
				type, 
				CELLPROCESSOR_MAPPING, 
				RelationshipRow.class, 
				SnomedRf2Headers.RELATIONSHIP_HEADER,
				INDEXES);
	}

	public SnomedRelationshipImporter(final SnomedImportContext importContext, 
			final InputStream releaseFileStream, 
			final String releaseFileIdentifier, 
			final ComponentImportType type) {
		
		super(createImportConfiguration(type), importContext, releaseFileStream, releaseFileIdentifier);
	}
	
	@Override
	protected Class<? extends SnomedDocument> getType() {
		return SnomedRelationshipIndexEntry.class;
	}

	@Override
	protected void applyRow(Relationship component, RelationshipRow row, Collection<Relationship> componentsToAttach) {
		if (row.getEffectiveTime() != null) {
			component.setEffectiveTime(row.getEffectiveTime());
			component.setReleased(true);
		} else {
			component.unsetEffectiveTime();
		}

		component.setActive(row.isActive());
		component.setModule(getConceptSafe(row.getModuleId(), SnomedRf2Headers.FIELD_MODULE_ID, row.getId()));
		
		if (component.getSource() == null || !component.getSource().getId().equals(row.getSourceId())) {
			component.setSource(getConceptSafe(row.getSourceId(), SnomedRf2Headers.FIELD_SOURCE_ID, row.getId()));
		}
		component.setDestination(getConceptSafe(row.getDestinationId(), SnomedRf2Headers.FIELD_DESTINATION_ID, row.getId()));
		component.setType(getConceptSafe(row.getTypeId(), SnomedRf2Headers.FIELD_TYPE_ID, row.getId()));
		component.setCharacteristicType(getConceptSafe(row.getCharacteristicTypeId(), SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, row.getId()));
		component.setModifier(getConceptSafe(row.getModifierId(), SnomedRf2Headers.FIELD_MODIFIER_ID, row.getId()));
		component.setGroup(row.getRelationshipGroup());
		
		// Universal "has active ingredient" relationships should be put into a union group
		if (Concepts.HAS_ACTIVE_INGREDIENT.equals(row.getTypeId()) 
				&& Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(row.getModifierId())
				&& component.getUnionGroup() != 1) {
			
			component.setUnionGroup(1);
		}
		getImportContext().conceptVisited(row.getSourceId());
	}
	
	@Override
	protected void attach(Collection<Relationship> componentsToAttach) {
		// nothing to do, we already registered the relationships to the source concepts
	}
	
	@Override
	protected Relationship createCoreComponent() {
		return SnomedFactory.eINSTANCE.createRelationship();
	}
	
}