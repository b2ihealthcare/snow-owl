/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.collections.deser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.floats.FloatCollection;
import com.b2international.collections.floats.FloatList;
import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.ints.IntDeque;
import com.b2international.collections.ints.IntList;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongDeque;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.collections.longs.LongSortedSet;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @since 4.7
 */
class CollectionDeserializers
{
    
    /**
     * Method called to see if this serializer (or a serializer this serializer
     * knows) should be used for given type; if not, null is returned.
     */
    public static JsonDeserializer<?> findDeserializer(DeserializationConfig config,
            final JavaType origType)
        throws JsonMappingException
    {
        JavaType type = origType;
        Class<?> raw = type.getRawClass();
        
        if (IntCollection.class.isAssignableFrom(raw)) {
            if (IntSet.class.isAssignableFrom(raw)) {
                return new IntSetDeserializer(type, config);
            } else if (IntDeque.class.isAssignableFrom(raw)) {
                return new IntDequeDeserializer(type, config);
            } else if (IntList.class.isAssignableFrom(raw)) {
            	return new IntListDeserializer(type, config);
            }
        } else if (LongCollection.class.isAssignableFrom(raw)) {
        	if (LongSortedSet.class.isAssignableFrom(raw)) {
        		return new LongSortedSetDeserializer(type, config);
        	} else if (LongSet.class.isAssignableFrom(raw)) {
                return new LongSetDeserializer(type, config);
            } else if (LongDeque.class.isAssignableFrom(raw)) {
                return new LongDequeDeserializer(type, config);
            } else if (LongList.class.isAssignableFrom(raw)) {
            	return new LongListDeserializer(type, config);
            }
        } else if (FloatCollection.class.isAssignableFrom(raw)) {
        	if (FloatList.class.isAssignableFrom(raw)) {
                return new FloatListDeserializer(type, config);
            }
        } else if (ByteCollection.class.isAssignableFrom(raw)) {
//        	if (ByteSet.class.isAssignableFrom(raw)) {
//        		return new ByteSetDeserializer(type, config);
//        	} else if (ByteList.class.isAssignableFrom(raw)) {
//        		return new ByteListDeserializer(type, config);
//        	}
        }
        return null;
    }        
    
    /*
    /**********************************************************************
    /* Intermediate base classes
    /**********************************************************************
     */

    /**
     * Intermediate base class used for various integral (as opposed to
     * floating point) value container types.
     */
    @SuppressWarnings("serial")
    static abstract class IntCollectionDeserializerBase<T extends IntCollection> extends CollectionDeserializerBase<T>
    {
        public IntCollectionDeserializerBase(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        public void deserializeContents(JsonParser jp, DeserializationContext ctxt,
                T container)
            throws IOException, JsonProcessingException
        {
            JsonToken t;
            while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
                // whether we should allow truncating conversions?
                int value;
                if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                    // should we catch overflow exceptions?
                    value = jp.getIntValue();
                } else {
                    if (t != JsonToken.VALUE_NULL) {
                        throw ctxt.mappingException(_valueClass.getComponentType());
                    }
                    value = 0;
                }
                add(container, value);
            }
        }

