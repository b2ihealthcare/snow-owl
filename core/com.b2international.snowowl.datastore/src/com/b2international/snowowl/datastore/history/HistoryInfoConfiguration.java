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
 * Representation of a history info configuration.
 * <br>Encapsulates all information required for getting the history of
 * a terminology independent component. 
 *
 */
public interface HistoryInfoConfiguration {

	/**
	 * Returns with the unique primary key of a terminology independent
	 * component.
	 * @return the primary key of the component.
	 */
	long getStorageKey();

	/**
	 * Returns with the terminology/content specific ID of the component.
	 * @return the terminology specific component ID.
	 */
	String getComponentId();

	/**
	 * Returns with the application specific terminology component ID
	 * of a component. 
	 * @return the terminology component ID of a component.
	 */
	String getTerminologyComponentId();
	
	/**
	 * Returns with the active branch path of the terminology/content
	 * where the underlying terminology independent component belongs to.
	 * @return the active branch path configuration for the terminology/content.
	 */
	IBranchPath getBranchPath();
	
	/**
	 * Null implementation of a historical information configuration.
	 */
	HistoryInfoConfiguration NULL_IMPL = NullHistoryInfoConfiguration.INSTANCE;
	
}