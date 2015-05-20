/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

	public static class Parameter<T> extends Pair<Class<T>, T> {
		
		public Parameter() {
		}
		
		public Parameter(Class<T> clazz, T value) {
			super(clazz, value);
		}
	}
	
	public static Object getGetterValue(Object object, String property) {
		try {
			return getGetter(object.getClass(), property).invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Could not get value from getter: " + object + "-" + property, e);
		}
	}
	
	private static Method getGetter(Class<?> type, String property) {
		try {
			return type.getMethod(property);
		} catch (NoSuchMethodException | SecurityException e) {
			if (!property.startsWith("get")) {
				return getGetter(type, "get".concat(StringUtils.capitalizeFirstLetter(property)));
			}
			throw new RuntimeException("Could not find applicable getter method: " + property, e);
		}
	}
	
	public static <R, T> R getField(Class<T> clazz, T instance, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (R) field.get(instance);
		
		} catch (SecurityException e) {
			handleException(e);
		} catch (NoSuchFieldException e) {
			handleException(e);
		} catch (IllegalAccessException e) {
			handleException(e);
		}
		return null;
	}
	
	public static <T> void setField(Class<T> clazz, T instance, String fieldName, Object value) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		
		} catch (SecurityException e) {
			handleException(e);
		} catch (NoSuchFieldException e) {
			handleException(e);
		} catch (IllegalAccessException e) {
			handleException(e);
		}
	}
	
	public static <R, T> R callMethod(Class<T> clazz, T instance, String methhodName, Parameter<?>... parameters) {
		
		try {
			Method method = clazz.getDeclaredMethod(methhodName, getClasses(parameters));
			method.setAccessible(true);
			return (R) method.invoke(instance, getValues(parameters));
		} catch (SecurityException e) {
			handleException(e);
		} catch (NoSuchMethodException e) {
			handleException(e);
		} catch (IllegalAccessException e) {
			handleException(e);
		} catch (InvocationTargetException e) {
			handleException(e);
		}
		return null;
	}
	
	protected static void handleException(Throwable e) {
		throw new RuntimeException("An error occurred while reflecting", e);
	}

	protected static Class<?>[] getClasses(Parameter<?>... parameters) {
		Class<?>[] classes = new Class[parameters.length];
		for(int i = 0; i < classes.length; i++) {
			classes[i] = parameters[i].getA();
		}
		return classes;
	}

	protected static Object[] getValues(Parameter<?>... parameters) {
		Object[] values = new Object[parameters.length];
		for(int i = 0; i < values.length; i++) {
			values[i] = parameters[i].getB();
		}
		return values;
	}
}
 