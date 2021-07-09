/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.SnomedApiConfig;
import com.b2international.snowowl.snomed.core.rest.domain.ClassificationRunRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.ClassificationRunRestUpdate;
import com.b2international.snowowl.snomed.reasoner.domain.*;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.base.Strings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 */
@Tag(description="Classifications", name = "classifications")
@Controller
@RequestMapping(value = "/classifications")
public class SnomedClassificationRestService extends AbstractRestService {

	public SnomedClassificationRestService() {
		super(ClassificationTask.Fields.ALL);
	}
	
	@Operation(
		summary="Retrieve classification runs from branch", 
		description="Returns a list of classification runs for a branch."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<ClassificationTasks> getAllClassificationRuns(
			@Parameter(description = "The resource path", required = true)
			@RequestParam(value="path", required=false) 
			final String path,
			
			@Parameter(description ="The classification status")
			@RequestParam(value="status", required=false) 
			final ClassificationStatus status,

			@Parameter(description ="The user identifier")
			@RequestParam(value="userId", required=false) 
			final String userId,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description ="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort,
			
			@Parameter(description ="The maximum number of items to return")
			@RequestParam(value="limit", required=false) 
			final int limit) {

		return ClassificationRequests.prepareSearchClassification()
			.filterByBranch(path)
			.filterByUserId(userId)
			.filterByStatus(status)
			.sortBy(extractSortFields(sort))
			.setSearchAfter(searchAfter)
			.setLimit(limit)
			.build(SnomedApiConfig.REPOSITORY_ID)
			.execute(getBus());
	}

	@Operation(
		summary = "Start a classification on a branch",
		description = "Classification runs are async jobs. The call to this method immediately returns with a unique URL "
				+ "pointing to the classification run.<p>The URL can be used to fetch the state of the classification "
				+ "to determine whether it's completed or not."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public Promise<ResponseEntity<?>> beginClassification(
			@Parameter(description ="Classification parameters")
			@RequestBody 
			final ClassificationRunRestInput request,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		ApiValidation.checkInput(request);
		
		final UriComponentsBuilder linkTo = MvcUriComponentsBuilder.fromController(SnomedClassificationRestService.class);
		
		return ClassificationRequests.prepareCreateClassification()
				.setReasonerId(request.getReasonerId())
				.setUserId(author)
				.build(request.getPath())
				.execute(getBus())
				.then(id -> {
					final URI resourceUri = linkTo.pathSegment(id).build().toUri();
					return ResponseEntity.created(resourceUri).build();
				});
	}

	@Operation(
		summary="Retrieve the state of a classification run from branch"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Branch or classification not found")
	})
	@GetMapping(value = "/{classificationId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<ClassificationTask> getClassificationRun(
			@Parameter(description ="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId) {

		return ClassificationRequests.prepareGetClassification(classificationId)
				.build(SnomedApiConfig.REPOSITORY_ID)
				.execute(getBus());
	}

	@Operation(
		summary="Retrieve equivalent concepts from a classification run on a branch",
		description="Returns a list of equivalent concept sets if the classification completed successfully; an empty JSON array "
				+ " is returned if the classification hasn't finished yet, or no equivalent concepts could be found."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Branch or classification not found")
	})
	@GetMapping(value = "/{classificationId}/equivalent-concepts", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<EquivalentConceptSets> getEquivalentConceptSets(
			@Parameter(description ="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description ="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,
			
			@Parameter(description ="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {

		return ClassificationRequests.prepareSearchEquivalentConceptSet()
				.filterByClassificationId(classificationId)
				.setExpand("equivalentConcepts(expand(pt()))")
				.setLocales(acceptLanguage)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.build(SnomedApiConfig.REPOSITORY_ID)
				.execute(getBus());
	}

	@Operation(
		summary="Retrieve relationship changes made by a classification run on a branch",
		description="Returns a list of relationship changes if the classification completed successfully; an empty JSON array "
				+ " is returned if the classification hasn't finished yet, or no changed relationships could be found."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Branch or classification not found")
	})
	@GetMapping(
			value = "/{classificationId}/relationship-changes", 
			produces = { AbstractRestService.JSON_MEDIA_TYPE, AbstractRestService.CSV_MEDIA_TYPE })
	public @ResponseBody Promise<RelationshipChanges> getRelationshipChanges(
			@Parameter(description ="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,
			
			@Parameter(description ="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,

			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description ="The maximum number of items to return")
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
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.build(SnomedApiConfig.REPOSITORY_ID)
				.execute(getBus());
	}
	
//	@Operation(
//		summary="Retrieve a preview of a concept with classification changes applied",
//		description="Retrieves a preview of single concept and related information on a branch with classification changes applied."
//	)
////	@ApiResponses({
////			@ApiResponse(responseCode = "200", description = "OK", response = Void.class),
////			@ApiResponse(responseCode = "404", description = "Code system version or concept not found")
////	})
//	@GetMapping(value = "/{classificationId}/concept-preview/{conceptId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
//	public @ResponseBody
//	ISnomedBrowserConcept getConceptDetails(
//			@Parameter(value ="The classification identifier")
//			@PathVariable(value="classificationId")
//			final String classificationId,
//
//			@Parameter(value ="The concept identifier")
//			@PathVariable(value="conceptId")
//			final String conceptId,
//
//			@Parameter(value ="Language codes and reference sets, in order of preference")
//			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false)
//			final String languageSetting) {
//
//		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
//		
//		final ClassificationTask classificationTask = ClassificationRequests.prepareGetClassification(classificationId)
//			.setExpand(String.format("relationshipChanges(sourceId:\"%s\",expand(relationship()))", conceptId))
//			.build(SnomedApiConfig.REPOSITORY_ID)
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
//							.build(SnomedApiConfig.REPOSITORY_ID, branchPath)
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

	@Operation(
		summary="Update a classification run on a branch",
		description="Update the specified classification run by changing its state property. Saving the results is an async operation "
				+ "due to the possible high number of changes. It is advised to fetch the state of the classification run until "
				+ "the state changes to 'SAVED' or 'SAVE_FAILED'.<p>"
				+ "Currently only the state can be changed from 'COMPLETED' to 'SAVED'."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content, update successful"),
		@ApiResponse(responseCode = "404", description = "Branch or classification not found")
	})
	@PutMapping(value = "/{classificationId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void updateClassificationRun(
			@Parameter(description = "The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@Parameter(description = "The updated classification parameters")
			@RequestBody 
			final ClassificationRunRestUpdate update,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		ApiValidation.checkInput(update);
		
		// TODO: compare all fields to find out what the client wants us to do, check for conflicts, etc.
		if (ClassificationStatus.SAVED.equals(update.getStatus())) {
			ClassificationRequests.prepareSaveClassification()
					.setClassificationId(classificationId)
					.setAssignerType(update.getAssigner())
					.setModuleId(update.getModule())
					.setNamespace(update.getNamespace())
					.setUserId(author)
					.build(SnomedApiConfig.REPOSITORY_ID)
					.execute(getBus())
					.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
		}
	}

	@Operation(
		summary="Removes a classification run from a branch",
		description="Classification runs remain available until they are explicitly deleted by the client. Results of the classification cannot be retrieved after deletion."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content, delete successful"),
		@ApiResponse(responseCode = "404", description = "Branch or classification not found")
	})
	@DeleteMapping(value = "/{classificationId}")
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void deleteClassificationRun(
			@Parameter(description = "The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId) {
		
		ClassificationRequests.prepareDeleteClassification(classificationId)
			.build(SnomedApiConfig.REPOSITORY_ID)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}

}
