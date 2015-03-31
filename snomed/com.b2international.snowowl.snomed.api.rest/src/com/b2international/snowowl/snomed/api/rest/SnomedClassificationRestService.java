/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.b2international.snowowl.snomed.api.ISnomedClassificationService;
import com.b2international.snowowl.snomed.api.domain.classification.ClassificationStatus;
import com.b2international.snowowl.snomed.api.domain.classification.IClassificationRun;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConceptSet;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChange;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChangeList;
import com.b2international.snowowl.snomed.api.rest.domain.ClassificationRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.ClassificationRestRun;
import com.b2international.snowowl.snomed.api.rest.domain.CollectionResource;
import com.b2international.snowowl.snomed.api.rest.domain.PageableCollectionResource;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("SNOMED CT Classification")
@Controller
@RequestMapping(
		value="/{version}", 
		produces={ AbstractRestService.V1_MEDIA_TYPE })
public class SnomedClassificationRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedClassificationService delegate;

	@ApiOperation(
			value="Retrieve classification runs from version", 
			notes="Returns a list of classification runs for a version branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version not found")
	})
	@RequestMapping(value="/classifications", method=RequestMethod.GET)
	public @ResponseBody CollectionResource<IClassificationRun> getAllClassificationRuns(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			final Principal principal) {

		return getAllClassificationRunsOnTask(version, null, principal);
	}

	@ApiOperation(
			value="Retrieve classification runs from task", 
			notes="Returns a list of classification runs for a task branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or task not found")
	})
	@RequestMapping(value="/tasks/{taskId}/classifications", method=RequestMethod.GET)
	public @ResponseBody CollectionResource<IClassificationRun> getAllClassificationRunsOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId") 
			final String taskId,

			final Principal principal) {

		return CollectionResource.of(delegate.getAllClassificationRuns(version, taskId, principal.getName()));
	}

	@ApiOperation(
			value="Start a classification on a version",
			notes = "Classification runs are async jobs. The call to this method immediately returns with a unique URL "
					+ "pointing to the classification run.<p>The URL can be used to fetch the state of the classification "
					+ "to determine whether it's completed or not.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Code system version not found")
	})
	@RequestMapping(
			value="/classifications", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public ResponseEntity<Void> beginClassification(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="Classification parameters")
			@RequestBody 
			final ClassificationRestInput classificationInput,

			final Principal principal) {

		final IClassificationRun classificationRun = delegate.beginClassification(version, null, classificationInput.getReasonerId(), principal.getName());
		return Responses.created(getClassificationUri(version, principal, classificationRun)).build();
	}

	@ApiOperation(
			value="Start a classification on a task",
			notes = "Classification runs are async jobs. The call to this method immediately returns with a unique URL "
					+ "pointing to the classification run.<p>The URL can be used to fetch the state of the classification "
					+ "to determine whether it's completed or not.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Code system version or task not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/classifications", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public ResponseEntity<Void> beginClassificationOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId") final String taskId,

			@ApiParam(value="Classification parameters")
			@RequestBody final ClassificationRestInput classificationInput,

			final Principal principal) {

		final IClassificationRun classificationRun = delegate.beginClassification(version, taskId, classificationInput.getReasonerId(), principal.getName());
		return Responses.created(getClassificationUriOnTask(version, taskId, principal, classificationRun)).build();
	}

	@ApiOperation(
			value="Retrieve the state of a classification run from version")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or classification not found")
	})
	@RequestMapping(value="/classifications/{classificationId}", method=RequestMethod.GET)
	public @ResponseBody IClassificationRun getClassificationRun(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,
			
			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			final Principal principal) {

		return getClassificationRunOnTask(version, null, classificationId, principal);
	}

	@ApiOperation(
			value="Retrieve the state of a classification run from task")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version, task or classification not found")
	})
	@RequestMapping(value="/tasks/{taskId}/classifications/{classificationId}", method=RequestMethod.GET)
	public @ResponseBody IClassificationRun getClassificationRunOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId") final String taskId,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			final Principal principal) {

		return delegate.getClassificationRun(version, taskId, classificationId, principal.getName());
	}

	@ApiOperation(
			value="Retrieve equivalent concepts from a classification run on a version",
			notes="Returns a list of equivalent concept sets if the classification completed successfully; an empty JSON array "
					+ " is returned if the classification hasn't finished yet, or no equivalent concepts could be found.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or classification not found")
	})
	@RequestMapping(value="/classifications/{classificationId}/equivalent-concepts", method=RequestMethod.GET)
	public @ResponseBody CollectionResource<IEquivalentConceptSet> getEquivalentConceptSets(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			final Principal principal) {

		return getEquivalentConceptSetsOnTask(version, null, classificationId, principal);
	}

	@ApiOperation(
			value="Retrieve equivalent concepts from a classification run on a task",
			notes="Returns a list of equivalent concept sets if the classification completed successfully; an empty JSON array "
					+ " is returned if the classification hasn't finished yet, or no equivalent concepts could be found.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version, task or classification not found")
	})
	@RequestMapping(value="/tasks/{taskId}/classifications/{classificationId}/equivalent-concepts", method=RequestMethod.GET)
	public @ResponseBody CollectionResource<IEquivalentConceptSet> getEquivalentConceptSetsOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId") 
			final String taskId,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			final Principal principal) {

		return CollectionResource.of(delegate.getEquivalentConceptSets(version, taskId, classificationId, principal.getName()));
	}

	@ApiOperation(
			value="Retrieve relationship changes made by a classification run on a version",
			notes="Returns a list of relationship changes if the classification completed successfully; an empty JSON array "
					+ " is returned if the classification hasn't finished yet, or no changed relationships could be found.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or classification not found")
	})
	@RequestMapping(value="/classifications/{classificationId}/relationship-changes", method=RequestMethod.GET)
	public @ResponseBody PageableCollectionResource<IRelationshipChange> getRelationshipChanges(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,

			final Principal principal) {

		return getRelationshipChangesOnTask(version, null, classificationId, offset, limit, principal);
	}

	@ApiOperation(
			value="Retrieve relationship changes made by a classification run on a task",
			notes="Returns a list of relationship changes if the classification completed successfully; an empty JSON array "
					+ " is returned if the classification hasn't finished yet, or no changed relationships could be found.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version, task or classification not found")
	})
	@RequestMapping(value="/tasks/{taskId}/classifications/{classificationId}/relationship-changes", method=RequestMethod.GET)
	public @ResponseBody PageableCollectionResource<IRelationshipChange> getRelationshipChangesOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId") 
			final String taskId,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,

			final Principal principal) {

		final IRelationshipChangeList relationshipChangeList = delegate.getRelationshipChanges(version, taskId, classificationId, principal.getName(), offset, limit);
		return PageableCollectionResource.of(relationshipChangeList.getChanges(), offset, limit, relationshipChangeList.getTotal());
	}

	@ApiOperation(
			value="Update a classification run on a version",
			notes="Update the specified classification run by changing its state property. Saving the results is an async operation "
					+ "due to the possible high number of changes. It is advised to fetch the state of the classification run until "
					+ "the state changes to 'SAVED' or 'SAVE_FAILED'.<p>"
					+ "Currently only the state can be changed from 'COMPLETED' to 'SAVED'.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, update successful"),
		@ApiResponse(code = 404, message = "Code system version or classification not found")
	})
	@RequestMapping(
			value="/classifications/{classificationId}", 
			method=RequestMethod.PUT,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void updateClassificationRun(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@ApiParam(value="The updated classification parameters")
			@RequestBody 
			final ClassificationRestRun updatedRun,

			final Principal principal) {

		updateClassificationRunOnTask(version, null, classificationId, updatedRun, principal);
	}

	@ApiOperation(
			value="Update a classification run on a task",
			notes="Update the specified classification run by changing its state property. Saving the results is an async operation "
					+ "due to the possible high number of changes. It is advised to fetch the state of the classification run until "
					+ "the state changes to 'SAVED' or 'SAVE_FAILED'.<p>"
					+ "Currently only the state can be changed from 'COMPLETED' to 'SAVED'.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, update successful"),
		@ApiResponse(code = 404, message = "Code system version, task or classification not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/classifications/{classificationId}", 
			method=RequestMethod.PUT,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void updateClassificationRunOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId") 
			final String taskId,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			@ApiParam(value="The updated classification parameters")
			@RequestBody 
			final ClassificationRestRun updatedRun,

			final Principal principal) {

		// TODO: compare all fields to find out what the client wants us to do, check for conflicts, etc.
		if (ClassificationStatus.SAVED.equals(updatedRun.getStatus())) {
			delegate.persistChanges(version, taskId, classificationId, principal.getName());
		}
	}

	@ApiOperation(
			value="Removes a classification run from a version",
			notes="Classification runs remain available until they are explicitly deleted by the client. Results of the classification cannot be retrieved after deletion.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, delete successful"),
		@ApiResponse(code = 404, message = "Code system version or classification not found")
	})
	@RequestMapping(value="/classifications/{classificationId}", method=RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void deleteClassificationRun(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			final Principal principal) {

		deleteClassificationRunOnTask(version, null, classificationId, principal);
	}

	@ApiOperation(
			value="Removes a classification run from a task",
			notes="Classification runs remain available until they are explicitly deleted by the client. Results of the classification cannot be retrieved after deletion.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, delete successful"),
		@ApiResponse(code = 404, message = "Code system version, task or classification not found")
	})
	@RequestMapping(value="/tasks/{taskId}/classifications/{classificationId}", method=RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void deleteClassificationRunOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId") 
			final String taskId,

			@ApiParam(value="The classification identifier")
			@PathVariable(value="classificationId") 
			final String classificationId,

			final Principal principal) {

		delegate.removeClassificationRun(version, taskId, classificationId, principal.getName());
	}

	private URI getClassificationUri(final String version, final Principal principal, final IClassificationRun classificationRun) {
		return linkTo(methodOn(SnomedClassificationRestService.class).getClassificationRun(version, classificationRun.getId(), principal)).toUri();
	}

	private URI getClassificationUriOnTask(final String version, final String taskId, final Principal principal, final IClassificationRun classificationRun) {
		return linkTo(methodOn(SnomedClassificationRestService.class).getClassificationRunOnTask(version, taskId, classificationRun.getId(), principal)).toUri();
	}
}