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
package com.b2international.snowowl.snomed.cis.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.identity.Credentials;
import com.b2international.snowowl.core.identity.Token;
import com.b2international.snowowl.core.identity.request.UserRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.snomed.cis.rest.model.EmptyJsonResponse;
import com.b2international.snowowl.snomed.cis.rest.model.UserData;
import com.google.common.base.Strings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 6.18
 */
@Tag(description = "Authentication", name = "Authentication")
@RestController
@RequestMapping(produces={ MediaType.APPLICATION_JSON_VALUE })
public class CisAuthenticationService extends AbstractRestService {

	@Autowired
	private JWTVerifier jwtVerifier;
	
	@Operation(summary="Creates a session, obtaining a token for next operations.")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "Error"),
		@ApiResponse(responseCode = "401", description = "Unauthorized")
	})
	@PostMapping(value="/login")
	public Token login(
			@Parameter(description = "The user credentials.", required = true) 
			@RequestBody Credentials credentials) {
		return UserRequests.prepareLogin()
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.buildAsync()
				.execute(getBus())
				.getSync();
	}
	
	@Operation(summary="Closes a session, identified by the token.")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "Error")
	})
	@PostMapping(value="/logout")
	public ResponseEntity<EmptyJsonResponse> logout(
			@Parameter(description = "The security access token.", required = true)
			@RequestBody 
			Token token) {
		return new ResponseEntity<>(new EmptyJsonResponse(), HttpStatus.OK);
	}
	
	@Operation(summary = "Validates a token, checking if it's assigned to a current session, and retrieves user data.")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "Error"),
		@ApiResponse(responseCode = "401", description = "Unauthorized")
	})
	@PostMapping(value="/authenticate")
	public UserData authenticate(
			@Parameter(description = "The security access token.", required = true)
			@RequestBody 
			Token token) {
		String username = verify(token.getToken());
		if (Strings.isNullOrEmpty(username)) {
			throw new UnauthorizedException("Token does not validate.");
		} else {
			final UserData userData = new UserData();
			userData.setUsername(username);
			return userData;
		}
	}

	private String verify(String token) {
		try {
		    return jwtVerifier.verify(token).getSubject();
		} catch (JWTVerificationException exception){
		    // Invalid signature/claims
			return null;
		}
	}

	
}
