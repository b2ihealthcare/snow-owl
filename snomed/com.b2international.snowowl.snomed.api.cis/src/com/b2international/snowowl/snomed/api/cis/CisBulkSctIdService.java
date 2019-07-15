/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.cis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.cis.model.CisError;
import com.b2international.snowowl.snomed.api.cis.util.DeferredResults;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 6.18
 */
@Api(value = "SCTIDS - Bulk Operations", description = "SCTIDS", tags = {"SCTIDS - Bulk Operations"})
@RestController
@RequestMapping(value = "/sct/bulk", produces = MediaType.APPLICATION_JSON_VALUE)
public class CisBulkSctIdService {

	@Autowired
	@Value("${repositoryId}")
	protected String repositoryId;
	
	@Autowired
	private IEventBus bus;
	
	@ApiOperation(
		value = "Returns the SCTIDs Record.",
		notes = "Returns the required SCTID Records. Bulk SCTID operations do not currently include AdditionalIds"
	)
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = CisError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = CisError.class)
	})
	@GetMapping(value = "/ids")
	public DeferredResult<List<SctId>> getSctIds(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The required sctids list, separated with commas (,).", required = true)
			@RequestParam(value = "sctids")
			String sctIds) {
		String[] sctIdValues = Strings.isNullOrEmpty(sctIds) ? new String[0] : sctIds.split(",");
		return DeferredResults.wrap(SnomedRequests.identifiers()
				.prepareGet()
				.setComponentIds(ImmutableSet.copyOf(sctIdValues))
				.build(repositoryId)
				.execute(bus)
				.then(ids -> ids.getItems()));
	}
	
	
	
}
