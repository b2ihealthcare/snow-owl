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

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;

/**
 * Interface for creating CDO specific connection between server and client.
 * <p>See: 'connectionFactory' extension point.
 * 
 */
public interface ICDOConnectionFactory {

	/**
	 * Creates a {@link CDOConnection} object and connects to the the CDO repository specified by the registered 
	 * {@link ClientPreferences}. The user will be authenticated with the given username and password. The created connection 
	 * will be automatically registered to the {@link ApplicationContext} as service.
	 * @param username the username
	 * @param password the password
	 * @throws SnowowlServiceException if there was an error when connecting to CDO, or when the provided credentials are invalid
	 */
	void connect(final String username, final String password) throws SnowowlServiceException;
	
}