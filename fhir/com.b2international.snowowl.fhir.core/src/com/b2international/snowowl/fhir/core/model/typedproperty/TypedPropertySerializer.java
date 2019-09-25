/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.typedproperty;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * A FHIR typed property is serialized into:
 * <pre>
 * {
 *    "name": "propertyName",
 *    "value[x]": value
 *  }
 * </pre>
 * 
 * @see <a href=" https://www.hl7.org/fhir/formats.html">Choice</a> for further information about how to use [x].
 * @since 7.1
 */
public class TypedPropertySerializer extends JsonSerializer<TypedProperty<?>> {

	@Override
	public void serialize(final TypedProperty<?> typedProperty, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
		gen.writeObjectField("value" + typedProperty.getTypeName(), typedProperty.getValue());
	}

	/*
	 * Enables this custom serializer to be used with the @JsonUnwrapped annotation
	 */
	@Override
    public boolean isUnwrappingSerializer() {
        return true;
    }
	
}
