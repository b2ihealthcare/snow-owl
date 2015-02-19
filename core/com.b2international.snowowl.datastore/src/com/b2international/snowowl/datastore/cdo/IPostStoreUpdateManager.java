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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

import com.b2international.snowowl.datastore.IPostStoreUpdateListener;

/**
 * Post store update manager representation.
 */
public interface IPostStoreUpdateManager {

	Object CHANGE_MANAGER_FAMILY = new Object();
	
	/**
	 * Delegates to {@link #addPostStoreUpdateListener(IPostStoreUpdateListener, boolean)} with {@code true} as the second argument.
	 * 
	 * @param listener a post store update listener.
	 */
	void addPostStoreUpdateListener(final IPostStoreUpdateListener listener);

	/**
	 * Adds a listener for post store update changes in this CDO change manager service.
	 * Has no effect is an identical listener is already registered.
	 * @param listener a post store update listener.
	 * @param activeBranchOnly {@code true} if the listener should only be notified of changes on the current branch, {@code false} otherwise
	 * @see IPostStoreUpdateListener
	 */
	void addPostStoreUpdateListener(final IPostStoreUpdateListener listener, final boolean activeBranchOnly);
	
	/**
	 * Removes a given a listener for post store update changes from this CDO change manager service.
	 * Has no effect is an identical listener is not registered or the listener is {@code null}.
	 * @param listener a post store update listener.
	 * @see IPostStoreUpdateListener
	 */
	void removePostStoreUpdateListener(final IPostStoreUpdateListener listener);

	/**
	 * Notifies listeners about a backend modification.
	 * @param invalidationEvent commit information representing the changes made in the backend. 
	 */
	void notifyListeners(final CDOCommitInfo invalidationEvent);

	/**
	 * Notifies all listeners without an incoming modifications; useful for triggering an application-wide UI refresh. 
	 */
	void notifyListeners();
}