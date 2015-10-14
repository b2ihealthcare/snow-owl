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
package com.b2international.snowowl.core.domain;

import java.util.List;

/**
 * Represents a partial list of components, in which the total number of components is stored separately.
 * 
 * @param <C> the component type
 */
public interface IComponentList<C> {

	/**
	 * Returns the number of total members of this list, which is the upper limit of the number of elements in {@link #getMembers()}.
	 *   
	 * @return the number of total members of this list
	 */
	int getTotalMembers();

	/**
	 * Returns the partial list of members for this component list.
	 * 
	 * @return the partial list of members
	 */
	List<C> getMembers();
}
