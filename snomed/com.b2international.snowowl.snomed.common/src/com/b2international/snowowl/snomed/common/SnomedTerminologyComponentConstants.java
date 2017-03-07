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
package com.b2international.snowowl.snomed.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import com.b2international.commons.VerhoeffCheck;
import com.google.common.collect.Sets;

public abstract class SnomedTerminologyComponentConstants {
	
	// suppress constructor
	private SnomedTerminologyComponentConstants() {}

	public static final Set<String> NS_URI_SET = Collections.unmodifiableSet(Sets.newHashSet(
			"http://b2international.com/snowowl/sct/1.0", 
			"http://b2international.com/snowowl/snomed/refset/1.0", 
			"http://b2international.com/snowowl/snomed/mrcm"));
	public static final String[] NS_URI = Arrays.copyOf(NS_URI_SET.toArray(), NS_URI_SET.size(), String[].class);
	
	
	public static final String TERMINOLOGY_ID = "com.b2international.snowowl.terminology.snomed";
	
	public static final String CONCEPT = "com.b2international.snowowl.terminology.snomed.concept";
	public static final short CONCEPT_NUMBER = 100;
	public static final String DESCRIPTION = "com.b2international.snowowl.terminology.snomed.description";
	public static final short DESCRIPTION_NUMBER = 101;
	public static final String RELATIONSHIP = "com.b2international.snowowl.terminology.snomed.relationship";
	public static final short RELATIONSHIP_NUMBER = 102;
	public static final String REFSET = "com.b2international.snowowl.terminology.snomed.refset";
	public static final short REFSET_NUMBER = 103;
	public static final String REFSET_MEMBER = "com.b2international.snowowl.terminology.snomed.refsetmember";
	public static final short REFSET_MEMBER_NUMBER = 104;
	public static final String DATA_TYPE_STRING = "com.b2international.snowowl.terminology.snomed.datatype.string";
	public static final short DATA_TYPE_STRING_NUMBER = 105;
	public static final String DATA_TYPE_INTEGER = "com.b2international.snowowl.terminology.snomed.datatype.integer";
	public static final short DATA_TYPE_INTEGER_NUMBER = 106;
	public static final String DATA_TYPE_DECIMAL= "com.b2international.snowowl.terminology.snomed.datatype.decimal";
	public static final short DATA_TYPE_DECIMAL_NUMBER = 107;
	public static final String DATA_TYPE_BOOLEAN = "com.b2international.snowowl.terminology.snomed.datatype.boolean";
	public static final short DATA_TYPE_BOOLEAN_NUMBER = 108;
	public static final String DATA_TYPE_DATE = "com.b2international.snowowl.terminology.snomed.datatype.date";
	public static final short DATA_TYPE_DATE_NUMBER = 109;
	
	public static final String SNOMED_SHORT_NAME = "SNOMEDCT";
	public static final String SNOMED_NAME = "SNOMED CT";
	
	public static final String SNOMED_INT_OID = "2.16.840.1.113883.6.96";
	public static final String SNOMED_INT_LANGUAGE = "ENG";
	public static final String SNOMED_INT_LINK = "http://www.snomed.org";
	public static final String SNOMED_INT_ICON_PATH = "icons/snomed.png";
	public static final String SNOMED_INT_CITATION = "SNOMED CT contributes to the improvement of patient care by underpinning the " +
			"development of Electronic Health Records that record clinical information in ways that enable meaning-based retrieval. " +
			"This provides effective access to information required for decision support and consistent reporting and analysis. " +
			"Patients benefit from the use of SNOMED CT because it improves the recording of EHR information and facilitates better communication, " +
			"leading to improvements in the quality of care.";
	
	public static final String SNOMED_B2I_SHORT_NAME = SNOMED_SHORT_NAME + "-B2I";
	public static final String SNOMED_B2I_NAME = SNOMED_NAME + ", B2i extension";
	public static final String SNOMED_B2I_OID = SNOMED_INT_OID + ".1000154";
	public static final String SNOMED_B2I_LINK = "https://b2i.sg";

	/**
	 * Fake terminology component type ID for predicates.
	 * <br>ID: {@value}. 
	 */
	public static final String PREDICATE_TYPE = "com.b2international.snowowl.terminology.snomed.predicate";
	public static final int PREDICATE_TYPE_ID = 999;
	
	private static final Pattern PATTERN = Pattern.compile("^\\d*$");

	/**
	 * Returns {@code true} if the given terminology component type is representing a SNOMED&nbsp;CT component.
	 * @param referencedComponentType the terminology component type. 
	 * @return {@code true} if SNOMED CT otherwise {@code false}.
	 */
	public static boolean isSnomed(final short terminologyComponentType) {
		
		switch (terminologyComponentType) {
			
			case CONCEPT_NUMBER: //$FALL-THROUGH$
			case DESCRIPTION_NUMBER: //$FALL-THROUGH$
			case RELATIONSHIP_NUMBER: //$FALL-THROUGH$
			case REFSET_NUMBER: //$FALL-THROUGH$
			case REFSET_MEMBER_NUMBER: //$FALL-THROUGH$
				
				return true;

			default:
				
				return false;
		}
		
	}
	
