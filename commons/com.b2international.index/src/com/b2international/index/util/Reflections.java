/*
 * Copyright 2011-2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index.util;

import static com.google.common.collect.Sets.newHashSet;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.b2international.index.IndexException;

/**
 * @since 4.7
 */
public class Reflections {

	private Reflections() {
	}
	
	public static Object getValue(Object object, Field field) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IndexException("Couldn't get value from field " + field, e);
		}
	}
	
	public static Object getValueOrNull(Object object, Field field) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// in case of any error treat the field as non-existent and return null
			return null;
		}
	}
	
	public static Collection<Field> getFields(Class<?> type) {
		if (type == Object.class) {
			return Collections.emptySet();
		}
		final Set<Field> fields = newHashSet();
		if (type.getSuperclass() != null) {
			fields.addAll(getFields(type.getSuperclass()));
		}
		for (Field field : type.getDeclaredFields()) {
			field.setAccessible(true);
			fields.add(field);
		}
		return fields;
	}
	
	public static Class<?> getType(Field field) {
		return getType(field.getGenericType());
	}

	private static Class<?> getType(Type type) {
		if (type instanceof ParameterizedType) {
			final ParameterizedType pType = (ParameterizedType) type;
			if (pType.getRawType() instanceof Class<?>) {
				final Class<?> rawType = (Class<?>) pType.getRawType();
				if (Iterable.class.isAssignableFrom(rawType)) {
					return getType(pType.getActualTypeArguments()[0]);
				}
			}
		} else if (type instanceof Class) {
			return (Class<?>) type;
		}
		throw new UnsupportedOperationException("Unsupported field type: " + type.getClass());
	}

	public static Field getField(Class<?> type, String field) {
		for (Field f : getFields(type)) {
			if (f.getName().equals(field)) {
				return f;
			}
		}
		throw new IndexException("Couldn't find field " + field + " on type " + type.getName(), null);
	}

	public static Class<?> getFieldType(Class<?> type, String field) {
		return getType(getField(type, field));
	}

	public static boolean isMapType(Field field) {
		final Type fieldType = field.getGenericType();
		if (fieldType instanceof ParameterizedType) {
			final ParameterizedType pType = (ParameterizedType) fieldType;
			if (pType.getRawType() instanceof Class<?>) {
				return Map.class.isAssignableFrom((Class<?>) pType.getRawType());
			}
		}
		return false;
	}
	
}
