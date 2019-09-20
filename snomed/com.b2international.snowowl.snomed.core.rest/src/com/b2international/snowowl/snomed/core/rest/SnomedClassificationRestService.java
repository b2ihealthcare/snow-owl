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
package com.b2international.snowowl.snomed.core.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.util.DeferredResults;
import com.b2international.snowowl.core.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.rest.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.core.rest.browser.ISnomedBrowserRelationship;
import com.b2international.snowowl.snomed.core.rest.browser.ISnomedBrowserRelationshipTarget;
import com.b2international.snowowl.snomed.core.rest.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.core.rest.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.core.rest.browser.SnomedBrowserRelationshipType;
import com.b2international.snowowl.snomed.core.rest.browser.SnomedBrowserService;
import com.b2international.snowowl.snomed.core.rest.domain.ClassificationRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.ClassificationRunRestUpdate;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTasks;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerRelationship;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChange;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.base.Strings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 */
@Tag(name = "classifications", description="Classifications")
@Controller
@RequestMapping(value = "/classifications")
public class SnomedClassificationRestService extends AbstractRestService {

	@Autowired
	protected SnomedBrowserService browserService;

	public SnomedClassificationRestService() {
		super(ClassificationTask.Fields.ALL);
	}
	
	@Operation(
		summary="Retrieve classification runs from branch", 
		description="Returns a list of classification runs for a branch."
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class)
//	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<ClassificationTasks> getAllClassificationRuns(
			@Parameter(description="The branch path")
			@RequestParam(value="branch", required=false) 
			final String branch,
			
			@Parameter(description="The classification status")
			@RequestParam(value="status", required=false) 
			final ClassificationStatus status,

			@Parameter(description="The user identifier")
			@RequestParam(value="userId", required=false) 
			final String userId,
			
			@Parameter(description = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@Parameter(description = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort,
			
			@Parameter(description="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {

		return DeferredResults.wrap(ClassificationRequests.prepareSearchClassification()
			.filterByBranch(branch)
			.filterByUserId(userId)
			.filterByStatus(status)
			.sortBy(extractSortFields(sort))
			.setScroll(scrollKeepAlive)
			.setScrollId(scrollId)
			.setSearchAfter(searchAfter)
			.setLimit(limit)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(bus));
	}

	@Operation(
		summary="Start a classification on a branch",
		description = "Classification runs are async jobs. The call to this method immediately returns with a unique URL "
				+ "pointing to the classification run.<p>The URL can be used to fetch the state of the classification "
				+ "to determine whether it's completed or not."
	)
//	@ApiResponses({
//		@ApiResponse(code = 201, message = "Created"),
//		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class)
//	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public DeferredResult<ResponseEntity<?>> beginClassification(
			@Parameter(description="Classification parameters")
			@RequestBody 
			final ClassificationRestInput request,

			final Principal principal) {
		
		ApiValidation.checkInput(request);
		
		final ControllerLinkBuilder linkBuilder = linkTo(SnomedClassificationRestService.class)
				.slash("classifications");
		
		return DeferredResults.wrap(ClassificationRequests.prepareCreateClassification()
				.setReasonerId(request.getReasonerId())
				.setUserId(principal.getName())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, request.getBranch())
				.execute(bus)
				.then(id -> {
					final URI resourceUri = linkBuilder.slash(id).toUri();
					return Responses.created(resourceUri).build();
				}));
	}

