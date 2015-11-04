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
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.rf2.csv.RelationshipRow;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SnomedRelationshipImporter extends AbstractSnomedTerminologyImporter<RelationshipRow, Relationship> {

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

	public SnomedRelationshipImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier, final ComponentImportType type) {
		super(createImportConfiguration(type), importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected void importRow(final RelationshipRow currentRow) {

		final Relationship editedRelationship = getOrCreateRelationship(currentRow.getSourceId(), currentRow.getId());
		
		if (skipCurrentRow(currentRow, editedRelationship)) {
			return;
		}

		if (currentRow.getEffectiveTime() != null) {
			editedRelationship.setEffectiveTime(currentRow.getEffectiveTime());
			editedRelationship.setReleased(true);
		} else {
			editedRelationship.unsetEffectiveTime();
		}

		editedRelationship.setActive(currentRow.isActive());
		editedRelationship.setModule(getConceptSafe(currentRow.getModuleId(), SnomedRf2Headers.FIELD_MODULE_ID, currentRow.getId()));
		editedRelationship.setDestination(getConceptSafe(currentRow.getDestinationId(), SnomedRf2Headers.FIELD_DESTINATION_ID, currentRow.getId()));
		editedRelationship.setType(getConceptSafe(currentRow.getTypeId(), SnomedRf2Headers.FIELD_TYPE_ID, currentRow.getId()));
		editedRelationship.setCharacteristicType(getConceptSafe(currentRow.getCharacteristicTypeId(), SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, currentRow.getId()));
		editedRelationship.setModifier(getConceptSafe(currentRow.getModifierId(), SnomedRf2Headers.FIELD_MODIFIER_ID, currentRow.getId()));
		editedRelationship.setGroup(currentRow.getRelationshipGroup());
		
		getImportContext().conceptVisited(currentRow.getSourceId());
	}

	private Relationship getOrCreateRelationship(final String conceptSctId, final String relationshipSctId) {

		Relationship result = getRelationship(relationshipSctId);
		
		if (null == result) {
			result = SnomedFactory.eINSTANCE.createRelationship();
			result.setId(relationshipSctId);
			result.setSource(getConceptSafe(conceptSctId, SnomedRf2Headers.FIELD_SOURCE_ID, relationshipSctId));
			getComponentLookup().addNewComponent(result, relationshipSctId);
		}

		return result;
	}

}