	public static short getTerminologyComponentIdValue(final String referencedComponentId) {
		final short s = getTerminologyComponentIdValueSafe(referencedComponentId);
		if (-1 == s) {
			throw new IllegalArgumentException("'" + referencedComponentId + "' referenced component type is unknown.");
		} else {
			return s;
		}
	}

	public static short getTerminologyComponentIdValueSafe(final String referencedComponentId) {

		if (!PATTERN.matcher(referencedComponentId).matches() || referencedComponentId.length() < 6 || referencedComponentId.length() > 18) {
			return -1;
		}

		if (!VerhoeffCheck.validateLastChecksumDigit(referencedComponentId)) {
			return -1;
		}

		switch (referencedComponentId.charAt(referencedComponentId.length() - 2)) {

			case '0':
				return CONCEPT_NUMBER;

			case '1':
				return DESCRIPTION_NUMBER;

			case '2':
				return RELATIONSHIP_NUMBER;

			default:
				return -1;
		}

	}

	public static String getTerminologyComponentId(final String referencedComponentId) {
		switch (getTerminologyComponentIdValue(referencedComponentId)) {
			case CONCEPT_NUMBER:
				return CONCEPT;
			case DESCRIPTION_NUMBER:
				return DESCRIPTION;
			case RELATIONSHIP_NUMBER:
				return RELATIONSHIP;
			default:
				throw new IllegalArgumentException("'" + referencedComponentId + "' referenced component type is unknown");
		}
	}

	/**
	 * Converts the specified SNOMED&nbsp;CT terminology component identifier value to the associated unique ID.  
	 * @param value the component identifier value.
	 * @return the unique terminology component ID.
	 */
	public static String getId(final short value) {
		switch (value) {
			case CONCEPT_NUMBER: return CONCEPT;
			case DESCRIPTION_NUMBER: return DESCRIPTION;
			case RELATIONSHIP_NUMBER: return RELATIONSHIP;
			case REFSET_NUMBER: return REFSET;
			case REFSET_MEMBER_NUMBER: return REFSET_MEMBER;
			case DATA_TYPE_BOOLEAN_NUMBER: return DATA_TYPE_BOOLEAN;
			case DATA_TYPE_DATE_NUMBER: return DATA_TYPE_DATE;
			case DATA_TYPE_DECIMAL_NUMBER: return DATA_TYPE_DECIMAL;
			case DATA_TYPE_INTEGER_NUMBER: return DATA_TYPE_INTEGER;
			case DATA_TYPE_STRING_NUMBER: return DATA_TYPE_STRING;
			default: throw new IllegalArgumentException("Unknown terminology component identifier value: " + value);
		}
	}
	
	/**
	 * Converts the specified SNOMED&nbsp;CT terminology component identifier to the associated value.  
	 * @param id the terminology component ID.
	 * @return the unique value of the specified ID.
	 */
	public static short getValue(final String id) {
		if (CONCEPT.equals(id)) {
			return CONCEPT_NUMBER;
		} else if (RELATIONSHIP.equals(id)) {
			return RELATIONSHIP_NUMBER;
		} else if (DESCRIPTION.equals(id)) {
			return DESCRIPTION_NUMBER;
		} else if (REFSET.equals(id)) {
			return REFSET_NUMBER;
		} else if (REFSET_MEMBER.equals(id)) {
			return REFSET_MEMBER_NUMBER;
		} else if (DATA_TYPE_BOOLEAN.equals(id)) {
			return DATA_TYPE_BOOLEAN_NUMBER;
		} else if (DATA_TYPE_DATE.equals(id)) {
			return DATA_TYPE_DATE_NUMBER;
		} else if (DATA_TYPE_DECIMAL.equals(id)) {
			return DATA_TYPE_DECIMAL_NUMBER;
		} else if (DATA_TYPE_INTEGER.equals(id)) {
			return DATA_TYPE_INTEGER_NUMBER;
		} else if (DATA_TYPE_STRING.equals(id)) {
			return DATA_TYPE_STRING_NUMBER;
		} else {
			throw new IllegalArgumentException("Unknown terminology component identifier: " + id);
		}
	}

	public static boolean isCoreComponentId(String componentId) {
		return isCoreComponentType(getTerminologyComponentIdValueSafe(componentId));
	}

	public static boolean isCoreComponentType(short componentType) {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == componentType || 
				SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER == componentType || 
				SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER == componentType;
	}
	
}