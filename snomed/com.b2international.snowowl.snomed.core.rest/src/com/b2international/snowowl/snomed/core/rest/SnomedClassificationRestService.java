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

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.snomed.core.rest.domain.ClassificationRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.ClassificationRunRestUpdate;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTasks;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.base.Strings;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api(value = "Classifications", description="Classifications", tags = "classifications")
@Controller
@RequestMapping(value = "/classifications")
public class SnomedClassificationRestService extends AbstractRestService {

	public SnomedClassificationRestService() {
		super(ClassificationTask.Fields.ALL);
	}
	
	@ApiOperation(
		value="Retrieve classification runs from branch", 
		notes="Returns a list of classification runs for a branch."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<ClassificationTasks> getAllClassificationRuns(
			@ApiParam(value ="The branch path")
			@RequestParam(value="branch", required=false) 
			final String branch,
			
			@ApiParam(value ="The classification status")
			@RequestParam(value="status", required=false) 
			final ClassificationStatus status,

			@ApiParam(value ="The user identifier")
			@RequestParam(value="userId", required=false) 
			final String userId,
			
			@ApiParam(value = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@ApiParam(value = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@ApiParam(value = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@ApiParam(value ="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort,
			
			@ApiParam(value ="The maximum number of items to return", defaultValue = "50")
			@RequestParam(value="limit", required=false) 
			final int limit) {

		return ClassificationRequests.prepareSearchClassification()
			.filterByBranch(branch)
			.filterByUserId(userId)
			.filterByStatus(status)
			.sortBy(extractSortFields(sort))
			.setScroll(scrollKeepAlive)
			.setScrollId(scrollId)
			.setSearchAfter(searchAfter)
			.setLimit(limit)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(getBus());
	}

	@ApiOperation(
		value = "Start a classification on a branch",
		notes = "Classification runs are async jobs. The call to this method immediately returns with a unique URL "
				+ "pointing to the classification run.<p>The URL can be used to fetch the state of the classification "
				+ "to determine whether it's completed or not."
	)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public Promise<ResponseEntity<?>> beginClassification(
			@ApiParam(value ="Classification parameters")
			@RequestBody 
			final ClassificationRestInput request,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		ApiValidation.checkInput(request);
		
		final UriComponentsBuilder linkTo = MvcUriComponentsBuilder.fromController(SnomedClassificationRestService.class);
		
		return ClassificationRequests.prepareCreateClassification()
				.setReasonerId(request.getReasonerId())
				.setUserId(author)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, request.getBranch())
				.execute(getBus())
				.then(id -> {
					final URI resourceUri = linkTo.pathSegment(id).build().toUri();
					return ResponseEntity.created(resourceUri).build();
				});
	}

	@ApiOperation(
		value="Retrieve the state of a classification run from branch"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@GetMapping(value = "/{classificationId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<ClassificationTask> getClassificationRun(
			@ApiParam(value ="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId) {

		return ClassificationRequests.prepareGetClassification(classificationId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getBus());
	}

	@ApiOperation(
		value="Retrieve equivalent concepts from a classification run on a branch",
		notes="Returns a list of equivalent concept sets if the classification completed successfully; an empty JSON array "
				+ " is returned if the classification hasn't finished yet, or no equivalent concepts could be found."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@GetMapping(value = "/{classificationId}/equivalent-concepts", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<EquivalentConceptSets> getEquivalentConceptSets(
			@ApiParam(value ="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,
			
			@ApiParam(value = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@ApiParam(value = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@ApiParam(value = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@ApiParam(value ="The maximum number of items to return", defaultValue = "50")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,
			
			@ApiParam(value ="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(acceptLanguage);
		return ClassificationRequests.prepareSearchEquivalentConceptSet()
				.filterByClassificationId(classificationId)
				.setExpand("equivalentConcepts(expand(pt()))")
				.setLocales(extendedLocales)
				.setScroll(scrollKeepAlive)
				.setScrollId(scrollId)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getBus());
	}

	@ApiOperation(
		value="Retrieve relationship changes made by a classification run on a branch",
		notes="Returns a list of relationship changes if the classification completed successfully; an empty JSON array "
				+ " is returned if the classification hasn't finished yet, or no changed relationships could be found."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@GetMapping(
			value = "/{classificationId}/relationship-changes", 
			produces = { AbstractRestService.JSON_MEDIA_TYPE, AbstractRestService.CSV_MEDIA_TYPE })
	public @ResponseBody Promise<RelationshipChanges> getRelationshipChanges(
			@ApiParam(value ="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,
			
			@ApiParam(value ="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,

			@ApiParam(value = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@ApiParam(value = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@ApiParam(value = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@ApiParam(value ="The maximum number of items to return", defaultValue = "50")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		
		final String expandWithRelationship;
		if (Strings.isNullOrEmpty(expand)) {
			expandWithRelationship = "relationship()";
		} else {
			expandWithRelationship = String.format("relationship(expand(%s))", expand);
		}
		
		return ClassificationRequests.prepareSearchRelationshipChange()
				.filterByClassificationId(classificationId)
				.setExpand(expandWithRelationship)
				.setScroll(scrollKeepAlive)
				.setScrollId(scrollId)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getBus());
	}
	
//	@Operation(
//		summary="Retrieve a preview of a concept with classification changes applied",
//		description="Retrieves a preview of single concept and related information on a branch with classification changes applied."
//	)
////	@ApiResponses({
////			@ApiResponse(code = 200, message = "OK", response = Void.class),
////			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
////	})
//	@GetMapping(value = "/{classificationId}/concept-preview/{conceptId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
//	public @ResponseBody
//	ISnomedBrowserConcept getConceptDetails(
//			@ApiParam(value ="The classification identifier")
//			@PathVariable(value="classificationId")
//			final String classificationId,
//
//			@ApiParam(value ="The concept identifier")
//			@PathVariable(value="conceptId")
//			final String conceptId,
//
//			@ApiParam(value ="Language codes and reference sets, in order of preference")
//			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false)
//			final String languageSetting) {
//
//		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
//		
//		final ClassificationTask classificationTask = ClassificationRequests.prepareGetClassification(classificationId)
//			.setExpand(String.format("relationshipChanges(sourceId:\"%s\",expand(relationship()))", conceptId))
//			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
//			.execute(getBus())
//			.getSync();
//		
//		final String branchPath = classificationTask.getBranch();
//		final SnomedBrowserConcept conceptDetails = (SnomedBrowserConcept) browserService.getConceptDetails(branchPath, conceptId, extendedLocales);
//
//		// Replace ImmutableCollection of relationships
//		final List<ISnomedBrowserRelationship> relationships = new ArrayList<ISnomedBrowserRelationship>(conceptDetails.getRelationships());
//		conceptDetails.setRelationships(relationships);
//
//		for (RelationshipChange relationshipChange : classificationTask.getRelationshipChanges()) {
//			final ReasonerRelationship relationship = relationshipChange.getRelationship();
//			
//			switch (relationshipChange.getChangeNature()) {
//				case REDUNDANT:
//					relationships.removeIf(r -> r.getId().equals(relationship.getOriginId()));
//					break;
//				
//				case UPDATED:
//					relationships.stream()
//						.filter(r -> r.getId().equals(relationship.getOriginId()))
//						.findFirst()
//						.ifPresent(r -> ((SnomedBrowserRelationship) r).setGroupId(relationship.getGroup()));
//					break;
//					
//				case NEW:
//					final SnomedBrowserRelationship inferred = new SnomedBrowserRelationship();
//					inferred.setType(new SnomedBrowserRelationshipType(relationship.getTypeId()));
//					inferred.setSourceId(relationship.getSourceId());
//
//					final SnomedConcept targetConcept = SnomedRequests.prepareGetConcept(relationship.getDestinationId())
//							.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
//							.execute(getBus())
//							.getSync();
//					final ISnomedBrowserRelationshipTarget relationshipTarget = browserService.getSnomedBrowserRelationshipTarget(
//							targetConcept, branchPath, extendedLocales);
//					inferred.setTarget(relationshipTarget);
//
//					inferred.setGroupId(relationship.getGroup());
//					inferred.setModifier(relationship.getModifier());
//					inferred.setActive(true);
//					inferred.setCharacteristicType(relationship.getCharacteristicType());
//
//					relationships.add(inferred);
//					break;
//				default:
//					throw new IllegalStateException(String.format("Unexpected relationship change '%s' found with SCTID '%s'.", 
//							relationshipChange.getChangeNature(), 
//							relationshipChange.getRelationship().getOriginId()));
//			}
//		}
//		
//		return conceptDetails;
//	}

	@ApiOperation(
		value="Update a classification run on a branch",
		notes="Update the specified classification run by changing its state property. Saving the results is an async operation "
				+ "due to the possible high number of changes. It is advised to fetch the state of the classification run until "
				+ "the state changes to 'SAVED' or 'SAVE_FAILED'.<p>"
				+ "Currently only the state can be changed from 'COMPLETED' to 'SAVED'."
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, update successful"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@PutMapping(value = "/{classificationId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void updateClassificationRun(
			@ApiParam(value = "The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@ApiParam(value = "The updated classification parameters")
			@RequestBody 
			final ClassificationRunRestUpdate updatedRun,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		// TODO: compare all fields to find out what the client wants us to do, check for conflicts, etc.
		if (ClassificationStatus.SAVED.equals(updatedRun.getStatus())) {
			ClassificationRequests.prepareSaveClassification()
					.setClassificationId(classificationId)
					.setUserId(author)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(getBus())
					.getSync();
		}
	}

	@ApiOperation(
		value="Removes a classification run from a branch",
		notes="Classification runs remain available until they are explicitly deleted by the client. Results of the classification cannot be retrieved after deletion."
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, delete successful"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@DeleteMapping(value = "/{classificationId}")
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void deleteClassificationRun(
			@ApiParam(value = "The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId) {
		
		ClassificationRequests.prepareDeleteClassification(classificationId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(getBus())
			.getSync();
	}

}
