/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.api;

import org.eclipse.emf.cdo.CDOObject;

/**
 * Represents the common service interface for all component lookup services.
 */
public interface ILookupService<T, V> {

	/**
	 * Returns with a component looked up in the passed in view with the
	 * specified unique identifier.
	 * 
	 * @param id
	 *            the unique identifier of the component.
	 * @param view
	 *            the view that stores the state of the component.
	 *            
	 * @return the looked up component. May return with {@code null} if no such
	 *         component exists in that view with the passed in ID.
	 */
	T getComponent(final String id, V view);

	/**
	 * Returns {@code true} if the component exists on a particular branch with the given unique ID.
	 * @param branchPath the path uniquely identifying the branch where the existence check has to be performed.
	 * @param id the unique ID of the component.
	 * @return {@code true} if the component exists. Otherwise returns with {@code false}.
	 */
	boolean exists(final IBranchPath branchPath, final String id);
	
	/**
	 * Returns with a lightweight representation of a {@link IComponent component} identified by the passed in ID.
	 * May return with {@code null} if no such component exists in the cached store. In this case clients should call 
	 * the {@link #getComponent(K, T)} method and make sure that searched component is just not cached or does not exists at all.
	 * 
	 * <b>Note: this method is meant to be called on the server side ONLY.</b>
	 * 
	 * @param id the unique identifier of the terminology independent component.
	 * @return the lightweight terminology component or {@code null} if no such component exists in the cached store.
	 */
	IComponent<String> getComponent(final IBranchPath branchPath, final String id);
	
	/**
	 * Returns with the unique storage identifier for a terminology independent specified by the component identifier argument
	 * on a given branch.
	 * <p><b>NOTE:&nbsp;</b>May return with {@code -1L}. Clients must check the returning storage key.
	 * @param branchPath the path uniquely identifying the branch where the lookup has to be performed. 
	 * @param id the unique identifier of component which storage key should be find.
	 * @return the unique storage key of the component.
	 */
	long getStorageKey(final IBranchPath branchPath, final String id);
	
	/**
	 * Extracts the unique business identifier from the specified component.
	 * 
	 * @param component
	 *            the component to inspect
	 *            
	 * @return the business key of the component
	 */
	String getId(final CDOObject component);
}
