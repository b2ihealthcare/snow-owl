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
import com.b2international.snowowl.core.events.util.AsyncSupport;
import com.b2international.snowowl.datastore.events.BranchReply;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.rest.domain.MergeRequest;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.wordnik.swagger.annotations.Api;

/**
 * @since 4.1
 */
@Api("SNOMED CT Branches")
@RestController
@RequestMapping(value="/merges")
public class SnomedBranchMergingController {

	@Autowired
	private IEventBus bus;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public DeferredResult<ResponseEntity<Void>> merge(@RequestBody MergeRequest request) {
		final ResponseEntity<Void> response = Responses.noContent().build();
		final DeferredResult<ResponseEntity<Void>> result = new DeferredResult<>();
		new AsyncSupport<BranchReply>(bus, BranchReply.class)
			.send(request.toEvent())
			.then(new Procedure<BranchReply>() { @Override protected void doApply(BranchReply reply) {
				result.setResult(response);
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(Throwable throwable) {
				result.setErrorResult(throwable);
			}});
		return result;
	}
	
}
