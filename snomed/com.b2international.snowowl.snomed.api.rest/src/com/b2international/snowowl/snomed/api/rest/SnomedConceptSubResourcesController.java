/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.api.exception.FullySpecifiedNameNotFoundException;
import com.b2international.snowowl.snomed.api.exception.PreferredTermNotFoundException;
import com.b2international.snowowl.snomed.api.impl.DescriptionService;
import com.b2international.snowowl.snomed.api.rest.domain.ExpandableSnomedRelationship;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedConceptDescriptions;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedInboundRelationships;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedOutboundRelationships;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("Concepts")
//@RestController
@RequestMapping(
		produces={ AbstractRestService.SO_MEDIA_TYPE })
public class SnomedConceptSubResourcesController extends AbstractSnomedRestService {

	@ApiOperation(
			value="Retrieve descriptions of a concept", 
			notes="Returns all descriptions associated with the specified concept.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/concepts/{conceptId}/descriptions", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<SnomedConceptDescriptions> getConceptDescriptions(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand) {
		
		return DeferredResults.wrap(
				SnomedRequests
					.prepareSearchDescription()
					.all()
					.filterByConcept(conceptId)
					.setExpand(expand)
					.build(repositoryId, branchPath)
					.execute(bus)
					.then(new Function<SnomedDescriptions, SnomedConceptDescriptions>() {
						@Override
						public SnomedConceptDescriptions apply(SnomedDescriptions input) {
							final SnomedConceptDescriptions result = new SnomedConceptDescriptions();
							result.setConceptDescriptions(ImmutableList.copyOf(input.getItems()));
							return result;
						};
					}));
	}

	@ApiOperation(
			value="Retrieve inbound relationships of a concept",
			notes="Returns a list of all inbound relationships of the specified concept."
					+ "<p>The following properties can be expanded:"
					+ "<p>"
					+ "&bull; source.fsn &ndash; the fully specified name of the relationship's source concept in the given locale<br>"
					+ "&bull; type.fsn &ndash; the fully specified name of the relationships's type concept in the given locale<br>",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/concepts/{conceptId}/inbound-relationships", method=RequestMethod.GET)
	public DeferredResult<SnomedInboundRelationships> getInboundStatements(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="What parts of the response information to expand.")
			@RequestParam(value="expand", defaultValue="", required=false)
			final String[] expand,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

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
		
		final StringBuilder newExpand = new StringBuilder();
		for (String expandParam : expand) {
			if (newExpand.length() > 0) {
				newExpand.append(',');
			}
			if ("type.fsn".equals(expandParam)) {
				newExpand.append("type(expand(fsn()))");
			} else if ("source.fsn".equals(expandParam)) {
				newExpand.append("source(expand(fsn()))");
			}
		}
		
		return DeferredResults.wrap(
				SnomedRequests
					.prepareSearchRelationship()
					.filterByDestination(conceptId)
					.setOffset(offset)
					.setLimit(limit)
					.setExpand(newExpand.toString())
					.setLocales(extendedLocales)
					.build(repositoryId, branchPath)
					.execute(bus)
					.then(new Function<SnomedRelationships, SnomedInboundRelationships>() {
						@Override
						public SnomedInboundRelationships apply(SnomedRelationships input) {
							final SnomedInboundRelationships result = new SnomedInboundRelationships();
							result.setTotal(input.getTotal());
							
							final List<ExpandableSnomedRelationship> members = newArrayList();
							for (SnomedRelationship relationship : input) {
								members.add(new ExpandableSnomedRelationship(relationship, expand));
							}
							
							result.setInboundRelationships(members);
							return result;
						};
					}));
	}

	@ApiOperation(
			value="Retrieve outbound relationships of a concept",
			notes="Returns a list of all outbound relationships of the specified concept.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/concepts/{conceptId}/outbound-relationships", method=RequestMethod.GET)
	public DeferredResult<SnomedOutboundRelationships> getOutboundStatements(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {

		return DeferredResults.wrap(
				SnomedRequests
					.prepareSearchRelationship()
					.filterBySource(conceptId)
					.setOffset(offset)
					.setLimit(limit)
					.build(repositoryId, branchPath)
					.execute(bus)
					.then(new Function<SnomedRelationships, SnomedOutboundRelationships>() {
						@Override
						public SnomedOutboundRelationships apply(SnomedRelationships input) {
							final SnomedOutboundRelationships result = new SnomedOutboundRelationships();
							result.setTotal(input.getTotal());
							result.setOutboundRelationships(input.getItems());
							return result;
						};
					}));
	}

	@ApiOperation(
			value = "Retrieve descendants of a concept",
			notes = "Returns a list of descendant concepts of the specified concept on a branch",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@RequestMapping(
			value="/{path:**}/concepts/{conceptId}/descendants",
			method = RequestMethod.GET)
	public DeferredResult<SnomedConcepts> getDescendants(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,

			@ApiParam(value="Return direct descendants only")
			@RequestParam(value="direct", defaultValue="false", required=false) 
			final boolean direct,
			
			@ApiParam(value="Return stated or inferred descendants")
			@RequestParam(value="form", defaultValue="inferred", required=false)
			final String form) {
	
		return DeferredResults.wrap(SnomedRequests.prepareGetConcept(conceptId)
				.setExpand(String.format("descendants(form:\"%s\",direct:%s,offset:%d,limit:%d)", form, direct, offset, limit))
				.build(repositoryId, branchPath)
				.execute(bus)
				.then(new Function<SnomedConcept, SnomedConcepts>() {
					@Override
					public SnomedConcepts apply(SnomedConcept input) {
						return input.getDescendants();
					}
				}));
	}

	@ApiOperation(
			value = "Retrieve ancestors of a concept",
			notes = "Returns a list of ancestor concepts of the specified concept on a branch",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@RequestMapping(
			value="/{path:**}/concepts/{conceptId}/ancestors",
			method = RequestMethod.GET)
	public DeferredResult<SnomedConcepts> getAncestors(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,

			@ApiParam(value="Return direct ancestors only")
			@RequestParam(value="direct", defaultValue="false", required=false) 
			final boolean direct,
			
			@ApiParam(value="Return stated or inferred ancestors")
			@RequestParam(value="form", defaultValue="inferred", required=false)
			final String form) {

		return DeferredResults.wrap(SnomedRequests.prepareGetConcept(conceptId)
				.setExpand(String.format("ancestors(form:\"%s\",direct:%s,offset:%d,limit:%d)", form, direct, offset, limit))
				.build(repositoryId, branchPath)
				.execute(bus)
				.then(new Function<SnomedConcept, SnomedConcepts>() {
					@Override
					public SnomedConcepts apply(SnomedConcept input) {
						return input.getAncestors();
					}
				}));
	}

	@ApiOperation(
			value = "Retrieve the preferred term of a concept",
			notes = "Returns the preferred term of the specified concept on a branch based on the defined language preferences in the header",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch, Concept or Preferred Term not found", response = RestApiError.class)
	})
	@RequestMapping(
			value="/{path:**}/concepts/{conceptId}/pt",
			method = RequestMethod.GET)
	public @ResponseBody SnomedDescription getPreferredTerm(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

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
		
		final DescriptionService descriptionService = new DescriptionService(bus, branchPath);
		final SnomedDescription pt = descriptionService.getPreferredTerm(conceptId, extendedLocales);
		
		if (pt == null) {
			throw new PreferredTermNotFoundException(conceptId);
		} else {
			return pt;
		}
	}
	
	@ApiOperation(
			value = "Retrieve the fully specified name of a concept",
			notes = "Returns the fully specified name of the specified concept on a branch based on the defined language preferences in the header",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch, Concept or FSN not found", response = RestApiError.class)
	})
	@RequestMapping(
			value="/{path:**}/concepts/{conceptId}/fsn",
			method = RequestMethod.GET)
	public @ResponseBody SnomedDescription getFullySpecifiedName(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(acceptLanguage));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		final DescriptionService descriptionService = new DescriptionService(bus, branchPath);
		final SnomedDescription fsn = descriptionService.getFullySpecifiedName(conceptId, extendedLocales);
		
		if (fsn == null) {
			throw new FullySpecifiedNameNotFoundException(conceptId);
		} else {
			return fsn;
		}
	}
}
