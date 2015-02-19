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
package com.b2international.commons.dynamic;

/**
 * Represents an immutable list that associates non-negative integer indexes
 * with {@link DynamicValue}s. Can also be viewed as a map where only numeric
 * keys are permitted.
 * 
 */
public interface DynamicList extends DynamicMap {

	/**
	 * Returns the mapped value for the specified index, or a value wrapping
	 * <code>null</code> if no such mapping exists.
	 * 
	 * @param idx
	 *            the requested index
	 * 
	 * @return the associated value, or {@link DynamicValue#MISSING} if the given
	 *         index is out of bounds
	 */
	DynamicValue get(int idx);
	
	/**
	 * Returns the mapped value for the specified key, or a value wrapping
	 * <code>defaulValue</code> if no such mapping exists.
	 * 
	 * @param key
	 *            the requested key
	 * 
	 * @param defaultValue
	 *            the value to wrap if no mapping exists
	 * 
	 * @return the associated value, or <code>defaultValue</code> if the given
	 *         index is out of bounds
	 */
	DynamicValue get(int idx, Object defaultValue);
}