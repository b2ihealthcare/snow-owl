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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.core.exceptions.ApiException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.events.CreateBranchReply;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.snomed.api.rest.domain.CreateBranchRequest;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.wordnik.swagger.annotations.Api;

/**
 * @since 4.1
 */
@Api("SNOMED CT Branches")
@RestController
@RequestMapping("/branches")
public class SnomedBranchingController extends AbstractRestService {

	@Autowired 
	private IEventBus bus;
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public DeferredResult<ResponseEntity<Void>> createBranch(@RequestBody CreateBranchRequest request) {
		final ResponseEntity<Void> response = Responses.created(getBranchLocationHeader(request.path())).build();
		final DeferredResult<ResponseEntity<Void>> result = new DeferredResult<ResponseEntity<Void>>();
		request.toEvent("SNOMEDCT").send(bus, new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				try {
					if (message.isSucceeded()) {
						// success, just try to read the reply
						message.body(CreateBranchReply.class);
						result.setResult(response);
					} else {
						result.setErrorResult(message.body(ApiException.class));
					}
				} catch (Exception e) {
					result.setErrorResult(e);
				}
			}
		});
		return result;
	}
	
	@RequestMapping(value="/{path:**}", method=RequestMethod.GET)
	public DeferredResult<ResponseEntity<Branch>> getBranch(@PathVariable("path") String branchPath) {
//		return new ReadBranchEvent().send(bus, new );
		return null;
	}
	
	
	@RequestMapping(value="/{path:**}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public DeferredResult<ResponseEntity<Void>> deleteBranch(@PathVariable("path") String branchPath) {
//		return new DeleteBranchEvent().on(bus).to("/branches").send();
		return null;
	}

	private URI getBranchLocationHeader(String branchPath) {
		return linkTo(SnomedBranchingController.class).slash(branchPath).toUri();
	}
	
}
