/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.browser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedRestService;

import io.swagger.annotations.Api;

/**
 * @since 1.0
 */
@Api(value = "IHTSDO SNOMED CT Browser", description="IHTSDO SNOMED CT Browser", tags = { "browser" })
@RestController
@RequestMapping(value="/browser/{path:**}")
public class SnomedBrowserRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedBrowserService browserService;
	
	public SnomedBrowserRestService() {
		super(Collections.emptySet());
	}
	
//	@ApiOperation(
//			value="Retrieve single concept properties",
//			notes="Retrieves a single concept and related information on a branch.")
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response = Void.class),
//		@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
//	})
	@GetMapping(value="/concepts/{conceptId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody ISnomedBrowserConcept getConceptDetails(
//			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

//			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

//			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		return browserService.getConceptDetails(branchPath, conceptId, extendedLocales);
	}

//	@ApiOperation(
//			value="Create a concept",
//			notes="Creates a new Concept on a branch.")
//	@ApiResponses({
//			@ApiResponse(code = 200, message = "OK", response = Void.class),
//			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
//	})
	@PostMapping(value = "/concepts", 
			consumes = { AbstractRestService.JSON_MEDIA_TYPE }, 
			produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody ISnomedBrowserConcept createConcept(
//			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

//			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			@RequestBody
			final SnomedBrowserConcept concept,

			@RequestHeader(value = X_AUTHOR)
			final String author) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		return browserService.create(branchPath, concept, author, extendedLocales);
	}

//	@ApiOperation(
//			value="Update a concept",
//			notes="Updates a new Concept on a branch.")
//	@ApiResponses({
//			@ApiResponse(code = 200, message = "OK", response = Void.class),
//			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
//	})
	@PutMapping(value="/concepts/{conceptId}", 
			consumes = { AbstractRestService.JSON_MEDIA_TYPE }, 
			produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody ISnomedBrowserConcept updateConcept(
//			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

//			@ApiParam(value="The SCTID of the concept being updated")
			@PathVariable(value="conceptId")
			final String conceptId,

//			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,

			@RequestBody
			final SnomedBrowserConceptUpdate concept,

			@RequestHeader(value = X_AUTHOR)
			final String author) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		
		if (!conceptId.equals(concept.getConceptId())) {
			throw new BadRequestException("The concept ID in the request body does not match the ID in the URL.");
		}
		
		return browserService.update(branchPath, concept, author, extendedLocales);
	}

//	@ApiOperation(
//			value = "Retrieve parents of a concept",
//			notes = "Returns a list of parent concepts of the specified concept on a branch.",
//			response=Void.class)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
//	})
	@GetMapping(value = "/concepts/{conceptId}/parents", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody List<ISnomedBrowserParentConcept> getConceptParents(
//			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
//			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
//			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting) {
		
		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		return browserService.getConceptParents(branchPath, conceptId, extendedLocales);
	}
	
//	@ApiOperation(
//			value = "Retrieve children of a concept",
//			notes = "Returns a list of child concepts of the specified concept on a branch.",
//			response=Void.class)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
//	})
	@GetMapping(value = "/concepts/{conceptId}/children", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody List<ISnomedBrowserChildConcept> getConceptChildren(
//			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

//			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

//			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting,
			
//			@ApiParam(value="Stated or inferred form", allowableValues="stated, inferred")
			@RequestParam(value="form", defaultValue="inferred")
			final String form) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		
		if ("stated".equals(form) || "inferred".equals(form)) {
			return browserService.getConceptChildren(branchPath, conceptId, extendedLocales, "stated".equals(form));
		}
		
		throw new BadRequestException("Form parameter should be either 'stated' or 'inferred'");
	}

//	@ApiOperation(
//			value = "Retrieve descriptions matching a query (sets FSN to property 'fsn' in concept section)",
//			notes = "Returns a list of descriptions which have a term matching the specified query string on a version.",
//			response=Void.class)
//	@ApiResponses({
//			@ApiResponse(code = 200, message = "OK"),
//			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
//	})
	@GetMapping(value="/descriptions-fsn", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody List<ISnomedBrowserDescriptionResult> searchDescriptionsFSN(
//			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

//			@ApiParam(value="The query string")
			@RequestParam(value="query")
			final String query,

//			@ApiParam(value="The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false) 
			final String scrollKeepAlive,
			
//			@ApiParam(value="A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false) 
			final String scrollId,
			
//			@ApiParam(value="The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,


//			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false)
			final int limit,

//			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false)
			final String languageSetting) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		
		return browserService.getDescriptions(branchPath, query, extendedLocales, ISnomedBrowserDescriptionResult.TermType.FSN,
				scrollKeepAlive, 
				scrollId, 
				searchAfter,
				limit);
	}

//	@ApiOperation(
//			value = "Retrieve descriptions matching a query (sets PT to property 'fsn' in concept section)",
//			notes = "Returns a list of descriptions which have a term matching the specified query string on a version.",
//			response=Void.class)
//	@ApiResponses({
//			@ApiResponse(code = 200, message = "OK"),
//			@ApiResponse(code = 404, message = "Code system version or concept not found", response = RestApiError.class)
//	})
	@GetMapping(value = "/descriptions", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody List<ISnomedBrowserDescriptionResult> searchDescriptionsPT(
//			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

//			@ApiParam(value="The query string")
			@RequestParam(value="query")
			final String query,

//			@ApiParam(value="The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false) 
			final String scrollKeepAlive,
			
//			@ApiParam(value="A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false) 
			final String scrollId,

//			@ApiParam(value="The sort key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,

//			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false)
			final int limit,

//			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false)
			final String languageSetting) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		
		return browserService.getDescriptions(branchPath, query, extendedLocales, ISnomedBrowserDescriptionResult.TermType.PT, 
				scrollKeepAlive, 
				scrollId, 
				searchAfter,
				limit);
	}

//	@ApiOperation(
//			value="Retrieve constants and properties",
//			notes="Retrieves referenced constants and related concept properties from a version branch.")
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response = Void.class)
//	})
	@GetMapping(value="/constants", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Map<String, ISnomedBrowserConstant> getConstants(

//			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

//			@ApiParam(value="Language codes and reference sets, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String languageSetting) {
		
		final List<ExtendedLocale> extendedLocales = getExtendedLocales(languageSetting);
		return browserService.getConstants(branchPath, extendedLocales);
	}
}
