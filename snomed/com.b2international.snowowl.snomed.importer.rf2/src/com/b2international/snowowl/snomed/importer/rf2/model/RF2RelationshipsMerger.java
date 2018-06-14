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
package com.b2international.snowowl.snomed.importer.rf2.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

/**
 * Merge {@link RF2Relationship}s from the inferred and stated relationship files
 * files.
 * 
 * 
 */
public class RF2RelationshipsMerger {

	private static String HEADER = "id\teffectiveTime\tactive\tmoduleId\tsourceId\tdestinationId\trelationshipGroup\ttypeId\tcharacteristicTypeId\tmodifierId\r\n";

	public Map<RF2Relationship, RF2Relationship> relationships;

	public RF2RelationshipsMerger() {
		relationships = Maps.newHashMap();
	}

	/**
	 * Create a new {@link RF2Relationship} from the inferred and stated
	 * relationships, or change an inferred relationship's value to the stated
	 * version
	 * 
	 * @param newRelationship the {@link String} type of the relationship whereof the {@link RF2Relationship} type will be created 
	 */
	public void addNewMember(String newRelationship) {
		
		RF2Relationship relationship = new RF2Relationship(newRelationship);
		relationships.put(relationship, relationship);
	}

	/**
	 * Write the {@link RF2Relationship}s out to the given file
	 * 
	 * @param tempFile where the relationships will be written
	 * @throws IOException
	 */
	public void writeRelationshipsToFile(File tempFile, URL inferredRelationshipsUrl) throws IOException {
		FileWriter fileWriter = new FileWriter(tempFile);
		fileWriter.write(HEADER);
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(inferredRelationshipsUrl.openStream(), Charsets.UTF_8));
		inputStream.readLine();
		String line;
		while ((line = inputStream.readLine()) != null) {
			RF2Relationship relationship = new RF2Relationship(line);
			if (relationships.containsKey(relationship)) {
				relationships.get(relationship).setCharacteristicTypeId(Concepts.STATED_RELATIONSHIP);
				relationship.setCharacteristicTypeId(Concepts.STATED_RELATIONSHIP);
			}
			
			fileWriter.write(createLine(relationship));
		}

		fileWriter.close();
	}

	private String createLine(RF2Relationship rf2Relationship) {
		String line = new StringBuilder(rf2Relationship.getID()).append("\t").append(rf2Relationship.getEffectiveTime()).append("\t").append(rf2Relationship.getActive())
				.append("\t").append(rf2Relationship.getModuleId()).append("\t").append(rf2Relationship.getSourceId()).append("\t").append(rf2Relationship.getDestinationId())
				.append("\t").append(rf2Relationship.getRelationshipGroup()).append("\t").append(rf2Relationship.getTypeId()).append("\t")
				.append(rf2Relationship.getCharacteristicTypeId()).append("\t").append(rf2Relationship.getModifierId()).append("\r\n").toString();
		return line;
	}

	/**
	 * Class for store the values of a RF2 Relationship
	 * 
	 *
	 */
	private class RF2Relationship {
		private String ID;
		private String effectiveTime;
		private String active;
		private String moduleId;
		private String sourceId;
		private String destinationId;
		private String relationshipGroup;
		private String typeId;
		private String characteristicTypeId;
		private String modifierId;

		private RF2Relationship(String relationship) {
			String[] values = relationship.split("\t");

			setID(values[0]);
			setEffectiveTime(values[1]);
			setActive(values[2]);
			setModuleId(values[3]);
			setSourceId(values[4]);
			setDestinationId(values[5]);
			setRelationshipGroup(values[6]);
			setTypeId(values[7]);
			setCharacteristicTypeId(values[8]);
			setModifierId(values[9]);
		}

		public void changeValues(String newRelationship) {
			String[] values = newRelationship.split("\t");
			setCharacteristicTypeId(values[8]);

			if (!values[1].equals(getEffectiveTime())) {
				setEffectiveTime(values[1]);
			}
		}

		public String getID() {
			return ID;
		}

		public void setID(String ID) {
			this.ID = ID;
		}

		public String getEffectiveTime() {
			return effectiveTime;
		}

		public void setEffectiveTime(String effectiveTime) {
			this.effectiveTime = effectiveTime;
		}

		public String getActive() {
			return active;
		}

		public void setActive(String active) {
			this.active = active;
		}

		public String getModuleId() {
			return moduleId;
		}

		public void setModuleId(String moduleId) {
			this.moduleId = moduleId;
		}

		public String getSourceId() {
			return sourceId;
		}

		public void setSourceId(String sourceId) {
			this.sourceId = sourceId;
		}

		public String getDestinationId() {
			return destinationId;
		}

		public void setDestinationId(String destinationId) {
			this.destinationId = destinationId;
		}

		public String getRelationshipGroup() {
			return relationshipGroup;
		}

		public void setRelationshipGroup(String relationshipGroup) {
			this.relationshipGroup = relationshipGroup;
		}

		public String getTypeId() {
			return typeId;
		}

		public void setTypeId(String typeId) {
			this.typeId = typeId;
		}

		public String getCharacteristicTypeId() {
			return characteristicTypeId;
		}

		public void setCharacteristicTypeId(String characteristicTypeId) {
			this.characteristicTypeId = characteristicTypeId;
		}

		public String getModifierId() {
			return modifierId;
		}

		public void setModifierId(String modifierId) {
			this.modifierId = modifierId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((destinationId == null) ? 0 : destinationId.hashCode());
			result = prime * result
					+ ((modifierId == null) ? 0 : modifierId.hashCode());
			result = prime
					* result
					+ ((relationshipGroup == null) ? 0 : relationshipGroup
							.hashCode());
			result = prime * result
					+ ((sourceId == null) ? 0 : sourceId.hashCode());
			result = prime * result
					+ ((typeId == null) ? 0 : typeId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RF2Relationship other = (RF2Relationship) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (destinationId == null) {
				if (other.destinationId != null)
					return false;
			} else if (!destinationId.equals(other.destinationId))
				return false;
			if (modifierId == null) {
				if (other.modifierId != null)
					return false;
			} else if (!modifierId.equals(other.modifierId))
				return false;
			if (relationshipGroup == null) {
				if (other.relationshipGroup != null)
					return false;
			} else if (!relationshipGroup.equals(other.relationshipGroup))
				return false;
			if (sourceId == null) {
				if (other.sourceId != null)
					return false;
			} else if (!sourceId.equals(other.sourceId))
				return false;
			if (typeId == null) {
				if (other.typeId != null)
					return false;
			} else if (!typeId.equals(other.typeId))
				return false;
			return true;
		}

		private RF2RelationshipsMerger getOuterType() {
			return RF2RelationshipsMerger.this;
		}
		
		
		
	}

}