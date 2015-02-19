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
package com.b2international.snowowl.datastore.personalization;

import java.util.Set;

import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;

/**
 * An interface for managing a set of components for a particular user.
 * 
 */
public interface IComponentSetManager extends IQuickSearchContentProvider {

	/**
	 * 
	 * @param id
	 * @param terminologyComponentId
	 * @param userId
	 */
	public void registerComponent(final ComponentIdentifierPair<String> componentIdentifierPair, final String userId);
	
	/**
	 * 
	 * @param componentIdentifierPair
	 * @param userId
	 */
	public void unregisterComponent(final ComponentIdentifierPair<String> componentIdentifierPair, final String userId);
	
	/**
	 * 
	 * @param userId
	 * @param limit
	 * @return
	 */
	public Set<ComponentIdentifierPair<String>> getComponentsForUser(final String userId, final int limit);
	
	/**
	 * 
	 * @param userId
	 */
	public void clearAllComponentsForUser(final String userId);
}