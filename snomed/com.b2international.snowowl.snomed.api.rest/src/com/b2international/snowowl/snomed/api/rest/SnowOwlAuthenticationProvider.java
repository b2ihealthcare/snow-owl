/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import javax.security.auth.login.LoginException;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.b2international.snowowl.api.IAuthenticationService;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.google.common.collect.ImmutableList;

/**
 * Provides HTTP basic authentication in the RESTful API. Uses a given {@link IAuthenticationService} to do the actual authentication and responds
 * with an {@link Authentication} according to the result of the auth.
 * 
 * @since 1.0
 */
public class SnowOwlAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
		final String username = authentication.getPrincipal().toString();
		final Object credentials = authentication.getCredentials();
		final String password = credentials == null ? null : credentials.toString();

		try {
			ApplicationContext.getServiceForClass(IApplicationSessionManager.class).authenticate(username, password);
			return new UsernamePasswordAuthenticationToken(username, password, ImmutableList.of(new SimpleGrantedAuthority("ROLE_USER")));
		} catch (LoginException e) {
			throw new BadCredentialsException("Incorrect user name or password.");
		}
	}

	@Override
	public boolean supports(final Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
	
}