	@Operation(
		summary="Retrieve the state of a classification run from branch"
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
//	})
	@GetMapping(value = "/{classificationId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<ClassificationTask> getClassificationRun(
			@Parameter(description="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId) {

		return DeferredResults.wrap(ClassificationRequests.prepareGetClassification(classificationId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus));
	}

	@Operation(
		summary="Retrieve equivalent concepts from a classification run on a branch",
		description="Returns a list of equivalent concept sets if the classification completed successfully; an empty JSON array "
				+ " is returned if the classification hasn't finished yet, or no equivalent concepts could be found."
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
//	})
	@GetMapping(value = "/{classificationId}/equivalent-concepts", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<EquivalentConceptSets> getEquivalentConceptSets(
			@Parameter(description="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,
			
			@Parameter(description = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@Parameter(description = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,
			
			@Parameter(description="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(acceptLanguage);
		return DeferredResults.wrap(ClassificationRequests.prepareSearchEquivalentConceptSet()
				.filterByClassificationId(classificationId)
				.setExpand("equivalentConcepts(expand(pt()))")
				.setLocales(extendedLocales)
				.setScroll(scrollKeepAlive)
				.setScrollId(scrollId)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus));
	}

	@Operation(
		summary="Retrieve relationship changes made by a classification run on a branch",
		description="Returns a list of relationship changes if the classification completed successfully; an empty JSON array "
				+ " is returned if the classification hasn't finished yet, or no changed relationships could be found."
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
//	})
	@GetMapping(
			value = "/{classificationId}/relationship-changes", 
			produces = { AbstractRestService.JSON_MEDIA_TYPE, AbstractRestService.CSV_MEDIA_TYPE })
	public @ResponseBody DeferredResult<RelationshipChanges> getRelationshipChanges(
			@Parameter(description="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,
			
			@Parameter(description="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,

			@Parameter(description = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@Parameter(description = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		
		final String expandWithRelationship;
		if (Strings.isNullOrEmpty(expand)) {
			expandWithRelationship = "relationship()";
		} else {
			expandWithRelationship = String.format("relationship(expand(%s))", expand);
		}
		
		return DeferredResults.wrap(ClassificationRequests.prepareSearchRelationshipChange()
				.filterByClassificationId(classificationId)
				.setExpand(expandWithRelationship)
				.setScroll(scrollKeepAlive)
				.setScrollId(scrollId)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus));
	}
	
	@Operation(
		summary="Retrieve a preview of a concept with classification changes applied",
		description="Retrieves a preview of single concept and related information on a branch with classification changes applied."
	)
//	@ApiResponses({
//			@ApiResponse(code = 200, message = "OK", response = Void.class),
//			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
//	})
	@GetMapping(value = "/{classificationId}/concept-preview/{conceptId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody
	ISnomedBrowserConcept getConceptDetails(
			@Parameter(description="The classification identifier")
			@PathVariable(value="classificationId")
			final String classificationId,

			@Parameter(description="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@Parameter(description="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false)
			final String languageSetting) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		
		final ClassificationTask classificationTask = ClassificationRequests.prepareGetClassification(classificationId)
			.setExpand(String.format("relationshipChanges(sourceId:\"%s\",expand(relationship()))", conceptId))
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(bus)
			.getSync();
		
		final String branchPath = classificationTask.getBranch();
		final SnomedBrowserConcept conceptDetails = (SnomedBrowserConcept) browserService.getConceptDetails(branchPath, conceptId, extendedLocales);

		// Replace ImmutableCollection of relationships
		final List<ISnomedBrowserRelationship> relationships = new ArrayList<ISnomedBrowserRelationship>(conceptDetails.getRelationships());
		conceptDetails.setRelationships(relationships);

		for (RelationshipChange relationshipChange : classificationTask.getRelationshipChanges()) {
			final ReasonerRelationship relationship = relationshipChange.getRelationship();
			
			switch (relationshipChange.getChangeNature()) {
				case REDUNDANT:
					relationships.removeIf(r -> r.getId().equals(relationship.getOriginId()));
					break;
				
				case UPDATED:
					relationships.stream()
						.filter(r -> r.getId().equals(relationship.getOriginId()))
						.findFirst()
						.ifPresent(r -> ((SnomedBrowserRelationship) r).setGroupId(relationship.getGroup()));
					break;
					
				case NEW:
					final SnomedBrowserRelationship inferred = new SnomedBrowserRelationship();
					inferred.setType(new SnomedBrowserRelationshipType(relationship.getTypeId()));
					inferred.setSourceId(relationship.getSourceId());

					final SnomedConcept targetConcept = SnomedRequests.prepareGetConcept(relationship.getDestinationId())
							.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
							.execute(bus)
							.getSync();
					final ISnomedBrowserRelationshipTarget relationshipTarget = browserService.getSnomedBrowserRelationshipTarget(
							targetConcept, branchPath, extendedLocales);
					inferred.setTarget(relationshipTarget);

					inferred.setGroupId(relationship.getGroup());
					inferred.setModifier(relationship.getModifier());
					inferred.setActive(true);
					inferred.setCharacteristicType(relationship.getCharacteristicType());

					relationships.add(inferred);
					break;
				default:
					throw new IllegalStateException(String.format("Unexpected relationship change '%s' found with SCTID '%s'.", 
							relationshipChange.getChangeNature(), 
							relationshipChange.getRelationship().getOriginId()));
			}
		}
		
		return conceptDetails;
	}

	@Operation(
		summary="Update a classification run on a branch",
		description="Update the specified classification run by changing its state property. Saving the results is an async operation "
				+ "due to the possible high number of changes. It is advised to fetch the state of the classification run until "
				+ "the state changes to 'SAVED' or 'SAVE_FAILED'.<p>"
				+ "Currently only the state can be changed from 'COMPLETED' to 'SAVED'."
	)
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "No content, update successful"),
//		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
//	})
	@PutMapping(value = "/{classificationId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void updateClassificationRun(
			@Parameter(description="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@Parameter(description="The updated classification parameters")
			@RequestBody 
			final ClassificationRunRestUpdate updatedRun,
			
			final Principal principal) {
		
		// TODO: compare all fields to find out what the client wants us to do, check for conflicts, etc.
		if (ClassificationStatus.SAVED.equals(updatedRun.getStatus())) {
			ClassificationRequests.prepareSaveClassification()
					.setClassificationId(classificationId)
					.setUserId(principal.getName())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(bus)
					.getSync();
		}
	}

	@Operation(
		summary="Removes a classification run from a branch",
		description="Classification runs remain available until they are explicitly deleted by the client. Results of the classification cannot be retrieved after deletion."
	)
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "No content, delete successful"),
//		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
//	})
	@DeleteMapping(value = "/{classificationId}")
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void deleteClassificationRun(
			@Parameter(description="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId) {
		
		ClassificationRequests.prepareDeleteClassification(classificationId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(bus)
			.getSync();
	}

}
