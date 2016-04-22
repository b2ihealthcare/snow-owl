/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.mapping;

import java.util.Map;

/**
 * @since 4.7
 *            - the type of the object to map from/to Map<String, Object>/JSON
 */
public interface MappingStrategy<T> {

	/**
	 * Converts the POJO representation to a Map of String, Object pairs.
	 * 
	 * @param t
	 * @return
	 */
	Map<String, Object> convert(T t);

	/**
	 * Converts the given Map of String, Object pairs to a POJO representation.
	 * 
	 * @param map
	 * @return
	 */
	T convert(Map<String, Object> map);

	/**
	 * Returns the type of the value this mapping strategy can work with.
	 * 
	 * @return
	 */
	String getType();

	/**
	 * Returns the definition of the elasticsearch mapping for the component of this {@link MappingStrategy}.
	 * 
	 * @return
	 */
	String getMapping();

}