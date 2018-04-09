/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.codesystem;

import com.b2international.snowowl.fhir.core.codesystems.ConceptPropertyType;
import com.b2international.snowowl.fhir.core.codesystems.FhirCodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Property;
import com.b2international.snowowl.fhir.core.model.dt.Property.Builder;

/**
 * @since 6.4
 */
public interface ConceptProperties extends FhirCodeSystem {

	Code getType();
	
	ConceptPropertyType getConceptPropertyType();
	
	default Property propertyOf(Object value, String description) {
		Builder prop = Property.builder()
				.code(getCodeValue())
				.description(description);
		
		switch (getConceptPropertyType()) {
		case CODE:
			prop.valueCode((String) value);
			break;
		case BOOLEAN:
			prop.valueBoolean((Boolean) value);
			break;
		case STRING:
			prop.valueString((String) value);
			break;
		default: 
			throw new UnsupportedOperationException("Unsupported property type " + getConceptPropertyType());
		}
		
		return prop.build();
	}
	
}
