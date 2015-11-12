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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;

/**
 * @since 4.5
 */
public class ComponentIdentifierServiceAuthenticatorTest {
	
	private String username;
	private String password;

	private ComponentIdentifierServiceAuthenticator authenticator;

	@Before
	public void init() {
		SnomedCoreConfiguration conf = new SnomedCoreConfiguration();
		conf.setCisUrl("http://107.170.101.181");
		conf.setCisPort("3000");
		conf.setCisContextRoot("api");
		conf.setCisClientSoftwareKey("Snow Owl dev. tests");

		username = "snowowl-dev-b2i";
		password = "hAAYLYMX5gc98SDEz9cr";

		final ComponentIdentifierServiceClient client = new ComponentIdentifierServiceClient(conf);
		this.authenticator = new ComponentIdentifierServiceAuthenticator(client);
	}

	@Test
	public void whenLoggingInWithValidCredentials_ThenTokenShouldBeCreated() {
		final String token = authenticator.login(username, password);
		assertFalse(StringUtils.isEmpty(token));

		authenticator.logout(token);
	}

	@Test
	public void whenLoggingInWithInvalidCredentials_ThenBadRequestExcecptionShouldBeThrowed() {
		try {
			authenticator.login(username, "abc");
			fail("BadRequestException should be thrown in case of invalid credentials.");
		} catch (BadRequestException e) {
			// ignore, correct behavior
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown instead of BadRequestException. Exception class: %s.", e.getClass()));
		}
	}

	@Test
	public void whenLoggingOutWithValidToken_NoExpcetionShouldOccurred() {
		final String token = authenticator.login(username, password);

		try {
			authenticator.logout(token);
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown when logging out. Exception class: %s.", e.getClass()));
		}
	}

}
