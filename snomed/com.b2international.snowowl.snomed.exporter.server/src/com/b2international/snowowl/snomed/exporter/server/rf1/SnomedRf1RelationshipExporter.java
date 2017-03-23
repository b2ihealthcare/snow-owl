/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;

/**
 * Exporter for SNOMED CT relationships.
 */
public class SnomedRf1RelationshipExporter extends AbstractSnomedRf1CoreExporter<SnomedRelationshipIndexEntry> {

	/**
	 * Line in the Relationship RF1 file
	 */
	class Rf1Relationship {
		
		public String id;
		public String sourceId;
		public String typeId;
		public String destinationId;
		public String characteristicTypeId;
		public String refinability = Concepts.OPTIONAL_REFINABLE;
		public String group;
		
		@Override
		public String toString() {
			
			return new StringBuilder(valueOfOrEmptyString(id))
					.append(HT)
					.append(valueOfOrEmptyString(sourceId))
					.append(HT)
					.append(valueOfOrEmptyString(typeId))
					.append(HT)
					.append(valueOfOrEmptyString(destinationId))
					.append(HT)
					.append(mapper.getRelationshipType(valueOfOrEmptyString(characteristicTypeId)))
					.append(HT)
					.append(mapper.getRefinabilityType(valueOfOrEmptyString(refinability)))
					.append(HT)
					.append(valueOfOrEmptyString(group)) 
					.toString();
		}
	}
	
	Id2Rf1PropertyMapper mapper;
	
	public SnomedRf1RelationshipExporter(final SnomedExportContext exportContext, final RevisionSearcher revisionSearcher) {
		super(exportContext, SnomedRelationshipIndexEntry.class, revisionSearcher);
		mapper = exportContext.getId2Rf1PropertyMapper();
	}
	
	@Override
	public String convertToString(SnomedRelationshipIndexEntry doc) {
		Rf1Relationship relationship = new Rf1Relationship();
		
		relationship.id = doc.getId();
		relationship.sourceId = doc.getSourceId();
		relationship.typeId = doc.getTypeId();
		relationship.destinationId = doc.getDestinationId();
		relationship.characteristicTypeId = doc.getCharacteristicTypeId();
		relationship.group = String.valueOf(doc.getGroup());
		
		return relationship.toString();
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.RELATIONSHIP_HEADER;
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.RELATIONSHIP;
	}
}
