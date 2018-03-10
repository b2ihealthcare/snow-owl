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
package com.b2international.snowowl.fhir.api.model.dt;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ToJsonStringSerializer extends StdSerializer<JsonStringProvider> {

	private static final long serialVersionUID = 1L;

	public final static ToJsonStringSerializer instance = new ToJsonStringSerializer();

	public ToJsonStringSerializer() {
		super(JsonStringProvider.class);
	}

	public ToJsonStringSerializer(Class<?> handledType) {
		super(handledType, false);
	}

	@Override
	public boolean isEmpty(SerializerProvider prov, JsonStringProvider value) {
		return value.toJsonString().isEmpty();
	}

	@Override
	public void serialize(JsonStringProvider value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(value.toJsonString());
	}

}