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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.datastore.server.events.BranchReply;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.rest.domain.MergeRequest;
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
@RequestMapping(value="/merges", produces={AbstractRestService.SO_MEDIA_TYPE, AbstractRestService.APPLICATION_JSON_VALUE})
public class SnomedBranchMergingController extends AbstractRestService {

	@Autowired
	private IEventBus bus;
	
	@ApiOperation(
			value = "Merge branches", 
			notes = "Merge source branch into a target branch in SNOMED-CT repository.")
		@ApiResponses({
			@ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
			@ApiResponse(code = 404, message = "Source or Target branch is not found", response=RestApiError.class),
			@ApiResponse(code = 409, message = "Merge conflict", response=RestApiError.class)
		})
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public DeferredResult<ResponseEntity<Void>> merge(@RequestBody MergeRequest request) {
		ApiValidation.checkInput(request);
		final ResponseEntity<Void> response = Responses.noContent().build();
		final DeferredResult<ResponseEntity<Void>> result = new DeferredResult<>();
		request.toEvent(repositoryId)
			.send(bus, BranchReply.class)
			.then(new Procedure<BranchReply>() { @Override protected void doApply(BranchReply reply) {
				result.setResult(response);
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(Throwable throwable) {
				result.setErrorResult(throwable);
			}});
		return result;
	}
	
}
