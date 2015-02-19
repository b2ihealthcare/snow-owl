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
package com.b2international.snowowl.api.history.domain;

/**
 * Provides detailed information about some historical modifications made on an element in the past.
 * 
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link IHistoryInfoDetails#getComponentType() <em>Component type</em>}</li>
 *   <li>{@link IHistoryInfoDetails#getDescription() <em>Description</em>}</li>
 *   <li>{@link IHistoryInfoDetails#getChangeType() <em>Change type</em>}</li>
 * </ul>
 * </p>
 * @see ChangeType
 * @see IHistoryInfo 
 */
public interface IHistoryInfoDetails {

	/**
	 * Returns with a human readable representation of the modified element's type.
	 * @return the human readable representation of the modified element's type.
	 * @see IHistoryInfoDetails
	 */
	String getComponentType();

	/**
	 * Returns with a detailed description of the modification that has been made on the element.
	 * @return a human readable detailed description of the modification.
	 * @see IHistoryInfoDetails
	 */
	String getDescription();

	/**
	 * Returns with the {@link ChangeType type} of the modification. 
	 * @return the change type.
	 * @see IHistoryInfoDetails
	 */
	ChangeType getChangeType();
	
}