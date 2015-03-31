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

import static com.google.common.collect.Maps.newHashMap;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

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

import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.snomed.api.ISnomedConceptService;
import com.b2international.snowowl.snomed.api.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.api.domain.ISnomedConceptInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedConceptUpdate;
import com.b2international.snowowl.snomed.api.domain.SearchKind;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.PageableCollectionResource;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedConceptRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedConceptRestUpdate;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("SNOMED CT Concepts")
@Controller
@RequestMapping(
		value="/{version}", 
		produces={ AbstractRestService.V1_MEDIA_TYPE })
public class SnomedConceptRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedConceptService delegate;

	@ApiOperation(
			value="Retrieve concepts from a version", 
			notes="Returns a list with all/filtered concepts from a version branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 400, message = "Invalid filter config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Code system version not found")
	})
	@RequestMapping(value="/concepts", method=RequestMethod.GET)
	public @ResponseBody PageableCollectionResource<ISnomedConcept> getConcepts(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The label to match")
			@RequestParam(value="label", defaultValue="", required=false) 
			final String labelFilter,

			@ApiParam(value="The ESCG filtering expression to apply")
			@RequestParam(value="escg", defaultValue="", required=false) 
			final String escgFilter,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {

		return getConceptsOnTask(version, null, labelFilter, escgFilter, offset, limit);
	}

	@ApiOperation(
			value="Retrieve concepts from a task", 
			notes="Returns a list with all/filtered concepts from a task branch on a version.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 400, message = "Invalid filter config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Code system version or task not found")
	})
	@RequestMapping(value="/tasks/{taskId}/concepts", method=RequestMethod.GET)
	public @ResponseBody PageableCollectionResource<ISnomedConcept> getConceptsOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="The label to match")
			@RequestParam(value="label", defaultValue="", required=false) 
			final String labelFilter,

			@ApiParam(value="The ESCG filtering expression to apply")
			@RequestParam(value="escg", defaultValue="", required=false) 
			final String escgFilter,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {

		final IComponentList<ISnomedConcept> concepts;
		final Map<SearchKind, String> queryParams = newHashMap();

		registerIfNotNull(SearchKind.LABEL, labelFilter, queryParams);
		registerIfNotNull(SearchKind.ESCG, escgFilter, queryParams);

		if (queryParams.isEmpty()) {
			concepts = delegate.getAllConcepts(version, taskId, offset, limit);
		} else {
			concepts = delegate.search(version, taskId, queryParams, offset, limit);
		}

		return PageableCollectionResource.of(concepts.getMembers(), offset, limit, concepts.getTotalMembers());
	}

	private void registerIfNotNull(SearchKind kind, final String filterText, final Map<SearchKind, String> queryParams) {
		if (!Strings.isNullOrEmpty(filterText)) {
			queryParams.put(kind, filterText);
		}
	}

	@ApiOperation(
			value="Retrieve concept properties",
			notes="Returns all properties of the specified concept, including a summary of inactivation indicator and association members.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(value="/concepts/{conceptId}", method=RequestMethod.GET)
	public @ResponseBody ISnomedConcept read(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId) {

		return readOnTask(version, null, conceptId);
	}

	@ApiOperation(
			value="Retrieve concept properties on task",
			notes="Returns all properties of the specified concept on a task branch, including a summary of inactivation indicator "
					+ "and association members.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Code system version, task or concept not found")
	})
	@RequestMapping(value="/tasks/{taskId}/concepts/{conceptId}", method=RequestMethod.GET)
	public @ResponseBody ISnomedConcept readOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId) {

		final IComponentRef conceptRef = createComponentRef(version, taskId, conceptId);
		return delegate.read(conceptRef);
	}

	@ApiOperation(
			value="Create concept", 
			notes="Creates a new concept directly on a version branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Concept created on task"),
		@ApiResponse(code = 404, message = "Code system version not found")
	})
	@RequestMapping(
			value="/concepts", 
			method=RequestMethod.POST, 
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="Concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestInput> body,

			final Principal principal) {
		
		final ISnomedConcept createdConcept = doCreate(version, null, body, principal);
		return Responses.created(getConceptLocationURI(version, createdConcept)).build();
	}

	@ApiOperation(
			value="Create concept on task", 
			notes="Creates a new concept on a task branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Concept created on task"),
		@ApiResponse(code = 404, message = "Code system version or task not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/concepts", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="Concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestInput> body,

			final Principal principal) {

		final ISnomedConcept createdConcept = doCreate(version, taskId, body, principal);
		return Responses.created(getConceptOnTaskLocationURI(version, taskId, createdConcept)).build();
	}

	@ApiOperation(
			value="Update concept",
			notes="Updates properties of the specified concept, also managing inactivation indicator and association reference set "
					+ "membership in case of inactivation."
					+ "<p>The following properties are allowed to change:"
					+ "<p>"
					+ "&bull; module identifier<br>"
					+ "&bull; subclass definition status<br>"
					+ "&bull; definition status<br>"
					+ "&bull; associated concepts<br>"
					+ ""
					+ "<p>The following properties, when changed, will trigger inactivation:"
					+ "<p>"
					+ "&bull; inactivation indicator<br>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(
			value="/concepts/{conceptId}/updates", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(			
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value="Updated concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestUpdate> body,

			final Principal principal) {

		updateOnTask(version, null, conceptId, body, principal);
	}

	@ApiOperation(
			value="Update concept on task",
			notes="Updates properties of the specified concept on a task branch, also managing inactivation indicator and association "
					+ "reference set membership in case of inactivation."
					+ "<p>The following properties are allowed to change:"
					+ "<p>"
					+ "&bull; module identifier<br>"
					+ "&bull; subclass definition status<br>"
					+ "&bull; definition status<br>"
					+ "&bull; associated concepts<br>"
					+ ""
					+ "<p>The following properties, when changed, will trigger inactivation:"
					+ "<p>"
					+ "&bull; inactivation indicator<br>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Code system version, task or concept not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/concepts/{conceptId}/updates", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateOnTask(			
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId, 

			@ApiParam(value="Updated concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestUpdate> body,

			final Principal principal) {

		final IComponentRef conceptRef = createComponentRef(version, taskId, conceptId);
		final ISnomedConceptUpdate update = body.getChange().toComponentUpdate();
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();

		delegate.update(conceptRef, update, userId, commitComment);
	}

	@ApiOperation(
			value="Delete concept",
			notes="Permanently removes the specified unreleased concept and related components.<p>If any participating "
					+ "component has already been released, the concept can not be removed and a <code>409</code> "
					+ "status will be returned.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Deletion successful"),
		@ApiResponse(code = 409, message = "Cannot be deleted if released", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(value="/concepts/{conceptId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(			
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId, 

			final Principal principal) {

		deleteOnTask(version, null, conceptId, principal);
	}

	@ApiOperation(
			value="Delete concept on task",
			notes="Permanently removes the specified unreleased concept and related components from a task branch.<p>If any participating "
					+ "component has already been released, the concept can not be removed and a <code>409</code> "
					+ "status will be returned.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Deletion successfully completed"),
		@ApiResponse(code = 409, message = "Cannot be deleted if released", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Code system version, task or concept not found")
	})
	@RequestMapping(value="/tasks/{taskId}/concepts/{conceptId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOnTask(			
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,

			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId, 

			final Principal principal) {

		final IComponentRef conceptRef = createComponentRef(version, taskId, conceptId);
		final String userId = principal.getName();
		delegate.delete(conceptRef, userId, "Deleted concept from store.");
	}

	private ISnomedConcept doCreate(final String version, final String taskId, final ChangeRequest<SnomedConceptRestInput> body, final Principal principal) {
		final ISnomedConceptInput input = body.getChange().toComponentInput(version, taskId);
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		return delegate.create(input, userId, commitComment);
	}

	private URI getConceptLocationURI(String version, ISnomedConcept concept) {
		return linkTo(methodOn(SnomedConceptRestService.class).read(version, concept.getId())).toUri();
	}

	private URI getConceptOnTaskLocationURI(String version, String taskId, ISnomedConcept concept) {
		return linkTo(methodOn(SnomedConceptRestService.class).readOnTask(version, taskId, concept.getId())).toUri();
	}
}