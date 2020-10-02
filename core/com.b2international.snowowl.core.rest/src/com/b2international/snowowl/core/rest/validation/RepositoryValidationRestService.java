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
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.net.HttpHeaders;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Spring controller for exposing validation functionality.
 * 
 * @since 6.13
 */
@Tag(description="Validations", name = "validations")
@RequestMapping(value = "/validations", produces={ AbstractRestService.JSON_MEDIA_TYPE })
public abstract class RepositoryValidationRestService extends AbstractRestService {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private final String repositoryId;
	
	public RepositoryValidationRestService(String repositoryId) {
		super();
		this.repositoryId = repositoryId;
	}
	
	@Operation(
		summary="Retrieve all validation runs from the termserver", 
		description="Returns a list of validations runs"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK")
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
	
	@Operation(
		summary="Start a validation on a branch",
		description = "Validation runs are async jobs. The call to this method immediately returns with a unique URL "
					+ "pointing to the validation run.<p>The URL can be used to fetch the state of the validation "
					+ "to determine whether it's completed or not.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description="Created"),
		@ApiResponse(responseCode = "404", description="Branch or CodeSystem not found"),
		@ApiResponse(responseCode = "409", description="Validation job with the same id is already running")
	})
	@PostMapping(consumes={ AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public ResponseEntity<Void> beginValidation(
			@Parameter(description="Validation parameters")
			@RequestBody 
			final ValidationRestInput validationInput) {

		final String uniqueJobId = ValidationRequests.createUniqueValidationJobKey(repositoryId, validationInput.getBranchPath());
		
		final String jobId = ValidationRequests
				.prepareValidate()
				.setRuleParameters(ImmutableMap.of(
					ValidationConfiguration.IS_UNPUBLISHED_ONLY, validationInput.isUnpublishedOnly()
				))
				.setRuleIds(validationInput.getRuleIds())
				.build(repositoryId, validationInput.getBranchPath())
				.runAsJobWithRestart(uniqueJobId, String.format("Validating branch '%s'", validationInput.getBranchPath()))
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		
		return ResponseEntity.created(getResourceLocationURI(jobId)).build();
	}
	
	@Operation(
		summary="Retrieve the state of a validation run from branch"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK"),
		@ApiResponse(responseCode = "404", description="Validation not found")
	})
	@GetMapping(value="/{validationId}")
	public @ResponseBody Promise<RemoteJobEntry> getValidationRun(
			@Parameter(description="The validation identifier")
			@PathVariable(value="validationId") 
			final String validationId) {
		return JobRequests.prepareGet(validationId)
				.buildAsync()
				.execute(getBus());
	}

	@Operation(
		summary="Retrieve the validation issues from a completed validation on a branch. Output may differ by the chosen content type."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK"),
		@ApiResponse(responseCode = "404", description="Branch not found")
	})
	@RequestMapping(value="/validations/{validationId}/issues", method=RequestMethod.GET, produces={ AbstractRestService.JSON_MEDIA_TYPE, AbstractRestService.CSV_MEDIA_TYPE })
	public @ResponseBody Promise<Collection<Object>> getValidationResults(
			@Parameter(description="The unique validation identifier.")
			@PathVariable(value="validationId")
			final String validationId,
		
			@Parameter(description="The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,
			
			@Parameter(description="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false)   
			final int limit,
			
			@Parameter(hidden = true)
			@RequestHeader(value=HttpHeaders.ACCEPT, defaultValue=AbstractRestService.JSON_MEDIA_TYPE,  required=false)
			final String contentType) {
		final IEventBus bus = getBus();
		
		return getValidationRun(validationId).thenWith(validationJob -> {
			if (AbstractRestService.CSV_MEDIA_TYPE.equals(contentType)) {
				final String branchPath = getBranchFromJob(validationJob);
				return ValidationRequests.issues().prepareSearch()
						.isWhitelisted(false)
						.all()
						.filterByBranchPath(branchPath)
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
				final String branchPath = getBranchFromJob(validationJob);

				return ValidationRequests.issues().prepareSearch()
						.isWhitelisted(false)
						.setLimit(limit)
						.setSearchAfter(searchAfter)
						.filterByBranchPath(branchPath)
						.buildAsync()
						.execute(bus)
						.then(issues -> issues.getItems().stream().collect(Collectors.toList()));

			}
		});
	}
	
	private String getBranchFromJob(final RemoteJobEntry validationJob) {
		return (String) validationJob.getParameters(objectMapper).get("branchPath");
	}
	
}
