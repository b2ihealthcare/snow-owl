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
package com.b2international.commons.dynamic;

/**
 * Encapsulates an object of indeterminate type. 
 *
 */
public interface DynamicValue {

	/**
	 * A shared instance wrapping the value <code>null</code>.
	 */
	static final DynamicValue NULL = new AbstractDynamicValue() {

		@Override
		public <T> T as(Class<T> clazz, T defaultValue) {
			return defaultValue;
		}

		@Override
		public Object get() {
			return null;
		}
	};

	/**
	 * A shared instance representing a missing value.
	 */
	static final DynamicValue MISSING = new AbstractDynamicValue() {

		@Override
		public <T> T as(Class<T> clazz, T defaultValue) {
			return defaultValue;
		}
		
		@Override
		public Object get() {
			return null;
		}
		
		@Override
		public boolean exists() {
			return false;
		}
	};
	
	/**
	 * @return the encapsulated object
	 */
	Object get();
	
	/**
	 * @return <code>true</code> if the encapsulated object held in this
	 *         instance exists, <code>false</code> if this instance is a
	 *         placeholder for a non-existent object
	 */
	boolean exists();
	
	/**
	 * Returns the encapsulated object with type T if the object is an instance
	 * of the class representing type T, <code>null</code> otherwise.
	 * 
	 * @param <T>
	 *            the requested type
	 *            
	 * @param clazz
	 *            the class representing the requested type
	 *            
	 * @return the object cast to the requested type, or <code>null</code>
	 */
	<T> T as(Class<T> clazz);
	
	/**
	 * Returns the encapsulated object with type T if the object is an instance
	 * of the class representing type T, <code>defaultValue</code> otherwise.
	 * 
	 * @param <T>
	 *            the requested type
	 * 
	 * @param clazz
	 *            the class representing the requested type
	 * 
	 * @param defaultValue
	 *            the value to return when the encapsulated object is not an
	 *            instance of <code>clazz</code>
	 * 
	 * @return the object cast to the requested type, or
	 *         <code>defaultValue</code>
	 */
	<T> T as(Class<T> clazz, T defaultValue);

	/**
	 * Returns the encapsulated object as a boolean primitive if it is an
	 * instance of {@link Boolean}, <code>false</code> otherwise. This
	 * method is the equivalent of calling {@link #as(Class, Object)
	 * as(Boolean.class, false).booleanValue()}.
	 * 
	 * @return the object cast to <code>boolean</code>, or <code>false</code>
	 */
	boolean asBoolean();
	
	/**
	 * Returns the encapsulated object as a boolean primitive if it is an
	 * instance of {@link Boolean}, <code>defaultValue</code> otherwise. This
	 * method is the equivalent of calling {@link #as(Class, Object)
	 * as(Boolean.class, defaultValue).booleanValue()}.
	 * 
	 * @param defaultValue
	 *            the <code>boolean</code> value to return if the encapsulated
	 *            object is not an instance of <code>Boolean</code>
	 * 
	 * @return the object cast to <code>boolean</code>, or <code>defaultValue</code>
	 */
	boolean asBoolean(boolean defaultValue);

	/**
	 * Returns the encapsulated object as an integer primitive if it is an
	 * instance of {@link Number}, <code>0</code> otherwise. This method is the
	 * equivalent of calling {@link #as(Class, Object) as(Number.class,
	 * 0).intValue()}. The operation may involve rounding or truncation.
	 * 
	 * @return the object cast to <code>int</code>, or <code>0</code>
	 */
	int asInt();

	/**
	 * Returns the encapsulated object as an integer primitive if it is an
	 * instance of {@link Number}, <code>defaultValue</code> otherwise. This
	 * method is the equivalent of calling {@link #as(Class, Object)
	 * as(Number.class, defaultValue).intValue()}. The operation may involve
	 * rounding or truncation.
	 * 
	 * @param defaultValue
	 *            the <code>int</code> value to return if the encapsulated
	 *            object is not an instance of <code>Number</code>
	 * 
	 * @return the object cast to <code>int</code>, or <code>defaultValue</code>
	 */
	int asInt(int defaultValue);

	/**
	 * Returns the encapsulated object as a long primitive if it is an instance
	 * of {@link Number}, <code>0L</code> otherwise. This method is the
	 * equivalent of calling {@link #as(Class, Object) as(Number.class,
	 * 0L).longValue()}. The operation may involve rounding.
	 * 
	 * @return the object cast to <code>long</code>, or <code>0L</code>
	 */
	long asLong();
	
	/**
	 * Returns the encapsulated object as a long primitive if it is an instance
	 * of {@link Number}, <code>defaultValue</code> otherwise. This method is the
	 * equivalent of calling {@link #as(Class, Object) as(Number.class,
	 * defaultValue).longValue()}. The operation may involve rounding.
	 * 
	 * @param defaultValue
	 *            the <code>long</code> value to return if the encapsulated
	 *            object is not an instance of <code>Number</code>
	 * 
	 * @return the object cast to <code>long</code>, or <code>defaultValue</code>
	 */
	long asLong(long defaultValue);
}