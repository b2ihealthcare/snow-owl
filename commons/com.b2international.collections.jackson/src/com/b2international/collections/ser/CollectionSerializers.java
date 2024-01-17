/*
 * Copyright 2016-2017 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.floats.FloatCollection;
import com.b2international.collections.floats.FloatIterator;
import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

class CollectionSerializers
{
    public final static CollectionSerializerBase<?>[] _primitiveSerializers =
        new CollectionSerializerBase<?>[] {
            new ByteCollectionSerializer(),
            new IntContainerSerializer(),
            new LongContainerSerializer(),
            new FloatContainerSerializer()
        };

    /**
     * Method called to see if this serializer (or a serializer this serializer
     * knows) should be used for given type; if not, null is returned.
     */
    public static JsonSerializer<?> getMatchingSerializer(SerializationConfig config,
            JavaType type)
    {
        for (CollectionSerializerBase<?> ser : _primitiveSerializers) {
            JsonSerializer<?> actual = ser.getSerializer(type);
            if (actual != null) {
                return actual;
            }
        }
        return null;
    }        

    /*
    /**********************************************************************
    /* Concrete container implementations; basic integral types
    /**********************************************************************
     */

    /**
     * {@link ByteCollection}s are handled similar to byte[], meaning that they are
     * actually serialized as base64-encoded Strings by default
     *<p>
     * TODO: allow specifying other modes (serialize as array?)
     */
    final static class ByteCollectionSerializer
        extends CollectionSerializerBase<ByteCollection>
    {

        ByteCollectionSerializer() {
            super(ByteCollection.class, "string"); // really, "binary", but...
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("string", true);
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            // Logical content byte array/stream, but physically a JSON String so:
            if (visitor != null) {
            	visitor.expectStringFormat(typeHint);
            }
        }

        @Override
        public boolean isEmpty(ByteCollection value) {
        	return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(ByteCollection value) {
            return value.size() == 1;
        }

        @Override
        public void serialize(ByteCollection value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException
        {
            serializeContents(value, jgen, provider);
        }
        
        @Override
        public void serializeWithType(ByteCollection value, JsonGenerator jgen, SerializerProvider provider,
                TypeSerializer typeSer)
            throws IOException
        {
            // will be a JSON String, so can't use array prefix/suffix
            typeSer.writeTypePrefixForScalar(value, jgen);
            serializeContents(value, jgen, provider);
            typeSer.writeTypeSuffixForScalar(value, jgen);
        }
        @Override
        protected void serializeContents(final ByteCollection value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
            byte[] bytes = value.toArray();
            jgen.writeBinary(bytes);
        }
    }

    final static class IntContainerSerializer
        extends CollectionSerializerBase<IntCollection>
    {

        IntContainerSerializer() {
            super(IntCollection.class, "integer");
        }

        @Override
        public boolean isEmpty(IntCollection value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(IntCollection value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
                throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }

        @Override
        protected void serializeContents(final IntCollection value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
            final IntIterator iterator = value.iterator();
            while (iterator.hasNext()) {
            	jgen.writeNumber(iterator.next());
            }
        }

    }

    final static class LongContainerSerializer
        extends CollectionSerializerBase<LongCollection>
    {

        LongContainerSerializer() {
            super(LongCollection.class, "integer");
        }

        @Override
        public boolean isEmpty(LongCollection value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(LongCollection value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
                throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }

        @Override
        protected void serializeContents(final LongCollection value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
        	final LongIterator iterator = value.iterator();
        	while (iterator.hasNext()) {
        		jgen.writeNumber(iterator.next());
        	}
        }
    }

    /*
    /**********************************************************************
    /* Concrete container implementations; floating-point types
    /**********************************************************************
     */

    final static class FloatContainerSerializer
        extends CollectionSerializerBase<FloatCollection>
    {
        FloatContainerSerializer() {
            super(FloatCollection.class, "number");
        }

        @Override
        public boolean isEmpty(FloatCollection value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(FloatCollection value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.NUMBER);
                }
            }
        }

        @Override
        protected void serializeContents(final FloatCollection value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {	
        	final FloatIterator iterator = value.iterator();
        	while (iterator.hasNext()) {
        		jgen.writeNumber(iterator.next());
        	}
        }
    }

}