/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.codesystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.codesystem.ICodeSystemService;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystem;
import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.domain.CollectionResource;
import com.b2international.snowowl.api.rest.domain.RestApiError;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("Code System Metadata")
@RestController
@RequestMapping(
		value = "/codesystems",
		produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class CodeSystemRestService extends AbstractRestService {

	@Autowired
	protected ICodeSystemService delegate;

	@ApiOperation(
			value="Retrieve all code systems",
			notes="Returns a list containing generic information about registered code systems.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public CollectionResource<ICodeSystem> getCodeSystems() {
		return CollectionResource.of(delegate.getCodeSystems());
	}

	@ApiOperation(
			value="Retrieve code system by short name or OID",
			notes="Returns generic information about a single code system with the specified short name or OID.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system not found", response = RestApiError.class)
	})
	@RequestMapping(value="{shortNameOrOid}", method=RequestMethod.GET)
	public ICodeSystem getCodeSystemByShortNameOrOid(
			@ApiParam(value="The code system identifier (short name or OID)")
			@PathVariable(value="shortNameOrOid") final String shortNameOrOId) {
		return delegate.getCodeSystemByShortNameOrOid(shortNameOrOId);
	}

}