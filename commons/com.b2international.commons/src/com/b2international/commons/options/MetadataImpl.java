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
package com.b2international.commons.options;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 4.1
 */
public class MetadataImpl extends HashMap<String, Object> implements Metadata {

	private static final long serialVersionUID = -7225474709394975493L;

	public MetadataImpl() {
		super();
	}
	
	public MetadataImpl(Map<String, Object> map) {
		super(map);
	}
	
	@Override
	public <T> T get(String key, Class<T> expectedType) {
		if (key != null && expectedType != null) {
			final Object value = get(key);
			if (value != null) {
				if (expectedType.isInstance(value)) {
					return expectedType.cast(value);
				}
				throw new IllegalArgumentException(String.format(
						"Expected type '%s' is not valid for the value '%s(%s)' returned for the key '%s'",
						expectedType.getSimpleName(), value, value.getClass().getSimpleName(), key));
			}
		}
		return null;
	}

	@Override
	public Boolean getBoolean(final String key) {
		return get(key, Boolean.class);
	}
	
	@Override
	public String getString(final String key) {
		return get(key, String.class);
	}

	@Override
	public Integer getInt(String key) {
		return get(key, Integer.class);
	}

	@Override
	public Float getFloat(String key) {
		return get(key, Float.class);
	}

	@Override
	public Double getDouble(String key) {
		return get(key, Double.class);
	}

	@Override
	public Short getShort(String key) {
		return get(key, Short.class);
	}

	@Override
	public Character getChar(String key) {
		return get(key, Character.class);
	}

	@Override
	public Byte getByte(String key) {
		return get(key, Byte.class);
	}
}
