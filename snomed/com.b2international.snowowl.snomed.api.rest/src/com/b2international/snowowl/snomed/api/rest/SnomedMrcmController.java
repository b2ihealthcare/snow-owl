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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.impl.SnomedMrcmService;
import com.b2international.snowowl.snomed.api.impl.domain.Predicate;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api("MRCM")
@RestController
@RequestMapping(value="/mrcm", produces={AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
public class SnomedMrcmController extends AbstractSnomedRestService {

	@Autowired
	private IEventBus bus;
	
	@Autowired
	private SnomedMrcmService mrcmService;
	
	@ApiOperation(
		value = "Retrieve MRCM relationship rules for a concept.", 
		notes = "Retrieve mrcm relationship rules for a concept. There are other rule types but they are not yet returned here.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response=CollectionResource.class)
	})
	@RequestMapping(value="/{conceptId}", method=RequestMethod.GET)
	public List<Predicate> getPredicates(@PathVariable String conceptId) {
		return mrcmService.getPredicates(conceptId);
	}
	
	@RequestMapping(value="/{path:**}/domain-attributes", method=RequestMethod.GET)
	public @ResponseBody SnomedConcepts getDomainAttributes(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The identifiers of the parent concepts")
			@RequestParam(required = true)
			List<String> parentIds,
			
			@ApiParam(value="The parts of the response information to expand.")
			@RequestParam(value="expand", defaultValue="", required=false)
			final String expand,
			
			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,
			
			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		return mrcmService.getDomainAttributes(branchPath, parentIds, offset, limit, getExtendedLocales(acceptLanguage), expand);
	}

	@RequestMapping(value="/{path:**}/attribute-values/{attributeId}", method=RequestMethod.GET)
	public @ResponseBody SnomedConcepts getAttributeValues(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The attribute concept identifier")
			@PathVariable
			Long attributeId,
			
			@ApiParam(value="The first few characters of the concept term to match.")
			@RequestParam(value="termPrefix", defaultValue="", required=false)
			String termPrefix,
			
			@ApiParam(value="The parts of the response information to expand.")
			@RequestParam(value="expand", defaultValue="", required=false)
			final String expand,
			
			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,
			
			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		return mrcmService.getAttributeValues(branchPath, attributeId.toString(), termPrefix, 
				offset, limit, getExtendedLocales(acceptLanguage), expand);
	}

}