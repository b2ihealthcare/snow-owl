/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.exceptions.ApiValidation;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.snomed.api.browser.ISnomedBrowserService;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationshipTarget;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipType;
import com.b2international.snowowl.snomed.api.rest.domain.ClassificationRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.ClassificationRunRestUpdate;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api(value = "Classifications", description="Classifications", tags = { "classifications" })
@Controller
@RequestMapping(value = "/classifications")
public class SnomedClassificationRestService extends AbstractRestService {

	@Autowired
	protected ISnomedBrowserService browserService;

	@ApiOperation(
			value="Retrieve classification runs from branch", 
			notes="Returns a list of classification runs for a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<ClassificationTasks> getAllClassificationRuns(
			@ApiParam(value="The branch path")
			@RequestParam(value="branch") 
			final String branch,
			
			@ApiParam(value="The classification status")
			@RequestParam(value="status") 
			final ClassificationStatus status,

			@ApiParam(value="The user identifier")
			@RequestParam(value="userId") 
			final String userId) {

		return DeferredResults.wrap(ClassificationRequests.prepareSearchClassification()
			.all()
			.filterByBranch(branch)
			.filterByUserId(userId)
			.filterByStatus(status)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(bus));
	}

	@ApiOperation(
			value="Start a classification on a branch",
			notes = "Classification runs are async jobs. The call to this method immediately returns with a unique URL "
					+ "pointing to the classification run.<p>The URL can be used to fetch the state of the classification "
					+ "to determine whether it's completed or not.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found", response=RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public ResponseEntity<?> beginClassification(
			@ApiParam(value="Classification parameters")
			@RequestBody 
			final ClassificationRestInput request,

			final Principal principal) {
		
		ApiValidation.checkInput(request);
		
		final String classificationId = ClassificationRequests.prepareCreateClassification()
				.setReasonerId(request.getReasonerId())
				.setUserId(principal.getName())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, request.getBranch())
				.execute(bus)
				.getSync();
		
		return Responses.created(getClassificationUri(classificationId)).build();
	}

	@ApiOperation(
			value="Retrieve the state of a classification run from branch")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@GetMapping(value = "/{classificationId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<ClassificationTask> getClassificationRun(
			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId) {

		return DeferredResults.wrap(ClassificationRequests.prepareGetClassification(classificationId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus));
	}

	@ApiOperation(
			value="Retrieve equivalent concepts from a classification run on a branch",
			notes="Returns a list of equivalent concept sets if the classification completed successfully; an empty JSON array "
					+ " is returned if the classification hasn't finished yet, or no equivalent concepts could be found.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@GetMapping(value = "/{classificationId}/equivalent-concepts", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<EquivalentConceptSets> getEquivalentConceptSets(
			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,
			
			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {

		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(acceptLanguage));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return DeferredResults.wrap(ClassificationRequests.prepareSearchEquivalentConceptSet()
				.all()
				.filterByClassificationId(classificationId)
				.setExpand("equivalentConcepts(expand(pt()))")
				.setLocales(extendedLocales)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus));
	}

	@ApiOperation(
			value="Retrieve relationship changes made by a classification run on a branch",
			notes="Returns a list of relationship changes if the classification completed successfully; an empty JSON array "
					+ " is returned if the classification hasn't finished yet, or no changed relationships could be found.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@GetMapping(
			value = "/{classificationId}/relationship-changes", 
			produces = { AbstractRestService.JSON_MEDIA_TYPE, AbstractRestService.CSV_MEDIA_TYPE })
	public @ResponseBody DeferredResult<RelationshipChanges> getRelationshipChanges(
			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@ApiParam(value="The search key")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		
		return DeferredResults.wrap(ClassificationRequests.prepareSearchRelationshipChange()
				.filterByClassificationId(classificationId)
				.setExpand("relationship()")
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus));
	}

	@ApiOperation(
			value="Retrieve a preview of a concept with classification changes applied",
			notes="Retrieves a preview of single concept and related information on a branch with classification changes applied.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = Void.class),
			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{classificationId}/concept-preview/{conceptId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody
	ISnomedBrowserConcept getConceptDetails(
			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId")
			final String classificationId,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false)
			final String languageSetting) {

		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(languageSetting));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
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
					relationships.removeIf(r -> r.getId().equals(relationship.getId()));
					break;
					
				case INFERRED:
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
			}
		}
		
		return conceptDetails;
	}

	@ApiOperation(
			value="Update a classification run on a branch",
			notes="Update the specified classification run by changing its state property. Saving the results is an async operation "
					+ "due to the possible high number of changes. It is advised to fetch the state of the classification run until "
					+ "the state changes to 'SAVED' or 'SAVE_FAILED'.<p>"
					+ "Currently only the state can be changed from 'COMPLETED' to 'SAVED'.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, update successful"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@PutMapping(value = "/{classificationId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void updateClassificationRun(
			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@ApiParam(value="The updated classification parameters")
			@RequestBody 
			final ClassificationRunRestUpdate updatedRun,
			
			final Principal principal) {
		
		// TODO: compare all fields to find out what the client wants us to do, check for conflicts, etc.
		if (ClassificationStatus.SAVED.equals(updatedRun.getStatus())) {
			ClassificationRequests.prepareSaveClassification(classificationId, principal.getName())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(bus)
					.getSync();
		}
	}

	@ApiOperation(
			value="Removes a classification run from a branch",
			notes="Classification runs remain available until they are explicitly deleted by the client. Results of the classification cannot be retrieved after deletion.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, delete successful"),
		@ApiResponse(code = 404, message = "Branch or classification not found", response=RestApiError.class)
	})
	@DeleteMapping(value = "/{classificationId}")
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void deleteClassificationRun(
			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId) {
		
		ClassificationRequests.prepareDeleteClassification(classificationId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(bus)
			.getSync();
	}

	private URI getClassificationUri(final String classificationId) {
		return linkTo(methodOn(SnomedClassificationRestService.class)
				.getClassificationRun(classificationId))
				.toUri();
	}
}
