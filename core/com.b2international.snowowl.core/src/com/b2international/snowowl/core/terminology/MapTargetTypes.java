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
package com.b2international.snowowl.core.terminology;

/**
 * @since 7.2
 */
public final class MapTargetTypes {
	
	private MapTargetTypes() {
		// Prevent instantiation
	}
	
	public static final String LANGUAGE = "LANGUAGE";
	public static final String ATTRIBUTE_VALUE = "ATTRIBUTE_VALUE";
	public static final String SIMPLE = "SIMPLE";
	public static final String SIMPLE_MAP = "SIMPLE_MAP";
	public static final String SIMPLE_MAP_WITH_DESCRIPTION = "SIMPLE_MAP_WITH_DESCRIPTION";
	public static final String COMPLEX_MAP = "COMPLEX_MAP";
	public static final String EXTENDED_MAP = "EXTENDED_MAP";
	public static final String QUERY = "QUERY";
	
}
