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
package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.codesystem.ConceptProperties;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * FHIR Common concept properties code system
 * @since 6.3
 */
public enum CommonConceptProperties implements ConceptProperties {
	
	//True if the concept is not considered active - e.g. not a valid concept any more. Property type is boolean, default value is false
	INACTIVE("Inactive", ConceptPropertyType.BOOLEAN),
	
	//The date at which a concept was deprecated. Concepts that are deprecated but not inactive can still be used, 
	//but their use is discouraged, and they should be expected to be made inactive in a future release. Property type is dateTime
	DEPRECATED("Deprecated", ConceptPropertyType.DATETIME),
	
	//The concept is not intended to be chosen by the user - only intended to be used as a selector for other concepts. 
	//Note, though, that the interpretation of this is highly contextual; all concepts are selectable in some context. Property type is boolean, default value is false
	NOT_SELECTABLE("Not Selectable", ConceptPropertyType.BOOLEAN),
	
	//The concept identified in this property is a parent of the concept on which it is a property. 
	//The property type will be 'code'. The meaning of 'parent' is defined by the hierarchyMeaning attribute
	PARENT("Parent", ConceptPropertyType.CODE),
	
	//The concept identified in this property is a child of the concept on which it is a property. 
	//The property type will be 'code'. The meaning of 'child' is defined by the hierarchyMeaning attribute
	CHILD("Child", ConceptPropertyType.CODE);
		
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/concept-properties";
	
	private String displayName;
	
	private ConceptPropertyType type;

	private CommonConceptProperties(String displayName, ConceptPropertyType type) {
		this.displayName = displayName;
		this.type = type;
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public Code getType() {
		return type.getCode();
	}
	
	@Override
	public ConceptPropertyType getConceptPropertyType() {
		return type;
	}
	
	@Override
	public Code getCode() {
		return new Code(getCodeValue());
	}
	
	@Override
	public String getCodeValue() {
		return StringUtils.camelCase(name(), "_");
	}
	
	@Override
	public Uri getUri() {
		return new Uri(CODE_SYSTEM_URI + "/" + getCodeValue());
	}

}