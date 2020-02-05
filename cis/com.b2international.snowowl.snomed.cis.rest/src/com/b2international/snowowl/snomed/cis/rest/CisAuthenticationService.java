/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.identity.Credentials;
import com.b2international.snowowl.identity.Token;
import com.b2international.snowowl.identity.request.UserRequests;
import com.b2international.snowowl.snomed.cis.rest.model.EmptyJsonResponse;
import com.b2international.snowowl.snomed.cis.rest.model.UserData;
import com.google.common.base.Strings;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 6.18
 */
@Api(value = "Authentication", description="Authentication", tags = { "Authentication" })
@RestController
@RequestMapping(produces={ MediaType.APPLICATION_JSON_VALUE })
public class CisAuthenticationService extends AbstractRestService {

	@Autowired
	private JWTVerifier jwtVerifier;
	
	@ApiOperation(value="Creates a session, obtaining a token for next operations.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "Error", response = RestApiError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = RestApiError.class)
	})
	@PostMapping(value="/login")
	public Token login(
			@ApiParam(value = "The user credentials.", required = true) 
			@RequestBody Credentials credentials) {
		return UserRequests.prepareLogin()
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.buildAsync()
				.execute(getBus())
				.getSync();
	}
	
	@ApiOperation(value="Closes a session, identified by the token.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "Error", response = RestApiError.class)
	})
	@PostMapping(value="/logout")
	public ResponseEntity<EmptyJsonResponse> logout(
			@ApiParam(value = "The security access token.", required = true)
			@RequestBody 
			Token token) {
		return new ResponseEntity<>(new EmptyJsonResponse(), HttpStatus.OK);
	}
	
	@ApiOperation(value = "Validates a token, checking if it's assigned to a current session, and retrieves user data.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "Error", response = RestApiError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = RestApiError.class)
	})
	@PostMapping(value="/authenticate")
	public UserData authenticate(
			@ApiParam(value = "The security access token.", required = true)
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
