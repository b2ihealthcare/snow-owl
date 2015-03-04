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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.history.domain.IHistoryInfo;
import com.b2international.snowowl.snomed.api.ISnomedConceptHistoryService;
import com.b2international.snowowl.snomed.api.rest.domain.CollectionResource;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("SNOMED CT History")
@RestController
@RequestMapping(
		value="/{version}", 
		produces={ AbstractRestService.V1_MEDIA_TYPE })
public class SnomedConceptHistoryRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedConceptHistoryService delegate;

	@ApiOperation(
			value="Retrieve history for a concept", 
			notes="Returns the change history for the specified concept.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(value="/concepts/{conceptId}/history", method=RequestMethod.GET)
	public CollectionResource<IHistoryInfo> getHistory(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId) {

		return getHistoryOnTask(version, null, conceptId);
	}

	@ApiOperation(
			value="Retrieve history for a concept on task", 
			notes="Returns the change history for the specified concept.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Code system version, task or concept not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/concepts/{conceptId}/history", 
			method=RequestMethod.GET)
	public CollectionResource<IHistoryInfo> getHistoryOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId) {

		final IComponentRef conceptRef = createComponentRef(version, taskId, conceptId);
		return CollectionResource.of(delegate.getHistory(conceptRef));
	}
}