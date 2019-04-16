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
package com.b2international.snowowl.snomed.api.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.snomed.api.rest.domain.SnomedIdentifierRequest;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedIdentifierResponse;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * @since 1.0
 */
@Api(value = "Identifiers", description="Identifiers", tags = { "identifiers" })
@RestController
@RequestMapping(value = "/ids")
public class SnomedIdentifierRestService extends AbstractRestService {

	@ApiOperation(
			value="Create a new SNOMED CT Identifier", 
			notes="Creates a new, unique SNOMED CT Identifier.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE }, produces = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value = HttpStatus.CREATED)
	public DeferredResult<SnomedIdentifierResponse> generate(@RequestBody final SnomedIdentifierRequest request) {
		return DeferredResults.wrap(SnomedRequests.identifiers()
					.prepareGenerate()
					.setCategory(request.getType())
					.setNamespace(request.getNamespace())
					.build(repositoryId)
					.execute(bus)
					.then(result -> new SnomedIdentifierResponse(result.first().get())));
	}
}
