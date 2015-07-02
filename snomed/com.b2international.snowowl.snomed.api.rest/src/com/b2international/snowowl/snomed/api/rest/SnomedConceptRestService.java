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

import java.net.URI;
import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.snomed.api.ISnomedConceptService;
import com.b2international.snowowl.snomed.api.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.api.domain.ISnomedConceptInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedConceptUpdate;
import com.b2international.snowowl.snomed.api.domain.SearchKind;
import com.b2international.snowowl.snomed.api.rest.domain.*;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.*;

/**
 * @since 1.0
 */
@Api("Concepts")
@Controller
@RequestMapping(produces={ AbstractRestService.SO_MEDIA_TYPE })
public class SnomedConceptRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedConceptService delegate;

	@ApiOperation(
			value="Retrieve Concepts from a branch", 
			notes="Returns a list with all/filtered Concepts from a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 400, message = "Invalid filter config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(value="/{path:**}/concepts", method=RequestMethod.GET)
	public @ResponseBody PageableCollectionResource<ISnomedConcept> getConcepts(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

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
			concepts = delegate.getAllConcepts(branchPath, offset, limit);
		} else {
			concepts = delegate.search(branchPath, queryParams, offset, limit);
		}

		return PageableCollectionResource.of(concepts.getMembers(), offset, limit, concepts.getTotalMembers());
	}

	private void registerIfNotNull(SearchKind kind, final String filterText, final Map<SearchKind, String> queryParams) {
		if (!Strings.isNullOrEmpty(filterText)) {
			queryParams.put(kind, filterText);
		}
	}

	@ApiOperation(
			value="Retrieve Concept properties",
			notes="Returns all properties of the specified Concept, including a summary of inactivation indicator and association members.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Concept not found")
	})
	@RequestMapping(value="/{path:**}/concepts/{conceptId}", method=RequestMethod.GET)
	public @ResponseBody ISnomedConcept read(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId) {

		final IComponentRef conceptRef = createComponentRef(branchPath, conceptId);
		return delegate.read(conceptRef);
	}

	@ApiOperation(
			value="Create Concept", 
			notes="Creates a new Concept directly on a branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Concept created on task"),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(
			value="/{path:**}/concepts", 
			method=RequestMethod.POST, 
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="Concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestInput> body,

			final Principal principal) {
		
		final ISnomedConcept createdConcept = doCreate(branchPath, body, principal);
		return Responses.created(getConceptLocationURI(branchPath, createdConcept)).build();
	}

	@ApiOperation(
			value="Update Concept",
			notes="Updates properties of the specified Concept, also managing inactivation indicator and association reference set "
					+ "membership in case of inactivation."
					+ "<p>The following properties are allowed to change:"
					+ "<p>"
					+ "&bull; module identifier<br>"
					+ "&bull; subclass definition status<br>"
					+ "&bull; definition status<br>"
					+ "&bull; associated Concepts<br>"
					+ ""
					+ "<p>The following properties, when changed, will trigger inactivation:"
					+ "<p>"
					+ "&bull; inactivation indicator<br>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or Concept not found")
	})
	@RequestMapping(
			value="/{path:**}/concepts/{conceptId}/updates", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(			
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value="Updated Concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestUpdate> body,

			final Principal principal) {

		final IComponentRef conceptRef = createComponentRef(branchPath, conceptId);
		final ISnomedConceptUpdate update = body.getChange().toComponentUpdate();
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();

		delegate.update(conceptRef, update, userId, commitComment);
	}

	@ApiOperation(
			value="Delete Concept",
			notes="Permanently removes the specified unreleased Concept and related components.<p>If any participating "
					+ "component has already been released, the Concept can not be removed and a <code>409</code> "
					+ "status will be returned.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Deletion successful"),
		@ApiResponse(code = 409, message = "Cannot be deleted if released", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch or Concept not found")
	})
	@RequestMapping(value="/{path:**}/concepts/{conceptId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(			
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId, 

			final Principal principal) {
		final IComponentRef conceptRef = createComponentRef(branchPath, conceptId);
		final String userId = principal.getName();
		delegate.delete(conceptRef, userId, String.format("Deleted Concept '%s' from store.", conceptId));
	}

	private ISnomedConcept doCreate(final String branchPath, final ChangeRequest<SnomedConceptRestInput> body, final Principal principal) {
		final ISnomedConceptInput input = body.getChange().toComponentInput(branchPath, codeSystemShortName);
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		return delegate.create(input, userId, commitComment);
	}

	private URI getConceptLocationURI(String branchPath, ISnomedConcept concept) {
		return linkTo(SnomedConceptRestService.class).slash(branchPath).slash("concepts").slash(concept.getId()).toUri();
	}
}
