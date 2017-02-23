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
package com.b2international.snowowl.datastore.history;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Encapsulates all information required for getting the history of a terminology independent component.
 */
public interface HistoryInfoConfiguration {
	
	/**
	 * Returns the storage key of a terminology independent component.
	 */
	long getStorageKey();

	/**
	 * Returns the component identifier.
	 */
	String getComponentId();

	/**
	 * Returns the application-specific terminology component identifier.
	 */
	String getTerminologyComponentId();
	
	/**
	 * Returns the active branch path from where history should be computed.
	 */
	IBranchPath getBranchPath();
	
	/**
	 * Returns a Cache mapping between conceptId and  storageKey.
	 */
	StorageKeyCache getStorageKeyCache();
}
