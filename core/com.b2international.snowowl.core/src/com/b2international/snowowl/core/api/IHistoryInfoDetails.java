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

import com.b2international.commons.ChangeKind;

/**
 * Represents history information details.
 * 
 */
public interface IHistoryInfoDetails {

	/**
	 * Returns the type of the affected component.
	 * 
	 * @return the type of the affected component
	 */
	String getComponentType();

	/**
	 * Returns the human readable description of the change.
	 * 
	 * @return the human readable description of the change
	 */
	String getDescription();

	/**
	 * Returns the type of the change.
	 * 
	 * @return the type of the change
	 */
	ChangeKind getChangeType();
	
	/**Represents a history info details that has to be ignored by clients.
	 *<p>Always throws {@link UnsupportedOperationException} when accessing any of its accessors, 
	 *so it's the client's responsibility to ignore it.*/
	IHistoryInfoDetails IGNORED_DETAILS = new IHistoryInfoDetails() {
		
		@Override
		public String getDescription() {
			throw new UnsupportedOperationException("Implementation error.");
		}
		
		@Override
		public String getComponentType() {
			throw new UnsupportedOperationException("Implementation error.");
		}
		
		@Override
		public ChangeKind getChangeType() {
			throw new UnsupportedOperationException("Implementation error.");
		}
	};
}