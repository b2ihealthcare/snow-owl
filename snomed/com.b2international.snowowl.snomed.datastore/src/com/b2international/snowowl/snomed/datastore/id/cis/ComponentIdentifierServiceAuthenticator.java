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
package com.b2international.snowowl.snomed.datastore.id.cis;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

/**
 * Class to authenticate against the CIS.
 * 
 * @since 4.5
 */
public class ComponentIdentifierServiceAuthenticator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentIdentifierServiceAuthenticator.class);

	private final ComponentIdentifierServiceClient client;

	private final ObjectMapper mapper = new ObjectMapper();

	public ComponentIdentifierServiceAuthenticator(ComponentIdentifierServiceClient client) {
		this.client = client;
	}

	public String login(final String username, final String password) {
		Preconditions.checkNotNull(username, "Username must not be null.");
		Preconditions.checkNotNull(password, "Password must not be null.");

		LOGGER.info("Logging in to Component Identifier service.");

		HttpPost request = null;

		try {
			final Credentials credentials = new Credentials(username, password);
			request = client.httpPost("login", mapper.writeValueAsString(credentials));

			final String response = client.execute(request);

			return mapper.readValue(response, Token.class).getToken();
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while logging in.", e);
		} finally {
			if (null != request)
				client.release(request);
		}
	}

	public void logout(final String token) {
		LOGGER.info("Logging out from Component Identifier service.");

		HttpPost request = null;

		try {
			request = client.httpPost("logout", mapper.writeValueAsString(new Token(token)));
			client.execute(request);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while logging out.", e);
		} finally {
			if (null != request)
				client.release(request);
		}
	}

}
