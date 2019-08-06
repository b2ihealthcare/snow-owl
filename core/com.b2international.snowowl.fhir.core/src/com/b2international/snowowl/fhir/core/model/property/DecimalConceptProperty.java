/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * Decimal concept property
 * Introduced in FHIR R4
 * @since 6.19
 */
public class DecimalConceptProperty extends ConceptProperty<Float> {

	DecimalConceptProperty(Code code, Float value) {
		super(code, value);
	}
	
	@Override
	public PropertyType getPropertyType() {
		return PropertyType.DECIMAL;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ConceptProperty.Builder<Builder, DecimalConceptProperty, Float> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected DecimalConceptProperty doBuild() {
			return new DecimalConceptProperty(code, value);
		}
	}

}
