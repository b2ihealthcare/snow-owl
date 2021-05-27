/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.compare;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.branch.compare.BranchCompareResult;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 7.0
 */
@Api(value = "Compare", description = "Compare", tags = "compare")
@RequestMapping(value = "/compare")
public abstract class RepositoryBranchCompareRestService extends AbstractRestService {
	
	private final String repositoryId;

	public RepositoryBranchCompareRestService(String repositoryId) {
		super(Collections.emptySet());
		this.repositoryId = repositoryId;
	}
	
	@ApiOperation(
		value = "Compare two branches", 
		notes = "Returns the new, changed and deleted components of two branches"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad Request", response=RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE }, produces = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.OK)
	public Promise<BranchCompareResult> compareBranches(@RequestBody CompareRestRequest request) {
		ApiValidation.checkInput(request);
		return RepositoryRequests.branching().prepareCompare()
			.setBase(request.getBaseBranch())
			.setCompare(request.getCompareBranch())
			.setExcludeComponentChanges(true)
			.setLimit(request.getLimit())
			.build(repositoryId)
			.execute(getBus());
		
	}
}
