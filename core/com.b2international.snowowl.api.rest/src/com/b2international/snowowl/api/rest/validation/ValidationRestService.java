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
package com.b2international.snowowl.api.rest.validation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.security.Principal;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.admin.AbstractAdminRestService;
import com.b2international.snowowl.api.rest.domain.RestApiError;
import com.b2international.snowowl.api.rest.util.DeferredResults;
import com.b2international.snowowl.api.rest.util.Responses;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.internal.validation.ValidationConfiguration;
import com.b2international.snowowl.core.validation.ValidateRequestBuilder;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.ValidationResult;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobs;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Spring controller for exposing validation functionality.
 * 
 * @since 6.13
 */
@Api("Validations")
@Controller
@RequestMapping(produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class ValidationRestService extends AbstractAdminRestService {
	
	private Map<String, String> uniqueIdByUuid = Maps.newHashMap();
	
	@ApiOperation(
			value="Retrieve validation runs from branch", 
			notes="Returns a list of validations runs for a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class)
	})
	@RequestMapping(value="/validations", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<RemoteJobs> getAllValidationRuns(
			@ApiParam(value="The branch path")
			@RequestParam(value="branch", required=false) 
			final String branch) {

		return DeferredResults.wrap(JobRequests.prepareSearch()
			.all()
			.filterByIds(uniqueIdByUuid.values())
			.buildAsync()
			.execute(bus));
	}
	
	
	@ApiOperation(
			value="Start a validation on a branch",
			notes = "Validation runs are async jobs. The call to this method immediately returns with a unique URL "
					+ "pointing to the validation run.<p>The URL can be used to fetch the state of the validation "
					+ "to determine whether it's completed or not.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class),
		@ApiResponse(code = 409, message = "Validation job with the same id is already running", response=RestApiError.class)
	})
	@RequestMapping(
			value="/validations", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public DeferredResult<ResponseEntity<?>> beginValidation(
			@ApiParam(value="Validation parameters")
			@RequestBody 
			final ValidationRestInput validationInput,

			final Principal principal) {
		
		ApiValidation.checkInput(validationInput);
		final String shortName = SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
		final String separator = Branch.SEPARATOR;
		final String activeBranch = validationInput.branch();
		final String uniqueJobId = String.format("%s%s%s%s", "Validation", shortName, separator, activeBranch);
		
		final Map<String, Object> ruleParams = ImmutableMap.<String, Object>builder()
				.put(ValidationConfiguration.IS_UNPUBLISHED_ONLY, validationInput.isUnpublishedValidation())
				.build();
		
		final Promise<Boolean> deleteValidationJobPromise;
		final RemoteJobEntry remoteJobEntry = JobRequests.prepareSearch()
				.filterById(uniqueJobId)
				.buildAsync()
				.execute(bus)
				.getSync().stream()
				.sorted(Comparator.comparing(RemoteJobEntry::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.findFirst()
				.orElse(null);
		
		if (remoteJobEntry != null) {
			if (remoteJobEntry.isDone() || remoteJobEntry.isCancelled() || remoteJobEntry.isDeleted()) {
				deleteValidationJobPromise = JobRequests.prepareDelete(uniqueJobId)
					.buildAsync()
					.execute(bus);
			} else {
				return DeferredResults.wrap(Promise.immediate(Responses.status(HttpStatus.CONFLICT).build()));
			}
		} else {
			deleteValidationJobPromise = Promise.immediate(Boolean.TRUE);
		}
		
		
		final ControllerLinkBuilder linkBuilder = linkTo(ValidationRestService.class)
				.slash("validations");
		return DeferredResults.wrap(deleteValidationJobPromise
			.<String>thenWith(success -> {
				final ValidateRequestBuilder validateRequestBuilder = ValidationRequests
						.prepareValidate()
						.setRuleParameters(ruleParams)
						.setRuleIds(validationInput.ruleIds());
				
				final Request<ServiceProvider, ValidationResult> request = validateRequestBuilder
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, validationInput.branch())
						.getRequest();

					
				return JobRequests.prepareSchedule()
					.setRequest(request)
					.setDescription(String.format("Validating SNOMED CT on branch '%s'", validationInput.branch()))
					.setUser(principal.getName())
					.setId(uniqueJobId)
					.buildAsync()
					.execute(bus);
			})
			.then(success -> {
				final String validationUuid = UUID.randomUUID().toString();
				uniqueIdByUuid.put(validationUuid, uniqueJobId);
				
				final URI responseURI = linkBuilder.slash(validationUuid).toUri();
				return Responses.created(responseURI).build();
				
			})
			.fail(e -> {
				return Responses.status(HttpStatus.BAD_REQUEST).build();
			}));
	}
	
	@ApiOperation(
			value="Retrieve the state of a validation run from branch")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or validation not found", response=RestApiError.class)
	})
	@RequestMapping(value="/validations/{validationId}", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<RemoteJobEntry> getValidationRun(
			@ApiParam(value="The validation identifier")
			@PathVariable(value="validationId") 
			final String validationId) {
		
		final String uniqueJobId = uniqueIdByUuid.get(validationId);
		if (uniqueJobId != null) {
			return DeferredResults.wrap(JobRequests.prepareGet(uniqueJobId)
					.buildAsync()
					.execute(bus));
			
		} else {
			throw new NotFoundException("Validation job", validationId);
		}
				
	}
	
	@ApiOperation(
			value="Retrieve the validation issues that belong from the collection of ruleIds")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or validation not found", response=RestApiError.class)
	})
	@RequestMapping(value="/validations/results", method=RequestMethod.POST)
	public @ResponseBody DeferredResult<ValidationIssues> getValidationResults(
			@ApiParam(value="Validation ruleIds")
			@RequestBody
			final Set<String> ruleIds) {

		return DeferredResults.wrap(ValidationRequests.issues().prepareSearch().filterByRules(ruleIds).buildAsync().execute(bus));
	}
	
}
