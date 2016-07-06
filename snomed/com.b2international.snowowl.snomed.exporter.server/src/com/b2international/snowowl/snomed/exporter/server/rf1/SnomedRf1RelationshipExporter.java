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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;

import java.io.IOException;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;

/**
 * RF1 exporter for SNOMED&nbsp;CT relationships.
 */
public class SnomedRf1RelationshipExporter extends AbstractSnomedRf1Exporter<SnomedRelationshipIndexEntry> {
	
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
	
	/**
	 * Constructor
	 * @param configuration export configuration
	 * @param mapper RF2->RF1 mapper
	 */
	public SnomedRf1RelationshipExporter(final SnomedExportContext configuration, final Id2Rf1PropertyMapper mapper, final RevisionSearcher revisionSearcher) {
		super(SnomedRelationshipIndexEntry.class, configuration, mapper, revisionSearcher);
	}
	
	/**
	 * @param snomedConceptDocument
	 * @return
	 * @throws IOException 
	 */
	@Override
	protected String convertToRF1(SnomedRelationshipIndexEntry revisionDocument) throws IOException {
		
		Rf1Relationship relationship = new Rf1Relationship();
		
		relationship.id = revisionDocument.getId();
		relationship.sourceId = revisionDocument.getSourceId();
		relationship.typeId = revisionDocument.getTypeId();
		relationship.destinationId = revisionDocument.getDestinationId();
		relationship.characteristicTypeId = revisionDocument.getCharacteristicTypeId();
		relationship.group = String.valueOf(revisionDocument.getGroup());
		
		return relationship.toString();
	}
	
	@Override
	public ComponentExportType getType() {
		return ComponentExportType.RELATIONSHIP;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedReleaseFileHeaders.RF1_RELATIONSHIP_HEADER;
	}

}
