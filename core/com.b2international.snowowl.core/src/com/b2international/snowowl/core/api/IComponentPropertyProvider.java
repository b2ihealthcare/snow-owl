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
package com.b2international.snowowl.core.api;

/**
 * Represents a component property provider with the following feature:
 * <ul>
 *   <li>{@link IComponentPropertyProvider#getId() <em>Component identifier.</em>}</li>
 *   <li>{@link IComponentPropertyProvider#getLabel() <em>Component label.</em>}</li>
 *   <li>{@link IComponentPropertyProvider#getArtefactType() <em>Component type.</em>}</li>
 * </ul>
 * </ul>
 *
 */
public interface IComponentPropertyProvider {

	/**Returns with the component unique ID.*/
	String getId();
	
	/**Returns with the component label.*/
	String getLabel();
	
	/**Returns with the component artefact type.*/
	String getArtefactType();
	
	/**Unknown value.*/
	String UNKNOWN = "unknown";
	
	/**Empty string value.*/
	String EMPTY = "";
	
}