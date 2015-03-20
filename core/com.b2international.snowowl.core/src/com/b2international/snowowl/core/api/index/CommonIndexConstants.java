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
package com.b2international.snowowl.core.api.index;

/**
 * Collection of constants related to indexing.
 *  
 */
public abstract class CommonIndexConstants {

	public static final String ROOT_ID = "ROOT";
	public static final String COMPONENT_ID = "component_id";
	public static final String COMPONENT_LABEL = "component_label";
	public static final String COMPONENT_LABEL_SORT_KEY = "component_label_sort_key"; // Label with diacritical marks removed; serves as a sort key
	public static final String COMPONENT_TYPE = "component_type";
	public static final String COMPONENT_STORAGE_KEY = "component_storage_key";
	public static final String COMPONENT_ICON_ID = "component_icon_id";
	public static final String COMPONENT_PARENT = "concept_parent_id";
	public static final String COMPONENT_RELEASED = "component_released";
	public static final String COMPONENT_COMPARE_UNIQUE_KEY = "component_compare_unique_key";
	public static final String COMPONENT_IGNORE_COMPARE_UNIQUE_KEY = "component_ignore_compare_unique_key";
	
	private CommonIndexConstants() {
		// Prevent instantiation
	}
}