/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.refset;

/**
 * Enumeration for SNOMED&nbsp;CT reference set types. 
 */
public enum SnomedRefSetType {
	
	
	/**
	 * Simple type.
	 */
	SIMPLE,

	/**
	 * Simple map type.
	 * @deprecated
	 */
	SIMPLE_MAP,
	
	/**
	 * Language type.
	 */
	LANGUAGE,

	/**
	 * Attribute value type.
	 */
	ATTRIBUTE_VALUE,
	
	/**
	 * Query specification type.
	 */
	QUERY,

	/**
	 * Complex map type.
	 */
	COMPLEX_MAP,
	
	/**
	 * Description type.
	 */
	DESCRIPTION_TYPE,

	/**
	 * Concrete data type.
	 */
	CONCRETE_DATA_TYPE,

	/**
	 * Association value type.
	 */
	ASSOCIATION, 
	
	/**
	 * Module dependency value type.
	 */
	MODULE_DEPENDENCY,
	
	/**
	 * Extended map type.
	 */
	EXTENDED_MAP, 
	
	/**
	 * Simple map type with map target description included.
	 */
	SIMPLE_MAP_WITH_DESCRIPTION,
	
	/**
	 * The '<em><b>OWL AXIOM</b></em>' literal object.
	 */
	OWL_AXIOM,
	
	/**
	 * The '<em><b>OWL ONTOLOGY</b></em>' literal object.
	 */
	OWL_ONTOLOGY, 
	
	/**
	 * The '<em><b>MRCM DOMAIN</b></em>' literal object.
	 */
	MRCM_DOMAIN,
	
	/**
	 * The '<em><b>MRCM ATTRIBUTE DOMAIN</b></em>' literal object.
	 */
	MRCM_ATTRIBUTE_DOMAIN,
	
	/**
	 * The '<em><b>MRCM ATTRIBUTE RANGE</b></em>' literal object.
	 */
	MRCM_ATTRIBUTE_RANGE,
	
	/**
	 * The '<em><b>MRCM MODULE SCOPE</b></em>' literal object.
	 */
	MRCM_MODULE_SCOPE,

	/**
	 * The '<em><b>ANNOTATION</b></em>' literal object.
	 */
	ANNOTATION,
	
	/**
	 * Complex map with map block type.
	 */
	COMPLEX_BLOCK_MAP,
	
	/**
	 * Simple map to SNOMEDCT type.
	 */
	SIMPLE_MAP_TO_SNOMEDCT,
	
	/**
	 * Simple map from SNOMEDCT type. 
	 */
	SIMPLE_MAP_FROM_SNOMEDCT,
;

}
