/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.merge;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since 4.7
 */
@JsonDeserialize(as = MergeConflictImpl.class)
public interface MergeConflict {

	public enum ConflictType {
		CONFLICTING_CHANGE,
		DELETED_WHILE_CHANGED,
		CHANGED_WHILE_DELETED,
		HAS_MISSING_REFERENCE,
		CAUSES_MISSING_REFERENCE
	}
	
	/**
	 * Returns the unique identifier of the component that induced this merge conflict.
	 * 
	 * @return unique identifier of the component
	 */
	String getComponentId();
	
	/**
	 * Returns the type of the component that induced this merge conflict.
	 * 
	 * @return the type of the component
	 */
	String getComponentType();
	
	/**
	 * Returns a list of attributes represented as {@link String}s, all of which was involved in causing the conflict.
	 * 
	 * @return the list of attributes involved in causing the conflict
	 */
	List<ConflictingAttribute> getConflictingAttributes();
	
	/**
	 * Returns the type of the conflict, which reflects the nature of problem causing the merge to fail.
	 * 
	 * @return the conflict type
	 */
	ConflictType getType();
	
	/**
	 * Returns an interpretable message about the conflict, built using it's properties.
	 * 
	 * @return a summarizing, interpretable message about the conflict
	 */
	String getMessage();
	
}
