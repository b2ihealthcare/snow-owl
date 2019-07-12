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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.cis.model.CisError;
import com.b2international.snowowl.snomed.api.cis.util.DeferredResults;
import com.b2international.snowowl.snomed.datastore.id.cis.request.GenerationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.RegistrationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.ReservationData;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 6.18
 */
@Api(value = "SCTIDS", description = "SCTIDS", tags = {"SCTIDS"})
@RestController
@RequestMapping(value = "/sct", produces = MediaType.APPLICATION_JSON_VALUE)
public class CisSctIdService {

	@Autowired
	@Value("${repositoryId}")
	protected String repositoryId;
	
	@Autowired
	private IEventBus bus;
	
	@ApiOperation(value = "Returns the SCTIDs Record.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = CisError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = CisError.class)
	})
	@GetMapping(value = "/ids/{sctid}")
	public DeferredResult<SctId> getIds(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token, 
			@ApiParam(value = "The required id.", required = true)
			@PathVariable(value = "sctid")
			String sctid) {
		return DeferredResults.wrap(SnomedRequests.identifiers()
				.prepareGet()
				.setComponentId(sctid)
				.build(repositoryId)
				.execute(bus)
				.then(ids -> ids.first().get()));
	}
	
	@ApiOperation(
		value = "Generates a new SCTID", 
		notes = "Generates a new SCTID, based on the metadata passed in the GenerationData parameter. The first available SCTID will be assigned. Returns a SCTID Record with status 'Assigned'"
	)
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = CisError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = CisError.class)
	})
	@PostMapping(value = "/ids/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
	public DeferredResult<SctId> generate(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody GenerationData generationData) {
		return DeferredResults.wrap(SnomedRequests.identifiers()
				.prepareGenerate()
				.setNamespace(generationData.getNamespaceAsString())
				.setCategory(generationData.getComponentCategory())
				.build(repositoryId)
				.execute(bus)
				.then(ids -> ids.first().get()));
	}
	
	@ApiOperation(
		value = "Reserves a new SCTID", 
		notes = "Reserves a SCTID for use in an external system, based on the metadata passed in the ReservationData parameter. The first available SCTID will be reserved. Returns a SCTID Record with status 'Reserved'."
	)
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = CisError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = CisError.class)
	})
	@PostMapping(value = "/ids/reserve", consumes = MediaType.APPLICATION_JSON_VALUE)
	public DeferredResult<SctId> reserve(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody ReservationData reservationData) {
		return DeferredResults.wrap(SnomedRequests.identifiers()
				.prepareReserve()
				.setCategory(reservationData.getComponentCategory())
				.setNamespace(reservationData.getNamespaceAsString())
				.build(repositoryId)
				.execute(bus)
				.then(ids -> ids.first().get()));
	}
	
	@ApiOperation(
		value = "Registers a SCTID", 
		notes = "Registers a SCTID already in use in an external system, based on the metadata passed in the RegistrationData parameter. Returns a SCTID Record with status 'Assigned'. If the SCTID is already assigned it will return an error."
	)
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = CisError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = CisError.class)
	})
	@PostMapping(value = "/ids/register", consumes = MediaType.APPLICATION_JSON_VALUE)
	public DeferredResult<SctId> register(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody RegistrationData registrationData) {
		return DeferredResults.wrap(SnomedRequests.identifiers()
				.prepareRegister()
				.setComponentId(registrationData.getSctId())
				.build(repositoryId)
				.execute(bus)
				.then(ids -> ids.first().get()));
	}
	
}
