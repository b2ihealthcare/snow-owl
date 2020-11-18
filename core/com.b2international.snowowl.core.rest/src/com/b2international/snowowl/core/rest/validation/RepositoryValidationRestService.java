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
package com.b2international.snowowl.core.rest.validation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.internal.validation.ValidationConfiguration;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.jobs.RemoteJobs;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.net.HttpHeaders;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Spring controller for exposing validation functionality.
 * 
 * @since 6.13
 */
@Api(value = "Validations", description = "Validations", tags = { "validations" })
@RequestMapping(value = "/validations", produces={ AbstractRestService.JSON_MEDIA_TYPE })
public abstract class RepositoryValidationRestService extends AbstractRestService {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private final String repositoryId;
	
	public RepositoryValidationRestService(String repositoryId) {
		super();
		this.repositoryId = repositoryId;
	}
	
	@ApiOperation(
			value="Retrieve all validation runs from the termserver", 
			notes="Returns a list of validations runs")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@GetMapping
	public @ResponseBody Promise<RemoteJobs> getAllValidationRuns() {
		return JobRequests.prepareSearch()
			.all()
			.buildAsync()
			.execute(getBus())
			.then(jobs -> {
				final List<RemoteJobEntry> validationJobs = jobs.stream()
						.filter(ValidationRequests::isValidationJob)
						.map(entry -> RemoteJobEntry.from(entry).id(IDs.sha1(entry.getId())).build())
						.collect(Collectors.toList());
				return new RemoteJobs(validationJobs, null, jobs.getLimit(), validationJobs.size());
			});
	}
	
	@ApiOperation(
			value="Start a validation on a branch",
			notes = "Validation runs are async jobs. The call to this method immediately returns with a unique URL "
					+ "pointing to the validation run.<p>The URL can be used to fetch the state of the validation "
					+ "to determine whether it's completed or not.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch or CodeSystem not found", response=RestApiError.class),
		@ApiResponse(code = 409, message = "Validation job with the same id is already running", response=RestApiError.class)
	})
	@PostMapping(consumes={ AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public ResponseEntity<Void> beginValidation(
			@ApiParam(value="Validation parameters")
			@RequestBody 
			final ValidationRestInput validationInput) {

		final String uniqueJobId = ValidationRequests.createUniqueValidationJobKey(validationInput.getPath());
		
		final String jobId = ValidationRequests
				.prepareValidate()
				.setRuleParameters(ImmutableMap.of(
					ValidationConfiguration.IS_UNPUBLISHED_ONLY, validationInput.isUnpublishedOnly()
				))
				.setRuleIds(validationInput.getRuleIds())
				.build(repositoryId, validationInput.getPath())
				.runAsJobWithRestart(uniqueJobId, String.format("Validating '%s'", validationInput.getPath()))
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		
		return ResponseEntity.created(getResourceLocationURI(jobId)).build();
	}
	
	@ApiOperation(
			value="Retrieve the state of a validation run from branch")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Validation not found", response=RestApiError.class)
	})
	@GetMapping(value="/{validationId}")
	public @ResponseBody Promise<RemoteJobEntry> getValidationRun(
			@ApiParam(value="The validation identifier")
			@PathVariable(value="validationId") 
			final String validationId) {
		return JobRequests.prepareGet(validationId)
				.buildAsync()
				.execute(getBus());
	}

	@ApiOperation(
			value="Retrieve the validation issues from a completed validation on a branch. Output may differ by the chosen content type.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class)
	})
	@RequestMapping(value="/validations/{validationId}/issues", method=RequestMethod.GET, produces={ AbstractRestService.JSON_MEDIA_TYPE, AbstractRestService.CSV_MEDIA_TYPE })
	public @ResponseBody Promise<Collection<Object>> getValidationResults(
			@ApiParam(value="The unique validation identifier.")
			@PathVariable(value="validationId")
			final String validationId,
		
			@ApiParam(value="The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,
			
			@ApiParam(value="The maximum number of items to return", defaultValue = "50")
			@RequestParam(value="limit", defaultValue="50", required=false)   
			final int limit,
			
			@ApiIgnore
			@RequestHeader(value=HttpHeaders.ACCEPT, defaultValue=AbstractRestService.JSON_MEDIA_TYPE,  required=false)
			final String contentType) {
		final IEventBus bus = getBus();
		
		return getValidationRun(validationId).thenWith(validationJob -> {
			final CodeSystemURI codeSystemURI = getCodeSystemURIFromJob(validationJob);
			if (AbstractRestService.CSV_MEDIA_TYPE.equals(contentType)) {
				return ValidationRequests.issues().prepareSearch()
						.isWhitelisted(false)
						.all()
						.filterByCodeSystemUri(codeSystemURI)
						.sortBy(SortField.ascending(ValidationIssue.Fields.RULE_ID))
						.buildAsync()
						.execute(bus)
						.then(issues -> {
							final Set<String> rulesToFetch = issues.stream()
									.map(ValidationIssue::getRuleId)
									.collect(Collectors.toSet());
							final Map<String, String> ruleDescriptionById = ValidationRequests.rules().prepareSearch()
									.all()
									.filterByIds(rulesToFetch)
									.buildAsync()
									.execute(bus)
									.getSync(1, TimeUnit.MINUTES)
									.stream()
									.collect(Collectors.toMap(ValidationRule::getId, ValidationRule::getMessageTemplate));
							
							return issues.stream().map(issue -> {
								final String ruleId = issue.getRuleId();
								final String ruleDescription = ruleDescriptionById.get(ruleId);
								final String affectedComponentLabel = Iterables.getFirst(issue.getAffectedComponentLabels(), "No label found");
								final String affectedComponentId = issue.getAffectedComponent().getComponentId();
								return new ValidationIssueReport(ruleId, ruleDescription, affectedComponentId, affectedComponentLabel);
							}).collect(Collectors.toList());
						});
			} else {
				return ValidationRequests.issues().prepareSearch()
						.isWhitelisted(false)
						.setLimit(limit)
						.setSearchAfter(searchAfter)
						.filterByCodeSystemUri(codeSystemURI )
						.buildAsync()
						.execute(bus)
						.then(issues -> issues.getItems().stream().collect(Collectors.toList()));

			}
		});
	}
	
	private CodeSystemURI getCodeSystemURIFromJob(final RemoteJobEntry validationJob) {
		return new CodeSystemURI((String) validationJob.getParameters(objectMapper).get("uri"));
	}
	
}
