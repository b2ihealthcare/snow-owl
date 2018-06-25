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

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.AlreadyLoggedInException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * {@link CDOConnectionFactory} implementation for activating a {@link CDOConnectionManager}
 * instance and registering it to the {@link ApplicationContext application context} under the hood
 * to establish a connection between a CDO server and client.
 *
 */
public class CDOConnectionFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CDOConnectionFactory.class);
	
	public void connect(final String username, final String password) throws SnowowlServiceException {
		connect(new CDOConnectionManager(username, password));
	}

	/*tries to activate the connection manager argument. in case of successful activation argument will be registered to the application context.*/
	private void connect(final CDOConnectionManager manager) throws SnowowlServiceException {
		
		try {
			
			Preconditions.checkNotNull(manager, "Connection manager argument cannot be null.");
			manager.activate();
			ApplicationContext.getInstance().registerService(CDOConnectionManager.class, manager);
			
		} catch (final Throwable t) {
			
			if (t instanceof RemoteException) {
				
				if (t.getCause() instanceof IllegalStateException) { //assuming root resource of the repository is not initialized yet (server is in startup phase)
					
					LOGGER.error("The server is not running! Please contact the administrator.", t.getCause());
					throw new SnowowlServiceException("The server is not running! Please contact the administrator.");
					
				} else if (t.getCause() instanceof SecurityException) {
					
					LOGGER.error(t.getCause().getMessage(), t.getCause());
					throw new SnowowlServiceException(t.getCause().getMessage());
					
				}
				
			} else if (t instanceof RuntimeException) {
			
				if (t instanceof LifecycleException) {
                    throw new SnowowlServiceException("The server is not running! Please contact the administrator.");
				}
				
				if (t.getCause() instanceof SnowowlServiceException) {
					
					final SnowowlServiceException sse = (SnowowlServiceException) t.getCause();
					
					if (sse.getCause() instanceof SecurityException) {
						
						if (sse.getCause() instanceof AlreadyLoggedInException) {
							throw new SnowowlServiceException(sse.getCause().getMessage());
						}
						
						final SecurityException se = (SecurityException) sse.getCause();
						if (se.getCause() instanceof LoginException && se.getCause().getCause() instanceof LoginException) {
						
							if (se.getCause().getCause() instanceof FailedLoginException) {
								final String message = StringUtils.getLine(se.getCause().getCause().getMessage(), "\n", 0);
								throw new SnowowlServiceException(message);
							}
							
							//check for communication exception traces
							if (Strings.nullToEmpty(se.getCause().getCause().getMessage()).startsWith("Cannot bind to LDAP server.\njavax.naming.CommunicationException:")) {
								throw new SnowowlServiceException("Authentication failed. Cannot bind to LDAP server. Please try again.");
							}
							
						}

						if (se.getCause() == null && !StringUtils.isEmpty(se.getMessage())) {
							throw new SnowowlServiceException(se.getMessage());
						}
						throw new SnowowlServiceException("Authentication failed.");
					}
					
				}
				
			} else if (t.getCause() instanceof SnowowlServiceException) {
				
				if (t.getCause().getCause() instanceof LifecycleException) {
					throw new SnowowlServiceException("The server is not running! Please contact the administrator.");
				} else {
					throw (SnowowlServiceException) t.getCause();
				}
				
			} else if (t.getCause() instanceof LifecycleException) {
				
				throw new SnowowlServiceException("The server is not running! Please contact the administrator.");
				
				
			} else if (t.getCause() instanceof Throwable) {
				
				LOGGER.error("Unexpected error while connecting to server.", t.getCause());
				throw new SnowowlServiceException("Error while connecting: " + StringUtils.getLine(t.getCause().getMessage(), "\n", 0));
				
			}
				
			LOGGER.error("Unexpected error while connecting to server.", t);
			throw new SnowowlServiceException("An unexpected error occurred while connecting to the server.");
		}
	}
}