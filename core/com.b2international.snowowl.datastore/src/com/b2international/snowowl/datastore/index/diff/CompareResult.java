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
package com.b2international.snowowl.datastore.index.diff;

import java.io.Serializable;

import com.b2international.commons.hierarchy.Derivation;
import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Representation of terminology independent change set as the outcome of a version compare
 * operation between two points of time. 
 */
public interface CompareResult extends Serializable, Iterable<NodeDiff> {

	/**
	 * Returns with a derivation of {@link NodeDiff nodes} representing a change set between two 
     * points of time for a terminology.
	 * @return a collection of changed nodes.
	 */
	Derivation<NodeDiff> getChanges();
	
	/**Returns with the source branch path for the compare.*/
	IBranchPath getSourcePath();
	
	/**Returns with the target branch path for the compare.*/
	IBranchPath getTargetPath();
	
	/**Returns with the repository UUID for a given terminology.*/
	String getRepositoryUuid();
	
	/**Returns with the configuration used for the version compare.*/
	VersionCompareConfiguration getConfiguration();
	
	/**Returns with the size of the contained {@link NodeDiff node differences}.*/
	int size();
	
	/**Returns with the statistics for the compare result.*/
	CompareStatistics getStatistics();
	
}