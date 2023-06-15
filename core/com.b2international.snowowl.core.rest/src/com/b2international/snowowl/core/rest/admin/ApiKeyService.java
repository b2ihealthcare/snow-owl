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
package com.b2international.snowowl.core.rest.admin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.identity.Credentials;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.identity.request.UserRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.CoreApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.2
 */
@Tag(description = "API Key", name = CoreApiConfig.API_KEY)
@RestController
@RequestMapping(produces={ MediaType.APPLICATION_JSON_VALUE })
public class ApiKeyService extends AbstractRestService {

	@Operation(deprecated = true, description = "Use the `POST /token` endpoint instead")
	@PostMapping("/login")
	public User login(
			@Parameter(name = "credentials", description = "The user credentials.", required = true) 
			@RequestBody Credentials credentials) {
		try {
			return UserRequests.prepareGenerateApiKey()
					.setUsername(credentials.getUsername())
					.setPassword(credentials.getPassword())
					.setToken(credentials.getToken())
					.buildAsync()
					.execute(getBus())
					.getSync();
		} catch (UnauthorizedException e) {
			// convert HTTP 401 to HTTP 400 in this endpoint
			throw new BadRequestException(e.getMessage());
		}
	}
	
	@Operation(description = "Generates a new API key using the given configuration.")
	@PostMapping("/token")
	public User token(
			@Parameter(name = "credentials", description = "The user credentials.", required = true) 
			@RequestBody ApiKeyCreateRequest request) {
		try {
			return UserRequests.prepareGenerateApiKey()
					.setUsername(request.getUsername())
					.setPassword(request.getPassword())
					.setToken(request.getToken())
					.setExpiration(request.getExpiration())
					.setPermissions(request.getPermissions())
					.buildAsync()
					.execute(getBus())
					.getSync();
		} catch (UnauthorizedException e) {
			// convert HTTP 401 to HTTP 400 in this endpoint
			throw new BadRequestException(e.getMessage());
		}
	}
	
	
}
