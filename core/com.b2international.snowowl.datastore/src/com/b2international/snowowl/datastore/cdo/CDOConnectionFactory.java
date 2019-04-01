/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.ICDOConnectionFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * {@link ICDOConnectionFactory} implementation for activating a {@link ICDOConnectionManager}
 * instance and registering it to the {@link ApplicationContext application context} under the hood
 * to establish a connection between a CDO server and client.
 *
 */
public class CDOConnectionFactory implements ICDOConnectionFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CDOConnectionFactory.class);
	
	private static final String COULD_NOT_ACTIVATE_PREFIX = "Could not activate TCPClientConnector";
	private static final String ALREADY_LOGGED_IN_PREFIX = "Already logged in";
	private static final String REPOSITORY_NOT_FOUND_PREFIX = "org.eclipse.emf.cdo.server.RepositoryNotFoundException: Repository not found: ";
	private static final String INCORRECT_USER_NAME_OR_PASSWORD = "Incorrect user name or password.";
	private static final String LOGIN_DISABLED = "Logging in for non-administrator users is temporarily disabled.";
	private static final String LDAP_CONNECTION_REFUSED = "Connection refused: connect";
	
	@Override
	public void connect(final String username, final String password) throws SnowowlServiceException {
		connect(new CDOConnectionManager(username, password));
	}

	/*tries to activate the connection manager argument. in case of successful activation argument will be registered to the application context.*/
	private void connect(final ICDOConnectionManager manager) throws SnowowlServiceException {
		
		try {
			
			Preconditions.checkNotNull(manager, "Connection manager argument cannot be null.");
			manager.activate();
			ApplicationContext.getInstance().registerService(ICDOConnectionManager.class, manager);
			
		} catch (final Throwable t) {
			
			final Throwable rootCause = Throwables.getRootCause(t);
			final String message = Strings.nullToEmpty(StringUtils.getLine(rootCause.getMessage(), "\n", 0))
					.replace("\r", "");
			LOGGER.error("Exception caught while connecting to the server.", t);
			
			// FIXME: "Sentiment analysis" for exception messages
			if (message.startsWith(COULD_NOT_ACTIVATE_PREFIX)) {
				throw new SnowowlServiceException("The server could not be reached. Please verify the connection URL.");
			} else if (message.startsWith(ALREADY_LOGGED_IN_PREFIX)) {
				throw new SnowowlServiceException("Another client with the same user is already connected to the server.");
			} else if (message.startsWith(REPOSITORY_NOT_FOUND_PREFIX)) {
				throw new SnowowlServiceException(String.format(
						"Server does not include support for required component store '%s'. Please verify the connection URL.",
						message.substring(REPOSITORY_NOT_FOUND_PREFIX.length())));
			} else if (message.startsWith(INCORRECT_USER_NAME_OR_PASSWORD)) {
				throw new SnowowlServiceException(message);
			} else if (message.startsWith(LOGIN_DISABLED)) {
				throw new SnowowlServiceException(message);
			} else if (message.startsWith(LDAP_CONNECTION_REFUSED)) {
				throw new SnowowlServiceException("The LDAP server could not be reached for authentication. Please contact the administrator.");
			} else {
				throw new SnowowlServiceException("An unexpected error occurred while connecting to the server. Please contact the administrator.");
			}
		}
	}
}
