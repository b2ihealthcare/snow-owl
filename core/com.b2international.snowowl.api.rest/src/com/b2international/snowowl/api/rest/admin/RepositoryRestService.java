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
package com.b2international.snowowl.api.rest.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.admin.IRepositoryService;
import com.b2international.snowowl.api.admin.exception.LockConflictException;
import com.b2international.snowowl.api.admin.exception.LockException;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Spring controller for exposing {@link IRepositoryService} functionality.
 * 
 */
@RestController
@RequestMapping(value={"/repositories"}, produces={ MediaType.TEXT_PLAIN_VALUE })
@Api("Administration")
@ApiIgnore
public class RepositoryRestService extends AbstractAdminRestService {

	@Autowired
	protected IRepositoryService delegate;

	@ExceptionHandler(LockConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody String handleLockConflictException(final LockConflictException e) {
		return handleException(e);
	}

	@ExceptionHandler(LockException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleLockException(final LockException e) {
		return handleException(e);
	}

	@RequestMapping(method=RequestMethod.GET)
	@ApiOperation(
			value="Retrieve all repository identifiers",
			notes="Retrieves the unique identifier of each running repository that stores terminology content.")
	public String getRepositoryUuids() {
		final List<String> repositoryUuids = delegate.getRepositoryUuids();
		return joinStrings(repositoryUuids);
	}

	@RequestMapping(value="lock", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(
			value="Lock all repositories",
			notes="Places a global lock, which prevents other users from making changes to any of the repositories "
					+ "while a backup is created. The call may block up to the specified timeout to acquire the lock; "
					+ "if timeoutMillis is set to 0, it returns immediately.")
	@ApiResponses({
		@ApiResponse(code=204, message="Lock successful"),
		@ApiResponse(code=409, message="Conflicting lock already taken"),
		@ApiResponse(code=400, message="Illegal timeout value, or locking-related issue")
	})
	public void lockGlobal(
			@RequestParam(value="timeoutMillis", defaultValue="5000", required=false) 
			@ApiParam(value="lock timeout in milliseconds")
			final int timeoutMillis) {

		delegate.lockGlobal(timeoutMillis);
	}

	@RequestMapping(value="unlock", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(
			value="Unlock all repositories",
			notes="Releases a previously acquired global lock.")
	@ApiResponses({
		@ApiResponse(code=204, message="Unlock successful"),
		@ApiResponse(code=400, message="Unspecified unlock-related issue")
	})
	public void unlockGlobal() {
		delegate.unlockGlobal();
	}

	@RequestMapping(value="{repositoryUuid}/lock", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(
			value="Lock single repository",
			notes="Places a repository-level lock, which prevents other users from making changes to the specified repository. "
					+ "The call may block up to the specified timeout to acquire the lock; if timeoutMillis is set to 0, "
					+ "it returns immediately.")
	@ApiResponses({
		@ApiResponse(code=204, message="Lock successful"),
		@ApiResponse(code=409, message="Conflicting lock already taken"),
		@ApiResponse(code=404, message="Repository not found"),
		@ApiResponse(code=400, message="Illegal timeout value, or locking-related issue")
	})
	public void lockRepository(
			@PathVariable(value="repositoryUuid") 
			@ApiParam(value="a unique identifier pointing to a particular repository")
			final String repositoryUuid, 

			@RequestParam(value="timeoutMillis", defaultValue="5000", required=false)
			@ApiParam(value="lock timeout in milliseconds")
			final int timeoutMillis) {

		delegate.lockRepository(repositoryUuid, timeoutMillis);
	}

	@RequestMapping(value="{repositoryUuid}/unlock", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(
			value="Unlock single repository",
			notes="Releases a previously acquired repository-level lock on the specified repository.")
	@ApiResponses({
		@ApiResponse(code=204, message="Unlock successful"),
		@ApiResponse(code=404, message="Repository not found"),
		@ApiResponse(code=400, message="Unspecified unlock-related issue")
	})
	public void unlockRepository(
			@PathVariable(value="repositoryUuid") 
			@ApiParam(value="a unique identifier pointing to a particular repository")
			final String repositoryUuid) {

		delegate.unlockRepository(repositoryUuid);
	}

	@RequestMapping(value="{repositoryUuid}/versions", method=RequestMethod.GET)
	@ApiOperation(
			value="Retrieve all version identifiers for a repository",
			notes="Retrieves all version identifiers for the specified repository.")
	@ApiResponses({
		@ApiResponse(code=404, message="Repository not found")
	})
	public String getRepositoryVersionIds(
			@PathVariable(value="repositoryUuid") 
			@ApiParam(value="a unique identifier pointing to a particular repository")
			final String repositoryUuid) {

		final List<String> versions = delegate.getRepositoryVersionIds(repositoryUuid);
		return joinStrings(versions);
	}

}