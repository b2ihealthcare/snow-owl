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
package com.b2international.snowowl.snomed.fhir.codesystems;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.codesystems.ConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.ConceptPropertyType;
import com.b2international.snowowl.fhir.core.codesystems.FhirCodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * FHIR additional SNOMED CT properties
 * 
 * In addition, any SNOMED CT relationships where the relationship type 
 * is subsumed by Attribute (246061005) automatically become properties.
 * 
 * TODO: move it to the SNOMED CT specific bundle.
 * 
 * @see ConceptProperties
 * @since 6.3
 */
public enum SnomedConceptProperties implements FhirCodeSystem {
	
	//Whether the code is active or not (defaults to false). 
	//This is derived from the active column in the Concept file of the RF2 Distribution (by inverting the value)
	INACTIVE(ConceptPropertyType.BOOLEAN.getCodeValue()),
	
	//True if the description logic definition of the concept includes sufficient conditions
	//(i.e., if the concept is not primitive - found in the value of definitionStatusId in the concept file).
	SUFFICIENTLY_DEFINED(ConceptPropertyType.BOOLEAN.getCodeValue()),
	
	//The SNOMED CT concept id of the module that the concept belongs to.
	MODULE_ID(ConceptPropertyType.CODE.getCodeValue()),
	
	//Generated Normal form expression for the provided code or expression, with terms
	NORMAL_FORM(ConceptPropertyType.STRING.getCodeValue()),
	
	//Generated Normal form expression for the provided code or expression, conceptIds only
	NORMAL_FORM_TERSE(ConceptPropertyType.STRING.getCodeValue());
	
	private Code type;
	
	public final static String CODE_SYSTEM_URI = "http://snomed.info"; //$NON-NLS-N$
	
	private SnomedConceptProperties(String type) {
		this.type = new Code(type);
	}

	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	public Code getType() {
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

	//TODO: what is the URI? 
    //@see hl7.org/fhir/codesystem-snomedct.html with all the ?
	//for now we will just concat the URI and the value
	@Override
	public Uri getUri() {
		return new Uri(CODE_SYSTEM_URI+"/field/Concept." + getCodeValue());
	}

}
