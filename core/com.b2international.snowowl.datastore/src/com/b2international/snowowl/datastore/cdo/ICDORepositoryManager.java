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

/**
 * Service interface for managing {@link ICDORepository CDO repositories} and their lifecycle.
 *
 */
public interface ICDORepositoryManager {

	/**Disconnects all users from the all managed repositories.*/
	void disconnectAll(final ISessionOperationCallback... callbacks);
	
	/**Disconnects all users from all managed repositories except ones those unique IDs are specified.*/
	void disconnect(final Iterable<String> userId, final ISessionOperationCallback... callbacks);

	/**Sends a message to all users from the all managed repositories.*/
	void sendMessageToAll(final String message, final ISessionOperationCallback... callbacks);
	
	/**Sends a message to all users based on the subset of user IDs from all managed repositories.*/
	void sendMessageTo(final String message, final Iterable<String> userId, final ISessionOperationCallback... callbacks);
	
	/**Represents a {@link CDORemoteSession remote session} operation callback.*/
	public static interface ISessionOperationCallback {
		
		/**Invoked when any arbitrary operation was performed on the remote session.*/
		void done(final CDORemoteSession session);
		
	}
	
	
}