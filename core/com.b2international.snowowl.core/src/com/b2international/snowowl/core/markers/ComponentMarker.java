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
package com.b2international.snowowl.core.markers;

/**
 * Contains constants for the customized marker framework.
 * 
 */
public abstract class ComponentMarker {

	/**Represents a terminology specific unique identifier. (Value: {@value})*/
	public static final String ID = "id";
	/**Represents a unique identifier of a terminology component. (Value: {@value})*/
	public static final String TERMINOLOGY_COMPONENT_ID = "terminologyComponentId";
	/**Attribute identifier to store a human readable label of the marker. (Value: {@value)}*/
	public static final String LABEL = "label";
	/**Unique attribute identifier for storing a validation constraint identifier. (Value: {@value})*/
	public static final String CONSTRAINT_ID = "constraintId";
	/** Attribute identifier for component image ids */
	public static final String IMAGE_ID = "imageId";
	/** Attribute identifier for component storage key */
	public static final String STORAGE_KEY = "storageKey";

	private ComponentMarker() {
		//suppress instantiation
	}
	
}