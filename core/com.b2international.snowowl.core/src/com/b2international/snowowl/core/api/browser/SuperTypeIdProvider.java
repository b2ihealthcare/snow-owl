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
package com.b2international.snowowl.core.api.browser;

import java.util.Collection;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Represents a provider responsible for supplying direct ancestor IDs for
 * a terminology independent component.  
 */
public interface SuperTypeIdProvider<K> {

	/**
	 * Returns with the direct ancestor component IDs of the specified component.
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null}).
	 * @param conceptId the component ID.
	 * @return a collection of all ancestor component IDs.
	 */
	Collection<K> getSuperTypeIds(final IBranchPath branchPath, final K conceptId);
	
}