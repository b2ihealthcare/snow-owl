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
package com.b2international.snowowl.core.merge;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since 4.7
 */
@JsonDeserialize(as = ConflictingAttributeImpl.class)
public interface ConflictingAttribute extends Serializable {

	/**
	 * String representation of the attribute that induced - or was involved in - the conflict
	 * 
	 * @return
	 */
	String getProperty();
	
	/**
	 * The value of the property
	 * 
	 * @return
	 */
	String getValue();
	
	/**
	 * If the property was changed and it is possible to extract the old value, then this will return it.
	 * 
	 * @return
	 */
	String getOldValue();
	
	/**
	 * Converts a {@link ConflictingAttribute} instance to a human readable form. Default conflict message uses this pattern as well.
	 * 
	 * @return
	 */
	String toDisplayName();
	
}
