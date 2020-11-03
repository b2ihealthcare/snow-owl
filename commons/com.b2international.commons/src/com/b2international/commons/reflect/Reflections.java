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
package com.b2international.commons.reflect;

import static com.google.common.collect.Sets.newHashSet;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
			throw new RuntimeException("Couldn't get value from field " + field, e);
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
		final Type fieldType = field.getGenericType();
		if (fieldType instanceof ParameterizedType) {
			final ParameterizedType pType = (ParameterizedType) fieldType;
			if (pType.getRawType() instanceof Class<?>) {
				final Class<?> rawType = (Class<?>) pType.getRawType();
				if (Iterable.class.isAssignableFrom(rawType)) {
					return (Class<?>) pType.getActualTypeArguments()[0];
				}
			}
		} else if (fieldType instanceof Class) {
			return (Class<?>) fieldType;
		}
		throw new UnsupportedOperationException("Unsupported field type: " + fieldType.getClass());
	}

	public static Field getField(Class<?> type, String field) {
		for (Field f : getFields(type)) {
			if (f.getName().equals(field)) {
				return f;
			}
		}
		throw new RuntimeException("Couldn't find field " + field + " on type " + type.getName(), null);
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
