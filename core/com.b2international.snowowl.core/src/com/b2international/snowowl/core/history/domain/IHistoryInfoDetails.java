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
package com.b2international.snowowl.core.history.domain;

/**
 * Provides detailed information about a single commit.
 * <p>
 * Any edited component part of the commit will have a separate detail object appended.
 */
public interface IHistoryInfoDetails {

	/**
	 * Returns with the modified element's type.
	 * 
	 * @return the human readable representation of the modified element's type
	 */
	String getComponentType();

	/**
	 * Returns a detailed description of the modification that has been made on the element.
	 * 
	 * @return a human readable description of the modification
	 */
	String getDescription();

	/**
	 * Returns the type of the modification.
	 *  
	 * @return the change type ({@link ChangeType#NEW NEW}, {@link ChangeType#CHANGED CHANGED} or {@link ChangeType#DETACHED DETACHED})
	 */
	ChangeType getChangeType();
}
