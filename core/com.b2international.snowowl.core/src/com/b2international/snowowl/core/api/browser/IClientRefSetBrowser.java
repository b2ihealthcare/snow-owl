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

import com.b2international.snowowl.core.api.IComponent;


/**
 * Interface for browsing a hierarchy of reference sets.
 * 
 * 
 * @param <R> the reference set type
 * @param <C> the concept type
 * @param <K> the concept unique identifier type 
 */
public interface IClientRefSetBrowser<R extends IComponent<K>, C extends IComponent<K>, K> {

	/**
	 * Returns with a collection of IDs for all reference sets. 
	 * @return a collection of reference set IDs.
	 */
	Collection<K> getAllRefSetIds();
	
	/**
	 * Returns the number of reference set members in the reference set identified by the specified identifier.
	 * 
	 * @param refsetId
	 * @return the number of members in the specified reference set
	 */
	int getMemberCount(final K refsetId);
	
	/**
	 * Returns the number of active reference set members in the reference set identified by the specified identifier.
	 * 
	 * @param refsetId
	 * @return the number of active members in the specified reference set
	 */
	int getActiveMemberCount(final K refSetId);

	/**
	 * Returns the member concepts of the reference set identified by the specified identifier.
	 * 
	 * @param refsetId
	 * @return the member concepts of the specified reference set
	 */
	Collection<C> getMemberConcepts(final K refsetId);
	
	/**
	 * Returns the member concept IDs of the reference set identified by the specified identifier.
	 * 
	 * @param refsetId
	 * @return the member concept IDs of the specified reference set
	 */
	Collection<K> getMemberConceptIds(final K refsetId);

	/**
	 * Returns the lightweight representation of the reference set identified by the specified identifier.
	 * 
	 * @param refSetId
	 * @return the specified reference set
	 */
	R getRefSet(final K refSetId);

	/**
	 * Returns all the lightweight reference set representations known by this browser.
	 * 
	 * @return all reference sets
	 */
	Iterable<R> getRefsSets();
	
	/**
	 * Returns true, when the reference set with the specified unique identifier contains at least one member, which points to
	 * the component identified by the specified unique identifier.
	 * 
	 * @param refSetId the reference set's unique identifier
	 * @param componentId the component's unique identifier
	 * @return true if the component is referenced by at least one member of the reference set, false otherwise
	 */
	boolean isReferenced(K refSetId, K componentId);
}