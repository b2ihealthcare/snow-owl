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
package com.b2international.snowowl.snomed.mrcm.mini;

import static com.b2international.snowowl.snomed.core.domain.refset.DataType.BOOLEAN;
import static com.b2international.snowowl.snomed.core.domain.refset.DataType.DECIMAL;
import static com.b2international.snowowl.snomed.core.domain.refset.DataType.STRING;
import static com.b2international.snowowl.snomed.mrcm.mini.SectionType.PROPERTY_SECTION;

import com.b2international.snowowl.snomed.core.domain.refset.DataType;

/**
 * Collects known data type labels associated with SNOMED CT concepts in the
 * Singapore Drug Dictionary extension.
 * 
 */
public enum DataTypeConstants {

	/*
	 * Boolean typed elements
	 */
	QUALIFIER_EFFECTS_DOSAGE("qualifierEffectsDosage", "Qualifier effects dosage", PROPERTY_SECTION, BOOLEAN),
	INCLUDE_ORIGININ_PT("includeOriginInPt", "Include origin in preferred term", PROPERTY_SECTION, BOOLEAN),
	NAME_INCLUDES_DOSE_FORM("nameIncludesDoseForm", "Dose form included in name", PROPERTY_SECTION, BOOLEAN),
	QUALIFIER_NEEDS_BRACKETS("qualifierNeedsBrackets", "Qualifier needs bracket", PROPERTY_SECTION, BOOLEAN),
	SHOW_QUALIFIER_BEFORE_ORIGIN("showQualifierBeforeOrigin", "Show qualifier before origin", PROPERTY_SECTION, BOOLEAN),
	
	IS_CLINICALLY_SIGNIFICANT("isClinicallySignificant", "Clinically significant", PROPERTY_SECTION, BOOLEAN),
	IS_ABSTRACT("isAbstract", "Abstract", PROPERTY_SECTION, BOOLEAN),
	INCLUDES_OTHER_DEFINING_INFORMATION("includesOtherDefiningInformation", "Other defining information", PROPERTY_SECTION, BOOLEAN),
	CREATE_CONTAINER_HIERARCHY("createContainerHierarchy", "Create container hierarchy", PROPERTY_SECTION, BOOLEAN),
	CAN_BE_TAGGED_WITH_VACCINE("canBeTaggedWithVaccine", "Can be tagged with vaccine", PROPERTY_SECTION, BOOLEAN),
	INCLUDES_INGREDIENT_QUALIFER("includesIngredientQualifer", "Includes ingredient qualifier", PROPERTY_SECTION, BOOLEAN),
	IS_A_VITAMIN("isVitamin", "Vitamin", PROPERTY_SECTION, BOOLEAN),
	REQUIRES_SPECIFIC_CONTAINER("requiresSpecificContainer", "Requires specific container", PROPERTY_SECTION, BOOLEAN),
	DISCRETE("isDiscrete", "Discrete", PROPERTY_SECTION, BOOLEAN),
	PROPRIETARY("isProprietary", "Proprietary", PROPERTY_SECTION, BOOLEAN),
	IS_THERAPEUTIC_GROUP("isTherapeuticGroup", "Is therapeutic group", PROPERTY_SECTION, BOOLEAN),
	MAY_USE_STRENGTH("mayUseStrength", "May use strength", PROPERTY_SECTION, BOOLEAN),
	
	//Ontology generation related dts
	IS_VACCINE("isVaccine", "Vaccine", PROPERTY_SECTION, BOOLEAN),
	ODI("odi", "ODI", PROPERTY_SECTION, STRING),
	USE_MPF_ODI("useMpfOdi", "Use MPF ODI", PROPERTY_SECTION, BOOLEAN),
	
	//MPQ
	PREFERRED_STRENGTH1_NUMERATOR_VALUE("preferredStrength1NumeratorValue", "Preferred strength numerator value", PROPERTY_SECTION, DECIMAL),
	HIDE_SINGLE_CONTAINER_TOTAL_QUANTITY("hideSingleContainerTotalQuantityInPt", "Hide single container total quantity in PT", PROPERTY_SECTION, BOOLEAN),
	SUPERPACK_TOTAL_QUANTITY_VALUE("superpackTotalQuantityValue", "Total quantity value", PROPERTY_SECTION, DECIMAL),
	
	/*
	 * String typed elements
	 */
	FN_QUALIFIER("fnQualifier", "Full name qualifier", PROPERTY_SECTION, STRING),
	PT_QUALIFIER("ptQualifier", "Preferred term qualifier", PROPERTY_SECTION, STRING),
	PT_WITHOUT_QUALIFIER("ptWithoutQualifier", "Preferred term without qualifier", PROPERTY_SECTION, STRING),
	FN_WITHOUT_QUALIFIER("fnWithoutQualifier", "Full name without qualifier", PROPERTY_SECTION, STRING),
	NAME_WITHOUT_BASE("nameWithoutBase", "Name without base", PROPERTY_SECTION, STRING),
	
	WATER_PRODUCT_TERM("waterProductTerm", "Water product term", PROPERTY_SECTION, STRING),
	CONVERSION_BASE_UOM("conversionBaseUOM", "Conversion base UOM", PROPERTY_SECTION, STRING),
	ADDITIONAL_COMMENTS("additionalComments", "Additional comments", PROPERTY_SECTION, STRING),
	
	/*
	 * Decimal typed elements
	 */
	CONVERSION_FACTOR("conversionFactor", "Conversion factor", PROPERTY_SECTION, DECIMAL);
	
	/**
	 * Returns with the data type constant enumeration identified by its ordinal.
	 * @param ordinal the ordinal associated with the data type constant.
	 * @return data type constant.
	 */
	public static final DataTypeConstants getByOrdinal(final int ordinal) {
		return DataTypeConstants.values()[ordinal];
	}

	private final String id;
	private final String name;
	
	private DataTypeConstants(final String id, final String name, final SectionType type, final DataType dataType) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}