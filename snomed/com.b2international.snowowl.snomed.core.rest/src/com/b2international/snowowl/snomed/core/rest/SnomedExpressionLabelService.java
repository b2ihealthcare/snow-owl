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

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.snomed.core.ecl.LabeledEclExpressions;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.17.0
 */
@Tag(description="Label ECL Expressions", name = "label-expressions")
@RestController
@RequestMapping(value = "/{path:**}/label-expressions") 
public class SnomedExpressionLabelService extends AbstractRestService {
	
	@Operation(
		summary="Retrieve ECL Labels", 
		description="Returns a collection resource containing ECL expressions."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<LabeledEclExpressions> getLabel(		
			
			@Parameter(description = "The branch path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@Parameter(description = "The request body", required = true)
			@RequestBody
			final SnomedExpressionLabelRestInput body,
			
			@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
				return SnomedRequests.prepareEclLabeler(body.getExpressions())
						.setDescriptionType(body.getDescriptionType())
						.setLocales(acceptLanguage)
						.build(path)
						.execute(getBus());
		
	}

}