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
package com.b2international.snowowl.snomed.importer.net4j;

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a defect type of the release files.
 * 
 */
public class SnomedValidationDefect implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final DefectType defectType;
	private final Collection<String> defects;
	
	public SnomedValidationDefect(final DefectType defectType, final Collection<String> defects) {
		this.defectType = defectType;
		this.defects = defects;
	}
	
	public DefectType getDefectType() {
		return defectType;
	}
	
	public Collection<String> getDefects() {
		return defects;
	}
	
	public enum DefectType {
		
		HEADER_DIFFERENCES("Header format is different from the standard format defined by IHTSDO"),
		INCORRECT_COLUMN_NUMBER("Incorrect column number in release file"),
		MODULE_CONCEPT_NOT_EXIST("Module concept does not exist"),
		NOT_UNIQUE_DESCRIPTION_ID("Description identifier is not unique"),
		NOT_UNIQUE_FULLY_SPECIFIED_NAME("Fully specified name is not unique"),
		CONCEPT_DEFINITION_STATUS_NOT_EXIST("Concepts refers to a non-existing concept in column 'definitionStatusId'"),
		DESCRIPTION_CONCEPT_NOT_EXIST("Description refers to a non-existing concept in column 'conceptId'"),
		DESCRIPTION_TYPE_NOT_EXIST("Description refers to a non-existing concept in column 'typeId'"),
		DESCRIPTION_CASE_SIGNIFICANCE_NOT_EXIST("Description refers to a non-existing concept in column 'caseSignificanceId'"),
		NOT_UNIQUE_RELATIONSHIP_ID("Relationship identifier is not unique"),
		RELATIONSHIP_SOURCE_DESTINATION_EQUALS("Relationship source and destination identifiers are equal"),
		RELATIONSHIP_SOURCE_NOT_EXIST("Relationship refers to a non-existing concept in column 'sourceId'"),
		RELATIONSHIP_DESTINATION_NOT_EXIST("Relationship refers to a non-existing concept in column 'destinationId'"),
		RELATIONSHIP_TYPE_NOT_EXIST("Relationship refers to a non-existing concept in column 'typeId'"),
		RELATIONSHIP_CHARACTERISTIC_TYPE_NOT_EXIST("Relationship refers to a non-existing concept in column 'characteristicTypeId'"),
		RELATIONSHIP_MODIFIER_NOT_EXIST("Relationship refers to a non-existing concept in column 'modifierId'"),
		INCORRECT_REFSET_MEMBER_ID("Reference set member identifier is not a valid universally unique identifier"),
		NOT_UNIQUE_REFSET_MEMBER_ID("Reference set member identifier is not unique"),
		REFSET_MEMBER_COMPONENT_NOT_EXIST("Reference set member refers to a non-existing component in column 'referencedComponentId'"),
		ATTRIBUTE_REFSET_VALUE_CONCEPT_NOT_EXIST("Attribute value reference set member refers to a non-existing concept in column 'valueId'"),
		CONCRETE_DOMAIN_UNIT_CONCEPT_NOT_EXIST("Concrete domain reference set member refers to a non-existing concept in column 'unitId'"),
		CONCRETE_DOMAIN_OPERATOR_CONCEPT_NOT_EXIST("Concrete domain reference set member refers to a non-existing concept in column 'operatorId'"),
		CONCRETE_DOMAIN_VALUE_IS_EMPTY("Concrete domain reference set member value is empty in column 'value'"),
		SIMPLE_MAP_TARGET_IS_EMPTY("Simple map type reference set member target is empty in column 'mapTarget'"),
		DESCRIPTION_TYPE_DESCRIPTION_FORMAT_NOT_EXIST("Description type reference set member refers to a non-existing concept in column 'descriptionFormat'"),
		DESCRIPTION_TYPE_DESCRIPTION_LENGTH_IS_EMPTY("Description type reference set member value is empty in column 'descriptionLength'"),
		ASSOCIATION_REFSET_TARGET_COMPONENT_NOT_EXIST("Association reference set member refers to a non-existing concept in column 'targetComponent'"),
		UNKOWN_REFSET_TYPE("Unknown reference set type"), 
		INVALID_EFFECTIVE_TIME_FORMAT("Effective time format is not valid. Acceptable effective time format is 'yyyyMMdd'."),
		INCONSISTENT_TAXONOMY("The concepts below are referenced in active relationships. The newly imported version inactivates " + 
				"these concepts but leaves the other end of the relationship active, which is invalid."),
		IO_PROBLEM("Encountered an I/O error while running validation.");
		
		private String label;
		
		private DefectType(final String label) {
			this.label = label;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}

}