/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;
import java.util.regex.Pattern;

import com.b2international.commons.VerhoeffCheck;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public abstract class SnomedTerminologyComponentConstants {
	
	// suppress constructor
	private SnomedTerminologyComponentConstants() {}

	public static final Set<String> NS_URI_SET = ImmutableSet.of(
			"http://b2international.com/snowowl/sct/1.0", 
			"http://b2international.com/snowowl/snomed/refset/1.0", 
			"http://b2international.com/snowowl/snomed/mrcm");
	
	public static final String[] NS_URI = Iterables.toArray(NS_URI_SET, String.class);
	
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
	public static final String CONSTRAINT = "com.b2international.snowowl.terminology.snomed.constraint";
	public static final short CONSTRAINT_NUMBER = 105;
	
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

	private static final Pattern PATTERN = Pattern.compile("^\\d*$");
	
	public static short getTerminologyComponentIdValue(final String referencedComponentId) {
		final short s = getTerminologyComponentIdValueSafe(referencedComponentId);
		if (-1 == s) {
			throw new IllegalArgumentException("'" + referencedComponentId + "' referenced component type is unknown.");
		} else {
			return s;
		}
	}

	public static short getTerminologyComponentIdValueSafe(final String referencedComponentId) {
		if (Strings.isNullOrEmpty(referencedComponentId)) {
			return -1;
		}
		
		if (!PATTERN.matcher(referencedComponentId).matches() || referencedComponentId.length() < 6 || referencedComponentId.length() > 18) {
			return -1;
		}

		if (!VerhoeffCheck.validateLastChecksumDigit(referencedComponentId)) {
			return -1;
		}

		switch (referencedComponentId.charAt(referencedComponentId.length() - 2)) {
			case '0': return CONCEPT_NUMBER;
			case '1': return DESCRIPTION_NUMBER;
			case '2': return RELATIONSHIP_NUMBER;
			default: return -1;
		}
	}

	public static String getTerminologyComponentId(final String referencedComponentId) {
		switch (getTerminologyComponentIdValue(referencedComponentId)) {
			case CONCEPT_NUMBER: return CONCEPT;
			case DESCRIPTION_NUMBER: return DESCRIPTION;
			case RELATIONSHIP_NUMBER: return RELATIONSHIP;
			default: throw new IllegalArgumentException("'" + referencedComponentId + "' referenced component type is unknown");
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
			case CONSTRAINT_NUMBER: return CONSTRAINT;
			default: throw new IllegalArgumentException("Unknown terminology component identifier value: " + value);
		}
	}
	
	/**
	 * Converts the specified SNOMED&nbsp;CT terminology component identifier to the associated value.  
	 * @param id the terminology component ID.
	 * @return the unique value of the specified ID.
	 */
	public static short getValue(final String id) {
		switch (id) {
			case CONCEPT: return CONCEPT_NUMBER;
			case DESCRIPTION: return DESCRIPTION_NUMBER;
			case RELATIONSHIP: return RELATIONSHIP_NUMBER;
			case REFSET: return REFSET_NUMBER;
			case REFSET_MEMBER: return REFSET_MEMBER_NUMBER;
			case CONSTRAINT: return CONSTRAINT_NUMBER;
			default: throw new IllegalArgumentException("Unknown terminology component identifier: " + id);
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