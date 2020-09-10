/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

final class Mutables {

	// TODO make these threadsafe
	private static final Map<Class<?>, Map<String, Field>> RESOLVED_GETTERS = Maps.newHashMap();
	private static final Map<Class<?>, Map<String, Method>> RESOLVED_SETTERS = Maps.newHashMap();
	
	private Mutables() {}
	
	/**
	 * Reads an object from the given serialized json value and returns it.
	 * 
	 * @param mapper
	 * @param fieldType
	 * @param property
	 * @param json
	 * @return
	 * @throws JsonProcessingException - if processing of the json value fails for any reason
	 */
	static Object readValue(ObjectMapper mapper, final Class<?> fieldType, String property, String json) throws JsonProcessingException {
		if (String.class == fieldType) {
			return json;
		} else if (Boolean.class == fieldType || boolean.class == fieldType) {
			return Boolean.valueOf(json);
		} else if (Byte.class == fieldType || byte.class == fieldType) {
			return Byte.valueOf(json);
		} else if (Short.class == fieldType || short.class == fieldType) {
			return Short.valueOf(json);
		} else if (Integer.class == fieldType || int.class == fieldType) {
			return Integer.valueOf(json);
		} else if (Long.class == fieldType || long.class == fieldType) {
			return Long.valueOf(json);
		} else if (Float.class == fieldType || float.class == fieldType) {
			return Float.valueOf(json);
		} else if (Double.class == fieldType || double.class == fieldType) {
			return Double.valueOf(json);
		} else if (String[].class.isAssignableFrom(fieldType)) {
			return mapper.readValue(json, String[].class);
		} else if (SortedSet.class.isAssignableFrom(fieldType)) {
			return Set.of(mapper.readValue(json, String[].class));
		} else if (Set.class.isAssignableFrom(fieldType)) {
			return Set.of(mapper.readValue(json, String[].class));
		} else if (List.class.isAssignableFrom(fieldType)) {
			return List.of(mapper.readValue(json, String[].class));
		} else {
			throw new UnsupportedOperationException("TODO reflective property setter is not supported for property: " + property + " > " + fieldType);
		}
	}

	static <T> T set(T obj, String property, Object value) {
		Class<?> type = obj.getClass();
		if (!RESOLVED_SETTERS.containsKey(type)) {
			RESOLVED_SETTERS.put(type, Maps.newHashMap());
		}
		
		Map<String, Method> resolvedSetters = RESOLVED_SETTERS.get(type);
		if (!resolvedSetters.containsKey(property)) {
			resolvedSetters.put(property, findSetter(obj, property));
		}

		try {
			// FIXME assuming self returning setters here
			return (T) resolvedSetters.get(property).invoke(obj, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	static Object get(Object obj, String property) {
		Class<?> type = obj.getClass();
		if (!RESOLVED_GETTERS.containsKey(type)) {
			RESOLVED_GETTERS.put(type, Maps.newHashMap());
		}
		
		Map<String, Field> resolvedGetters = RESOLVED_GETTERS.get(type);
		if (!resolvedGetters.containsKey(property)) {
			resolvedGetters.put(property, findGetter(obj, property));
		}
		
		try {
			return resolvedGetters.get(property).get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Field findGetter(Object obj, String property) {
		for (Field field : obj.getClass().getFields()) {
			if (Objects.equals(property, field.getName())) {
				return field;
			}
		}
		throw new IllegalArgumentException("Couldn't find public builder method for property: " + property);
	}
	
	private static Method findSetter(Object obj, String property) {
		for (Method method : obj.getClass().getMethods()) {
			if (method.getName().equals(property)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Couldn't find public builder method for property: " + property);
	}
	
}
