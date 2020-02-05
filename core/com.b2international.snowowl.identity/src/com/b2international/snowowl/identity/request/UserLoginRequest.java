/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.authorization.Unprotected;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.JWTGenerator;
import com.b2international.snowowl.identity.Token;
import com.b2international.snowowl.identity.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.2
 */
@Unprotected
public final class UserLoginRequest implements Request<ServiceProvider, Token> {

	@NotEmpty
	@JsonProperty
	private String username;
	
	@NotEmpty
	private String password;

	UserLoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public Token execute(ServiceProvider context) {
		final User user = context.service(IdentityProvider.class).auth(username, password);
		if (user == null) {
			throw new UnauthorizedException("Incorrect username or password.");
		}
		return new Token(context.service(JWTGenerator.class).generate(user));
	}
	
}
