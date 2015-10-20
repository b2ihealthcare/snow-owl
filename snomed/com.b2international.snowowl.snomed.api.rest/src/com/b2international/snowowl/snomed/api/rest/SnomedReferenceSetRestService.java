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

import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.CollectionResource;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRefSetRestInput;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.refset.SnomedReferenceSetService;
import com.b2international.snowowl.snomed.datastore.server.domain.DefaultSnomedRefSetCreateAction;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 4.5
 */
@Api("Reference Sets")
@Controller
@RequestMapping(produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class SnomedReferenceSetRestService extends AbstractSnomedRestService {

	@Autowired
	private SnomedReferenceSetService delegate;
	
	@ApiOperation(
			value="Retrieve Reference Sets from a branch", 
			notes="Returns a list with all reference sets from a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CollectionResource.class),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(value="/{path:**}/refsets", method=RequestMethod.GET)	
	public @ResponseBody CollectionResource<SnomedReferenceSet> getReferenceSets(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath) {
		return CollectionResource.of(delegate.getReferenceSets(branchPath));
	}
	
	@ApiOperation(
			value="Retrieve Reference Set",
			notes="Returns all properties of the specified Reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Reference set not found")
	})
	@RequestMapping(value="/{path:**}/refsets/{refSetId}", method=RequestMethod.GET)
	public @ResponseBody SnomedReferenceSet read(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Reference set identifier")
			@PathVariable(value="refSetId")
			final String refSetId) {
		return delegate.read(createComponentRef(branchPath, refSetId));
	}
	
	@ApiOperation(
			value="Create a reference set",
			notes="Creates a new reference set directly on a branch. Creates the corresponding identifier concept.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(value="/{path:**}/refsets", method=RequestMethod.POST)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="Reference set parameters")
			@RequestBody 
			final ChangeRequest<SnomedRefSetRestInput> body,

			final Principal principal) {
		
		final SnomedReferenceSet createdRefSet = doCreate(branchPath, body, principal);
		return Responses.created(getRefSetLocationURI(branchPath, createdRefSet)).build();
	}
	
	private SnomedReferenceSet doCreate(final String branchPath, final ChangeRequest<SnomedRefSetRestInput> body, final Principal principal) {
		final DefaultSnomedRefSetCreateAction input = body.getChange().toComponentInput(branchPath, codeSystemShortName);
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		return delegate.create(input, userId, commitComment);
	}

	private URI getRefSetLocationURI(String branchPath, SnomedReferenceSet refSet) {
		return linkTo(SnomedConceptRestService.class).slash(branchPath).slash("refsets").slash(refSet.getId()).toUri();
	}
	
}
