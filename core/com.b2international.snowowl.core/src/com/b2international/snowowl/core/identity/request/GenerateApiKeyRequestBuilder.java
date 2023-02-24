/*
 * Copyright 2019-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 7.2
 */
public final class GenerateApiKeyRequestBuilder extends BaseRequestBuilder<GenerateApiKeyRequestBuilder, ServiceProvider, User> implements SystemRequestBuilder<User> {

	private String username;
	private String password;
	private String token;
	private String expiration;
	
	public GenerateApiKeyRequestBuilder setUsername(String username) {
		this.username = username;
		return getSelf();
	}
	
	public GenerateApiKeyRequestBuilder setPassword(String password) {
		this.password = password;
		return getSelf();
	}
	
	public GenerateApiKeyRequestBuilder setToken(String token) {
		this.token = token;
		return getSelf();
	}
	
	public GenerateApiKeyRequestBuilder setExpiration(String expiration) {
		this.expiration = expiration;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, User> doBuild() {
		GenerateApiKeyRequest req = new GenerateApiKeyRequest(username, password, token);
		req.setExpiration(expiration);
		return req;
	}

}
