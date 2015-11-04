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
package com.b2international.snowowl.core.api;

import java.io.Serializable;

import com.b2international.snowowl.core.annotations.Client;

/**
 * Represents the common service interface for all component lookup services.
 * 
 * 
 * @param <K>
 *            serializable unique identifier of the component
 * @param <T>
 *            type of the searched component
 * @param <V>
 *            should be CDO View or its subclass.
 */
public interface ILookupService<K extends Serializable, T, V> {

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
	T getComponent(final K id, final V view);

	/**
	 * Returns the requested component with the identifier {@code id} on the currently active branch in the associated repository.
	 * 
	 * @param id the unique identifier of the component (may not be {@code null})
	 * @return the resolved component, or {@code null}
	 */
	@Client
	IComponent<K> getComponent(final K id);
	
	/**
	 * Returns {@code true} if the component exists on a particular branch with the given unique ID.
	 * @param branchPath the path uniquely identifying the branch where the existence check has to be performed.
	 * @param id the unique ID of the component.
	 * @return {@code true} if the component exists. Otherwise returns with {@code false}.
	 */
	boolean exists(final IBranchPath branchPath, final K id);
	
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
	IComponent<K> getComponent(final IBranchPath branchPath, final K id);
	
	/**
	 * Returns with the unique storage identifier for a terminology independent specified by the component identifier argument
	 * on a given branch.
	 * <p><b>NOTE:&nbsp;</b>May return with {@code -1L}. Clients must check the returning storage key.
	 * @param branchPath the path uniquely identifying the branch where the lookup has to be performed. 
	 * @param id the unique identifier of component which storage key should be find.
	 * @return the unique storage key of the component.
	 */
	long getStorageKey(final IBranchPath branchPath, final K id);
}
