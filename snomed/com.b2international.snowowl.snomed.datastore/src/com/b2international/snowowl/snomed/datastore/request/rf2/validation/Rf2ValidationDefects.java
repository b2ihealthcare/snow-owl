package com.b2international.snowowl.snomed.datastore.request.rf2.validation;

public enum Rf2ValidationDefects {
	
	INCORRECT_COLUMN_NUMBER("Incorrect column number in release file"),
	INVALID_ID("Component identifier is not of the expected type, malformed, or has an incorrect Verhoeff check digit."),
	MISSING_DEPENDANT_ID("Component refers to a non-existing concept"),
	MODULE_CONCEPT_NOT_EXIST("Module concept does not exist"),
	NOT_UNIQUE_DESCRIPTION_ID("Description identifier is not unique"),
	CONCEPT_DEFINITION_STATUS_NOT_EXIST("Concept refers to a non-existing concept in column 'definitionStatusId'"),
	DESCRIPTION_CONCEPT_NOT_EXIST("Description refers to a non-existing concept in column 'conceptId'"),
	DESCRIPTION_TYPE_NOT_EXIST("Description refers to a non-existing concept in column 'typeId'"),
	DESCRIPTION_CASE_SIGNIFICANCE_NOT_EXIST("Description refers to a non-existing concept in column 'caseSignificanceId'"),
	NOT_UNIQUE_RELATIONSHIP_ID("Relationship identifier is not unique"),
	RELATIONSHIP_SOURCE_DESTINATION_EQUALS("Relationship source and destination identifiers are equal"),
	RELATIONSHIP_REFERENCED_NONEXISTENT_CONCEPT("Relationship refers to a non-existing component"),
	RELATIONSHIP_REFERENCED_INACTIVE_CONCEPT("Relationship refers to a inactive component"),
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
	ASSOCIATION_REFSET_TARGET_COMPONENT_NOT_EXIST("Association reference set member refers to a non-existing concept in column 'targetComponentId'"),
	COMPLEX_MAP_REFERENCED_INVALID_CONCEPT("Complex map reference set member refers to a non-existing concept"),
	EXTENDED_MAP_REFERENCED_INVALID_CONCEPT("Extended map reference set member refers to a non-existing concept"),
	INVALID_EFFECTIVE_TIME_FORMAT("Effective time format is not valid. Acceptable effective time format is 'yyyyMMdd'."),
	EMPTY_REFSET_MEMBER_FIELD("Reference set member field is empty"),
	ENCOUNTER_UNKNOWN_RELEASE_FILE("Encountered unknown release file"), 
	UNEXPECTED_COMPONENT_CATEGORY("Unexpected component category found in column of the release file"),
	MISSING_DESCRIPTION_TERM("Description term is missing from release file");
	
	private final String label;
	
	private Rf2ValidationDefects(final String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
}
