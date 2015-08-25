/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.b2international.snowowl.snomed.api.rest.browser;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.impl.domain.StorageRef;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.api.ISnomedConceptService;
import com.b2international.snowowl.snomed.api.browser.ISnomedBrowserService;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserChildConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConstant;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescriptionResult;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserParentConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedRestService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("IHTSDO SNOMED CT Browser")
@Controller
@RequestMapping(
		value="/browser/{path:**}",
		produces={ SnomedBrowserRestService.IHTSDO_V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class SnomedBrowserRestService extends AbstractSnomedRestService {

	/**
	 * The currently supported versioned media type of the IHTSDO SNOMED CT Browser RESTful API.
	 */
	public static final String IHTSDO_V1_MEDIA_TYPE = "application/vnd.org.ihtsdo.browser+json";

	@Autowired
	protected ISnomedBrowserService browserService;
	
	@Autowired
	private ISnomedConceptService conceptService;
	
	@ApiOperation(
			value="Retrieve single concept properties",
			notes="Retrieves a single concept and related information on a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(value="/concepts/{conceptId}", method=RequestMethod.GET)
	public @ResponseBody ISnomedBrowserConcept getConceptDetails(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		final IComponentRef conceptRef = createComponentRef(branchPath, conceptId);
		return browserService.getConceptDetails(conceptRef, Collections.list(request.getLocales()));
	}

	@ApiOperation(
			value="Create a concept",
			notes="Creates a new Concept on a branch.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = Void.class),
			@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(value="/concepts", method=RequestMethod.POST)
	public @ResponseBody ISnomedBrowserConcept createConcept(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@RequestBody
			final SnomedBrowserConcept concept,

			final Principal principal,

			final HttpServletRequest request) {

		final String userId = principal.getName();
		if (concept.getConceptId() != null) {
			throw new BadRequestException("The concept in the request body should not have an ID when creating. When performing an update include the concept ID in the URL.");
		}
		return browserService.create(branchPath, concept, userId, Collections.list(request.getLocales()));
	}

	@ApiOperation(
			value="Update a concept",
			notes="Updates a new Concept on a branch.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = Void.class),
			@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(value="/concepts/{conceptId}", method=RequestMethod.PUT)
	public @ResponseBody ISnomedBrowserConcept updateConcept(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The SCTID of the concept being updated")
			@PathVariable(value="conceptId")
			final String conceptId,

			@RequestBody
			final SnomedBrowserConcept concept,

			final Principal principal,

			final HttpServletRequest request) {

		if (!conceptId.equals(concept.getConceptId())) {
			throw new BadRequestException("The concept ID in the request body does not match the ID in the URL.");
		}

		final String userId = principal.getName();
		return browserService.update(branchPath, concept, userId, Collections.list(request.getLocales()));
	}

	@ApiOperation(
			value = "Retrieve parents of a concept",
			notes = "Returns a list of parent concepts of the specified concept on a branch.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(
			value="/concepts/{conceptId}/parents",
			method = RequestMethod.GET)
	public @ResponseBody List<ISnomedBrowserParentConcept> getConceptParents(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,
			
			final HttpServletRequest request) {
		
		final IComponentRef ref = createComponentRef(branchPath, conceptId);
		return browserService.getConceptParents(ref, Collections.list(request.getLocales()));
	}
	
	@ApiOperation(
			value = "Retrieve children of a concept",
			notes = "Returns a list of child concepts of the specified concept on a branch.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(
			value="/concepts/{conceptId}/children",
			method = RequestMethod.GET)
	public @ResponseBody List<ISnomedBrowserChildConcept> getConceptChildren(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {

		final IComponentRef ref = createComponentRef(branchPath, conceptId);
		return browserService.getConceptChildren(ref, Collections.list(request.getLocales()));
	}

	@ApiOperation(
			value = "Retrieve descriptions matching a query (hits DB to fetch FSN in concept section)",
			notes = "Returns a list of descriptions which have a term matching the specified query string on a version.",
			response=Void.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(
			value="/descriptions-fsn",
			method = RequestMethod.GET)
	public @ResponseBody List<ISnomedBrowserDescriptionResult> searchDescriptionsFSN(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The query string")
			@RequestParam(value="query")
			final String query,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false)
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false)
			final int limit,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false)
			final String languageSetting,

			final HttpServletRequest request) {

		final StorageRef ref = new StorageRef(codeSystemShortName, branchPath);
		return browserService.getDescriptions(ref, query, Collections.list(request.getLocales()), ISnomedBrowserDescriptionResult.TermType.FSN, offset, limit);
	}

	@ApiOperation(
			value = "Retrieve descriptions matching a query (returns indexed fields only - PT not FSN within concept section)",
			notes = "Returns a list of descriptions which have a term matching the specified query string on a version.",
			response=Void.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Code system version or concept not found")
	})
	@RequestMapping(
			value="/descriptions",
			method = RequestMethod.GET)
	public @ResponseBody List<ISnomedBrowserDescriptionResult> searchDescriptionsPT(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The query string")
			@RequestParam(value="query")
			final String query,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false)
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false)
			final int limit,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false)
			final String languageSetting,

			final HttpServletRequest request) {

		final StorageRef ref = new StorageRef(codeSystemShortName, branchPath);
		return browserService.getDescriptions(ref, query, Collections.list(request.getLocales()), ISnomedBrowserDescriptionResult.TermType.PT, offset, limit);
	}

	@ApiOperation(
			value="Retrieve constants and properties",
			notes="Retrieves referenced constants and related concept properties from a version branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class)
	})
	@RequestMapping(value="/constants", method=RequestMethod.GET)
	public @ResponseBody Map<String, ISnomedBrowserConstant> getConstants(

			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			final HttpServletRequest request) {
		final StorageRef ref = new StorageRef(codeSystemShortName, branchPath);
		return browserService.getConstants(ref, Collections.list(request.getLocales()));
	}

}
