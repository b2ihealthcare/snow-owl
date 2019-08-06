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

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.history.domain.IHistoryInfo;
import com.b2international.snowowl.snomed.api.ISnomedReferenceSetHistoryService;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedReferenceSetHistory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api(value = "History", description="History", tags = { "history" })
@RestController
@RequestMapping(produces={ AbstractRestService.SO_MEDIA_TYPE })
public class SnomedReferenceSetHistoryRestService extends AbstractRestService {

	@Autowired
	protected ISnomedReferenceSetHistoryService delegate;
	
	public SnomedReferenceSetHistoryRestService() {
		super(Collections.emptySet());
	}

	@ApiOperation(
			value="Get history for a reference set", 
			notes="Retrieves history for the specified SNOMED CT reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or reference set not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/refsets/{id}/history", method=RequestMethod.GET)
	public SnomedReferenceSetHistory getHistory(
			@PathVariable(value="path") final String branchPath,
			@PathVariable(value="id") final String refSetId,
			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		final List<IHistoryInfo> referenceSetHistory = delegate.getHistory(branchPath, refSetId, getExtendedLocales(acceptLanguage));
		final SnomedReferenceSetHistory result = new SnomedReferenceSetHistory();
		result.setReferenceSetHistory(referenceSetHistory);
		return result;
	}

}