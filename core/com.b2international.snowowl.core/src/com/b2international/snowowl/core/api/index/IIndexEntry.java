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

import java.io.Serializable;

import com.b2international.snowowl.core.api.IComponent;

/**
 * Common interface for elements which can be returned from an index.
 *
 */
public interface IIndexEntry extends IComponent<String>, Serializable {

	/**
	 * @return the calculated fitness of this entry for a particular query (may be {@code NaN}, {@code 0.0f} or some
	 * other, not meaningful constant value; may be different for the same element in different queries)
	 */
	float getScore();

	/**
	 * @return the storage key with which the original object can be retrieved from the persistent store
	 */
	long getStorageKey();
	
	/**
	 * Sets the label for this component.
	 * If possible, avoid this method.
	 * @param label
	 * @deprecated
	 */
	void setLabel(String label);
}