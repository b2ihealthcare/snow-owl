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

/**
 * Represents an attribute for the SNOMED CT complex map type reference set member.
 * <br><br><b>Note:</b> used for e.g.: <b>MAP ADVICE</b> and <b>MAP RULE</b>
 */
public interface IComplexMapAttribute {

	public static final String SNOMED_CT_ID = "${SNOMED_CT_ID}";
	public static final String ICD_10_ID = "${ICD-10_ID}";
	public static final String SNOMED_CT_LABEL = "${SNOMED_CT_LABEL}";
	public static final String SNOMED_CT_FSN = "${SNOMED_CT_FSN}";
	public static final String UNSPECIFIED_ID = "${SOURCE_COMPONENT_ID}";
	
	/**
	 * Sets an attribute for a specified value.
	 * @param key the unique identifier of the attribute.
	 * @param value the value associated with the attribute.
	 */
	void setAttribute(final Object key, final Object value);
	
}