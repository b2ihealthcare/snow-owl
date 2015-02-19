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
 * Abstract implementation of {@link DynamicValue} that handles
 * {@link #as(Class)} and {@link #as(Class, Object)} methods and offers
 * reasonable defaults for the rest of the interface.
 * 
 */
public abstract class AbstractDynamicValue implements DynamicValue {

	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public <T> T as(Class<T> clazz) {
		return as(clazz, null);
	}

	@Override
	public <T> T as(Class<T> clazz, T defaultValue) {

		Object value = get();
		
		if (value == null) {
			return null;
		}
		
		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}
		
		return defaultValue;
	}

	@Override
	public boolean asBoolean() {
		return asBoolean(false);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation always returns <code>defaultValue</code>; subclasses should override. 
	 */
	@Override
	public boolean asBoolean(boolean defaultValue) {
		return defaultValue;
	}

	@Override
	public int asInt() {
		return asInt(0);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation always returns <code>defaultValue</code>; subclasses should override. 
	 */
	@Override
	public int asInt(int defaultValue) {
		return defaultValue;
	}

	@Override
	public long asLong() {
		return asLong(0L);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation always returns <code>defaultValue</code>; subclasses should override. 
	 */
	@Override
	public long asLong(long defaultValue) {
		return defaultValue;
	}
}