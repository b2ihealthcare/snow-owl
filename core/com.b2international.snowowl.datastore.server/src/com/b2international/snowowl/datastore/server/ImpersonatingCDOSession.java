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
package com.b2international.snowowl.datastore.server;

import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import com.b2international.snowowl.datastore.cdo.ImpersonatingSessionProtocol;

/**
 * Session for impersonating a user on the server side
 * @see ImpersonatingSessionProtocol
 */
@SuppressWarnings("restriction")
public class ImpersonatingCDOSession extends Session {

	/**
	 * Creates a impersonating session the server side.
	 * @param manager session manager.
	 * @param protocol the protocol.
	 * @param sessionID the ID of the session.
	 * @param userID unique ID of the user.
	 */
	public ImpersonatingCDOSession(final InternalSessionManager manager, final ISessionProtocol protocol, final int sessionID, final String userID) {
		super(manager, protocol, sessionID, userID);
	}

	
}