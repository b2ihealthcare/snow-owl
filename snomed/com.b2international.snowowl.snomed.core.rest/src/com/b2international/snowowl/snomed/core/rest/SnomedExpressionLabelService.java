/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.snomed.core.ql.Expressions;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.annotations.*;

/**
 * @since 7.17.0
 */
@Api(value = "label-expressions", description="Label expressions", tags = { "label-expressions" })
@RestController
@RequestMapping(value = "/{path:**}/label-expressions") 
public class SnomedExpressionLabelService extends AbstractSnomedRestService {
	
	@ApiOperation(
			value="Retrieve ECL Labels", 
			notes="Returns a collection resource containing ECL expressions."
		)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Expressions.class),
		@ApiResponse(code = 400, message = "Invalid config", response = BadRequest.class),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class)
	})
	@GetMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<Expressions> getLabel(		
			
			@ApiParam(value = "The branch path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@ApiParam(value = "The request body", required = true)
			@RequestBody
			final SnomedExpressionLabelRestInput body,
			
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
				return SnomedRequests.prepareQueryLabeler(body.getExpressions())
						.setDescriptionType(body.getDescriptionType())
						.setLocales(acceptLanguage)
						.build(repositoryId, path)
						.execute(getBus());
		
	}

}