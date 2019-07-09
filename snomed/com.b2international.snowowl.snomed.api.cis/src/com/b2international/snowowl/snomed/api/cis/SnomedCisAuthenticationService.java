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
package com.b2international.snowowl.snomed.api.cis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.snomed.api.cis.model.CisError;
import com.b2international.snowowl.snomed.datastore.id.cis.Credentials;
import com.b2international.snowowl.snomed.datastore.id.cis.Token;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 6.18
 */
@Api(value = "Authentication", description="Authentication", tags = { "authentication" })
@RestController
@RequestMapping(produces={ MediaType.APPLICATION_JSON_VALUE })
public class SnomedCisAuthenticationService {

	@Autowired
	private CisAuthenticationProvider auth;
	
	public SnomedCisAuthenticationService() {
		System.err.println();
	}
	
	@ApiOperation(value="Creates a session, obtaining a token for next operations.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "Error", response = CisError.class)
	})
	@PostMapping(value="/login")
	public Token login(@RequestBody Credentials credentials) {
		return auth.login(credentials);
	}
	
	@ApiOperation(value="Closes a session, identified by the token.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "Error", response = CisError.class)
	})
	@PostMapping(value="/logout")
	public void logout(@RequestBody Token token) {
		
	}
	
	@ApiOperation(value = "Validates a token, checking if it' assigned to a current session, and retrieves user data.")
	@PostMapping(value="/authenticate")
	public void authenticate() {
		
	}
	
}
