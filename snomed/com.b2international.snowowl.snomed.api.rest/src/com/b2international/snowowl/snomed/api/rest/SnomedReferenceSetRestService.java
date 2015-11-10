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

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRefSetRestInput;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRefSetCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
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

	@ApiOperation(
			value="Retrieve Reference Sets from a branch", 
			notes="Returns a list with all reference sets from a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CollectionResource.class),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(value="/{path:**}/refsets", method=RequestMethod.GET)	
	public @ResponseBody DeferredResult<SnomedReferenceSets> getReferenceSets(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath) {
		return DeferredResults.wrap(SnomedRequests.prepareGetReferenceSets(branchPath).execute(bus));
	}
	
	@ApiOperation(
			value="Retrieve Reference Set",
			notes="Returns all properties of the specified Reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Reference set not found")
	})
	@RequestMapping(value="/{path:**}/refsets/{id}", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<SnomedReferenceSet> read(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Reference set identifier")
			@PathVariable(value="id")
			final String referenceSetId) {
		return DeferredResults.wrap(SnomedRequests.prepareGetReferenceSet(branchPath, referenceSetId).execute(bus));
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
		
		final SnomedRefSetRestInput change = body.getChange();
		
		final SnomedRefSetCreateRequestBuilder req = SnomedRequests
			.prepareNewRefSet()
			.setIdentifierConcept(change.toComponentInput())
			.setType(change.getType())
			.setReferencedComponentType(change.getReferencedComponentType());
		
		final SnomedReferenceSet createdRefSet = 
				SnomedRequests
					.<SnomedReferenceSet>prepareCommit(principal.getName(), branchPath)
					.setBody(req)
					.setCommitComment(body.getCommitComment())
					.build()
					.executeSync(bus, 120L * 1000L)
					.getResultAs(SnomedReferenceSet.class);
		
		return Responses.created(getRefSetLocationURI(branchPath, createdRefSet)).build();
	}
	
	private URI getRefSetLocationURI(String branchPath, SnomedReferenceSet refSet) {
		return linkTo(SnomedReferenceSetRestService.class).slash(branchPath).slash("refsets").slash(refSet.getId()).toUri();
	}
	
}
