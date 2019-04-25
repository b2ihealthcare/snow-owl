/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.xtext.xbase.lib.Pair;

/**
 * Useful Xtend extensions when working with JSON data.
 * 
 * @since 1.0 
 */
@SuppressWarnings("unchecked")
public class JsonExtensions {

	public static String asJson(Object it) {
		if (it instanceof String) {
			return asJson((String) it);
		} else if (it instanceof Map.Entry<?, ?>) {
			return asJson((Map.Entry<String, Object>) it);
		}
		return String.valueOf(it);
	}
	
	public static String asJson(String it) {
		return String.format("\"%s\"", it);
	}
	
	public static String asJson(Entry<String, Object> it) {
		return String.format("\"%s\":%s", it.getKey(), asJson(it.getValue()));
	}
	
	public static String asJson(Pair<String, Object> it) {
		return String.format("\"%s\":%s", it.getKey(), asJson(it.getValue()));
	}
	
	public static String asJson(Map<String, Object> properties) {
		if (properties.isEmpty()) {
			return "{}";
		} else {
			return properties.entrySet()
					.stream()
					.map(JsonExtensions::asJson)
					.collect(Collectors.joining(",", "{", "}"));
		}
	}
	
	public static String asJson(Collection<Pair<String, Object>> properties) {
		return properties.stream().map(JsonExtensions::asJson).collect(Collectors.joining(",", "{", "}"));
	}
	
	public static String asJson(List<?> properties) {
		return properties.stream().map(JsonExtensions::asJson).collect(Collectors.joining(",", "[", "]"));
	}
	
}