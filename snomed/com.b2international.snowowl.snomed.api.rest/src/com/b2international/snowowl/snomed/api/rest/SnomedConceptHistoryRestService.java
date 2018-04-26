/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.history.domain.IHistoryInfo;
import com.b2international.snowowl.snomed.api.ISnomedConceptHistoryService;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("History")
@RestController
@RequestMapping(
		produces={ AbstractRestService.SO_MEDIA_TYPE })
public class SnomedConceptHistoryRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedConceptHistoryService delegate;

	@ApiOperation(
			value="Retrieve history for a Concept", 
			notes="Returns the change history for the specified Concept.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/concepts/{conceptId}/history", method=RequestMethod.GET)
	public CollectionResource<IHistoryInfo> getHistory(
			@ApiParam(value="The branch path")
			@PathVariable(value="path") 
			final String branchPath,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId) {

		return CollectionResource.of(delegate.getHistory(branchPath, conceptId));
	}

}