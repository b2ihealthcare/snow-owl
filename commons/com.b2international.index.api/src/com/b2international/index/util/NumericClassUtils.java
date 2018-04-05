/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.b2international.collections.PrimitiveCollection;
import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.floats.FloatCollection;
import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.longs.LongCollection;
import com.b2international.commons.reflect.Reflections;

/**
 * @since 5.4
 */
public class NumericClassUtils {

	/**
	 * @param field the document mapping field to test
	 * @return if the field's type is either the same as, or is a subtype of
	 *         {@link Collection} or {@link PrimitiveCollection}, the contained
	 *         elements' type, otherwise the field's type
	 */
	public static Class<?> unwrapCollectionType(final Field field) {
		final Class<?> fieldType = field.getType();

		if (Collection.class.isAssignableFrom(fieldType)) {
			return Reflections.getType(field);
		} else if (FloatCollection.class.isAssignableFrom(fieldType)) {
			return float.class;
		} else if (LongCollection.class.isAssignableFrom(fieldType)) {
			return long.class;
		} else if (IntCollection.class.isAssignableFrom(fieldType)) {
			return int.class;
		} else if (ByteCollection.class.isAssignableFrom(fieldType)) {
			return byte.class;
		}

		return fieldType;
	}

	/**
	 * @param field the document mapping field to test
	 * @return <code>true</code> if the field's type is either the same as, or
	 *         is a subtype of {@link Collection} or
	 *         {@link PrimitiveCollection}, <code>false</code> otherwise
	 */
	public static boolean isCollection(final Field field) {
		return isCollection(field.getType());
	}
	
	/**
	 * @param fieldType the document mapping field's type to test
	 * @return <code>true</code> if the field's type is either the same as, or
	 *         is a subtype of {@link Collection} or
	 *         {@link PrimitiveCollection}, <code>false</code> otherwise
	 */
	public static boolean isCollection(final Class<?> fieldType) {
		return Collection.class.isAssignableFrom(fieldType) || PrimitiveCollection.class.isAssignableFrom(fieldType);
	}

	public static boolean isBigDecimal(final Class<?> fieldType) {
		return fieldType == BigDecimal.class;
	}

	public static boolean isFloat(final Class<?> fieldType) {
		return fieldType == Float.class || fieldType == float.class;
	}

	public static boolean isLong(final Class<?> fieldType) {
		return fieldType == Long.class || fieldType == long.class;
	}

	public static boolean isInt(final Class<?> fieldType) {
		return fieldType == Integer.class || fieldType == int.class;
	}

	public static boolean isShort(final Class<?> fieldType) {
		return fieldType == Short.class || fieldType == short.class;
	}

	private NumericClassUtils() {
		throw new UnsupportedOperationException(NumericClassUtils.class.getSimpleName() + " is not supposed to be instantiated.");
	}

	public static boolean isDate(Class<?> fieldType) {
		return fieldType == Date.class;
	}
}
