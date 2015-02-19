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
package com.b2international.snowowl.datastore.server;

import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Interface for providing {@link EClass}.
 */
public interface IEClassProvider {

	/**
	 * Returns with the {@link EClass} of an object identified by a unique storage key.
	 * @param branchPath the branch path.
	 * @param storageKey the unique storage key.
	 * @return the {@link EClass}.
	 */
	EClass getEClass(IBranchPath branchPath, final long storageKey);
	
	/**
	 * Returns with the priority of the implementation. The less the more important. 
	 * @return the priority.
	 */
	int getPriority();
	
	/**
	 * Returns with the UUID of the repository where the current provider works on.  
	 * @return the repositroy UUID.
	 */
	String getRepositoryUuid();
	
}