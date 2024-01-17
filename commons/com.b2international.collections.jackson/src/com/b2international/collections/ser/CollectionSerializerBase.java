/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.collections.ser;

import java.io.IOException;
import java.lang.reflect.Type;

import com.b2international.collections.PrimitiveCollection;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;

/**
 * Base class for various container (~= PrimitiveCollection) serializers.
 */
abstract class CollectionSerializerBase<T extends PrimitiveCollection>
    extends ContainerSerializer<T>
{
    protected final String _schemeElementType;

    protected CollectionSerializerBase(Class<T> type, String schemaElementType)
    {
        super(type);
        _schemeElementType = schemaElementType;
    }

    protected CollectionSerializerBase(CollectionSerializerBase<?> src) {
        super(src._handledType, true);
        _schemeElementType = src._schemeElementType;
    }

    /*
    /**********************************************************
    /* Simple accessor overrides, defaults
    /**********************************************************
     */
    
    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    {
        ObjectNode o = createSchemaNode("array", true);
        o.set("items", createSchemaNode(_schemeElementType));
        return o;
    }
    
    @Override
    public JsonSerializer<?> getContentSerializer() {
        // We are not delegating, for most part, so while not dynamic claim we don't have it
        return null;
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        // May or may not be supportable, but for now fail loudly, not quietly
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaType getContentType() {
        // Not sure how to efficiently support this; could resolve types of course
        return null;
    }

    @Override
    public abstract void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException;

    /*
    /**********************************************************
    /* Serialization
    /**********************************************************
     */
    
    @Override
    public void serialize(T value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException
    {
        jgen.writeStartArray();
        serializeContents(value, jgen, provider);
        jgen.writeEndArray();
    }

    protected abstract void serializeContents(T value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException;
    
    @Override
    public void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException
    {
        typeSer.writeTypePrefixForArray(value, jgen);
        serializeContents(value, jgen, provider);
        typeSer.writeTypeSuffixForArray(value, jgen);
    }

    /*
    /**********************************************************
    /* Helper methods for sub-classes
    /**********************************************************
     */
    
    protected JsonSerializer<?> getSerializer(JavaType type)
    {
        if (_handledType.isAssignableFrom(type.getRawClass())) {
            return this;
        }
        return null;
    }
}