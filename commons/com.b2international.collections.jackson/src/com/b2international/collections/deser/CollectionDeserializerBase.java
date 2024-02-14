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
package com.b2international.collections.deser;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.b2international.collections.PrimitiveCollection;
import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

/**
 * @since 4.7
 */
@SuppressWarnings("serial")
abstract class CollectionDeserializerBase<T extends PrimitiveCollection> extends StdDeserializer<T>
{
    /**
     * We will use the default constructor of the class for
     * instantiation
     */
    protected final Constructor<T> _defaultCtor;

    @SuppressWarnings("unchecked")
    protected CollectionDeserializerBase(JavaType type, DeserializationConfig config)
    {
        super(type);
        boolean fixAccess = config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        _defaultCtor = (Constructor<T>) ClassUtil.findConstructor(type.getRawClass(), fixAccess);
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt,
        TypeDeserializer typeDeserializer)
        throws IOException, JsonProcessingException
    {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        // Ok: usually must point to START_ARRAY (or equivalent)
        // (note: caller handles nulls)
        if (!jp.isExpectedStartArrayToken()) {
            return handleNonArray(jp, ctxt);
        }
        T container = createContainerInstance(ctxt);
        deserializeContents(jp, ctxt, container);
        return container;
    }
    
    protected T handleNonArray(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        // default impl will just throw an exception; except if 'accept single as collection' is enabled...
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            T single = handleSingleAsArray(jp, ctxt, createContainerInstance(ctxt));
            if (single != null) {
                return single;
            }
        }
        ctxt.reportWrongTokenException(_valueClass, JsonToken.START_ARRAY, "");
        // XXX the method above will throw an exception, so null won't be returned from here ever
        return null;
    }

    private final T createContainerInstance(DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        try {
            return newInstance();
        } catch (Exception e) {
            throw ctxt.instantiationException(handledType(), e);
        }
    }

	protected abstract T newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException;
    
    protected T handleSingleAsArray(JsonParser jp, DeserializationContext ctxt, T container)
        throws IOException, JsonProcessingException
    {
        return null;
    }

    // // // Abstract methods for sub-classes to implement

    public abstract void deserializeContents(JsonParser jp, DeserializationContext ctxt, T container)
        throws IOException, JsonProcessingException;
}