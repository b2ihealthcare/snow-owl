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
package com.b2international.snowowl.fhir.core.model.dt;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.b2international.commons.reflect.Reflections;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @since 6.4
 */
@ApiModel("Parameters")
public final class Parameters {

	private final List<Parameter> parameters;

	public Parameters(List<Parameter> parameters) {
		this.parameters = parameters == null ? Collections.emptyList() : parameters;
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	@JsonSerialize(using = Parameters.Json.Ser.class)
	public static final class Json {

		private final Parameters parameters;

		public Json(Parameters parameters) {
			this.parameters = parameters;
		}
		
		public Parameters parameters() {
			return parameters;
		}
		
		static final class Ser extends StdSerializer<Parameters.Json> {

			private Ser() {
				super(Parameters.Json.class);
			}

			@Override
			public void serialize(Parameters.Json value, JsonGenerator gen, SerializerProvider provider) throws IOException {
				gen.writeStartObject();
				writeValue(gen, value.parameters);
				gen.writeEndObject();
			}

			private void writeValue(JsonGenerator gen, Parameters value) throws IOException {
				final Multimap<String, Parameter> valuesByProperty = Multimaps.index(value.parameters, Parameter::getName);
				for (String fieldName : valuesByProperty.keySet()) {
					Collection<Parameter> parameters = valuesByProperty.get(fieldName);
					final int numberOfParameters = parameters.size();
					final boolean isCollection = numberOfParameters > 1;
					
					gen.writeFieldName(fieldName);
					
					if (isCollection) {
						gen.writeStartArray();
					}
						
					for (Parameter param : parameters) {
						if (param.getValue() instanceof Parameters) {
							gen.writeStartObject();
							writeValue(gen, (Parameters) param.getValue());
							gen.writeEndObject();
						} else {
							gen.writeObject(param.getValue());
						}
					}
				
					if (isCollection) {
						gen.writeEndArray();
					}
					
				}
			}
			
		}
		
	}
	
	@ApiModel("Parameters")
	@JsonSerialize(using = Parameters.Fhir.Ser.class)
	@JsonDeserialize(using = Parameters.Fhir.Deser.class)
	public static final class Fhir {

		private final Parameters parameters;

		public Fhir(Parameters parameters) {
			this.parameters = parameters;
		}
		
		@ApiModelProperty("parameter")
		public List<Parameter> getParameters() {
			return parameters.getParameters();
		} 

		static final class Ser extends StdSerializer<Parameters.Fhir> {

			private Ser() {
				super(Parameters.Fhir.class);
			}

			@Override
			public void serialize(Parameters.Fhir value, JsonGenerator gen, SerializerProvider provider) throws IOException {
				gen.writeStartObject();
				gen.writeStringField("resourceType", "Parameters");
				gen.writeFieldName("parameter");
				writeValue(gen, value.parameters);
				gen.writeEndObject();
			}

			private void writeValue(JsonGenerator gen, Parameters value) throws IOException {
				gen.writeStartArray();
				for (Parameter param : value.parameters) {
					gen.writeStartObject();
					gen.writeStringField("name", param.getName());
					gen.writeFieldName(param.getType().getSerializedName());
					if (param.getValue() instanceof Parameters) {
						writeValue(gen, (Parameters) param.getValue());
					} else {
						gen.writeObject(param.getValue());
					}
					gen.writeEndObject();
				}
				gen.writeEndArray();
			}
			
		}
		
		static final class Deser extends StdDeserializer<Parameters.Fhir> {
			
			private Deser() {
				super(Parameters.Fhir.class);
			}

			@Override
			public Parameters.Fhir deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				ObjectNode node = parser.getCodec().readTree(parser);
				
				List<Parameter> deserializedParameters = newArrayList();
				Iterator<JsonNode> parameters = node.get("parameter").iterator();
				while (parameters.hasNext()) {
					deserializedParameters.add(parser.getCodec().treeToValue(parameters.next(), Parameter.class));
				}
				
		        return new Parameters.Fhir(new Parameters(deserializedParameters));
			}
			
		}

		public Parameters.Json toJson() {
			return new Parameters.Json(parameters);
		}
		
	}
	
	/**
	 * Creates a {@link Parameters} instance from the given object. The resulting {@link Parameters} instance then can be serialized to {@link Json JSON} or {@link Fhir FHIR+JSON} format using Jackson. 
	 * The object parameter's type must be annotated with {@link JsonPropertyOrder}. This annotation will contain all fields that need to be translated to {@link Parameter} in the specified order.
	 * 
	 * @param object
	 * @return
	 */
	public static Parameters from(Object object) {
		final Class<?> type = object.getClass();
		checkArgument(type.getAnnotation(JsonPropertyOrder.class) != null, "%s must be annotated with @JsonPropertyOrder to specify FHIR properties and their order", type.getName());
		String[] fhirFields = type.getAnnotation(JsonPropertyOrder.class).value();
		final List<Parameter> parameters = newArrayListWithExpectedSize(fhirFields.length);
		
		for (String field : fhirFields) {
			try {
				Field javaField = Reflections.getField(type, field);
				Object val = javaField.get(object);
				if (val != null) {
					FhirDataType fhirType = getFhirType(object, javaField);
					if (fhirType != null) {
						if (val instanceof Iterable) {
							for (Object valItem : (Iterable<?>) val) {
								parameters.add(new Parameter(field, fhirType, FhirDataType.PART == fhirType ? from(valItem) : valItem));
							}
						} else {
							parameters.add(new Parameter(field, fhirType, FhirDataType.PART == fhirType ? from(val) : val));
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return new Parameters(parameters);
	}

	private static FhirDataType getFhirType(Object object, Field javaField) {
		// if the enclosing object is a FhirProperty instance and the field is the value field, use the type field as source of the FHIR type
		if (object instanceof FhirProperty && "value".equals(javaField.getName())) {
			return ((FhirProperty) object).getType();
		}
		
		Class<?> javaFieldType = Reflections.getType(javaField);
		if (javaField.isAnnotationPresent(FhirType.class)) {
			return javaField.getAnnotation(FhirType.class).value();
		}
		
		// as a last resort, try to infer the FHIR Data Type from the field type directly using its simple name
		return FhirDataType.getBySerializedName("value" + javaFieldType.getSimpleName());
	}
	
}
