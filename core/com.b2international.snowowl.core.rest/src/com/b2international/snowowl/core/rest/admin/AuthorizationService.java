/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.admin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.Credentials;
import com.b2international.snowowl.core.identity.Token;
import com.b2international.snowowl.core.identity.request.UserRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.2
 */
@Tag(description = "Authorization", name = "authorization")
@RestController
@RequestMapping(produces={ MediaType.APPLICATION_JSON_VALUE })
public class AuthorizationService extends AbstractRestService {

	@PostMapping("/login")
	public Promise<Token> login(
			@Parameter(name = "credentials", description = "The user credentials.", required = true) 
			@RequestBody Credentials credentials) {
		return UserRequests.prepareLogin()
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.buildAsync()
				.execute(getBus());
	}
	
}
