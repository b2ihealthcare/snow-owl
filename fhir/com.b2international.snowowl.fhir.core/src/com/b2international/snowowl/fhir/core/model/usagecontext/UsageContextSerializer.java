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
package com.b2international.snowowl.fhir.core.model.usagecontext;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;

/**
 * Custom serializer for usage context implementations.
 * @since 6.6
 */
public class UsageContextSerializer extends JsonSerializer<UsageContext<?>> {

	@Override
	public void serialize(UsageContext<?> usageContext, JsonGenerator jGen, SerializerProvider sp) throws IOException, JsonProcessingException {
		
		jGen.writeStartObject();
		jGen.writeObjectField("value" + usageContext.getType(), usageContext.getValue());
		serializeFields(usageContext, jGen, sp);
		jGen.writeEndObject();
	}
	
	private void serializeFields(@SuppressWarnings("rawtypes") UsageContext bean, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        
		JavaType javaType = provider.constructType(UsageContext.class);
        BeanDescription beanDesc = provider.getConfig().introspect(javaType);
        JsonSerializer<Object> serializer =  BeanSerializerFactory.instance.findBeanOrAddOnSerializer(provider, javaType, beanDesc, false);
        serializer.unwrappingSerializer(null).serialize(bean, gen, provider);
    }

}
