/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;

/**
 * Custom serializer to serialize typed properties. The name of the reference
 * is obtained from the {@link ContextualSerializer} 
 * The serializer supports the @JsonUnwrapped annotation
 * 
 * @since 8.0.0
 * 
 * @see ContextualSerializer
 */
@SuppressWarnings("rawtypes")
public class TypedPropertySerializer extends JsonSerializer<TypedProperty> 
	implements ContextualSerializer {

	static class UnwrappingTypedPropertySerializer extends JsonSerializer<TypedProperty> {
	    
		private NameTransformer nameTransformer;
		private final String propertyName;

		public UnwrappingTypedPropertySerializer(NameTransformer nameTransformer, String propertyName) {
			this.nameTransformer = nameTransformer;
			this.propertyName = propertyName;
		}

		@Override
	    public void serialize(
	        final TypedProperty value,
	        final JsonGenerator gen,
	        final SerializerProvider serializers
	    ) throws IOException {
	        gen.writeStringField(nameTransformer.transform(propertyName) + value.getTypeName(), value.getValueString());
		}
	    
	    @Override
	    public boolean isUnwrappingSerializer() {
	    	return true;
	    }
	}
	
	private JsonSerializer<TypedProperty> delegate;
	
	private String propertyName;
	
	public TypedPropertySerializer() {
		this("value");
	}
	
    public TypedPropertySerializer(String propertyName) {
    	this.propertyName = propertyName;
    	delegate = new UnwrappingTypedPropertySerializer(NameTransformer.NOP, propertyName);
    }
	
	@Override
	public void serialize(TypedProperty property, JsonGenerator jGen, SerializerProvider sp)
			throws IOException, JsonProcessingException {

		jGen.writeStartObject();
		delegate.serialize(property, jGen, sp);
		jGen.writeEndObject();
	}
	
	@Override
	public JsonSerializer<TypedProperty> unwrappingSerializer(NameTransformer nameTransformer) {
		return new UnwrappingTypedPropertySerializer(nameTransformer, propertyName);
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider sp, BeanProperty property) throws JsonMappingException {
		return new TypedPropertySerializer(property.getName());
	}
}
