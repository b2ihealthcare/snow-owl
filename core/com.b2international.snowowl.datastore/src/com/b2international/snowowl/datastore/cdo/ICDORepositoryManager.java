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

import org.eclipse.emf.cdo.server.IRepository.Handler;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;

import com.b2international.commons.emf.NsUriProvider;

/**
 * Service interface for managing {@link ICDORepository CDO repositories} and their lifecycle.
 *
 */
public interface ICDORepositoryManager extends ICDOContainer<ICDORepository> {

	/**Sends a message to all users from the all managed repositories.*/
	void sendMessageToAll(final String message, final ISessionOperationCallback... callbacks);
	
	/**Sends a message to all users based on the subset of user IDs from all managed repositories.*/
	void sendMessageTo(final String message, final Iterable<String> userId, final ISessionOperationCallback... callbacks);
	
	/**Clears the server side revision cache by clearing all soft reference caches in all managed repositories.*/
	void clearRevisionCache();
	
	/**Adds the the given handler to all managed repositories.*/
	void addRepositoryHandler(final Handler handler);
	
	/**Returns with the namespace URI provider for the repository.*/
	NsUriProvider getNsUriProvider(final String uuid);
	
	/**Represents a {@link CDORemoteSession remote session} operation callback.*/
	public static interface ISessionOperationCallback {
		
		/**Invoked when any arbitrary operation was performed on the remote session.*/
		void done(final CDORemoteSession session);
		
	}
	
	
}