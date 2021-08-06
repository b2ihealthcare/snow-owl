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
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Code concept property
 * @since 6.3
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = CodeConceptProperty.Builder.class)
public class CodeConceptProperty extends ConceptProperty<Code> {

	CodeConceptProperty(Code code, Code value) {
		super(code, value);
	}
	
	@Override
	public PropertyType getPropertyType() {
		return PropertyType.CODE;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ConceptProperty.Builder<Builder, CodeConceptProperty, Code> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		/*
		 * For deserialization support.
		 */
		protected Builder valueCode(final Code value) {
			this.value = value;
			return this;
		}

		@Override
		protected CodeConceptProperty doBuild() {
			return new CodeConceptProperty(code, value);
		}
	}
}
