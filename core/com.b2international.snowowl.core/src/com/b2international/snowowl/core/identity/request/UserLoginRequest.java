/*
 * Copyright 2019-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.identity.request;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.authorization.AuthorizedRequest;
import com.b2international.snowowl.core.authorization.Unprotected;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.RequestHeaders;
import com.b2international.snowowl.core.identity.AuthorizationHeaderVerifier;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.identity.JWTSupport;
import com.b2international.snowowl.core.identity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 7.2
 */
@Unprotected
public final class UserLoginRequest implements Request<ServiceProvider, User> {

	private static final long serialVersionUID = 2L;

	@JsonProperty
	private String username;
	
	private String password;
	
	private String token;

	UserLoginRequest(String username, String password, String token) {
		this.username = username;
		this.password = password;
		this.token = token;
	}
	
	@Override
	public User execute(ServiceProvider context) {
		
		User user = Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password) ? null : context.service(IdentityProvider.class).auth(username, password);
		if (user == null && !Strings.isNullOrEmpty(token)) {
			user = context.service(AuthorizationHeaderVerifier.class).authJWT(token);
		}
		if (user == null) {
			// check if there is an authorization token header and use that to login the user 
			final RequestHeaders requestHeaders = context.service(RequestHeaders.class);
			final String authorizationToken = requestHeaders.header(AuthorizedRequest.AUTHORIZATION_HEADER);
			user = context.service(AuthorizationHeaderVerifier.class).auth(authorizationToken);
		}
		
		if (user == null) {
			throw new BadRequestException("Incorrect username or password.");
		}
		
		// generate and attach a token
		return user.withAccessToken(context.service(JWTSupport.class).generate(user));
	}
	
}
