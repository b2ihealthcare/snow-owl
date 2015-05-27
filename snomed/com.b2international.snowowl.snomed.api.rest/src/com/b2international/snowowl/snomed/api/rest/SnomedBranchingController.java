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
package com.b2international.snowowl.snomed.api.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.events.util.AsyncSupport;
import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.datastore.server.branch.Branch;
import com.b2international.snowowl.datastore.server.events.BranchReply;
import com.b2international.snowowl.datastore.server.events.BranchesReply;
import com.b2international.snowowl.datastore.server.events.DeleteBranchEvent;
import com.b2international.snowowl.datastore.server.events.ReadAllBranchEvent;
import com.b2international.snowowl.datastore.server.events.ReadBranchChildrenEvent;
import com.b2international.snowowl.datastore.server.events.ReadBranchEvent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.rest.domain.CollectionResource;
import com.b2international.snowowl.snomed.api.rest.domain.CreateSnomedBranchRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 4.1
 */
@Api("Branches")
@RestController
@RequestMapping(value="/branches", produces={AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
public class SnomedBranchingController extends AbstractRestService {

	private static final String SNOMED_REPOSITORY = "snomedStore";
	
	@Autowired 
	private IEventBus bus;

	@ApiOperation(
		value = "Create a new branch", 
		notes = "Create a new branch in the SNOMED-CT repository.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 400, message = "Bad Request", response=RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public DeferredResult<ResponseEntity<Void>> createBranch(@RequestBody CreateSnomedBranchRequest request) {
		ApiValidation.checkInput(request);
		final ResponseEntity<Void> response = Responses.created(getBranchLocationHeader(request.path())).build();
		final DeferredResult<ResponseEntity<Void>> result = new DeferredResult<>();
		new AsyncSupport<>(bus, BranchReply.class)
			.send(request.toEvent())
			.then(new Procedure<BranchReply>() { @Override protected void doApply(BranchReply reply) {
				result.setResult(response);
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}
	
	@ApiOperation(
		value = "Retrieve all branches", 
		notes = "Returns all SNOMED-CT branches from the repository.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response=CollectionResource.class)
	})
	@RequestMapping(method=RequestMethod.GET)
	public DeferredResult<CollectionResource<Branch>> getBranches() {
		final DeferredResult<CollectionResource<Branch>> result = new DeferredResult<>();
		new AsyncSupport<>(bus, BranchesReply.class)
			.send(new ReadAllBranchEvent(SNOMED_REPOSITORY))
			.then(new Procedure<BranchesReply>() { @Override protected void doApply(BranchesReply reply) {
				result.setResult(CollectionResource.of(reply.getBranches()));
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}
	
	@ApiOperation(
		value = "Retrieve children of a single branch", 
		notes = "Returns the children of a single SNOMED-CT branch (both direct and transitive).")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response=CollectionResource.class),
		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
	})
	@RequestMapping(value="/{path:**}/children", method=RequestMethod.GET)
	public DeferredResult<CollectionResource<Branch>> getChildren(@PathVariable("path") String branchPath) {
		final DeferredResult<CollectionResource<Branch>> result = new DeferredResult<>();
		new AsyncSupport<>(bus, BranchesReply.class)
			.send(new ReadBranchChildrenEvent(SNOMED_REPOSITORY, branchPath))
			.then(new Procedure<BranchesReply>() { @Override protected void doApply(BranchesReply reply) {
				result.setResult(CollectionResource.of(reply.getBranches()));
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}
	
	@ApiOperation(
		value = "Retrieve a single branch", 
		notes = "Returns a SNOMED-CT branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
	})
	@RequestMapping(value="/{path:**}", method=RequestMethod.GET)
	public DeferredResult<Branch> getBranch(@PathVariable("path") String branchPath) {
		final DeferredResult<Branch> result = new DeferredResult<>();
		new AsyncSupport<>(bus, BranchReply.class)
			.send(new ReadBranchEvent(SNOMED_REPOSITORY, branchPath))
			.then(new Procedure<BranchReply>() { @Override protected void doApply(BranchReply reply) {
				result.setResult(reply.getBranch());
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}
	
	@ApiOperation(
		value = "Delete a branch", 
		notes = "Deletes a branch and all its children."
				+ "<p>"
				+ "Note that deleted branch are still available and will be listed in <b>GET /branches</b> but with the flag <b>deleted</b> set to <i>true</i>. "
				+ "The API will return <strong>HTTP 400</strong> responses, if clients send requests to <strong>deleted</strong> branches."
				+ "</p>")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
	})
	@RequestMapping(value="/{path:**}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public DeferredResult<ResponseEntity<Void>> deleteBranch(@PathVariable("path") String branchPath) {
		final ResponseEntity<Void> response = Responses.noContent().build();
		final DeferredResult<ResponseEntity<Void>> result = new DeferredResult<>();
		new AsyncSupport<>(bus, BranchReply.class)
			.send(new DeleteBranchEvent(SNOMED_REPOSITORY, branchPath))
			.then(new Procedure<BranchReply>() { @Override protected void doApply(BranchReply reply) {
				result.setResult(response);
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}
	
	private URI getBranchLocationHeader(String branchPath) {
		return linkTo(SnomedBranchingController.class).slash(branchPath).toUri();
	}
	
}
