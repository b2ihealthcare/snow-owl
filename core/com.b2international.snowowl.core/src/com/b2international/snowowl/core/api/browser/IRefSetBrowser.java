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
import com.b2international.snowowl.core.api.IComponent;

/**
 * Interface for browsing the reference set hierarchy on the server side.
 * @param <R> - type of the reference set.
 * @param <C> - type of the components.
 * @param <K> - type of the unique component and reference set identifiers.
 */
public interface IRefSetBrowser<R extends IComponent<K>, C extends IComponent<K>, K> {

	/**
	 * Returns with a collection of IDs for all reference sets. 
	 * @param branchPath the branch path.
	 * @return a collection of reference set IDs.
	 */
	Collection<K> getAllRefSetIds(final IBranchPath branchPath);
	
	/**
	 * Returns the number of reference set members in the reference set identified by the specified identifier.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param refsetId
	 * @return the number of members in the specified reference set
	 */
	int getMemberCount(final IBranchPath branchPath, final K refsetId);

	/**
	 * Returns the number of active reference set members in the reference set identified by the specified identifier.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param refsetId
	 * @return the number of active members in the specified reference set
	 */
	int getActiveMemberCount(final IBranchPath branchPath, final K refSetId);

	/**
	 * Returns the member concepts of the reference set identified by the specified identifier.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param refsetId
	 * @return the member concepts of the specified reference set
	 */
	Collection<C> getMemberConcepts(final IBranchPath branchPath, final K refsetId);
	
	/**
	 * Returns the member concept IDs of the reference set identified by the specified identifier.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param refsetId
	 * @return the member concept IDs of the specified reference set
	 */
	Collection<K> getMemberConceptIds(final IBranchPath branchPath, final K refsetId);

	/**
	 * Returns the lightweight representation of the reference set identified by the specified identifier.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param refSetId
	 * @return the specified reference set
	 * @deprecated - use lookup services instead
	 */
	R getRefSet(final IBranchPath branchPath, final K refSetId);

	/**
	 * Returns all the lightweight reference set representations known by this browser.
	 *
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @return all reference sets
	 */
	Iterable<R> getRefsSets(final IBranchPath branchPath);
	
	/**
	 * Returns true, when the reference set with the specified unique identifier contains at least one member, which points to
	 * the component identified by the specified unique identifier.
	 * 
	 * @param refSetId the reference set's unique identifier
	 * @param componentId the component's unique identifier
	 * @return true if the component is referenced by at least one member of the reference set, false otherwise
	 */
	boolean isReferenced(final IBranchPath branchPath, K refSetId, K componentId);
}