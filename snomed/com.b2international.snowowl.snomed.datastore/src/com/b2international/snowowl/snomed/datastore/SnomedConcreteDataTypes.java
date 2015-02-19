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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_BOOLEAN_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DATETIME_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_FLOAT_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_INTEGER_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_STRENGTH;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_STRING_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_SUBPACK_QUANTITY;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_UNIT_OF_USE_QUANTITY;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_UNIT_OF_USE_SIZE;
import static com.b2international.snowowl.snomed.snomedrefset.DataType.BOOLEAN;
import static com.b2international.snowowl.snomed.snomedrefset.DataType.DATE;
import static com.b2international.snowowl.snomed.snomedrefset.DataType.DECIMAL;
import static com.b2international.snowowl.snomed.snomedrefset.DataType.INTEGER;
import static com.b2international.snowowl.snomed.snomedrefset.DataType.STRING;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Preconditions;

/**
 * This enumeration contains all available SNOMED&nbsp;CT measurement reference sets
 * released by NEHTA with the AMT extension.
 */
public enum SnomedConcreteDataTypes {

	//for NEHTA AMT SNOMED CT extension
	STRENGTH("strength", "Strength", REFSET_STRENGTH, DECIMAL, ""),
	UNIT_OF_USE_QUANTITY("unitOfUseQuantity", "Unit of use quantity", REFSET_UNIT_OF_USE_QUANTITY, DECIMAL, ""),
	UNIT_OF_USE_SIZE("unitOfUseSize", "Unit of use size", REFSET_UNIT_OF_USE_SIZE, DECIMAL, ""),
	SUBPACK_QUANTITY("subpackQuantity", "Subpack quantity", REFSET_SUBPACK_QUANTITY, INTEGER, ""),
	
	//for SNOMED CT SG extension
	BOOLEAN_TYPE("", "", REFSET_BOOLEAN_TYPE, BOOLEAN, ""),
	DATETIME_TYPE("", "", REFSET_DATETIME_TYPE, DATE, ""),
	INTEGER_TYPE("", "", REFSET_INTEGER_TYPE, INTEGER, ""),
	FLOAT_TYPE("", "", REFSET_FLOAT_TYPE, DECIMAL, ""),
	STRING_TYPE("", "", REFSET_STRING_TYPE, STRING, "");
	
	private final String owlLabel;
	private final String refSetId;
	private final DataType dataType;
	private final String parentConceptId;
	private final String name;
	
	private SnomedConcreteDataTypes(final String owlLabel, final String name, final String refSetId, final DataType dataType, final String parentConceptId) {
		this.owlLabel = owlLabel;
		this.name = name;
		this.refSetId = refSetId;
		this.dataType = dataType;
		this.parentConceptId = parentConceptId; 
	}
	
	/**
	 * The label of the measurement data type. Used for OWL2 language generation.
	 * @return the label of the data type.
	 */
	public String getOwlLabel() {
		return owlLabel;
	}
	
	/**
	 * The identifier concept ID of the associated SNOMED&nbsp;CT measurement reference set.
	 * @return the concept identifier.
	 */
	public String getRefSetId() {
		return refSetId;
	}
	
	/**
	 * The associated data type. Either {@link DataType#FLOAT} or {@link DataType#INTEGER}.
	 * @return the data type.
	 */
	public DataType getDataType() {
		return dataType;
	}
	
	/**
	 * Returns with a human readable name of the current SNOMED&nbsp;CT concrete domain type.
	 * @return the human readable name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns with the parent concept ID of the reference set identifier concept.
	 * @return the parent concept ID of the identifier concept.
	 */
	public String getParentConceptId() {
		return parentConceptId;
	}
	
	/**
	 * Returns {@code true} if creating is a concrete data type associated with a SNOMED&nbsp;CT relationship is supported. Otherwise returns {@code false}.
	 * This method check if the measurement type reference set concept identifiers are exists. If not this method returns with {@code false}.
	 * @return {@code true} if creating is a concrete data type associated with a SNOMED&nbsp;CT relationship is supported
	 */
	public static boolean isRelationshipConcreteDataTypeSupported() {
		SnomedClientTerminologyBrowser browser = getTerminologyBrowser();
		if (null == browser.getConcept(STRENGTH.getRefSetId()))
			return false;
		if (null == browser.getConcept(UNIT_OF_USE_QUANTITY.getRefSetId()))
			return false;
		if (null == browser.getConcept(UNIT_OF_USE_SIZE.getRefSetId()))
			return false;
		if (null == browser.getConcept(SUBPACK_QUANTITY.getRefSetId()))
			return false;
		return true;
	}

	private static SnomedClientTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
	}
	
	/**
	 * Method for getting the data type of the concrete domain element specified with the unique reference set identifier.
	 * @param refSetId the unique identifier of the reference set.
	 * @return the data type of the concrete data type reference set members.
	 */
	public static DataType getDataTypeByRefSetId(final String refSetId) {
		for (final SnomedConcreteDataTypes type : values()) {
			if (Preconditions.checkNotNull(refSetId, "The ID argument of a SNOMED CT reference set identifier concept cannot be null.").equals(type.getRefSetId())) {
				return type.getDataType();
			}
		}
		throw new NullPointerException("Error while getting datatype for reference set ID: " + refSetId);
	}
	
	/**
	 * Method for getting the parent concept of the reference set identifier concept.
	 * @param refSetId the identifier concept ID of the SNOMED&nbsp;CT reference set. 
	 * @return the identifier of the parent concept.
	 */
	public static String getParentConceptId(final String refSetId) {
		for (final SnomedConcreteDataTypes type : values()) {
			if (Preconditions.checkNotNull(refSetId, "The ID argument of a SNOMED CT reference set identifier concept cannot be null.").equals(type.getRefSetId())) {
				return type.getParentConceptId();
			}
		}
		throw new NullPointerException("Error while getting datatype for reference set ID: " + refSetId);
	}
	
	/**
	 * Method for getting the OWL 2 label of the SNOMED&nbsp;CT concrete data type element identified by the ID of the reference set identifier concept.
	 * @param refSetId the identifier concept ID of the SNOMED&nbsp;CT reference set. 
	 * @return the name of the concrete data type.
	 */
	public static String getOwlLabel(final String refSetId) {
		for (final SnomedConcreteDataTypes type : values()) {
			if (Preconditions.checkNotNull(refSetId, "The ID argument of a SNOMED CT reference set identifier concept cannot be null.").equals(type.getRefSetId())) {
				return type.getOwlLabel();
			}
		}
		throw new NullPointerException("Error while getting datatype for reference set ID: " + refSetId);
	}
	
	/**
	 * Method for getting the human readable name of the SNOMED&nbsp;CT concrete data type element identified by the ID of the reference set identifier concept.
	 * @param refSetId the identifier concept ID of the SNOMED&nbsp;CT reference set. 
	 * @return the name of the concrete data type.
	 */
	public static String getName(final String refSetId) {
		for (final SnomedConcreteDataTypes type : values()) {
			if (Preconditions.checkNotNull(refSetId, "The ID argument of a SNOMED CT reference set identifier concept cannot be null.").equals(type.getRefSetId())) {
				return type.getName();
			}
		}
		throw new NullPointerException("Error while getting datatype for reference set ID: " + refSetId);
	}
	
}