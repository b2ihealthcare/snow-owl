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

import com.google.common.base.MoreObjects;

/**
 * An implementation of {@link DynamicValue} that captures a single {@link Object}. 
 *
 */
public class DynamicValueImpl extends AbstractDynamicValue {

	private final Object value;
	
	public static DynamicValue create(Object value) {
		return (value == null) ? DynamicValue.NULL : new DynamicValueImpl(value);
	}
	
	private DynamicValueImpl(Object value) {
		this.value = value;
	}
	
	@Override
	public Object get() {
		return value;
	}

	@Override
	public boolean asBoolean(boolean defaultValue) {
		return as(Boolean.class, defaultValue).booleanValue();
	}

	@Override
	public int asInt(int defaultValue) {
		return as(Number.class, defaultValue).intValue();
	}

	@Override
	public long asLong(long defaultValue) {
		return as(Number.class, defaultValue).longValue();
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("value", value).toString();
	}
}