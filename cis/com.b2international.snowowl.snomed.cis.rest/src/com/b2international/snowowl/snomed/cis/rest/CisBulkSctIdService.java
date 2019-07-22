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
package com.b2international.snowowl.snomed.cis.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.snomed.cis.Identifiers;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.cis.model.BulkDeprecationData;
import com.b2international.snowowl.snomed.cis.model.BulkGenerationData;
import com.b2international.snowowl.snomed.cis.model.BulkPublicationData;
import com.b2international.snowowl.snomed.cis.model.BulkRegistrationData;
import com.b2international.snowowl.snomed.cis.model.BulkReleaseData;
import com.b2international.snowowl.snomed.cis.model.BulkReservationData;
import com.b2international.snowowl.snomed.cis.model.Record;
import com.b2international.snowowl.snomed.cis.rest.model.BulkJob;
import com.b2international.snowowl.snomed.cis.rest.model.CisError;
import com.b2international.snowowl.snomed.cis.rest.model.SctIdsList;
import com.b2international.snowowl.snomed.cis.rest.util.DeferredResults;
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
	private IEventBus bus;
	
	private Identifiers identifiers = new Identifiers();
	
	@ApiOperation(
		value = "Returns the SCTIDs Record.",
		notes = "Returns the required SCTID Records. Bulk SCTID operations do not currently include AdditionalIds"
	)
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = CisError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = CisError.class)
	})
	@GetMapping(value = "/ids")
	public DeferredResult<List<SctId>> getSctIdsViaGet(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The required sctids list, separated with commas (,).", required = true)
			@RequestParam(value = "sctids")
			String sctIds) {
		return getSctIds(sctIds);
	}
	
	@ApiOperation(
		value = "Returns the SCTIDs Record.",
		notes = "Returns the required SCTID Records. Bulk SCTID operations do not currently include AdditionalIds"
	)
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = CisError.class),
		@ApiResponse(code = 401, message = "Unauthorized", response = CisError.class)
	})
	@PostMapping(value = "/ids")
	public DeferredResult<List<SctId>> getSctIdsViaPost(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The required sctids list, separated with commas (,).", required = true)
			@RequestBody
			SctIdsList sctIds) {
		return getSctIds(sctIds.getSctids());
	}
	
	private DeferredResult<List<SctId>> getSctIds(String sctIds) {
		String[] sctIdValues = Strings.isNullOrEmpty(sctIds) ? new String[0] : sctIds.split(",");
		return DeferredResults.wrap(identifiers
				.prepareGet()
				.setComponentIds(ImmutableSet.copyOf(sctIdValues))
				.buildAsync()
				.execute(bus)
				.then(ids -> ids.getItems()));
	}
	
	@ApiOperation(
		value = "Generates new SCTIDs",
		notes = "Generates new SCTIDs, based on the metadata passed in the GenerationData parameter. The first available SCTIDs will be assigned. Returns an array of SCTIDs Record with status 'Assigned'"
	)
	@PostMapping(value = "/generate")
	public DeferredResult<BulkJob> generateBulk(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody 
			BulkGenerationData generationData) {
		return runInJob("Generate new SCTIDs", identifiers
						.prepareGenerate()
						.setCategory(generationData.getComponentCategory())
						.setNamespace(generationData.getNamespaceAsString())
						.setQuantity(generationData.getQuantity())
						.buildAsync());
	}
	
	@ApiOperation(
		value = "Registers SCTIDs",
		notes = "Registers SCTIDs already in use in an external system, based on the metadata passed in the RegistrationData parameter. Returns an array of SCTID Records with status 'Assigned'."
	)
	@PostMapping(value = "/register")
	public DeferredResult<BulkJob> registerBulk(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody 
			BulkRegistrationData registrationData) {
		return runInJob("Register SCTIDs", identifiers
						.prepareRegister()
						.setComponentIds(registrationData.getRecords().stream().map(Record::getSctid).collect(Collectors.toSet()))
						.buildAsync());
	}
	
	@ApiOperation(
		value = "Reserves SCTIDs",
		notes = "Reserves SCTIDs for use in an external system, based on the metadata passed in the ReservationData parameter. The first available SCTIDs will be reserved. Returns an array of SCTID Records with status 'Reserved'."
	)
	@PostMapping(value = "/reserve")
	public DeferredResult<BulkJob> reserveBulk(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody 
			BulkReservationData reservationData) {
		return runInJob("Reserve SCTIDs", identifiers
						.prepareReserve()
						.setCategory(reservationData.getComponentCategory())
						.setNamespace(reservationData.getNamespaceAsString())
						.setQuantity(reservationData.getQuantity())
						.buildAsync());
	}
	
	@ApiOperation(
		value = "Deprecates SCTIDs",
		notes = "Deprecates SCTIDs, so they will not be assigned to any component, based on the metadata passed in the DeprecationData parameter. Returns an array of SCTID Records with status 'Deprecated'."
	)
	@PutMapping(value = "/deprecate")
	public DeferredResult<BulkJob> deprecateBulk(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody 
			BulkDeprecationData deprecationData) {
		return runInJob("Deprecate SCTIDs", identifiers
						.prepareDeprecate()
						.setComponentIds(deprecationData.getComponentIds())
						.buildAsync());
	}

	@ApiOperation(
		value = "Release SCTIDs",
		notes = "Releases SCTIDs, so they will be available to be assigned again, based on the metadata passed in the DeprecationData parameter. Returns an array SCTID Records with status 'Available'."
	)
	@PutMapping(value = "/release")
	public DeferredResult<BulkJob> releaseBulk(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody 
			BulkReleaseData releaseData) {
		return runInJob("Release SCTIDs", identifiers
						.prepareRelease()
						.setComponentIds(releaseData.getComponentIds())
						.buildAsync());
	}

	@ApiOperation(
		value = "Publish SCTIDs",
		notes = "Sets the SCTIDs as published, based on the metadata passed in the DeprecationData parameter. Returns an array SCTID Records with status 'Published'."
	)
	@PutMapping(value = "/publish")
	public DeferredResult<BulkJob> publishBulk(
			@ApiParam(value = "The security access token.", required = true)
			@RequestParam(value = "token")
			String token,
			@ApiParam(value = "The requested operation.", required = true)
			@RequestBody 
			BulkPublicationData publicationData) {
		return runInJob("Publish SCTIDs", identifiers
				.preparePublish()
				.setComponentIds(publicationData.getComponentIds())
				.buildAsync());
	}
	
	private DeferredResult<BulkJob> runInJob(String jobDescription, AsyncRequest<?> request) {
		return DeferredResults.wrap(JobRequests.prepareSchedule()
				.setDescription(jobDescription)
				.setRequest(request)
				.setUser(User.SYSTEM.getUsername())
				.buildAsync()
				.execute(bus)
				.thenWith(id -> JobRequests.prepareGet(id).buildAsync().execute(bus))
				.then(BulkJob::fromRemoteJob));
	}
	
}
