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
 * Enumeration type for indicating historical changes on an element.
 <p>
 * The following types are available:
 * <ul>
 *   <li>{@link ChangeType#NEW <em>New</em>}</li>
 *   <li>{@link ChangeType#DETACHED <em>Detached</em>}</li>
 *   <li>{@link ChangeType#CHANGED <em>Changed</em>}</li>
 * </ul>
 * </p>
 * @see IHistoryInfo
 * @see IHistoryInfoDetails
 *  
 */
public enum ChangeType {
	
	/**Indicates the creation of an element.
	 * @see ChangeType
	 * @see ChangeType#DETACHED
	 * @see ChangeType#CHANGED 
	 * */
	NEW,
	/**Indicates the deletion of an element.
	 * @see ChangeType
	 * @see ChangeType#CHANGED 
	 * @see ChangeType#NEW 
	 * */
	DETACHED,
	/**Indicates that some modification has been made on the element.
	 * @see ChangeType
	 * @see ChangeType#DETACHED
	 * @see ChangeType#NEW 
	 * */
	CHANGED
}