/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.json;

import java.util.List;
import java.util.Map;

import com.b2international.commons.CompareUtils;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @since 7.14.1
 */
public final class Json extends ForwardingMap<String, Object> {

	@JsonUnwrapped
	private final Map<String, Object> source;
	
	public Json(Map<String, Object> source) {
		super();
		this.source = source;
	}
	
	@Override
	public String toString() {
		return source.toString();
	}
	
	@Override
	protected Map<String, Object> delegate() {
		return source;
	}
	
	public Json with(String property, Object value) {
		final Map<String, Object> newJson = Maps.newHashMap(source);
		newJson.put(property, value);
		return new Json(newJson);
	}
	
	public Json without(String property) {
		final Map<String, Object> newJson = Maps.newHashMap(source);
		newJson.remove(property);
		return new Json(newJson);
	}
	
	public Json with(Map<String, Object> object) {
		final Map<String, Object> newJson = Maps.newHashMap(source);
		newJson.putAll(object);
		return new Json(newJson);
	}
	
	@SafeVarargs
	public static <T> List<T> array(T...values) {
		return ImmutableList.copyOf(values);
	}
	
	public static Json object(Object...properties) {
		final ImmutableMap.Builder<String, Object> props = ImmutableMap.builder();
		if (properties != null) {
			Preconditions.checkArgument(properties.length % 2 == 0, "Invalid number of property-value pairs specified. Got: %s", properties.length);
			for (int i = 0; i < properties.length / 2; i++) {
				final int propIdx = i * 2;
				final Object key = properties[propIdx];
				Preconditions.checkArgument(key instanceof String, "Property key at index '%s' is not a String object", propIdx);
				final Object value = properties[propIdx + 1];
				props.put((String) key, value);
			}
		}
		return new Json(props.build());
	}
	
	@SafeVarargs
	public static Json assign(Map<String, Object>...sources) {
		if (CompareUtils.isEmpty(sources)) {
			return new Json(Map.of());
		} else {
			Json result = new Json(sources[0]);
			if (sources.length > 1) {
				for (int i = 1; i < sources.length; i++) {
					result = result.with(sources[i]);
				}
			}
			return result;
		}
	}

}
