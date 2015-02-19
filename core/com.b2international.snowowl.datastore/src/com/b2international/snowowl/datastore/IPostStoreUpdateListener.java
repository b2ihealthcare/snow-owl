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
package com.b2international.snowowl.datastore;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

/**
 * This {@code IPostStoreUpdateListener} is informed when all available store has been modified
 * and updated after a CDO invalidation event. Clients are guaranteed that all available
 * store has been persisted in sync and in consistent state.
 * <p>
 * In oder to get notified the listener must be registered via the {@link PostStoreUpdateManager#addPostStoreUpdateListener(IPostStoreUpdateListener) add listener}.
 * Clients should unregister themselves if no longer what to be informed about a store change event 
 * by {@link PostStoreUpdateManager#removePostStoreUpdateListener(IPostStoreUpdateListener) remove listener} method.</p>
 * <p>
 * The notification order of post save listeners is unspecified.</p>
 * <p>
 * Clients may implement this interface.
 * </p>
 * @see PostStoreUpdateManager
 * @see PostStoreUpdateManager#addPostStoreUpdateListener(IPostStoreUpdateListener)
 * @see PostStoreUpdateManager#removePostStoreUpdateListener(IPostStoreUpdateListener)
 */
public interface IPostStoreUpdateListener {

	/**
	 * Fires when all of the available store (RDBMS, Lucene and all other ephemeral store) has been updated after a CDO invalidation event.
	 * When fires clients are guaranteed that all available store are in sync and are in consistent state.
	 * @param commitInfo the commit info instance that triggered the store updates.
	 * @see IPostStoreUpdateListener
	 * @see PostStoreUpdateManager
	 */
	void storeUpdated(final CDOCommitInfo commitInfo);
	
}