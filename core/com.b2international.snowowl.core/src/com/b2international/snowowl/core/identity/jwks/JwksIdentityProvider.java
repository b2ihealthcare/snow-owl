/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.identity.jwks;

import java.util.Collection;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.JWTCapableIdentityProvider;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.identity.Users;

/**
 * @since 8.8.0
 */
public final class JwksIdentityProvider extends JWTCapableIdentityProvider<JwksIdentityProviderConfig> {

	static final String TYPE = "jwks";
	
	public JwksIdentityProvider(JwksIdentityProviderConfig conf) {
		super(conf);
	}
	
	@Override
	protected String getType() {
		return TYPE;
	}
	
	@Override
	public User auth(String username, String password) {
		return null;
	}

	@Override
	public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
		return Promise.immediate(new Users(limit, 0));
	}

	@Override
	public String getInfo() {
		return String.join("@", TYPE, getConfiguration().getJwksUrl());
	}
	
}
