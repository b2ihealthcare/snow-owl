/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a script that need to be executed within an Elasticsearch instance. 
 * 
 * @since 6.11
 */
public interface ScriptExpression {

	/**
	 * @return a named script or the actual raw script implementation
	 */
	String getScript();
	
	/**
	 * @return the script parameters
	 */
	Map<String, Object> getParams();

	/**
	 * Converts the given value if it is going to be unrecognized by Elasticsearch instances connected via the TCP client.
	 * Recognized types are listed in {@link org.elasticsearch.common.io.stream.StreamOutput}.
	 * 
	 * @param value - the value to convert
	 * @return the value that will be recognized by Elasticsearch without issues
	 */
	static Object convertScriptParam(Object value) {
		if (value instanceof ImmutableSet<?>) {
			return ((ImmutableSet<?>) value).asList();
		} else if (value instanceof Set<?>) {
			return ImmutableList.copyOf((Set<?>) value);
		}
		return value;
	}
}
