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

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Provides a terminology independent representation of a {@link ExtendedComponent component}.
 *
 */
public interface ExtendedComponentProvider {

	/**
	 * Returns with the {@link ExtendedComponent component} given with its unique storage key argument from the 
	 * specified branch. Could return with {@code null} if the component does not exist.  
	 * @param branchPath the branch path.
	 * @param storageKey the unique storage key of the component.
	 * @return the {@link ExtendedComponent component}.
	 */
	@Nullable ExtendedComponent getExtendedComponent(final IBranchPath branchPath, final long storageKey);
	
}