        protected abstract void add(T container, int value);
    }
    
    @SuppressWarnings("serial")
    static abstract class LongCollectionDeserializerBase<T extends LongCollection> extends CollectionDeserializerBase<T>
    {
        public LongCollectionDeserializerBase(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        public void deserializeContents(JsonParser jp, DeserializationContext ctxt,
                T container)
            throws IOException, JsonProcessingException
        {
            JsonToken t;
            while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
                // whether we should allow truncating conversions?
                long value;
                if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                    // should we catch overflow exceptions?
                    value = jp.getLongValue();
                } else {
                    if (t != JsonToken.VALUE_NULL) {
                        throw ctxt.mappingException(_valueClass.getComponentType());
                    }
                    value = 0;
                }
                add(container, value);
            }
        }

        protected abstract void add(T container, long value);
    }
    
    @SuppressWarnings("serial")
    static abstract class FloatCollectionDeserializerBase<T extends FloatCollection> extends CollectionDeserializerBase<T>
    {
        public FloatCollectionDeserializerBase(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        public void deserializeContents(JsonParser jp, DeserializationContext ctxt,
                T container)
            throws IOException, JsonProcessingException
        {
            JsonToken t;
            while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
                // whether we should allow truncating conversions?
                float value;
                if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                    // should we catch overflow exceptions?
                    value = jp.getFloatValue();
                } else {
                    if (t != JsonToken.VALUE_NULL) {
                        throw ctxt.mappingException(_valueClass.getComponentType());
                    }
                    value = 0;
                }
                add(container, value);
            }
        }

        protected abstract void add(T container, float value);
    }

    /*
    /**********************************************************************
    /* Concrete container implementations; basic integral types
    /**********************************************************************
     */

    static class IntSetDeserializer extends IntCollectionDeserializerBase<IntSet>
    {
        private static final long serialVersionUID = 1L;

        public IntSetDeserializer(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        protected void add(IntSet container, int value) {
            container.add(value);
        }
        
        @Override
        protected IntSet newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        	return PrimitiveSets.newIntOpenHashSet();
        }
        
    }

    static class IntDequeDeserializer extends IntCollectionDeserializerBase<IntDeque>
    {
        private static final long serialVersionUID = 1L;

        public IntDequeDeserializer(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        protected void add(IntDeque container, int value) {
            container.addLast(value);
        }
        
        @Override
        protected IntDeque newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        	return PrimitiveLists.newIntArrayDeque();
        }
    }
    
    static class IntListDeserializer extends IntCollectionDeserializerBase<IntList>
    {
        private static final long serialVersionUID = 1L;

        public IntListDeserializer(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        protected void add(IntList container, int value) {
            container.add(value);
        }
        
        @Override
        protected IntList newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        	return PrimitiveLists.newIntArrayList();
        }
    }
    
    static class LongSortedSetDeserializer extends LongCollectionDeserializerBase<LongSortedSet>
    {
        private static final long serialVersionUID = 1L;

        public LongSortedSetDeserializer(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        protected void add(LongSortedSet container, long value) {
            container.add(value);
        }
        
        @Override
        protected LongSortedSet newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        	return PrimitiveSets.newLongSortedSet();
        }
        
    }
    
    static class LongSetDeserializer extends LongCollectionDeserializerBase<LongSet>
    {
        private static final long serialVersionUID = 1L;

        public LongSetDeserializer(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        protected void add(LongSet container, long value) {
            container.add(value);
        }
        
        @Override
        protected LongSet newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        	return PrimitiveSets.newLongOpenHashSet();
        }
        
    }
    
    static class LongListDeserializer extends LongCollectionDeserializerBase<LongList>
    {
        private static final long serialVersionUID = 1L;

        public LongListDeserializer(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        protected void add(LongList container, long value) {
            container.add(value);
        }
        
        @Override
        protected LongList newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        	return PrimitiveLists.newLongArrayList();
        }
        
    }
    
    static class LongDequeDeserializer extends LongCollectionDeserializerBase<LongDeque>
    {
        private static final long serialVersionUID = 1L;

        public LongDequeDeserializer(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        protected void add(LongDeque container, long value) {
            container.add(value);
        }
        
        @Override
        protected LongDeque newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        	return PrimitiveLists.newLongArrayDeque();
        }
        
    }
    
    static class FloatListDeserializer extends FloatCollectionDeserializerBase<FloatList>
    {
        private static final long serialVersionUID = 1L;

        public FloatListDeserializer(JavaType type, DeserializationConfig config)
        {
            super(type, config);
        }

        @Override
        protected void add(FloatList container, float value) {
            container.add(value);
        }
        
        @Override
        protected FloatList newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        	return PrimitiveLists.newFloatArrayList();
        }
        
    }

}
