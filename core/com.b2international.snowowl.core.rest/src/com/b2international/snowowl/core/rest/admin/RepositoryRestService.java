/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.identity.domain.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Spring controller for exposing repository related administration functionalities.
 * @since 7.0
 */
@Api(value = "Repositories", description="Repositories", tags = { "repositories" })
@RestController
@RequestMapping(value = "/repositories") 
public class RepositoryRestService extends AbstractRestService {
	
	@ApiOperation(
			value="Retrieve all repositories",
			notes="Retrieves all repositories that store terminology content.")
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<Repositories> getRepositories(
			@ApiParam
			@RequestParam(value="id", required=false)
			String[] idFilter) {
		return RepositoryRequests.prepareSearch()
				.all()
				.filterByIds(idFilter == null ? null : Collections3.toImmutableSet(idFilter))
				.buildAsync()
				.execute(getBus());
	}
	
	@ApiOperation(
		value="Retrieve a repository",
		notes="Retrieves a single repository by its identifier"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<RepositoryInfo> getRepository(
			@ApiParam("The repository identifier")
			@PathVariable("id")
			String id) {
		return RepositoryRequests.prepareGet(id).buildAsync().execute(getBus());
	}
	
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
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping("/lock")
	public void lockGlobal(
			@ApiParam(value="lock timeout in milliseconds", defaultValue = "5000")
			@RequestParam(value="timeoutMillis", defaultValue="5000", required=false) 
			final int timeoutMillis) {

		checkValidTimeout(timeoutMillis);

		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), 
				DatastoreLockContextDescriptions.CREATE_BACKUP);

		final DatastoreLockTarget target = DatastoreLockTarget.ALL;
		doLock(timeoutMillis, context, target);
	}

	@ApiOperation(
			value="Unlock all repositories",
			notes="Releases a previously acquired global lock.")
	@ApiResponses({
		@ApiResponse(code=204, message="Unlock successful"),
		@ApiResponse(code=400, message="Unspecified unlock-related issue")
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping("/unlock")
	public void unlockGlobal() {
		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), DatastoreLockContextDescriptions.CREATE_BACKUP);
		final DatastoreLockTarget target = DatastoreLockTarget.ALL;
		doUnlock(context, target);
	}

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
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping("/{id}/lock")
	public void lockRepository(
			@PathVariable(value="id") 
			@ApiParam(value="The repository id")
			final String id, 

			@ApiParam(value="lock timeout in milliseconds", defaultValue = "5000")
			@RequestParam(value="timeoutMillis", defaultValue="5000", required=false)
			final int timeoutMillis) {
		checkValidRepositoryUuid(id);
		checkValidTimeout(timeoutMillis);

		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), 
				DatastoreLockContextDescriptions.CREATE_REPOSITORY_BACKUP,
				DatastoreLockContextDescriptions.CREATE_BACKUP);

		final DatastoreLockTarget target = new DatastoreLockTarget(id, null);
		doLock(timeoutMillis, context, target);
	}

	@ApiOperation(
			value="Unlock single repository",
			notes="Releases a previously acquired repository-level lock on the specified repository.")
	@ApiResponses({
		@ApiResponse(code=204, message="Unlock successful"),
		@ApiResponse(code=404, message="Repository not found"),
		@ApiResponse(code=400, message="Unspecified unlock-related issue")
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping("/{id}/unlock")
	public void unlockRepository(
			@ApiParam(value="The repository id")
			@PathVariable(value="id") 
			final String repositoryUuid) {

		checkValidRepositoryUuid(repositoryUuid);

		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), 
				DatastoreLockContextDescriptions.CREATE_REPOSITORY_BACKUP,
				DatastoreLockContextDescriptions.CREATE_BACKUP);

		final DatastoreLockTarget target = new DatastoreLockTarget(repositoryUuid, null);
		doUnlock(context, target);
	}
	
	private void checkValidRepositoryUuid(final String repositoryUuid) {
		checkNotNull(repositoryUuid, "Repository identifier may not be null.");

		final Repository repository = ApplicationContext.getServiceForClass(RepositoryManager.class).get(repositoryUuid);
		if (repository == null) {
			throw new NotFoundException("Repository", repositoryUuid);
		}
	}
	
	private void checkValidTimeout(final int timeoutMillis) {
		checkArgument(timeoutMillis >= 0, "Timeout in milliseconds may not be negative.");
	}

	private void doUnlock(final DatastoreLockContext context, final DatastoreLockTarget target) {
		try {
			getLockManager().unlock(context, target);
		} catch (final OperationLockException e) {
			throw new BadRequestException(e.getMessage());
		}
	}
	
	private void doLock(final int timeoutMillis, final DatastoreLockContext context, final DatastoreLockTarget target) {
		try {
			getLockManager().lock(context, timeoutMillis, target);
		} catch (final DatastoreOperationLockException e) {
			final DatastoreLockContext conflictingContext = e.getContext(target);
			throw new BadRequestException("Failed to acquire lock for all repositories because %s is %s.", 
					conflictingContext.getUserId(), 
					conflictingContext.getDescription());
		} catch (final OperationLockException | InterruptedException e) {
			throw new BadRequestException(e.getMessage());
		}
	}
	
	private static IOperationLockManager getLockManager() {
		return ApplicationContext.getServiceForClass(IOperationLockManager.class);
	}
	
}
