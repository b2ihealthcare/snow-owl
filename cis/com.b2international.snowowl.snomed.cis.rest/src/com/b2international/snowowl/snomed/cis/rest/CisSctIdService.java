/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.snomed.cis.Identifiers;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.cis.model.DeprecationData;
import com.b2international.snowowl.snomed.cis.model.GenerationData;
import com.b2international.snowowl.snomed.cis.model.PublicationData;
import com.b2international.snowowl.snomed.cis.model.RegistrationData;
import com.b2international.snowowl.snomed.cis.model.ReleaseData;
import com.b2international.snowowl.snomed.cis.model.ReservationData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 6.18
 */
@Tag(description = "SCTIDS", name = "SCTIDS")
@RestController
@RequestMapping(value = "/sct", produces = MediaType.APPLICATION_JSON_VALUE)
public class CisSctIdService extends AbstractRestService {

	private Identifiers identifiers = new Identifiers();
	
	@Operation(summary = "Returns the SCTIDs Record.")
	@ApiResponses({
		@ApiResponse(responseCode = "400", description="Bad Request"),
		@ApiResponse(responseCode = "401", description="Unauthorized")
	})
	@GetMapping(value = "/ids/{sctid}")
	public Promise<SctId> getIds(
			@Parameter(description = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token, 
			@Parameter(description = "The required id.", required = true)
			@PathVariable(value = "sctid")
			String sctid) {
		return identifiers
				.prepareGet()
				.setComponentId(sctid)
				.buildAsync()
				.execute(getBus())
				.then(ids -> ids.first().get());
	}
	
	@Operation(
		summary = "Generates a new SCTID", 
		description = "Generates a new SCTID, based on the metadata passed in the GenerationData parameter. The first available SCTID will be assigned. Returns a SCTID Record with status 'Assigned'"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "400", description="Bad Request"),
		@ApiResponse(responseCode = "401", description="Unauthorized")
	})
	@PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Promise<SctId> generate(
			@Parameter(description = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@Parameter(description = "The requested operation.", required = true)
			@RequestBody GenerationData generationData) {
		return identifiers
				.prepareGenerate()
				.setNamespace(generationData.getNamespaceAsString())
				.setCategory(generationData.getComponentCategory())
				.buildAsync()
				.execute(getBus())
				.then(ids -> ids.first().get());
	}
	
	@Operation(
		summary = "Reserves a new SCTID", 
		description = "Reserves a SCTID for use in an external system, based on the metadata passed in the ReservationData parameter. The first available SCTID will be reserved. Returns a SCTID Record with status 'Reserved'."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "400", description="Bad Request"),
		@ApiResponse(responseCode = "401", description="Unauthorized")
	})
	@PostMapping(value = "/reserve", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Promise<SctId> reserve(
			@Parameter(description = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@Parameter(description = "The requested operation.", required = true)
			@RequestBody 
			ReservationData reservationData) {
		return identifiers
				.prepareReserve()
				.setCategory(reservationData.getComponentCategory())
				.setNamespace(reservationData.getNamespaceAsString())
				.buildAsync()
				.execute(getBus())
				.then(ids -> ids.first().get());
	}
	
	@Operation(
		summary = "Registers a SCTID", 
		description = "Registers a SCTID already in use in an external system, based on the metadata passed in the RegistrationData parameter. Returns a SCTID Record with status 'Assigned'. If the SCTID is already assigned it will return an error."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "400", description="Bad Request"),
		@ApiResponse(responseCode = "401", description="Unauthorized")
	})
	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Promise<SctId> register(
			@Parameter(description = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@Parameter(description = "The requested operation.", required = true)
			@RequestBody 
			RegistrationData registrationData) {
		return identifiers
				.prepareRegister()
				.setComponentId(registrationData.getSctId())
				.buildAsync()
				.execute(getBus())
				.then(ids -> ids.first().get());
	}
	
	@Operation(
		summary = "Deprecates a SCTID", 
		description = "Deprecates a SCTID, so it will not be assigned to any component, based on the metadata passed in the DeprecationData parameter. Returns a SCTID Record with status 'Deprecated'."
	)
	@PutMapping(value = "/deprecate", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Promise<SctId> deprecate(
			@Parameter(description = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@Parameter(description = "The requested operation.", required = true)
			@RequestBody 
			DeprecationData deprecationData) {
		return identifiers
				.prepareDeprecate()
				.setComponentId(deprecationData.getSctId())
				.buildAsync()
				.execute(getBus())
				.then(ids -> ids.first().get());
	}

	@Operation(
		summary = "Releases a SCTID", 
		description = "Releases a SCTID, so it will available to be assigned again, based on the metadata passed in the DeprecationData parameter. Returns a SCTID Record with status 'Available'."
	)
	@PutMapping(value = "/release", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Promise<SctId> release(
			@Parameter(description = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@Parameter(description = "The requested operation.", required = true)
			@RequestBody
			ReleaseData releaseData) {
		return identifiers
				.prepareRelease()
				.setComponentId(releaseData.getSctId())
				.buildAsync()
				.execute(getBus())
				.then(ids -> ids.first().get());
	}

	@Operation(
		summary = "Publishes a SCTID", 
		description = "Sets the SCTID as published, based on the metadata passed in the DeprecationData parameter. Returns a SCTID Record with status 'Published'."
	)
	@PutMapping(value = "/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Promise<SctId> publish(
			@Parameter(description = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@Parameter(description = "The requested operation.", required = true)
			@RequestBody
			PublicationData publicationData) {
		return identifiers
				.preparePublish()
				.setComponentId(publicationData.getSctId())
				.buildAsync()
				.execute(getBus())
				.then(ids -> ids.first().get());
	}
	
}
