/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.io.StringReader;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.snomed.api.browser.ISnomedBrowserService;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserChildConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConstant;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescriptionResult;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserParentConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConceptUpdate;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedRestService;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
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
	
	@ApiOperation(
			value="Retrieve single concept properties",
			notes="Retrieves a single concept and related information on a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
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
			final String languageSetting) {

		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(languageSetting));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return browserService.getConceptDetails(branchPath, conceptId, extendedLocales);
	}

	@ApiOperation(
			value="Create a concept",
			notes="Creates a new Concept on a branch.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = Void.class),
			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
	})
	@RequestMapping(value="/concepts", 
			method=RequestMethod.POST, 
			consumes={ IHTSDO_V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody ISnomedBrowserConcept createConcept(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			@RequestBody
			final SnomedBrowserConcept concept,

			final Principal principal) {

		final String userId = principal.getName();
		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(languageSetting));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return browserService.create(branchPath, concept, userId, extendedLocales);
	}

	@ApiOperation(
			value="Update a concept",
			notes="Updates a new Concept on a branch.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = Void.class),
			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
	})
	@RequestMapping(value="/concepts/{conceptId}", 
			method=RequestMethod.PUT,
			consumes={ IHTSDO_V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody ISnomedBrowserConcept updateConcept(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The SCTID of the concept being updated")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			@RequestBody
			final SnomedBrowserConceptUpdate concept,

			final Principal principal) {

		final String userId = principal.getName();
		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(languageSetting));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		if (!conceptId.equals(concept.getConceptId())) {
			throw new BadRequestException("The concept ID in the request body does not match the ID in the URL.");
		}
		
		return browserService.update(branchPath, concept, userId, extendedLocales);
	}

	@ApiOperation(
			value = "Retrieve parents of a concept",
			notes = "Returns a list of parent concepts of the specified concept on a branch.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
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
			final String languageSetting) {
		
		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(languageSetting));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return browserService.getConceptParents(branchPath, conceptId, extendedLocales);
	}
	
	@ApiOperation(
			value = "Retrieve children of a concept",
			notes = "Returns a list of child concepts of the specified concept on a branch.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
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
			
			@ApiParam(value="Stated or inferred form", allowableValues="stated, inferred")
			@RequestParam(value="form", defaultValue="inferred")
			final String form) {

		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(languageSetting));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		if ("stated".equals(form) || "inferred".equals(form)) {
			return browserService.getConceptChildren(branchPath, conceptId, extendedLocales, "stated".equals(form));
		}
		
		throw new BadRequestException("Form parameter should be either 'stated' or 'inferred'");
	}

	@ApiOperation(
			value = "Retrieve descriptions matching a query (sets FSN to property 'fsn' in concept section)",
			notes = "Returns a list of descriptions which have a term matching the specified query string on a version.",
			response=Void.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
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

			@ApiParam(value="The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false) 
			final String scrollKeepAlive,
			
			@ApiParam(value="A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false) 
			final String scrollId,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false)
			final int limit,

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
		
		return browserService.getDescriptions(branchPath, query, extendedLocales, ISnomedBrowserDescriptionResult.TermType.FSN, scrollKeepAlive, scrollId, limit);
	}

	@ApiOperation(
			value = "Retrieve descriptions matching a query (sets PT to property 'fsn' in concept section)",
			notes = "Returns a list of descriptions which have a term matching the specified query string on a version.",
			response=Void.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
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

			@ApiParam(value="The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false) 
			final String scrollKeepAlive,
			
			@ApiParam(value="A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false) 
			final String scrollId,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false)
			final int limit,

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
		
		return browserService.getDescriptions(branchPath, query, extendedLocales, ISnomedBrowserDescriptionResult.TermType.PT, scrollKeepAlive, scrollId, limit);
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
			final String languageSetting) {
		
		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(languageSetting));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return browserService.getConstants(branchPath, extendedLocales);
	}
}
