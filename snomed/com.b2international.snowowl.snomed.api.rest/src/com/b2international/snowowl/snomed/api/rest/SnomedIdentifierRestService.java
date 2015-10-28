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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedIdentifierRequest;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedIdentifierResponse;
import com.b2international.snowowl.snomed.core.events.SnomedIdentifierRequestEvent;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("Identifiers")
@RestController
@RequestMapping(value="/ids", produces = { AbstractRestService.SO_MEDIA_TYPE })
public class SnomedIdentifierRestService extends AbstractRestService {

	@Autowired
	private IEventBus bus;
	
	@ApiOperation(
			value="Create a new SNOMED CT Identifier", 
			notes="Creates a new, unique SNOMED CT Identifier.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created")
	})
	@RequestMapping(method = RequestMethod.POST, consumes = { AbstractRestService.SO_MEDIA_TYPE, AbstractRestService.APPLICATION_JSON_VALUE })
	@ResponseStatus(value = HttpStatus.CREATED)
	public DeferredResult<SnomedIdentifierResponse> generate(@RequestBody final SnomedIdentifierRequest request) {
		ApiValidation.checkInput(request);
		final DeferredResult<SnomedIdentifierResponse> result = new DeferredResult<SnomedIdentifierResponse>();
		bus.send("/snomed-ct/ids", new SnomedIdentifierRequestEvent(request.getType(), request.getNamespace()), new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				if (message.isSucceeded()) {
					final SnomedIdentifierResponse response = new SnomedIdentifierResponse();
					response.setId(message.body(String.class));
					result.setResult(response);
				} else {
					result.setErrorResult(message.body(Exception.class));
				}
			}
		});
		return result;
	}
	
}
