/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.info;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.info.IRepositoryInfoService;
import com.b2international.snowowl.api.info.domain.IRepositoryInfo;
import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.domain.RestApiError;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 5.8
 */
@Api("Server info")
@RestController
@RequestMapping(
		value = "/info/repositories",
		produces = { AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class ServerInfoRestService extends AbstractRestService {

	@Autowired
	private IRepositoryInfoService delegate;
	
	@ApiOperation(value = "Retrieve metadata of all repositories", notes ="Returns terminology independent metadata about each of the available repositories.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public CollectionResource<IRepositoryInfo> getRepositoryInfo() {
		List<IRepositoryInfo> repositoryStates = delegate.getAllRepositoryInformation();
		return CollectionResource.of(repositoryStates);
	}
	
	@ApiOperation(value = "Retrieve metadata for the specified repository", notes ="Returns terminology independent metadata about the repository identified by the repositoryId parameter.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Repository exists, but does not provide meta information", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Repository not found", response = RestApiError.class),
	})
	@RequestMapping(value="{repositoryId}", method=RequestMethod.GET)
	public IRepositoryInfo getRepositoryInfo(
				@ApiParam(required = true, value = "")
				@PathVariable(value = "repositoryId")
				final String repositoryId) {
		return delegate.getRepositoryInfo(repositoryId);
	}
	
	
	@ApiOperation(value = "", notes = "")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@RequestMapping(method=RequestMethod.POST)
	public void refreshAllRespositoryHealthState() {
		delegate.updateAllRespositoryInformation();
	}
}
