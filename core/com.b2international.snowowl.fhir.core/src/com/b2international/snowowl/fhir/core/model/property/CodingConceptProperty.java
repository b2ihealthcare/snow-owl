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
package com.b2international.snowowl.fhir.core.model.property;

import com.b2international.snowowl.fhir.core.codesystems.PropertyType;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.property.ConceptProperty.Builder;

/**
 * Coding concept property
 * @since 6.3
 */
public class CodingConceptProperty extends ConceptProperty<Coding> {

	CodingConceptProperty(Code code, Coding value) {
		super(code, value);
	}
	
	@Override
	public PropertyType getPropertyType() {
		return PropertyType.CODING;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ConceptProperty.Builder<Builder, CodingConceptProperty, Coding> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected CodingConceptProperty doBuild() {
			return new CodingConceptProperty(code, value);
		}
	}
}
